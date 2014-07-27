package com.zxsoft.crawler.parse;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;

/**
 * Control which parser to parse the page.
 */
public final class ParserController extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(ParserController.class);
	private Configuration conf;
	private ThreadPoolExecutor pool = null;
	private AtomicBoolean continuePage = new AtomicBoolean(true);
	private AtomicInteger pageNum = new AtomicInteger(1);
	private String indexUrl;
	private boolean ajax;
	
	public ParserController(Configuration conf) {
//		super.setConf(conf);
		this.conf = conf;
	}
	
	private ThreadLocal<Parser> parserThreadLocal = new ThreadLocal<Parser>();

	public Parser getParser() {
		return parserThreadLocal.get();
	}
	
	public ParseStatus parse(WebPage page) throws ParserNotFoundException {
		ParserFactory factory = new ParserFactory();
		factory.setConf(conf);
		
		ListConf listConf = confDao.getListConf(page.getBaseUrl());
		ParseStatus status = null;
		
		if (listConf != null) {
			Parser parser = factory.getParserByCategory(listConf.getCategory());
			parserThreadLocal.set(parser);
			int numThreads = Utils.getPositiveNumber(listConf.getNumThreads(), 1);
			int maxThreads = conf.getInt("spider.parse.thread.max", 10);
			if (numThreads > maxThreads)
				numThreads = maxThreads;
			pool = newFixedThreadPool(6);
			status = parseListPage(page, parser, listConf);
		}
		return status;
	}

	/**
	 * 解析列表页()
	 */
	public ParseStatus parseListPage(WebPage page, Parser parser, ListConf listConf) {
		ParseStatus status = new ParseStatus();
		Document document = page.getDocument();
		indexUrl = page.getBaseUrl();
		ajax = page.isAjax();
		
		LOG.info("【" + listConf.getComment() + "】抓取开始");
		
		while (true) {
			Elements list = document.select(listConf.getListdom());
			if (CollectionUtils.isEmpty(list)) {
				LOG.warn("main dom set error:" + indexUrl);
				return null;
			}
			Elements lines = list.first().select(listConf.getLinedom());

			if (pageNum.get() > listConf.getPageNum()) {
				continuePage.set(false);
				break;
			}

			LOG.info("【" + listConf.getComment() + "】thread number in " + pageNum.get() + " page: " + lines.size());
			
			List<Callable<ParseStatus>> tasks = new ArrayList<Callable<ParseStatus>>();
			
			int i = 0;
			for (Element line : lines) {
//				if (i > 5) {
//					break;
//				}
//				i++;
				
				Date lastupdate = null;
				if (!StringUtils.isEmpty(listConf.getUpdatedom())
				        && !CollectionUtils.isEmpty(line.select(listConf.getUpdatedom()))) {
					try {
	                    lastupdate = Utils.formatDate(line.select(listConf.getUpdatedom()).first().text());
	                    if (lastupdate.before(new Date(page.getPrevFetchTime()))) {
	                    	continuePage.set(false);
	                    	break;
	                    }
                    } catch (ParseException e) {
                    	LOG.error("Cannot parse date: " + lastupdate + " in page " + indexUrl, e);
                    }
				}

				Date releasedate = null; // NOTE:有些列表页面可能没有发布时间
				if (!StringUtils.isEmpty(listConf.getDatedom())
				        && !CollectionUtils.isEmpty(line.select(listConf.getDatedom()))) {
					try {
						releasedate = Utils.formatDate(line.select(listConf.getDatedom()).first()
						        .text());
					} catch (ParseException e) {
						LOG.error("Cannot parse date: " + releasedate + " in page " + indexUrl, e);
					}
				}
				
				if (CollectionUtils.isEmpty(line.select(listConf.getUrldom()))
				        || StringUtils.isEmpty(line.select(listConf.getUrldom()).first().absUrl("href")))
					continue;

				String curl = line.select(listConf.getUrldom()).first().absUrl("href");
				String title = line.select(listConf.getUrldom()).first().text();
				LOG.info(title + lastupdate);
				
				ProtocolOutput otemp = fetch(curl, ajax); 
				if (otemp == null) continue;
				Document dtemp = otemp.getDocument();
				if (dtemp == null) {
					continue;
				}
				WebPage wp = new WebPage(title, curl, otemp.getFetchTime(), dtemp);
				
				try {
//					Parser clonedParser = parser.clone();
//					System.out.println(clonedParser);
					ParseCallable pc = new ParseCallable(parser, wp);
					tasks.add(pc);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				if (continuePage.get() == false) {
					break;
				}
			}
			
			try {
				LOG.info("task size: " + tasks.size());
	            List<Future<ParseStatus>> result = pool.invokeAll(tasks);
            } catch (InterruptedException e) {
	            e.printStackTrace();
            }

			if (/* page.getSeed().isLose() || */!continuePage.get()) { // 是丢失帖或符合停止翻页条件
				break;
			} else { // 翻页
				Document oldDoc = document;
				ProtocolOutput ptemp = fetchNextPage(pageNum.get(), document, ajax);
				if (!ptemp.getStatus().isSuccess())
					break;
				document = ptemp.getDocument();
				if (document == null || document.html().equals(oldDoc.html())) {
					LOG.info("document == null or current page is same to next page，break");
					break;
				}
				pageNum.incrementAndGet();
			}
		}
		// pool.shutdown();
		LOG.info("【" + listConf.getComment() + "】抓取结束");
		return status;
	}

	/**
	 * 解析丢失的详细页
	 */
	public ParseStatus parseDetailPage(WebPage page, Parser parser) {
		ParseStatus status = new ParseStatus();
		try {
			status = parser.parse(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

//	private static class ParseCallable implements Callable<ParseStatus> {
//		private Parser parser;
//		private WebPage page;
//
//		public ParseCallable(Parser parser, WebPage page) {
//			this.parser = parser;
//			this.page = page;
//		}
//
//		public ParseStatus call() throws Exception {
//			ParseStatus status = null;
//			try {
//				status = parser.parse(page);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return status;
//		}
//	}

	public ThreadPoolExecutor newFixedThreadPool(int nThreads) {
		final ThreadPoolExecutor result = new ThreadPoolExecutor(nThreads, nThreads + 10, 20, TimeUnit.SECONDS,
		        new ArrayBlockingQueue<Runnable>(20), new ThreadPoolExecutor.CallerRunsPolicy());
		result.setThreadFactory(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						LOG.error("Thread exception: " + t.getName(), e);
						result.shutdown();
					}
				});
				return t;
			}
		});
		return result;
	}

}
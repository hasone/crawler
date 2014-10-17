package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HtmlUnitTest {
	
	private static Logger LOG = LoggerFactory.getLogger(HtmlUnitTest.class);
	
	@BeforeClass
	public static void setup() {
		Configuration conf = CrawlerConfiguration.create();
		 htmlUnit = new HtmlUnit(conf);
		 httpFetcher = new HttpFetcher(conf);
	}
	
	static HttpBase htmlUnit;
	
	static HttpFetcher httpFetcher;
	
	@Test
	public void testLoadCurrentPage() throws ProtocolException, IOException {
//		String url = "http://roll.news.sina.com.cn/s/channel.php";
		String url = "http://roll.sohu.com/";
		WebPage page = new WebPage(url, true);
		ProtocolOutput protocolOutput = htmlUnit.getProtocolOutput(page);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		LOG.info(currentDoc.html());
	}
	
	@Test
	public void testLoadLastPage() throws IOException, PageBarNotFoundException {
		ProtocolOutput protocolOutput = httpFetcher.fetch(new WebPage("http://roll.news.sina.com.cn/s/channel.php", true));
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		WebPage page = new WebPage();
		page.setDocument(currentDoc);
		page.setAjax(false);
		protocolOutput = htmlUnit.getProtocolOutputOfLastPage(page);
		Assert.notNull(protocolOutput);
		currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		System.out.println(currentDoc.html());
	}
	
	@Test
	public void testLoadNextPage() throws IOException, PageBarNotFoundException {
		String url = "http://roll.sohu.com/index.shtml";
		url = "http://roll.news.sina.com.cn/s/channel.php";
//		url = "http://www.sogou.com/web?query=%E5%90%B8%E6%AF%92&repp=1&page=1&&ie=utf8";
//		url = "http://news.163.com/latest/";

		WebPage page = new WebPage(url, true);
		ProtocolOutput protocolOutput = httpFetcher.fetch(page);
		Document currentDoc = protocolOutput.getDocument();
		LOG.debug("1:" + currentDoc.location());
//		LOG.debug(currentDoc.html());
		
		LOG.debug("=================================");
		page.setDocument(currentDoc);
		page.setBaseUrl(currentDoc.location());
		protocolOutput = htmlUnit.getProtocolOutputOfNextPage(1, page);
		currentDoc = protocolOutput.getDocument();
		LOG.debug("2:" + currentDoc.location());
//		LOG.debug(currentDoc.html());
		
		LOG.debug("=================================");
		page.setDocument(currentDoc);
		page.setBaseUrl(currentDoc.location());
		protocolOutput = htmlUnit.getProtocolOutputOfNextPage(2, page);
		currentDoc = protocolOutput.getDocument();
		LOG.debug("3:" + currentDoc.location());
		LOG.debug(currentDoc.html());
	}
	
	@Test
	public void testLoadPrevPage() throws IOException, PrevPageNotFoundException, PageBarNotFoundException {
		WebPage page = new WebPage("http://roll.news.sina.com.cn/s/channel.php#col=89&spec=&type=&ch=&k=&offset_page=0&offset_num=0&num=60&asc=&page=2", true);
		
		
		ProtocolOutput protocolOutput = httpFetcher.fetch(page);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		
		page.setDocument(currentDoc);
		page.setBaseUrl(currentDoc.location());
		page.setAjax(true);
		
		protocolOutput = htmlUnit.getProtocolOutputOfPrevPage(2, page);
		Assert.notNull(protocolOutput);
		currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
//		System.out.println(currentDoc.html());
	}

}

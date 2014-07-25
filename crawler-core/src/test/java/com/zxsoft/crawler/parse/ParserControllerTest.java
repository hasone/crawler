package com.zxsoft.crawler.parse;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class ParserControllerTest {

	@Autowired
	private HttpFetcher httpFetcher;
	
	@Autowired
	public ApplicationContext ctx;
	
	private Configuration conf;
	
	@Before
	public void setup() {
		conf = CrawlerConfiguration.create();
		ParseTool.init(ctx);
	}
	
	@Test
	public void testParseTieBa() throws ParserNotFoundException {
		String urlStr = "http://tieba.baidu.com/f?kw=%B0%F6%B2%BA";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		ParserController parserController = new ParserController(conf);
		parserController.parse(page);
	}

	@Test
	public void testParseTianYa() throws ParserNotFoundException {
		String urlStr = "http://bbs.tianya.cn/list-free-1.shtml";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		ParserController parserController = new ParserController(conf);
		parserController.parse(page);
	}
	
	@Test
	public void testParseZhongAn() throws ParserNotFoundException {
		String urlStr = "http://bbs.anhuinews.com/forum-319-1.html";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		ParserController parserController = new ParserController(conf);
		parserController.parse(page);
	}
	
	
}

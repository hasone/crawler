package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.tika.metadata.Metadata;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;




//import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.EncodingDetector;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HttpClient extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

	private static org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient(connectionManager);
	private int maxThreadsTotal = 30;

	public HttpClient() {
//	        setup();
		configureClient();
	}

	private void configureClient() {
		ProtocolSocketFactory factory = new SSLProtocolSocketFactory();
		Protocol https = new Protocol("https", factory, 443);
		Protocol.registerProtocol("https", https);
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setConnectionTimeout(timeout);
		params.setSoTimeout(timeout);
		params.setSendBufferSize(BUFFER_SIZE);
		params.setReceiveBufferSize(BUFFER_SIZE);
		params.setMaxTotalConnections(maxThreadsTotal);
		client.getParams().setConnectionManagerTimeout(timeout);
		HostConfiguration hostConf = client.getHostConfiguration();
		ArrayList<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent", userAgent));
		headers.add(new Header("Accept-Language", acceptLanguage));
		headers.add(new Header("Accept-Charset", acceptCharset));
		headers.add(new Header("Accept",accept));
		headers.add(new Header("Connection", "keep-alive"));
		headers.add(new Header("Accept-Encoding", "x-gzip, gzip, deflate"));
		hostConf.getParams().setParameter("http.default-headers", headers);
		if (useProxy) {
			LOG.info("HttpClient use proxy " + proxyHost + ":" + proxyPort);
			hostConf.setProxy(proxyHost, proxyPort);
			if (!StringUtils.isEmpty(proxyUsername) && !StringUtils.isEmpty(proxyPassword)) {
        			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword);
        			client.getState().setProxyCredentials(AuthScope.ANY , credentials);
			}
		}
	}

	@Override
	public Response getResponse(WebPage page) throws ProtocolException, IOException {

		int code = -1;
		Metadata metadata = new Metadata();
		byte[] content = null/* new byte[1024] */;

		URL url = null;
		try {
			url = new URL(page.getBaseUrl());
		} catch (Exception e) {
//			throw new IOException("url: " + page.getBaseUrl());
			LOG.error(e.getMessage(), e);
			return null;
		}
		
		GetMethod get = new GetMethod(url.toString());
		HttpMethodParams params = get.getParams();
		params.makeLenient();
		params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		params.setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);

		try {
			code = client.executeMethod(get);
			Header[] heads = get.getResponseHeaders();
			for (int i = 0; i < heads.length; i++)
				metadata.set(heads[i].getName(), heads[i].getValue());

			String contentType = metadata.get(Response.CONTENT_TYPE);
			charset = EncodingDetector.parseCharacterEncoding(contentType, get.getResponseBody());
			if (StringUtils.isEmpty(charset)) {
			        charset = EncodingDetector.detect(get.getResponseBodyAsString(), metadata);
			}
			
			long contentLength = Long.MAX_VALUE;
			byte[] buffer = new byte[1024 * 1024];
			int bufferFilled = 0;
			int totalRead = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = get.getResponseBodyAsStream();
			try {
				while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1 && totalRead + bufferFilled <= contentLength) {
					totalRead += bufferFilled;
					out.write(buffer, 0, bufferFilled);
				}
				content = out.toByteArray();
			} catch (Exception e) {
				if (code == 200) {
				        LOG.error(e.getMessage(), e);
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = metadata.get(Response.CONTENT_ENCODING);
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = processGzipEncoded(content, url);
				} else if ("deflate".equals(contentEncoding)) {
					content = processDeflateEncoded(content, url);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + ": " + url.toString());
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}

		return new Response(url, code, metadata, content, charset);
	}

	@Override
	public Response postForResponse(URL url, NameValuePair[] data) throws IOException {
		PostMethod post = new PostMethod(url.toString());
		post.setRequestBody(data);
		HttpMethodParams params = post.getParams();
		params.makeLenient();
		params.setContentCharset("UTF-8");
		params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		params.setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);

		try {
			code = client.executeMethod(post);
			// post.getResponseBodyAsString();
			Header[] cookies = post.getResponseHeaders("Set-Cookie");
			StringBuilder sb = new StringBuilder();
			for (Header cookie : cookies) {
				sb.append(cookie.getValue());
			}
			// com.zxsoft.crawler.protocols.http.CookieStore.put(NetUtils.getHost(url),
			// sb.toString());

			headers.set("Cookie", sb.toString());

			Header[] heads = post.getRequestHeaders();
			for (int i = 0; i < heads.length; i++)
				headers.set(heads[i].getName(), heads[i].getValue());

			long contentLength = Long.MAX_VALUE;
			InputStream in = post.getResponseBodyAsStream();
			byte[] buffer = new byte[1024 * 1024];
			int bufferFilled = 0;
			int totalRead = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1 && totalRead + bufferFilled <= contentLength) {
					totalRead += bufferFilled;
					out.write(buffer, 0, bufferFilled);
				}
				content = out.toByteArray();
			} catch (Exception e) {
				if (code == 200) {
				         LOG.error(e.getMessage(), e);
				}
			} finally {
				if (in != null) {
					in.close();
				}
				post.abort();
			}
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers.get(Response.CONTENT_ENCODING);
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = processGzipEncoded(content, url);
				} else if ("deflate".equals(contentEncoding)) {
					content = processDeflateEncoded(content, url);
				}
			}
		} finally {
			post.releaseConnection();
		}
		return new Response(url, code, headers, content, charset);
	}

	@Override
	protected Response loadPrevPage(int pageNum, final WebPage page) throws ProtocolException, IOException, PrevPageNotFoundException,
	        PageBarNotFoundException {
		Document currentDoc = page.getDocument();
		Elements elements = null;
		elements = currentDoc.select("a:matchesOwn(上一页|上页|<上一页)");
		URL url = null;
		if (!CollectionUtils.isEmpty(elements)) {
			url = new URL(elements.first().absUrl("href"));
		} else if (pageNum > 1) {
			Element pagebar = getPageBar(currentDoc);
			if (pagebar != null) {
				Elements achors = pagebar.getElementsByTag("a");
				if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
					for (int i = 0; i < achors.size(); i++) {
						if (Utils.isNum(achors.get(i).text()) && Integer.valueOf(achors.get(i).text().trim()) == pageNum - 1) {
							url = new URL(achors.get(i).absUrl("href"));
						}
					}
				}
			}
		} else {
			url = PageHelper.calculatePrevPageUrl(currentDoc);
		}
		if (url != null) {
			WebPage np = page;
			np.setBaseUrl(url.toExternalForm());
			return getResponse(page);
		}

		throw new PrevPageNotFoundException("Preview Page Not Found");
	}

	@Override
	protected Response loadNextPage(int pageNum, final WebPage page) throws ProtocolException, IOException, PageBarNotFoundException {
		Document currentDoc = page.getDocument();
		Elements elements = null;
		elements = currentDoc.select("a:matchesOwn(下一页|下页|下一页>)");

		if (!CollectionUtils.isEmpty(elements)) {
			WebPage np = page;
			String next = elements.first().absUrl("href");
			if (StringUtils.isEmpty(next)) {
				throw new PageBarNotFoundException();
			}
			np.setBaseUrl(next);

			return getResponse(np);
		} else {
			Element pagebar = getPageBar(currentDoc);
			if (pagebar != null) {
				Elements achors = pagebar.getElementsByTag("a");
				if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
					for (int i = 0; i < achors.size(); i++) {
						if (Utils.isNum(achors.get(i).text()) && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
							WebPage np = page;
							np.setBaseUrl(achors.get(i).absUrl("href"));
							return getResponse(np);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Response loadLastPage(WebPage page) throws ProtocolException, IOException, PageBarNotFoundException {
		Document currentDoc = page.getDocument();
		Elements lastEles = currentDoc.select("a:matchesOwn(尾页|末页|最后一页)");
		if (!CollectionUtils.isEmpty(lastEles)) {
			WebPage np = page;
			np.setBaseUrl(lastEles.first().absUrl("href"));
			return getResponse(np);
		}

		// 1. get all links from page bar
		Element pagebar = getPageBar(currentDoc);
		if (pagebar == null)
			return null;
		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links)) {
			return null;
		}

		// 2. get max num or contains something in all links, that is last page
		int i = 1;
		Element el = null;
		for (Element ele : links) {
			String v = ele.text();
			if ("18255266882".equals(v)) {
				System.out.println(ele);
			}
			if (Utils.isNum(v) && Integer.valueOf(v) > i) { // get max num
				i = Integer.valueOf(v);
				el = ele;
			}
		}
		if (el == null || StringUtils.isEmpty(el.absUrl("href"))) {
			return null;
		}
		// LOG.info("Last Page url: " + url.toString());
		WebPage np = page;
		np.setBaseUrl(el.absUrl("href"));
		return getResponse(np);
	}
}
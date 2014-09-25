package com.zxsoft.crawler.web.verification;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.util.CrawlerConfiguration;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.web.model.ThreadInfo;

public class ListConfigVerification extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(ListConfigVerification.class);
	
	public ListConfigVerification() {
		Configuration conf = CrawlerConfiguration.create();
		setConf(conf);
	}

	public Map<String, Object> verify(ConfList listConf) {

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> errors = new HashMap<String, String>();

		if (StringUtils.isEmpty(listConf.getUrl())) {
			errors.put("urlerror", "必填");
			map.put("errors", errors);
			return map;
		}

		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		String pageStr = "";
		ProtocolOutput protocolOutput = fetch(listConf.getUrl(), listConf.getAjax());
		Document document = null;

		if (protocolOutput == null || !protocolOutput.getStatus().isSuccess()) {
			errors.put("urlerror", "连接失败");

		} else {
			document = protocolOutput.getDocument();
			try {
				Element pagebar = PageHelper.getPageBar(document);
				pageStr = pagebar.html();
			} catch (PageBarNotFoundException e) {
				e.printStackTrace();
			}

			if (StringUtils.isEmpty(listConf.getListdom())) {
				errors.put("listdomerror", "必填");
			} else {
				Elements elements = document.select(listConf.getListdom());
				if (CollectionUtils.isEmpty(elements)) {
					errors.put("listdomerror", "获取列表失败");
				} else {
					Element listElement = elements.first();
					if (StringUtils.isEmpty(listConf.getLinedom())) {
						errors.put("linedomerror", "必填");
					} else {
						Elements lineElements = listElement.select(listConf.getLinedom());
						if (CollectionUtils.isEmpty(lineElements) || lineElements.size() < 3) {
							errors.put("linedomerror", "获取列表行失败");
							LOG.info(listElement.html());
						} else {
							int i = 0;
							int updateErrorCount = 0;
							int urlErrorCount = 0;
							for (Element lineEle : lineElements) {
								i++;
								if (CollectionUtils.isEmpty(lineEle.select(listConf.getUrldom()))) {
									if (i < 10) {
										continue;
									}
									urlErrorCount++;
								}

								Element urlEle = lineEle.select(listConf.getUrldom()).first();
								if (urlEle == null) {
									continue;
								}
								String url = urlEle.absUrl("href");
								String title = urlEle.text();
								Date update = null;
								if (!StringUtils.isEmpty(listConf.getUpdatedom())) {
									Elements dateElements = lineEle.select(listConf.getUpdatedom());
									if (CollectionUtils.isEmpty(dateElements)) {
										updateErrorCount++;
									} else {
										try {
											update = Utils.formatDate(dateElements.first()
											        .text());
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								}
								ThreadInfo info = new ThreadInfo(url, title, update);
								list.add(info);
							}
							if (updateErrorCount > 10) {
								errors.put("updatedomerror", "获取更新时间失败");
							}
							if (urlErrorCount > 10) {
								errors.put("urldomerror", "获取详细页URL失败");
							}
						}
					}
				}
			}
		}

		map.put("errors", errors);
		map.put("list", list);
		if (StringUtils.isEmpty(pageStr))
			pageStr = "没有找到";
		map.put("pagebar", pageStr);
		return map;
	}

}

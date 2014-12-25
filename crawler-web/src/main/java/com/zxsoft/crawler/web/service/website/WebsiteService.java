package com.zxsoft.crawler.web.service.website;

import java.util.List;

import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Auth;
import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Website;

@Service
public interface WebsiteService {

	Page<Website> getWebsite(final Website website, int pageNo, int pageSize);
	void addWebsite(Website website);
	
	void save(Website website);
	Website getWebsite(String id);
	void deleteWebsite(String id);
	List<Auth> getAuths(String id);
	void saveAuth(Auth auth);
	Auth getAuth(String id);
	void deleteAuth(String id);
}

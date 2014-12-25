package com.zxsoft.crawler.web.service.website;

import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Section;

@Service
public interface SectionService {

	Section getSection(String sectionId);
	/**
	 * 查找版块
	 */
	Page<Section> getSections(Section section, int pageNo, int pageSize);
	
	void saveOrUpdate(Section section);
	
	void delete(String id);
	Section getSectionByUrl(String url);
        void delete(Section section);

}

package com.zxsoft.crawler.web.controller.website;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.service.WebsiteService;
import com.zxsoft.crawler.web.verification.ListConfigVerification;
import com.zxsoft.framework.utils.Page;

@Controller
@RequestMapping("/websiteInfo")
public class WebsiteInfoController {

	@Autowired
	private WebsiteService websiteService;

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, @ModelAttribute("listConf") ListConf listConf,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest,
	        @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo) {
		return "/website/list";
	}

	/**
	 * display list configuration infomation
	 */
	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.POST)
	public List<ListConf> list(@ModelAttribute("ajaxRequest") boolean ajaxRequest,
	        @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
	        @RequestParam(value = "rows", defaultValue = "15", required = false) Integer pageSize,
	        ListConf param) {
		Page<ListConf> page = websiteService.getListConfs(pageNo, pageSize, param);
		List<ListConf> list = page.getRes();
		return list;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute("listConf")
	public ListConf createFormBean() {
		return new ListConf();
	}

	@RequestMapping(value = "addListConf", method = RequestMethod.GET)
	public String addListConf(/*@Valid ListConf listConf, BindingResult result, Model model,*/
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		return "/website/listConf";
	}

	@Autowired
	private ListConfigVerification listConfigVerification;
	
	/**
	 * 验证列表页配置是否正确
	 */
	@ResponseBody
	@RequestMapping(value = "testListConf", method = RequestMethod.POST)
	public String testListConf(@Valid ListConf listConf, BindingResult result, Model model,
			@ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		model.addAttribute("listConf", listConf);
		if (result.hasErrors()) {
			return null;
		}
		Map<String, Object> listRes = listConfigVerification.verify(listConf);
		model.addAttribute("listRes", listRes);
		return "website/listConf";
	}
	
	/**
	 * 保存列表页配置
	 */
	@ResponseBody
	@RequestMapping(value = "addListConf", method = RequestMethod.POST)
	public Map<String, Object> saveListConf(@Valid ListConf listConf, BindingResult result, Model model,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		model.addAttribute("listConf", listConf);
		Map<String, Object> listRes = listConfigVerification.verify(listConf);
		model.addAttribute("listRes", listRes);
		return listRes;
	}

}

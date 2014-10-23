package com.zxsoft.crawler.web.controller.crawler;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thinkingcloud.framework.util.Assert;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.slave.SlavePath;
import com.zxsoft.crawler.web.service.crawler.JobService;
import com.zxsoft.crawler.web.service.crawler.SlaveService;
import com.zxsoft.crawler.web.service.crawler.impl.JobServiceImpl;
import com.zxsoft.crawler.web.service.crawler.impl.SlaveServiceImpl;
import com.zxsoft.crawler.web.service.website.DictService;

@Controller
@RequestMapping(MasterPath.SLAVE_RESOURCE_PATH)
public class SlaveController {

	private static Logger LOG = LoggerFactory.getLogger(SlaveController.class);

	@Autowired
	private DictService dictService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) {

		SlaveService slaveService = new SlaveServiceImpl();

		List<ConfList> engines = dictService.getSearchEngines();
		model.addAttribute("engines", engines);

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("slaves", slaveService.slaves());
			map.put("msg", "正常");
			map.put("code", "2000");

		} catch (ConnectException e) {
			LOG.warn(e.getMessage(), e);
			map.put("msg", "无法连接到主控，可能没有启动.");
			map.put("code", "5000");
		} catch (Exception e) {
			LOG.warn(e.getMessage(), e);
			map.put("code", "5000");
			map.put("msg", "无法连接到主控，可能没有启动.");
		}

		model.addAttribute("map", map);

		return "/crawler/list";
	}

	/**
	 * 加载更多
	 */
	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public Map<String, Object> slaves() {

		SlaveService slaveService = new SlaveServiceImpl();

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("slaves", slaveService.slaves());
			map.put("msg", "success");
			map.put("code", "2000");

		} catch (ConnectException e) {
			LOG.warn(e.getMessage(), e);
			map.put("msg", "无法连接到主控，可能没有启动.");
			map.put("code", "5000");
		} catch (Exception e) {
			LOG.warn(e.getMessage(), e);
			map.put("code", "5000");
			map.put("msg", "无法连接到主控，可能没有启动.");
		}

		return map;
	}

	private JobService jobService = new JobServiceImpl();

	@ResponseBody
	@RequestMapping(value = "addSearchJob", method = RequestMethod.POST)
	public Map<String, Object> addSearchJob(
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "engineId", required = false) List<String> engineIds) {
		Assert.hasLength(keyword);
		Assert.notEmpty(engineIds);
		for (String engineId : engineIds) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put(Params.KEYWORD, keyword);
			args.put(Params.ENGINE_URL, engineId);
			jobService.addSearchJob(args);
		}
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "addInspectJob", method = RequestMethod.POST)
	public Map<String, Object> addInspectJob(
	        @RequestParam(value = "url", required = false) String url) {

		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, url);

		jobService.addInsecptJob(args);

		return args;
	}

	/**
	 * 查看某个爬虫正在运行或完成的任务
	 */
	@RequestMapping(value = "moreinfo/{state}/{ip}/{port}", method = RequestMethod.GET)
	public String moreinfoOfHistory(Model model, @PathVariable(value = "state") final String state,
	        @PathVariable(value = "ip") final String ip,
	        @PathVariable(value = "port") final String port) {

		if (!"running".equals(state) && !"history".equals(state)) {
			return null;
		}

		model.addAttribute("state", state);
		model.addAttribute("ip", ip);
		model.addAttribute("port", port);

		String url = "http://" + ip + ":" + port + "/" + SlavePath.PATH + "/"
		        + SlavePath.JOB_RESOURCE_PATH + "?state=" + state;
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		List<JobStatus> res = new ArrayList<JobStatus>();
		try {
			String text = webResource.get(String.class);
			res = new Gson().fromJson(text, List.class);
		} catch (ClientHandlerException e) {
			e.printStackTrace();
		}

		model.addAttribute("list", res);
		return "crawler/detail";
	}

}

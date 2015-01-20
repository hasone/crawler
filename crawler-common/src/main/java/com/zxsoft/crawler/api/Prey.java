package com.zxsoft.crawler.entity;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.web.controller.crawler.JobStatus.JobType;

/**
 * 爬虫将根据这个对象信息进行循环抓取, 任务默认类型是<b>网络巡检</b>
 * @see Prey.urlType
 */
public class Prey implements Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = -5812527440561239425L;

	/**
	 * 网站地址
	 */
	private String site;
	
	/**
	 * 版块地址
	 */
	private String url;
	private String comment;
	/**
	 * 任务类型, 默认是网络巡检
	 */
	private String jobType = JobType.NETWORK_INSPECT.toString();
	
	/**
	 * 每隔fetchinteval(分钟)进行循环抓取
	 */
	private int fetchinterval;
	
	/**
	 * 开始时间
	 */
	private long start;
	
	/**
	 * 上次抓取时间，默认为0, 单位毫秒(ms)
	 */
	private long prevFetchTime;
	/**
	 * 任务状态，1表示执行，0表示暂停
	 */
	private int state = 1;

	public Prey(String site, String url, String comment, String jobType, int fetchinterval) {
	    super();
	    this.site = site;
	    this.url = url;
	    this.comment = comment;
	    this.jobType = jobType;
	    this.fetchinterval = fetchinterval;
//	    this.prevFetchTime = System.currentTimeMillis();
	    this.state = 1;
    }
	/**
	 * Only Constructor
	 * @param site
	 * @param url
	 * @param fetchinterval
	 * @param prevFetchTime
	 */
	public Prey(String site, String url, String comment, String jobType, int fetchinterval, long prevFetchTime, int state) {
	    super();
	    this.site = site;
	    this.url = url;
	    this.comment = comment;
	    this.jobType = jobType;
	    this.fetchinterval = fetchinterval;
	    this.prevFetchTime = prevFetchTime;
	    this.state = state;
    }
	public Prey(String site, String url, String comment, String jobType, int fetchinterval, long start, long prevFetchTime, int state) {
		super();
		this.site = site;
		this.url = url;
		this.comment = comment;
		this.jobType = jobType;
		this.start = start;
		this.fetchinterval = fetchinterval;
		this.prevFetchTime = prevFetchTime;
		this.state = state;
	}
	
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public int getFetchinterval() {
		return fetchinterval;
	}

	public void setFetchinterval(int fetchinterval) {
		this.fetchinterval = fetchinterval;
	}

	public long getPrevFetchTime() {
		return prevFetchTime;
	}

	public void setPrevFetchTime(long prevFetchTime) {
		this.prevFetchTime = prevFetchTime;
	}
	
	/**
	 * 返回Json
	 */
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(this); 
		json = json.replaceAll("\u003d", "=");
		return json;
	}
}
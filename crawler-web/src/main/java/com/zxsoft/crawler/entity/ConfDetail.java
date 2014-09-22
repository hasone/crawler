package com.zxsoft.crawler.entity;

// Generated 2014-9-19 17:19:57 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ConfDetail generated by hbm2java
 */
@Entity
@Table(name = "conf_detail", catalog = "crawler")
public class ConfDetail implements java.io.Serializable {

	private ConfDetailId id;
	private String replyNum;
	private String reviewNum;
	private String forwardNum;
	private String sources;
	private Boolean fetchOrder;
	private String master;
	private String author;
	private String date;
	private String content;
	private String reply;
	private String replyAuthor;
	private String replyDate;
	private String replyContent;
	private String subReply;
	private String subReplyAuthor;
	private String subReplyDate;
	private String subReplyContent;

	public ConfDetail() {
	}

	public ConfDetail(ConfDetailId id) {
		this.id = id;
	}

	public ConfDetail(ConfDetailId id, String replyNum, String reviewNum, String forwardNum,
	        String sources, Boolean fetchOrder, String master, String author, String date,
	        String content, String reply, String replyAuthor, String replyDate,
	        String replyContent, String subReply, String subReplyAuthor, String subReplyDate,
	        String subReplyContent) {
		this.id = id;
		this.replyNum = replyNum;
		this.reviewNum = reviewNum;
		this.forwardNum = forwardNum;
		this.sources = sources;
		this.fetchOrder = fetchOrder;
		this.master = master;
		this.author = author;
		this.date = date;
		this.content = content;
		this.reply = reply;
		this.replyAuthor = replyAuthor;
		this.replyDate = replyDate;
		this.replyContent = replyContent;
		this.subReply = subReply;
		this.subReplyAuthor = subReplyAuthor;
		this.subReplyDate = subReplyDate;
		this.subReplyContent = subReplyContent;
	}

	@EmbeddedId
	@AttributeOverrides({
	        @AttributeOverride(name = "listurl", column = @Column(name = "listurl", nullable = false, length = 150)),
	        @AttributeOverride(name = "host", column = @Column(name = "host", nullable = false, length = 150)) })
	public ConfDetailId getId() {
		return this.id;
	}

	public void setId(ConfDetailId id) {
		this.id = id;
	}

	@Column(name = "replyNum", length = 200)
	public String getReplyNum() {
		return this.replyNum;
	}

	public void setReplyNum(String replyNum) {
		this.replyNum = replyNum;
	}

	@Column(name = "reviewNum", length = 200)
	public String getReviewNum() {
		return this.reviewNum;
	}

	public void setReviewNum(String reviewNum) {
		this.reviewNum = reviewNum;
	}

	@Column(name = "forwardNum", length = 200)
	public String getForwardNum() {
		return this.forwardNum;
	}

	public void setForwardNum(String forwardNum) {
		this.forwardNum = forwardNum;
	}

	@Column(name = "sources", length = 100)
	public String getSources() {
		return this.sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	@Column(name = "fetchOrder")
	public Boolean getFetchOrder() {
		return this.fetchOrder;
	}

	public void setFetchOrder(Boolean fetchOrder) {
		this.fetchOrder = fetchOrder;
	}

	@Column(name = "master", length = 100)
	public String getMaster() {
		return this.master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Column(name = "author", length = 100)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Column(name = "date", length = 100)
	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Column(name = "content", length = 100)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "reply", length = 100)
	public String getReply() {
		return this.reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	@Column(name = "replyAuthor", length = 100)
	public String getReplyAuthor() {
		return this.replyAuthor;
	}

	public void setReplyAuthor(String replyAuthor) {
		this.replyAuthor = replyAuthor;
	}

	@Column(name = "replyDate", length = 100)
	public String getReplyDate() {
		return this.replyDate;
	}

	public void setReplyDate(String replyDate) {
		this.replyDate = replyDate;
	}

	@Column(name = "replyContent", length = 100)
	public String getReplyContent() {
		return this.replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	@Column(name = "subReply", length = 100)
	public String getSubReply() {
		return this.subReply;
	}

	public void setSubReply(String subReply) {
		this.subReply = subReply;
	}

	@Column(name = "subReplyAuthor", length = 100)
	public String getSubReplyAuthor() {
		return this.subReplyAuthor;
	}

	public void setSubReplyAuthor(String subReplyAuthor) {
		this.subReplyAuthor = subReplyAuthor;
	}

	@Column(name = "subReplyDate", length = 100)
	public String getSubReplyDate() {
		return this.subReplyDate;
	}

	public void setSubReplyDate(String subReplyDate) {
		this.subReplyDate = subReplyDate;
	}

	@Column(name = "subReplyContent", length = 100)
	public String getSubReplyContent() {
		return this.subReplyContent;
	}

	public void setSubReplyContent(String subReplyContent) {
		this.subReplyContent = subReplyContent;
	}

}

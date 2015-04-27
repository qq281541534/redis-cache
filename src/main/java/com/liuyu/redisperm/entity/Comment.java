package com.liuyu.redisperm.entity;

import java.io.Serializable;

import org.msgpack.annotation.MessagePackBeans;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * 评论
 * @author liuyu
 *
 * @since 2015年4月22日
 */
@MessagePackBeans
public class Comment implements Serializable{

	private static final long serialVersionUID = 6568310863644187637L;
	/** 评论ID */
	private String id;
	/** 评论内容 */
	private String content;
	/** 商品Id */
	private String productId;
	/** 用户ID */
	private String username;
	/** 赞数 */
	private int praise;
	
	public Comment(){}
	
	/**
	 * 构造方法
	 * @param id			评论ID
	 * @param content		评论内容
	 * @param username	用户名
	 * @param productId	商品ID
	 * @param praise		赞数
	 */
	public Comment(String id, String content, String username, String productId, int praise){
		this.id = id;
		this.content = content;
		this.productId = productId;
		this.username = username;
		this.praise = praise;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getPraise() {
		return praise;
	}
	public void setPraise(int praise) {
		this.praise = praise;
	}
	
}

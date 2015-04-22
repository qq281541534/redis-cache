package com.liuyu.redisperm;

import java.io.Serializable;


/**
 * 评论
 * @author liuyu
 *
 * @since 2015年4月22日
 */
public class Comment implements Serializable{

	private static final long serialVersionUID = 6568310863644187637L;
	private int id;
	private String content;
	private int productId;
	private int commentNum;
	
	public Comment(){}
	
	public Comment(int id, String content, int productId, int commentNum){
		this.id = id;
		this.content = content;
		this.productId = productId;
		this.commentNum = commentNum;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	
	
}

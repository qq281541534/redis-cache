package com.liuyu.redisperm;

import java.io.Serializable;
import java.util.List;

/**
 * 商品
 * @author liuyu
 *
 * @since 2015年4月22日
 */
public class Product implements Serializable{

	private static final long serialVersionUID = -5818433884770798578L;
	private int id;
	private String content;
	private List<Comment> comments;

	public Product(){}
	
	public Product(int id, String content){
		this.id = id;
		this.content = content;
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

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}

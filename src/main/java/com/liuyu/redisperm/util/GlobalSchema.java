package com.liuyu.redisperm.util;

public class GlobalSchema {

	/**
	 *  获取文章评论键
	 * @param articleId
	 * @param commentId
	 * @param username
	 * @return
	 */
	public static final String getArticleCommentUsernameKey(String articleId, String commentId, String username){
		return String.format("article:%s:comment:%s:username:%s", articleId, commentId, username);
	}
	
	/**
	 *  获取通过时间集的文章评论键
	 * @param articleId
	 * @return
	 */
	public static final String getArticleCommentByTimeKey(String articleId){
		return String.format("article:%s:comment:time", articleId);
	}
	
	/**
	 *  获取通过赞集的文章评论键
	 * @param articleId
	 * @return
	 */
	public static final String getArticleCommentByPraiseKey(String articleId){
		return String.format("article:%s:comment:praise", articleId);
	}
}
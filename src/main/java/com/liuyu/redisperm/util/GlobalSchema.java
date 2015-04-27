package com.liuyu.redisperm.util;

public class GlobalSchema {

	/**
	 * 获取文章浏览数键
	 * @param articleId
	 * @return
	 */
	public static final String getArticleBrowseNumKey(String articleId){
		return String.format("article:%s:browsenum", articleId);
	}
	
	/**
	 *  获取文章评论键
	 * @param articleId
	 * @param commentId
	 * @param username
	 * @return
	 */
	public static final String getArticleCommentKey(String articleId, String commentId){
		return String.format("article:%s:comment:%s", articleId, commentId);
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
	
	/**
	 * 获取用户的所有评论键
	 * @param username
	 * @return
	 */
	public static final String getCommentByUsernameKey(String username){
		return String.format("comment:username:%s", username);
	}
	
}
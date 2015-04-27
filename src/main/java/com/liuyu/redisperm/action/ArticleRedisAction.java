package com.liuyu.redisperm.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.liuyu.redisperm.entity.Comment;
import com.liuyu.redisperm.util.GlobalSchema;
import com.liuyu.redisperm.util.RedisPagingFactory;
import com.liuyu.redisperm.util.RedisPagingFactory.RedisPageHelper;

/**
 * 文章的评论的操作
 * @author liuyu
 *
 */
public class ArticleRedisAction {

	private static RedisTemplate redisTemplate;
	private static RedisPagingFactory redisPag = new RedisPagingFactory();
	
	/**
	 * 保存文章评论
	 * @param comment
	 */
	public static void saveArticleComment(final Comment comment){
		redisTemplate.execute(new SessionCallback<Comment>() {

			public <K, V> Comment execute(RedisOperations<K, V> operations)
					throws DataAccessException {
				operations.multi();
				//文章评论键
				String articleCommentKey = GlobalSchema.getArticleCommentUsernameKey(comment.getProductId(), comment.getId(), comment.getUsername());
				//文章评论时间键集
				String articleCommentTimeKey = GlobalSchema.getArticleCommentByTimeKey(comment.getProductId());
				//文章评论点赞键集
				String articleCommentPraiseKey = GlobalSchema.getArticleCommentByPraiseKey(comment.getProductId());
				//文章的评论使用set
				@SuppressWarnings("unchecked")
				SetOperations<String, Comment> setOper = (SetOperations<String, Comment>) operations.opsForSet();
				//单条评论
				setOper.add(articleCommentKey, comment);
				//文章的评论的展示排序使用zset
				@SuppressWarnings("unchecked")
				ZSetOperations<String, String> zsetOper = (ZSetOperations<String, String>) operations.opsForZSet();
				//按时间排序存
				zsetOper.add(articleCommentTimeKey, comment.getId(), System.currentTimeMillis());
				//按赞数存
				zsetOper.add(articleCommentPraiseKey, comment.getId(), comment.getPraise());
				operations.exec();
				return null;
			}
		});
	}
	
	/**
	 * 按赞数排序分页获取文章的评论Id集
	 * @param articleId	文章Id
	 * @param pageNo	当前页
	 * @return
	 */
	public static RedisPageHelper<String> getPageForArticleCommentIdsByPraise(String articleId, long pageNo){
		return redisPag.getPageForZSet(redisTemplate, GlobalSchema.getArticleCommentByPraiseKey(articleId), String.class, pageNo, false);
	}
	
	/**
	 * 按时间排序分页获取文章的评论Id集
	 * @param articleId	文章Id
	 * @param pageNo	当前页
	 * @return
	 */
	public static RedisPageHelper<String> getPageForArticleCommentIdsByTime(String articleId, long pageNo){
		return redisPag.getPageForZSet(redisTemplate, GlobalSchema.getArticleCommentByTimeKey(articleId), String.class, pageNo, false);
	}
	
	/**
	 * 获取文章评论
	 * @param articleId	文章ID
	 * @param commentId	评论ID
	 * @return
	 * @throws IOException 
	 */
	public static Comment getArticleComment(String articleId, String commentId) throws IOException{
		SetOperations<String, Comment> setOper = redisTemplate.opsForSet();
		Set<Comment> comments = setOper.members(GlobalSchema.getArticleCommentUsernameKey(articleId, commentId, "17090020140"));
		Iterator<Comment> iter = comments.iterator();
		Comment comment = null;
		
		while(iter.hasNext()){
			comment = iter.next();
		}
		return comment;
		
	}
	
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"app-redis.xml");
		//添加评论
//		redisTemplate = context.getBean(RedisTemplate.class);
//		Comment comment = new Comment();
//		comment.setId("1");
//		comment.setContent("I have a dream");
//		comment.setProductId("1");
//		comment.setUsername("17090020140");
//		comment.setPraise(32);
//		saveArticleComment(comment);
//		Comment comment1 = new Comment();
//		comment1.setId("2");
//		comment1.setContent("this is a dream");
//		comment1.setProductId("1");
//		comment1.setUsername("17090020140");
//		comment1.setPraise(42);
//		saveArticleComment(comment1);
		
		//分页获取文章的评论
		String articleId = "1";
		redisTemplate = context.getBean(RedisTemplate.class);
//		RedisPageHelper<Integer> redisPageHelper = getPageForArticleCommentIdsByPraise("1", 1l);
		RedisPageHelper<String> redisPageHelper = getPageForArticleCommentIdsByTime(articleId, 1l);
		
		List<String> commentIds = redisPageHelper.getData();
		List<Comment> comments = new LinkedList<Comment>();	
		for(int i = 0; i < commentIds.size(); i ++){
			String commentId = commentIds.get(i);
			Comment comment = getArticleComment("1", commentId);
			comments.add(comment);
		}
		
		System.out.println(comments);
		
		
		
	}
}

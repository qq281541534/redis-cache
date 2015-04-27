package com.liuyu.redisperm.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

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

	private static RedisTemplate<String, Long> redisTemplate1;
	private static RedisTemplate redisTemplate;
	private static RedisPagingFactory redisPag = new RedisPagingFactory();
	private static RedisAtomicLong redisAtomiclong;
	
	/**
	 * 保存文章评论
	 * @param comment
	 */
	public static void saveArticleComment(final Comment comment){
		redisTemplate.execute(new SessionCallback<Comment>() {

			public <K, V> Comment execute(RedisOperations<K, V> operations)
					throws DataAccessException {
				operations.multi();
				
				//文章评论key
				String articleCommentKey = GlobalSchema.getArticleCommentKey(comment.getProductId(), comment.getId());
				//文章评论时间key集
				String articleCommentTimeKey = GlobalSchema.getArticleCommentByTimeKey(comment.getProductId());
				//文章评论点赞key集
				String articleCommentPraiseKey = GlobalSchema.getArticleCommentByPraiseKey(comment.getProductId());
				//用户评论key集
				String commentUsernameKey = GlobalSchema.getCommentByUsernameKey(comment.getUsername());

				
				//文章的评论使用set
				@SuppressWarnings("unchecked")
				SetOperations<String, Comment> setOper = (SetOperations<String, Comment>) operations.opsForSet();
				//单条评论
				setOper.add(articleCommentKey, comment);
				
				//文章的评论的展示排序使用zset
				@SuppressWarnings("unchecked")
				ZSetOperations<String, String> zsetOper = (ZSetOperations<String, String>) operations.opsForZSet();
				//按时间排序存储
				zsetOper.add(articleCommentTimeKey, comment.getId(), System.currentTimeMillis());
				//按赞数存储
				zsetOper.add(articleCommentPraiseKey, comment.getId(), comment.getPraise());
				
				//用户评论使用list
				@SuppressWarnings("unchecked")
				ListOperations<String, String> listOper = (ListOperations<String, String>) operations.opsForList();
				//存储用户所有的评论键，value为具体文章评论的key
				listOper.leftPush(commentUsernameKey, articleCommentKey);
				
				operations.exec();
				return null;
			}
		});
	}
	
	/**
	 * 初始化文章浏览数
	 * @param articleId
	 */
	public static void saveArticBrowseNum(final String articleId){
		redisTemplate1.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
		
		//文章浏览数Key
		String articleBrowseNumKey = GlobalSchema.getArticleBrowseNumKey(articleId);
		//文章的浏览数使用String
		ValueOperations valueOper = redisTemplate1.opsForValue();
		valueOper.set(articleBrowseNumKey, 0l);
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
	 * 通过用户名获取评论Key集
	 * @param username
	 * @return
	 */
	public static RedisPageHelper<String> getPageForCommentByUsername(String username){
 		return redisPag.getPageForList(redisTemplate, GlobalSchema.getCommentByUsernameKey(username), String.class, 1l);
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
		Set<Comment> comments = setOper.members(GlobalSchema.getArticleCommentKey(articleId, commentId));
		Iterator<Comment> iter = comments.iterator();
		Comment comment = null;
		
		while(iter.hasNext()){
			comment = iter.next();
		}
		return comment;
	}
	
	/**
	 * 通过key获取评论
	 * @param key 
	 * @return
	 * @throws IOException
	 */
	public static Comment getCommentByKey(String key) throws IOException{
		SetOperations<String, Comment> setOper = redisTemplate.opsForSet();
		Set<Comment> comments = setOper.members(key);
		Iterator<Comment> iter = comments.iterator();
		Comment comment = null;
		
		while(iter.hasNext()){
			comment = iter.next();
		}
		return comment;
	}
	
	/**
	 * 获取文章浏览数
	 * @param key
	 * @return
	 */
	public static Long getArticleBrowesNumByKey(String key){
		ValueOperations<String, Long> valueOper = redisTemplate1.opsForValue();
		Long browesNum = valueOper.increment(key, 1l);
		return browesNum;
	}
	
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"app-redis.xml");
		redisTemplate1 = context.getBean(RedisTemplate.class);
		redisTemplate = context.getBean(RedisTemplate.class);
		
		//添加评论
		for(int i=0;i<10;i++){
			Comment comment = new Comment();
			comment.setId(UUID.randomUUID().toString());
			comment.setContent("I have a dream_" + i);
			comment.setProductId("1");
			comment.setUsername("17090020140");
			comment.setPraise(32-i);
			saveArticleComment(comment);
		}
		
//		String articleId = "1";
//		//通过赞排序分页获取文章评论
//		RedisPageHelper<String> redisPageHelper = getPageForArticleCommentIdsByPraise("1", 1l);
//		//通过时间排序分页获取文章评论
////		RedisPageHelper<String> redisPageHelper = getPageForArticleCommentIdsByTime(articleId, 1l);
//		
//		List<String> commentIds = redisPageHelper.getData();
//		List<Comment> comments = new LinkedList<Comment>();	
//		for(int i = 0; i < commentIds.size(); i ++){
//			String commentId = commentIds.get(i);
//			Comment comment = getArticleComment("1", commentId);
//			comments.add(comment);
//		}
//		System.out.println(comments);
		
		//通过用户名分页获取评论
//		RedisPageHelper<String> redisPageHelperByUsername = getPageForCommentByUsername("17090020140");
//		List<String> keys = redisPageHelperByUsername.getData();
//		List<Comment> comments = new LinkedList<Comment>();
//		for(int i = 0; i < keys.size(); i ++){
//			String key = keys.get(i);
//			Comment comment = getCommentByKey(key);
//			comments.add(comment);
//		}
//		System.out.println(comments.get(0));
		
		//初始化文章浏览数
//		saveArticBrowseNum("1");
		//浏览数，每次加一
//		System.out.println(getArticleBrowesNumByKey("article:1:browsenum"));
	}
}

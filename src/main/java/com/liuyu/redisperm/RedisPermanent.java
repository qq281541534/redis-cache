package com.liuyu.redisperm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.msgpack.MessagePack;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.liuyu.redisperm.entity.Comment;
import com.liuyu.redisperm.entity.Product;

public class RedisPermanent {

	private static RedisTemplate redisTemplate;

	public void valueOperationSample() {
		ValueOperations<String, Product> valueOper = redisTemplate
				.opsForValue();
		Product product = new Product(1, "iphone6");
		valueOper.set("product:" + product.getId(), product);
	}

	public static void listOperationSample() throws IOException {
		Product product = new Product(2, "iphone5");
		ListOperations<String, Product> listOper = redisTemplate.opsForList();
//		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
		//messagePack序列化
//		MessagePack mspack = new MessagePack();
//		byte[] data = mspack.write("abc");
//		String data = JSONUtil.toJSON(product);
//		System.out.println(data);
		listOper.leftPush("product:list", product);
//		listOper.rightPush("product:list", data);// rpush,tail
	}

	public void boundValueOperationSample() {
		Product product = new Product(3, "iphone4");
		BoundValueOperations<String, Product> bvo = redisTemplate
				.boundValueOps("product:" + product.getId());
		bvo.set(product);
		bvo.expire(60, TimeUnit.MINUTES);
	}

	/**
	 * 非连接池环境下，事务操作；每次操作(例如，get，set)都有会从pool中获取connection； 因此在连接池环境下，使用事务需要注意。
	 */
	public void txUnusedPoolSample() {
		Product product = new Product(1, "iphone6");
		redisTemplate.watch("product:" + product.getId());
		redisTemplate.multi();
		ValueOperations<String, Product> tvo = redisTemplate.opsForValue();
		tvo.set("product:" + product.getId(), product);
		redisTemplate.exec();
	}

	/**
	 * 在连接池环境中，需要借助sessionCallback来绑定connection
	 */
	public static void txProductPoolSample() {
		SessionCallback<Product> sessionCallBack = new SessionCallback<Product>() {

			public <K, V> Product execute(RedisOperations<K, V> operations)
					throws DataAccessException {
				operations.multi();
				Product product = new Product(1, "iphone6");
				String productKey = "product:" + product.getId();
				String productScoresKey = "product:scores";
				SetOperations<String, Product> setOper = (SetOperations<String, Product>) operations
						.opsForSet();
				setOper.add(productKey, product);
				ZSetOperations<String, Integer> zsetOper = (ZSetOperations<String, Integer>) operations
						.opsForZSet();
				zsetOper.add(productScoresKey, product.getId(),
						System.currentTimeMillis());
				operations.exec();
				return product;
			}
		};
		redisTemplate.execute(sessionCallBack);
	}

	public static void txCommentPoolSample() {
		SessionCallback<Comment> sessionCallBack = new SessionCallback<Comment>() {

			public <K, V> Comment execute(RedisOperations<K, V> operations)
					throws DataAccessException {
				operations.multi();
				Comment comment = new Comment(1, "good", 1, 3);
				String productCommentKey = "product:" + 1 + ":comment:"
						+ comment.getId();
				String productCommentScoreKey = "product:" + 1
						+ ":comment:scores";
				SetOperations<String, Comment> setOper = (SetOperations<String, Comment>) operations
						.opsForSet();
				setOper.add(productCommentKey, comment);
				ZSetOperations<String, Integer> zsetOper = (ZSetOperations<String, Integer>) operations
						.opsForZSet();
				zsetOper.add(productCommentScoreKey, 1,
						System.currentTimeMillis());
				operations.exec();
				return comment;
			}
		};
		redisTemplate.execute(sessionCallBack);
	}

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"app-redis.xml");
		redisTemplate = context.getBean(RedisTemplate.class);
//		txProductPoolSample();
//		txCommentPoolSample();
		listOperationSample();
	}
}

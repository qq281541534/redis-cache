package com.liuyu.redisperm.util; 

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

/**
 * Redis分页
 */
public class RedisPagingFactory {
	
	private static final Logger log = LoggerFactory.getLogger(RedisPagingFactory.class);
	
	/**
	 * 内部分页助手,分页大小由工厂设置
	 */
	public final class RedisPageHelper<T>{
		
		/** 分页大小 */
		private int pageSize;
		/** 当前页码 */
		private long pageNo;
		/** 总页数 */
		private long totalPages;
		/** 最小分数 */
		private long minScore;
		/** 最大分数 */
		private long maxScore;
		/** 元素总数 */
		private long totalElements;
		/** 当前页数据 */
		private List<T> data;
		
		/**
		 * 构造分页助手
		 * @param pageSize 分页大小
		 */
		private RedisPageHelper(int pageSize){
			this.pageSize = pageSize;
		}

		/**
		 * 获取分页大小
		 * @return 分页大小
		 */
		public int getPageSize() {
			return pageSize;
		}

		/**
		 * 设置分页大小
		 * @param pageSize 分页大小
		 */
		protected void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		/**
		 * 获取当前页码
		 * @return 当前页码
		 */
		public long getPageNo() {
			return pageNo;
		}

		/**
		 * 设置当前页码
		 * @param pageNo 当前页码
		 */
		public void setPageNo(long pageNo) {
			this.pageNo = pageNo;
		}

		/**
		 * 获取总页数
		 * @return 总页数
		 */
		public long getTotalPages() {
			return totalPages;
		}

		/**
		 * 设置总页数
		 * @param totalPages 总页数
		 */
		public void setTotalPages(long totalPages) {
			this.totalPages = totalPages;
		}

		/**
		 * 获取元素总数
		 * @return 元素总数
		 */
		public long getTotalElements() {
			return totalElements;
		}

		/**
		 * 设置元素总数
		 * @param totalElements 元素总数
		 */
		public void setTotalElements(long totalElements) {
			this.totalElements = totalElements;
		}

		/**
		 * 获取当前页数据
		 * @return 当前页数据
		 */
		public List<T> getData() {
			return data;
		}

		/**
		 * 设置当前页数据
		 * @param data 当前页数据
		 */
		public void setData(List<T> data) {
			this.data = data;
		}
		
		/**
		 * 获取最小分数
		 * @return
		 */
		public long getMinScore() {
			return minScore;
		}
		
		/**
		 * 设置最小分数
		 * @param minScore
		 */
		public void setMinScore(long minScore) {
			this.minScore = minScore;
		}

		/**
		 * 获取最大分数
		 * @return
		 */
		public long getMaxScore() {
			return maxScore;
		}

		/**
		 * 获取最小分数
		 * @param maxScore
		 */
		public void setMaxScore(long maxScore) {
			this.maxScore = maxScore;
		}
	}
	
	/**	默认分页大小 */
	private static final int DEFAULT_PAGE_SIZE = 10;
	
	/** 分页大小 */
	private int pageSize;

	/**
	 * 安全地获取分页大小
	 * @return 分页大小
	 */
	public int getPageSize() {
		return pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
	}

	/**
	 * 安全地分页大小
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		if (pageSize<=0){
			log.warn("pageSize must be larger than 0. this property will be restored to default:{}", DEFAULT_PAGE_SIZE);
			this.pageSize = DEFAULT_PAGE_SIZE;
		}else{
			this.pageSize = pageSize;
		}
	}

	/**
	 * 生成空的分页助手
	 * @param clazz 分页数据类型
	 * @return 分页助手
	 */
	public <T> RedisPageHelper<T> getPageHelper(Class<T> clazz){
		return new RedisPageHelper<T>(this.getPageSize());
	}
	
	/**
	 * 准备分页助手
	 * @param totalElements 元素总数
	 * @param pageNo 需要的页码
	 * @param clazz 分页数据类型
	 * @return 当元素总数=0或者需要的页码超出总页数时返回空,如果返回不为空再设置一次分页数据即可
	 */
	private <T> RedisPageHelper<T> preparePageHelper(long totalElements, long pageNo, Class<T> clazz, long minScore, long maxScore){
		if (totalElements <= 0){
			return null;
		}
		//计算总分页数
		long totalPages = (long)Math.ceil((double)totalElements/this.getPageSize());
		if (pageNo<1 || pageNo>totalPages){
			return null;
		}
		RedisPageHelper<T> helper = this.getPageHelper(clazz);
		helper.setPageNo(pageNo);
		helper.setTotalPages(totalPages);
		helper.setTotalElements(totalElements);
		helper.setMinScore(minScore);
		helper.setMaxScore(maxScore);
		return helper;
	}
	
	/**
	 * 获取指定页码的分页数据开始下标
	 * @param pageNo 页码
	 * @return 分页数据开始下标
	 */
	private long getStartIdxForPage(long pageNo){
		return Math.max((Math.max(pageNo, 1) - 1) * this.getPageSize(), 0);
	}
	
	/**
	 * 获取指定页码的分页数据结束下标
	 * @param pageNo 页码
	 * @param totalElements 元素总数
	 * @return 分页数据结束下标
	 */
	private long getEndIdxForPage(long pageNo, long totalElements){
		return Math.min((Math.max(pageNo, 1) * this.getPageSize()) - 1, totalElements);
	}
	
	/**
	 * 分页获取List内的数据
	 * @param template Redis操作模板
	 * @param k 数据键
	 * @param clazz 分页数据类型
	 * @param pageNo 页码
	 * @return 分页助手
	 */
	public <K, V> RedisPageHelper<V> getPageForList(RedisTemplate<K, V> template, K k, Class<V> clazz, long pageNo){
		ListOperations<K, V> opsForList = template.opsForList();
		long totalElements = opsForList.size(k);
		RedisPageHelper<V> helper = this.preparePageHelper(totalElements, pageNo, clazz, 0l, 0l);
		if (helper == null){
			return null;
		}
		List<V> data = opsForList.range(k, this.getStartIdxForPage(pageNo), this.getEndIdxForPage(pageNo, totalElements));
		helper.setData(data);
		return helper;
	}
	
	/**
	 * 分页获取ZSet内的数据
	 * @param template Redis操作模板
	 * @param k 数据键
	 * @param clazz 分页数据类型
	 * @param pageNo 页码
	 * @param isRevers true:正序，false:反序
	 * @return 分页助手
	 */
	public <K, V> RedisPageHelper<V> getPageForZSet(RedisTemplate<K, V> template, K k, Class<V> clazz, long pageNo, boolean isRevers){
		ZSetOperations<K, V> opsForZSet = template.opsForZSet();
		long totalElements = opsForZSet.size(k);
		RedisPageHelper<V> helper = this.preparePageHelper(totalElements, pageNo, clazz, 0l, 0l);
		if (helper == null){
			return null;
		}
		Set<V> retVal = null;
		if(isRevers){
			retVal = opsForZSet.range(k, this.getStartIdxForPage(pageNo), this.getEndIdxForPage(pageNo, totalElements));
		} else {
			retVal = opsForZSet.reverseRange(k, this.getStartIdxForPage(pageNo), this.getEndIdxForPage(pageNo, totalElements));
		}
		List<V> data = new LinkedList<V>();
		data.addAll(retVal);
		helper.setData(data);
		return helper;
	}
	
	/**
	 * 分页获取按ZSet的Score排序好的内部数据
	 * @param template Redis操作模板
	 * @param k 数据键
	 * @param clazz 分页数据类型
	 * @param pageNo 页码
	 * @return 分页助手
	 */
	public <K, V> RedisPageHelper<V> getPageForZSetByScore(RedisTemplate<K, V> template, K k, Class<V> clazz, long pageNo, long minScore, long maxScore){
		ZSetOperations<K, V> opsForZSet = template.opsForZSet();
		long totalElements = opsForZSet.size(k);
		RedisPageHelper<V> helper = this.preparePageHelper(totalElements, pageNo, clazz, minScore, maxScore);
		if (helper == null){
			return null;
		}
		Set<V> retVal = opsForZSet.rangeByScore(k, helper.getMinScore(), helper.getMaxScore());
		List<V> data = new LinkedList<V>();
		data.addAll(retVal);
		helper.setData(data);
		return helper;
	}
	
}

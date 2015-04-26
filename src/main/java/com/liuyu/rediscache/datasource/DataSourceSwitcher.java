package com.liuyu.rediscache.datasource;
import org.springframework.util.Assert;

/**
 * 数据源选择器
 * 
 * @author liuyu
 */
public class DataSourceSwitcher {
	@SuppressWarnings("rawtypes")
	private static final ThreadLocal contextHolder = new ThreadLocal();

	@SuppressWarnings("unchecked")
	public static void setDataSource(String dataSource) {
		Assert.notNull(dataSource, "dataSource cannot be null");
		contextHolder.set(dataSource);
	}

	public static void setWrite(){
		clearDataSource();
    }
	
	public static void setRead() {
		setDataSource("read");
	}
	
	public static String getDataSource() {
		return (String) contextHolder.get();
	}

	public static void clearDataSource() {
		contextHolder.remove();
	}
}


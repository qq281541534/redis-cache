package com.liuyu.rediscache.aop;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

import com.liuyu.rediscache.datasource.DataSourceSwitcher;

/**
 *  数据操作切面 
 * @author liuyu
 */
public class DataSourceAdvice implements MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice {
	
	private Logger logger = LoggerFactory.getLogger(DataSourceAdvice.class);
	
	// service方法执行之前被调用
	public void before(Method method, Object[] args, Object target) throws Throwable {
		logger.debug("切入点: " + target.getClass().getName() + "类中" + method.getName() + "方法");
		if(method.getName().startsWith("add") 
			|| method.getName().startsWith("create")
			|| method.getName().startsWith("save")
			|| method.getName().startsWith("edit")
			|| method.getName().startsWith("update")
			|| method.getName().startsWith("delete")
			|| method.getName().startsWith("remove")){
			logger.debug("切换到: writerDataSource");
			DataSourceSwitcher.setWrite();
		}
		else  {
			logger.debug("切换到: readDataSource");
			DataSourceSwitcher.setRead();
		}
	}

	// service方法执行完之后被调用
	public void afterReturning(Object arg0, Method method, Object[] args, Object target) throws Throwable {
	}

	// 抛出Exception之后被调用
	public void afterThrowing(Method method, Object[] args, Object target, Exception ex) throws Throwable {
		DataSourceSwitcher.setRead();
		logger.debug("出现异常,切换到: readDataSource");
	}

}


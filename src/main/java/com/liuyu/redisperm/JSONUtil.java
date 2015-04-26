package com.liuyu.redisperm;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public enum JSONUtil {
	/**
	 * 单例实例
	 */
	instance;

	private ObjectMapper objectMapper;

	/**
	 * 懒惰单例模式得到ObjectMapper实例 此对象为Jackson的核心
	 */
	private JSONUtil() {
		this.objectMapper = new ObjectMapper();
		// 当找不到对应的序列化器时 忽略此字段
		this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,
				false);
		this.objectMapper.configure(
				JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 使Jackson JSON支持Unicode编码非ASCII字符
		SimpleModule module = new SimpleModule();
		module.addSerializer(String.class, new StringUnicodeSerializer());
		this.objectMapper.registerModule(module);
		// 设置null值不参与序列化(字段不被显示)
		this.objectMapper.setSerializationInclusion(Include.NON_NULL);
		// 支持结束
	}

	/**
	 * 创建JSON处理器的静态方法
	 * 
	 * @param content
	 *            JSON字符串
	 * @return
	 */
	private JsonParser getParser(String content) {
		try {
			return this.objectMapper.getFactory().createParser(content);
		} catch (IOException ioe) {
			return null;
		}
	}

	/**
	 * 创建JSON生成器的静态方法, 使用标准输出
	 * 
	 * @return
	 */
	private JsonGenerator getGenerator(StringWriter sw) {
		try {
			return this.objectMapper.getFactory().createGenerator(sw);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * JSON对象序列化
	 * @throws IOException 
	 * 
	 * @throws JSONSerException
	 */
	public static String toJSON(Object obj) throws IOException {
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGen = JSONUtil.instance.getGenerator(sw);
		if (jsonGen == null) {
			IOUtils.closeQuietly(sw);
			return null;
		}
		// 由于在getGenerator方法中指定了OutputStream为sw
		// 因此调用writeObject会将数据输出到sw
		jsonGen.writeObject(obj);
		// 由于采用流式输出 在输出完毕后务必清空缓冲区并关闭输出流
		jsonGen.flush();
		jsonGen.close();
		return sw.toString();
	}

	/**
	 * JSON对象反序列化
	 * 
	 * @throws IOException
	 * 
	 * @throws JSONDeserException
	 */
	public static <T> T fromJSON(String json, Class<T> clazz) throws IOException {
			JsonParser jp = JSONUtil.instance.getParser(json);
			return jp.readValueAs(clazz);
	}
}

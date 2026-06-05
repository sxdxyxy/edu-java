package com.joyfishs.dawa.utils;

import java.util.Collection;
import java.util.function.Supplier;

import org.springframework.util.CollectionUtils;

import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

/**
 * @program: dawa-java
 * @description: 业务通用断言
 * @author: Yjhon
 * @create: 2022-02-23 09:47
 */
public final class BizAssert {

	/**
	 *  断言对象不为空
	 * @param object    对象
	 * @param msg       不满足断言的异常信息
	 */
	public static void notNull(Object object, String msg) {
		state(object != null, msg);
	}

	public static void notNull(Object object, Supplier<String> supplier) {
		state(object != null, supplier);
	}

	public static void notNull(String msg,Object... objects) {
		for (int i = 0; i < objects.length; i++) state(objects[i] != null, msg);
	}

	/**
	 * 断言字符串不为空
	 * @param str   字符串
	 * @param msg   不满足断言的异常信息
	 */
	public static void notEmpty(String str, String msg) {
		state(!StringUtils.isEmpty(str), msg);
	}

	/**
	 * 断言集合不为空
	 * @param collection  集合
	 * @param msg         不满足断言的异常信息
	 */
	public static void notEmpty(Collection<?> collection, String msg) {
		state(!CollectionUtils.isEmpty(collection), msg);
	}

	/**
	 * 断言一个boolean表达式
	 * @param expression  boolean表达式
	 * @param message     不满足断言的异常信息
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new CustomException(message);
		}
	}

	/**
	 * 断言一个boolean表达式，用于需要大量拼接字符串以及一些其他操作等
	 * @param expression  boolean表达式
	 * @param supplier    msg生产者
	 */
	public static void state(boolean expression, Supplier<String> supplier) {
		if (!expression) {
			throw new CustomException(supplier.get());
		}
	}
}


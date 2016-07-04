/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.paine.core.util;

/**
 *
 * @author lEo
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.java.com.paine.core.model.Bean;

/**
 *
 * @author lEo
 */
public class SqlUtil {

	private static final Log log = LogFactory.getLog(SqlUtil.class);
	private static final String NULL = " NULL ";

	/**
	 * Creates a SQL IN clause from a Collection.
	 * 
	 * In your query you should write the parentheses, this method only builds
	 * the numbers separated by comma, for example: 2, 3, 4, 5
	 * 
	 * @param <T>
	 * @param collection
	 * @param attr
	 *            Name of the class property from where build the SQL IN
	 * @return
	 */
	public static <T> String createSQLIn(Collection<T> collection, String attr) {

		if (collection == null || collection.isEmpty()) {
			return NULL;
		}

		String firstCharacter = attr.substring(0, 1).toUpperCase();
		String lastCharacters = attr.substring(1, attr.length());
		String methodName = "get" + firstCharacter + lastCharacters;

		Method method;
		StringBuilder sb = new StringBuilder();
		List<Object> ids = new ArrayList<Object>();

		try {

			for (T object : collection) {

				method = object.getClass().getMethod(methodName);
				Object value = method.invoke(object);

				if (value instanceof String) {
					sb.append("'");
					sb.append(value);
					sb.append("'");
					ids.add(sb.toString());
				} else {
					ids.add(value);
				}
			}

		} catch (NoSuchMethodException e) {
			log.error("Verifique os parametros de entrada", e);
			throw new IllegalArgumentException(e);
		} catch (SecurityException e) {
			log.error("Verifique os parametros de entrada", e);
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			log.error("Verifique os parametros de entrada", e);
			throw new IllegalArgumentException(e);
		} catch (IllegalArgumentException e) {
			log.error("Verifique os parametros de entrada", e);
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			log.error("Verifique os parametros de entrada", e);
			throw new IllegalArgumentException(e);
		}

		return StringUtils.join(ids.toArray(), ",");
	}

	/**
	 * Creates a SQL IN clause from a Collection.
	 * 
	 * In your query you should write the parentheses, this method only builds
	 * the numbers separated by comma, for example: 2, 3, 4, 5
	 * 
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T extends Bean> String createSQLIn(Collection<T> collection) {

		if (collection == null || collection.isEmpty()) {
			return NULL;
		}

		List<Integer> ids = new ArrayList<Integer>();

		for (T bean : collection) {
			ids.add(bean.getId());
		}

		return StringUtils.join(ids.toArray(), ",");
	}

	/**
	 * Creates a SQL IN clause from a int list.
	 * 
	 * In your query you should write the parentheses, this method only builds
	 * the numbers separated by comma, for example: 2, 3, 4, 5
	 * 
	 * @param <T>
	 * @param ids
	 * @return
	 */
	public static String createSQLInInteger(Collection<Integer> ids) {

		if (ids == null || ids.isEmpty()) {
			return NULL;
		}

		return StringUtils.join(ids.toArray(), ",");
	}

	public static String conditionalAppend(boolean conditional, String query) {
		return conditional ? query : StringUtils.EMPTY;
	}

	/**
	 * Converts java Date to SQL Timestamp
	 * 
	 * @param date
	 * @return
	 */
	public static Timestamp dateToTimestamp(Date date) {
		
		if(date == null) {
			return null;
		}
		
		return new Timestamp(date.getTime());
	}
}

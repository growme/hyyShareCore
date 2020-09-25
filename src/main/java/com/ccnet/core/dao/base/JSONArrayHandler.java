package com.ccnet.core.dao.base;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cn.ffcs.memory.ResultSetHandler;

/**
 * 
 * 
 * @Description:
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京简智科技有限责任公司
 * @author 黄君毅 2013-8-5
 * @version 1.00.00
 * @history:
 * 
 */
public class JSONArrayHandler implements ResultSetHandler<JSONArray> {
   
	private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private boolean camel;
	private SimpleDateFormat sdf;

	public JSONArrayHandler() {
		this(true, DATEFORMAT);
	}

	public JSONArrayHandler(String dateFormat) {
		this(true, dateFormat);
	}
	
	public JSONArrayHandler(boolean camel, String dateFormat) {
		this.camel = camel;
		this.sdf = new SimpleDateFormat(dateFormat);
	}

	@Override
	public JSONArray handle(ResultSet rs) {
		try {
			JSONArray array = new JSONArray();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				if (columnCount == 1) {
					array.add(rs.getObject(1));
					continue;
				}
				JSONObject object = new JSONObject();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = rsmd.getColumnLabel(i);
					Object value = rs.getObject(columnName);
					if (value == null)
						value = "";

					if (value instanceof Date) {
						value = rs.getTimestamp(columnName);
						value = sdf.format((Date) value);
					}

					if (value instanceof Clob) {
						Clob clob = (Clob) value;
						value = clob
								.getSubString((long) 1, (int) clob.length());
					}

					if (camel) {
						columnName = underscore2Camel(columnName);
					}
					object.put(columnName, value);

				}
				array.add(object);
			}
			return array;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private String underscore2Camel(String underscore) {
		StringBuffer buf = new StringBuffer();
		underscore = underscore.toLowerCase();
		Matcher m = Pattern.compile("_([a-z])").matcher(underscore);
		while (m.find()) {
			m.appendReplacement(buf, m.group(1).toUpperCase());
		}
		return m.appendTail(buf).toString();
	}
}

package com.hhdb.csadmin.common.bean;

import java.util.HashMap;
import java.util.Map;

public class ServerBean {
	private int dbid;
	private String connKey;
	private String port;
	private String dbName;
	private String host;
	private String password;
	private String userName;
	private String schema;
	private int schemaId;
	private String name;

	public ServerBean() {
	}

	public ServerBean(String connKey, String port, String dbName, String host, String password, String userName) {
		this.connKey = connKey;
		this.port = port;
		this.dbName = dbName;
		this.host = host;
		this.password = password;
		this.userName = userName;
	}

	public ServerBean(String port, String dbName, String host, String password, String userName) {
		this.port = port;
		this.dbName = dbName;
		this.host = host;
		this.password = password;
		this.userName = userName;
	}

	public ServerBean(String port, String dbName, String host, String password, String userName, String schema, int schemaId) {
		this.port = port;
		this.dbName = dbName;
		this.host = host;
		this.password = password;
		this.userName = userName;
		this.schema = schema;
		this.schemaId = schemaId;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDBName() {
		return this.dbName;
	}

	public void setDBName(String dbName) {
		this.dbName = dbName;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(int schemaId) {
		this.schemaId = schemaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConnKey() {
		return connKey;
	}

	public void setConnKey(String connKey) {
		this.connKey = connKey;
	}

	public Map<String, Object> getConnMap() {
		Map<String, Object> connMap = new HashMap<String, Object>();
		connMap.put("password", password);
		connMap.put("username", dbName);
		connMap.put("dbPort", port);
		connMap.put("dbUrl", host);
		return connMap;
	}

	public Map<String, Object> getConnMapWithDBName() {
		Map<String, Object> connMap = getConnMap();
		connMap.put("dbName", dbName);
		return connMap;
	}

	public Map<String, Object> getConnMapWithSchema() {
		Map<String, Object> connMap = getConnMapWithDBName();
		connMap.put("schema", schema);
		connMap.put("schemaId", schemaId);
		return connMap;
	}

	public ServerBean clone() {
		ServerBean sb = new ServerBean();
		sb.setHost(host);
		sb.setPort(port);
		sb.setUserName(userName);
		sb.setDBName(dbName);
		sb.setPassword(password);
		return sb;
	}
}

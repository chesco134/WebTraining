package org.capiz.develops.votingservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private Connection connection;
	private String url;
	private String user;
	private String password;
	
	public String getUrl(){
		return url;
	}
	
	public DatabaseConnection(String host, String database, String user, String password){
		url = "jdbc:mysql://" + host + "/" + database;
		this.user = user;
		this.password = password;
		connection = null;
	}
	
	public Connection getConnection(){
		if(connection == null)
		try{
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			connection = DriverManager.getConnection(url,user,password);
		}catch(SQLException e){
			connection = null;
			url = e.toString();
		} catch (InstantiationException e) {
			url = e.toString();
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			url = e.toString();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			url = e.toString();
			e.printStackTrace();
		}
		return connection;
	}
	
	public void closeConnection(){
		try{
			connection.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}

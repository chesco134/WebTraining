package org.capiz.develops.votingservice;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class LogIn {

	private DatabaseConnection dbConnection;
	
	public String validateUser(String usrAdmin, String psswdAdmin, String usr, String psswd){
		String result = "";
		dbConnection = new DatabaseConnection("localhost","UPIITA",usrAdmin,psswdAdmin);
		Connection con = dbConnection.getConnection();
		if( con != null ){
			System.out.println("\n\n\tGet ready to go!!!!\n");
			try{
				CallableStatement cStatement = con.prepareCall("{Call validateUser(?,?,?)}");
				cStatement.setString(1, usr);
				cStatement.setString(2, psswd);
				cStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
				cStatement.executeUpdate();
				result = cStatement.getString(3);
				System.out.println(result);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}else{
			result = dbConnection.getUrl();
		}
		return result;
	}
}

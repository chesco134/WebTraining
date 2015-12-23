package org.capiz.develops.votingservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Security.PublicEncryption;

/**
 * Servlet implementation class EchoService
 */
public class EchoService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	private DatabaseConnection dbCon;
	
    public EchoService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter writer = response.getWriter();
		DataOutputStream sal = new DataOutputStream(new FileOutputStream(new File("here")));
		sal.write("Greetings!".getBytes());
		sal.close();
		dbCon = new DatabaseConnection("192.168.1.75",getString("lasMorras.cpz"),
				getString("xochimihulxipilliTul.cpz"), getString("tutiilliiTul.cpz"));
		try{
			ResultSet rs =dbCon.getConnection().createStatement().executeQuery("Select * from opcion"); 
			while(rs.next())
				writer.write(rs.getString(1) + ", " + rs.getString(2) + "\n");
		}catch(SQLException e){
			e.printStackTrace();
		}
		dbCon.closeConnection();
		writer.write(request.getParameter("tul"));
	}
	
	private String getString(String key) {
		String dbUser = null;
		try {
			PublicEncryption pk = new PublicEncryption();
			pk.loadKey(new File("encryptedKeyFile.cpz"),
					new File("private.der"));
			File cipheredUserName = new File(key);
			FileInputStream cUserNameFileInputStream = new FileInputStream(
					cipheredUserName);
			byte[] userNameCipheredBytes = null;
			byte[] initialBytes = new byte[1024];
			int bytesRead = 0;
			int lastLength = 0;
			while ((bytesRead = cUserNameFileInputStream.read(initialBytes, 0,
					1024)) != -1) {
				userNameCipheredBytes = new byte[bytesRead + lastLength];
				for (int i = 0; i < bytesRead; i++) {
					userNameCipheredBytes[i + lastLength] = initialBytes[i];
				}
				lastLength = userNameCipheredBytes.length;
			}
			cUserNameFileInputStream.close();
			ByteArrayInputStream userNameBAIN = new ByteArrayInputStream(
					userNameCipheredBytes);
			ByteArrayOutputStream userNameBAOS = new ByteArrayOutputStream();
			pk.decrypt(userNameBAIN, userNameBAOS);
			dbUser = new String(userNameBAOS.toByteArray());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbUser;
	}
}

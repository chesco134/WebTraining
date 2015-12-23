package org.capiz.develops.votingservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Security.PublicEncryption;

public class Servicios {

	private DatabaseConnection dbCon;

	public Servicios() {
		dbCon = new DatabaseConnection(getString("choro.cpz"),getString("lasMorras.cpz"),
				getString("xochimihulxipilliTul.cpz"), getString("tutiilliiTul.cpz"));
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
			e.printStackTrace();
		}
		return dbUser;
	}
	
//	public int updateEntry(String pregunta, String opcion) {
//		int result = 0;
//		try {
//			Connection con = dbCon.getConnection();
//			CallableStatement cstmnt = con
//					.prepareCall("{Call updateEntry(?,?)}");
//			cstmnt.setString(1, pregunta);
//			cstmnt.setString(2, opcion);
//			cstmnt.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			result = -1;
//		} catch (NullPointerException e) {
//			result = -1;
//		}
//		return result;
//	}

	public int registraBoleta(String boleta, String encuesta) {
		int result = 0;
		try {
			Connection con = dbCon.getConnection();
			CallableStatement cstmnt = con
					.prepareCall("{Call registraBoleta(?,?)}");
			cstmnt.setString(1, boleta);
			cstmnt.setString(2, encuesta);
			cstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;
		} catch (NullPointerException e) {
			result = -1;
		}
		return result;
	}
	
	public boolean revisaBoleta(String boleta){
		return true;
	}
	
//	public String guardarVoto(String voto, int idVotacion, String boleta){
//		String success = null;
//		Connection con = dbCon.getConnection();
//		if (con != null) {
//			try {
//				PreparedStatement st = con.prepareCall("Call guardaVoto(?,?,?);");
//				st.setString(1,voto);
//				st.setInt(2,idVotacion);
//				st.setString(3,boleta);
//				st.execute();
//				success = "Hecho.";
//			} catch (SQLException e) {
//				String error = e.toString();
//				if( error.contains("a foreign key constrint fails")){
//					success = "Intento de registrar una opción no válida.";
//				}else{
//					success = error;
//				}
//			}
//			dbCon.closeConnection();
//		} else {
//			success = "No se pudo la conexión";
//		}
//		return success;
//	}
	
	public boolean registraEscuela(){
		return true;
	}
	
	public boolean registraParticipante(){
		return true;
	}
	
	public boolean registraVotacion(){
		return true;
	}
	
	public boolean registraPregunta(){
		return true;
	}
	
	public boolean registraOpcion(){
		return true;
	}
	
	public boolean enlazaParticipanteVotacion(){
		return true;
	}
	
	public boolean enlazaPreguntaVotacion(){
		return true;
	}
	
	public boolean enlazaOpcionPregunta(){
		return true;
	}
	
	public boolean creaVotando(){
		return true;
	}
	
	public boolean guardaVoto(){
		return true;
	}
}

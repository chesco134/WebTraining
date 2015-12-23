package org.capiz.develops.votingservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.soap.SOAPException;

import org.inspira.server.networking.SOAPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServAvailableVoteProcesses {

	private DatabaseConnection db;
	
	public ServAvailableVoteProcesses(){
		db = new DatabaseConnection("localhost", "PoliVotoGlobal", "root", "sharKhan319@Kodine!");
	}
	
	public String serviceChooser(String json){
		return makeAction(json);
	}
	
	public String makeAction(String elTul){
		String resp = "NOTINGGGG";
		int action = -1;
		JSONObject json;
		try {
			json = new JSONObject(elTul);
			action = json.getInt("action");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        switch(action){
        case 1: // Publish voting process.
        	resp = publishVotingProcess(elTul);
            break;
        case 2: // RegisterVotingPlace.
            resp = registerVotingPlace(elTul);
            break;
        case 3: // Join Voting Process.
            resp = joinVotingProcess(elTul);
            break;
        case 4: // Set Quiz.
            resp = setQuiz(elTul);
            break;
        case 5: // Select available voting processes.
        	resp = selectAvailableProcesses();
        	break;
        case 6:// Finish the voting process.
        	resp = finishVotingProcess(elTul);
        	break;
        case 7: // Download Quiz for voting process.
        	resp = getQuiz(elTul);
        	break;
        case 8: // Get validation from remote participants.
        	resp = getValidationFromParticipants(elTul);
        	break;
        default:
        }
		return resp;
	}
	
	public String selectAvailableProcesses(){
		String resp = null;
		try{
			ResultSet rs = db.getConnection().prepareStatement("select * from VotingProcess where StartDate > current_date() or ( current_date() > StartDate and current_date() < FinishDate) and isConcluded = 0").executeQuery();
			JSONArray jarr = new JSONArray();
			JSONObject json;
			while(rs.next()){
				json = new JSONObject();
				json.put("Title",rs.getString("Title"));
				json.put("StartDate",rs.getString("StartDate"));
				json.put("FinishDate",rs.getString("FinishDate"));
				jarr.put(json);
			}
			System.out.println("Nos andadn consultando: " + jarr.toString());
			resp = jarr.toString();
		}catch(SQLException | JSONException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return resp;
	}
	
	public String publishVotingProcess(String jstr){
		String resp = "NaN";
		try{
			JSONObject json = new JSONObject(jstr);
			CallableStatement stmnt = db.getConnection().prepareCall("{call insertVotingProcess(?,?,?)}");
			stmnt.setString(1, json.getString("Title"));
			stmnt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(json.getString("StartDate"))));
			stmnt.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(json.getString("FinishDate"))));
			stmnt.executeUpdate();
			resp = "success";
		}catch(ParseException | SQLException | JSONException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return resp;
	}
	
	public String registerVotingPlace(String jstr){
		String resp = "NaN";
		try{
			JSONObject json = new JSONObject(jstr);
			CallableStatement stmnt = db.getConnection().prepareCall("{call insertaPlace(?)}");
			stmnt.setString(1, json.getString("Name")); // Es el valor de la "Zona"
			stmnt.executeUpdate();
			resp = "success";
		}catch(JSONException | SQLException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return resp;
	}
	
	public String joinVotingProcess(String jstr){
		String resp = "NaN";
		try{
			JSONObject json = new JSONObject(jstr);
			CallableStatement stmnt = db.getConnection().prepareCall("{call joinVotingProcess(?,?,?)}");
			stmnt.setString(1, json.getString("title"));
			stmnt.setString(2, json.getString("place"));
			stmnt.setString(3, json.getString("host"));
			stmnt.executeUpdate();
			resp = "success";
		}catch(JSONException | SQLException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return resp;
	}
	
	public String setQuiz(String jstr){
		String resp = "NaN";
		try{
			JSONObject json = new JSONObject(jstr);
			JSONObject row;
			JSONArray jarr = json.getJSONArray("quiz");
			JSONArray opciones;
			CallableStatement stmnt;
			for(int i=0; i<jarr.length();i++){
				row = jarr.getJSONObject(i);
				opciones = row.getJSONArray("opciones");
				for(int j=0;j<opciones.length();j++){
					stmnt = db.getConnection().prepareCall("{call registerPreguntaOpcion(?,?,?)}");
					stmnt.setString(1,json.getString("title"));
					stmnt.setString(2,row.getString("pregunta"));
					stmnt.setString(3,opciones.getString(j));
					stmnt.executeUpdate();
				}
			}
			resp = "success";
		}catch(JSONException | SQLException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return resp;
	}
	
	public String finishVotingProcess(String jstr){
		String result = "NaN";
		try{
			JSONObject json = new JSONObject(jstr);
			String title = json.getString("title");
			PreparedStatement stmnt = db.getConnection().prepareStatement("UPDATE VotingProcess SET isConcluded = 1 where Title = ?");
			stmnt.setString(1, title);
			if(stmnt.executeUpdate() > 0)
				result = "success";
		}catch(JSONException | SQLException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return result;
	}
	
	public String getQuiz(String jstr){
		String resp = "NaN";
		try{
			JSONObject json = new JSONObject(jstr);
			String title = json.getString("title");
			PreparedStatement stmnt = db.getConnection().prepareStatement("select Pregunta from Pregunta join VotingProcess on VotingProcess.idVotingProcess = Pregunta.VotingProcess_idVotingProcess where Title = ?"); 
			stmnt.setString(1, title);
			ResultSet rs = stmnt.executeQuery();
			JSONArray content = new JSONArray();
			JSONObject row;
			JSONArray opciones;
			while(rs.next()){
				row = new JSONObject();
				row.put("pregunta",rs.getString("Pregunta"));
				PreparedStatement pstmnt = db.getConnection().prepareStatement("select Reactivo from (select Pregunta_idPregunta,Reactivo from Opcion join Pregunta_has_Opcion on Opcion.idOpcion = Pregunta_has_Opcion.Opcion_idOpcion) r join Pregunta on r.Pregunta_idPregunta = Pregunta.idPregunta where Pregunta = ?");
				pstmnt.setString(1, rs.getString("Pregunta"));
				ResultSet opsRs = pstmnt.executeQuery();
				opciones = new JSONArray();
				while(opsRs.next())
					opciones.put(opsRs.getString("Reactivo"));
				row.put("opciones", opciones);
				content.put(row);
			}
			stmnt = db.getConnection().prepareStatement("select * from VotingProcess where Title = ?");
			stmnt.setString(1, title);
			rs = stmnt.executeQuery();
			rs.next();
			json = new JSONObject();
			json.put("title", title);
			json.put("StartDate",rs.getString("StartDate"));
			json.put("FinishDate", rs.getString("FinishDate"));
			json.put("quiz", content);
			resp = json.toString();
		}catch(SQLException | JSONException e){
			e.printStackTrace();
		}
		db.closeConnection();
		return resp;
	}
	
	private synchronized String getValidationFromParticipants(String jstr){
		String resp = "NaN";
		File f = new File("ConsultaBoletas.log");
		try{
			JSONObject json = new JSONObject(jstr);
			String title = json.getString("title");
			PreparedStatement stmnt = db.getConnection().prepareStatement("select Host,Nombre from (select Host,Nombre,Place_idPlace VotingProcess join Place_has_VotingProcess on VotingProcess.idVotingProcess = Place_has_VotingProcess.VotingProcess_idVotingProcess) r join Place on r.Place_idPlace = idPlace where Title = ?");
			stmnt.setString(1, title);
			stmnt.executeUpdate();
			ResultSet rs = stmnt.getResultSet();
			JSONObject jResponse;
			PrintWriter pw = new PrintWriter(new FileWriter(f,true));
			while(rs.next()){
				SOAPClient cli = new SOAPClient(json);
				cli.setHost(rs.getString("Host"));
				String r = cli.main();
				jResponse = new JSONObject(r);
				pw.println(json.getString("boleta") + ", " + rs.getString("Nombre") + "@" + rs.getString("Host") + "," + (jResponse.getInt("response") == 1));
				if(jResponse.getInt("response") == 1)
					resp = jResponse.toString();
			}
			pw.close();
			if("NaN".equals(resp)){
				json = new JSONObject();
				json.put("response",0);
				resp = json.toString();
			}
		}catch(JSONException | SQLException e){
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}
}
package org.capiz.develops.votingservice;

public class FirstServiceBank {

	private Servicios servicios;
	
	public FirstServiceBank(){
		servicios = new Servicios();
	}

	public String  guardarVoto(String voto, int idVotacion, String boleta) {
		return "*"; //servicios.guardarVoto(voto,idVotacion,boleta);
	}
}
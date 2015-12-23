package Shared;

import java.io.Serializable;
import java.util.LinkedList;

public class SuperChunk implements Serializable{

	private LinkedList<Pregunta> preguntas;
	private String boleta;
	
	public SuperChunk(LinkedList<Pregunta> preguntas){
		this.preguntas = preguntas;
	}
	
	public LinkedList<Pregunta> getPreguntas(){
		return preguntas;
	}
	
	public void setBoleta(String boleta){
		this.boleta = boleta;
	}
}

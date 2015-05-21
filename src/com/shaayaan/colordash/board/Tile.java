package com.shaayaan.colordash.board;

import java.util.ArrayList;

public class Tile {
	
	private boolean isEmpty; 
	private boolean hasFruit; 
	private boolean hasAgent; 
	
	private int fruitType; 
	private int numAgent; 
	private ArrayList<Integer> agentMarkers; 
	
	public Tile() {
		
		isEmpty = true; 
		hasFruit = false; 
		hasAgent = false;
		
		fruitType = 0;  
		numAgent = 0; 
		agentMarkers = new ArrayList<Integer>(); 
	}
	
	public void placeFruit(int type) {
		isEmpty = false; 
		hasFruit = true; 
		fruitType = type; 
	}
	
	public void setPlayers(ArrayList<Integer> players) {
		
		isEmpty = false; 
		hasAgent = true; 
		
		numAgent = players.size(); 
		agentMarkers = players; 
	}
	
	public int takeFruit() {
		int tmp = fruitType; 
		hasFruit = false; 
		fruitType = 0; 
		return tmp; 
	}
	
	public void leave(int marker) {
		numAgent--; 
		for (int i = 0; i < agentMarkers.size(); i++) {
			if (agentMarkers.get(i) == marker) {
				agentMarkers.remove(i); 
			}
		}
		if (numAgent == 0 && !hasFruit) {
			isEmpty = true; 
		}
	}
	
	public void enter(int marker) {
		numAgent++; 
		agentMarkers.add(marker); 
		isEmpty = false; 
		hasAgent = true; 
	}
	
	public boolean isEmpty() {
		return isEmpty; 
	}
	
	public int hasFruit() {
		if (hasFruit) {
			return fruitType; 
		}
		else {
			return 0; 
		}
	}
	
	public boolean hasAgent() {
		return hasAgent; 
	}
	
	public int fruitType() {
		return fruitType; 
	}
	
	public int numAgents() {
		return numAgent; 
	}
	
	public ArrayList<Integer> getMarkers() {
		return agentMarkers; 
	}
}

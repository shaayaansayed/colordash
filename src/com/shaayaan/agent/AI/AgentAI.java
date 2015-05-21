package com.shaayaan.agent.AI;

import java.awt.Point;

import com.shaayaan.agent.Agent;
import com.shaayaan.colordash.board.State;

public abstract class AgentAI extends Agent{
	
	protected double[] weights; 

	public AgentAI(Point pos, int marker, State state, int nVars) {
		super(pos, marker, state);
		weights = new double[nVars]; 
	}  
	
	public void setWeights(double[] weights) {
		this.weights = weights; 
	}
}

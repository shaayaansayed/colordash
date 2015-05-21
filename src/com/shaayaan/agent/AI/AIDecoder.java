package com.shaayaan.agent.AI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.shaayaan.colordash.Game;
import com.shaayaan.genetic.GA;
import com.shaayaan.genetic.GeneticDecoder;
import com.shaayaan.genetic.Weights;

public class AIDecoder extends GeneticDecoder {
	
	private static final String AI = "Fire"; 
	private static Properties props; 
	
	public static void main(String [] args) throws FileNotFoundException, IOException {
		props = new Properties(); 
		props.load(new FileInputStream(new File(GA.PROPERTIES))); 
		props.load(new FileInputStream(new File("./colordash.properties")));
		props.setProperty("game.ai1", AI); 
		props.setProperty("game.fps", "10000");	
		props.setProperty("game.mode", "3"); 

		props.load(new FileInputStream(new File("./" + AI + ".properties")));
		props.setProperty("ga.nVars", String.valueOf(props.getProperty("nVars"))); 
		props.setProperty("ga.UB", String.valueOf(props.getProperty("UB"))); 
		props.setProperty("ga.LB", String.valueOf(props.getProperty("LB"))); 
		
		AIDecoder aid = new AIDecoder(); 
		GA ga = new GA(aid, props); 
		
		String file  = "./assets/AI/" + AI + ".ser";
		
		String bestInd = ga.run();
		Weights weights = new Weights(aid.getPhenotype(bestInd)); 
		weights.save(new File(file));
	}
	
	@Override
	public double getFitness(double[] phenotype) {
		int games = 25; 
		
		double score = 0; 
		
		for (int i = 0; i < games; i++) {
			
			//Train against Heat
			Game game = null; 
			props.setProperty("game.ai2", "Heat"); 
			try {
				game = Game.load(props);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (game.getMain() instanceof AgentAI) {
				((AgentAI) game.getMain()).setWeights(phenotype);
			}
	
			game.start(); 
			
			while(game.isRunning()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			
			int winner = game.getWinner();  
			score += winner == 1 ? 5 : (winner == 3 ? 3 : 0); 
		}	
		
		return score; 
	}
}

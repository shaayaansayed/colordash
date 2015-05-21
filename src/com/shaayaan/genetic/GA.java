package com.shaayaan.genetic;

import java.util.ArrayList;
import java.util.Properties;

public class GA {
	
	public static final String PROPERTIES = "./ga.properties";	
	
	private int popSize;  
	private int generations; 
	private int tourSize; 
	private int encoding; 
	
	private double pRep; 
	private double pCr; 
	private double pMut; 
	private double pMutDash; 
	private int nBits; 
	
	int nVars; 
	int LB; 
	int UB; 
	
	int direction; 
	
	GeneticDecoder decoder; 
	
	ArrayList<String> pop = new ArrayList<String>(); 
	ArrayList<Double> fit = new ArrayList<Double>(); 

	public GA(GeneticDecoder decoder, Properties props) {
		popSize = Integer.valueOf(props.getProperty("ga.popSize")); 
		generations = Integer.valueOf(props.getProperty("ga.gen")); 
		tourSize = Integer.valueOf(props.getProperty("ga.tourSize")); 
		encoding = Integer.valueOf(props.getProperty("ga.encoding")); 
		
		pRep = Double.valueOf(props.getProperty("ga.pRep")); 
		pCr = Double.valueOf(props.getProperty("ga.pCr")); 
		pMut = Double.valueOf(props.getProperty("ga.pMut")); 
		pMutDash = Double.valueOf(props.getProperty("ga.pMutDash")); 
		
		nBits = (int)(pMutDash*encoding/pMut);
		
		nVars = Integer.valueOf(props.getProperty("ga.nVars")); 
		LB = Integer.valueOf(props.getProperty("ga.LB")); 
		UB = Integer.valueOf(props.getProperty("ga.UB")); 
		
		direction = Integer.valueOf(props.getProperty("direction")); 
		
		this.decoder = decoder; 
		decoder.setParam(nVars, UB, LB, encoding); 
	}
	
	public String run() {
		
		genInitPop(); 

		// Main loop iterating over number of generations
		for (int i = 0; i < generations; i++) {
			System.out.println("Generating and evaluating population " + i); 
			ArrayList<String> newPop = new ArrayList<String>(); 
			ArrayList<Double> newFit = new ArrayList<Double>(); 
			
			//save best indvidual for next population
			double[] best; 
			if (direction == -1) {
				best = min(fit); 
			}
			else {
				best = max(fit); 
			}
			newPop.add(pop.get((int)best[1])); 
			newFit.add(fit.get((int)best[1])); 
			
			for (int k = 0; k < popSize; k++) {		
				int operator = multiflip(pRep, pCr);
				switch (operator) {
					case 0: 
						// tournament
						int ind = tournament(fit, tourSize, direction); 
						newPop.add(pop.get(ind)); 
						newFit.add(fit.get(ind)); 
						break; 
					case 1: 
						// crossover
						int ind1 = tournament(fit, tourSize, direction); 
						int ind2 = tournament(fit, tourSize, direction); 
						String[] offSpring =  crossover(pop.get(ind1), pop.get(ind2));
						newPop.add(offSpring[0]); 
						newFit.add(decoder.getFitness(offSpring[0])); 
						newPop.add(offSpring[1]); 
						newFit.add(decoder.getFitness(offSpring[1]));
						break; 
					case 3:
						// mutation
						int indm = tournament(fit, tourSize, direction); 
						String off = mutate(pop.get(indm), nBits); 
						newPop.add(off); 
						newFit.add(decoder.getFitness(off)); 
				}
			}
			
			while (newPop.size() > pop.size()) {
				int worst = (int)min(newFit)[1]; 
				newPop.remove(worst); 
				newFit.remove(worst); 
			}
			
			fit = newFit; 
			pop = newPop; 
		}
		
		double[] bestInd = max(fit); 
		return pop.get((int)bestInd[1]); 
	}
	
	public void genInitPop() {
		System.out.println("Generating and Evaluating Initial Population..."); 
		for (int i = 0; i < popSize; i++) {
			String ind = randInd(nVars*encoding); 
			double fitness = decoder.getFitness(ind); 
			pop.add(ind); 
			fit.add(fitness); 
		}
	}
	
	public int tournament(ArrayList<Double> fit, int tourSize, int direction) {
		ArrayList<Integer> pool = new ArrayList<Integer>(); 
		ArrayList<Double> poolFit = new ArrayList<Double>();
		for (int i = 0; i < tourSize; i++) {
			int rand = (int)(Math.random() * fit.size());
			pool.add(rand); 
			poolFit.add(fit.get(rand)); 
		}
		
		double[] best; 
		if (direction == -1) {
			best = min(poolFit);  
		}
		else {
			best = max(poolFit); 
		}
		
		return (int)best[1]; 
	}
	
	//simple one point crossover
	public String[] crossover(String ind1, String ind2) {
		int point = (int)(Math.random() * encoding);
		String off1 = ind1.substring(0, point) + ind2.substring(point); 
		String off2 = ind2.substring(0, point) + ind1.substring(point); 
		String[] offspring = {off1, off2}; 
		return offspring; 
	}
	
	public String mutate(String ind, int nBits) {
		StringBuffer newInd = new StringBuffer(ind); 
		for (int i = 0; i < nBits; i++) {
			int pos = (int)(Math.random() * encoding); 
			if (newInd.charAt(pos) == '0') {
				newInd.setCharAt(pos, '1');
			}
			else {
				newInd.setCharAt(pos, '0'); 
			}
		}
		return newInd.toString(); 
	}
	
	public double[] max(ArrayList<Double> fit) {
		double max = Integer.MIN_VALUE; 
		int index = 0; 
		
		for (int i = 0; i < fit.size(); i++) {
			if (fit.get(i) > max) {
				max = fit.get(i); 
				index = i; 
			}
		}
		
		double[] best = {max, index}; 
		return best; 
	}
	
	public double[] min(ArrayList<Double> fit) {
		double min = Integer.MAX_VALUE; 
		int index = 0; 
		
		for (int i = 0; i < fit.size(); i++) {
			if (fit.get(i) < min) {
				min = fit.get(i); 
				index = i; 
			}
		}
		
		double[] best = {min, index}; 
		return best; 
	}
	
	public String randInd(int length) {
		String ind = ""; 
		
		for (int i = 0; i < length; i++) {
			if (Math.random() < .5) {
				ind += "0"; 
			}
			else {
				ind += "1"; 
			}
		}
		
		return ind; 
	}
	
	public int multiflip(double pRep, double pCr) {
		//stochastic selection of operator 
		int operator; 
		double rand = Math.random(); 
		
		if (rand < pRep) {
			operator = 0; 
		}
		else if (rand >= pRep && rand < pRep + pCr) {
			operator = 1; 
		}
		else 
			operator = 2; 
		
		return operator; 
	}
}

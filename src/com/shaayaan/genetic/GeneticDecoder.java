package com.shaayaan.genetic;

public abstract class GeneticDecoder {
	
	int nVars; 
	int UB; 
	int LB; 
	int encoding; 
	
	public void setParam(int nVars, int UB, int LB, int encoding) {
		this.nVars = nVars; 
		this.UB = UB; 
		this.LB = LB; 
		this.encoding = encoding; 
	}
	
	public double[] getPhenotype(String genotype) {
		double[] phenotype = new double[nVars]; 
		for (int i = 0; i < nVars; i++) {
			int decode = Integer.parseInt(genotype.substring(encoding*i, encoding*(i + 1)), 2); 
			double gene = LB + (double)(decode)*(UB - LB)/(Math.pow(2, encoding)- 1); 
			phenotype[i] = gene; 
		}
		return phenotype; 
	}
	
	public double getFitness(String genotype) {
		return getFitness(getPhenotype(genotype));
	}

	abstract public double getFitness(double[] phenotype); 
	
}

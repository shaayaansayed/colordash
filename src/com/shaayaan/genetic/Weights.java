package com.shaayaan.genetic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Weights implements Serializable{

	private static final long serialVersionUID = 2676608544456862183L;
	
	double[] weights; 
	
	public Weights(double[] weights) {
		this.weights = weights; 
	}
	
	public double[] getWeights() {
		return weights; 
	}
	
	public void save(File file) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this);
		oos.close();
	}
	
	public static Weights load(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		Weights w = (Weights) in.readObject();
        in.close();
        return w;
	}
}

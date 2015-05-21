package com.shaayaan.agent.AI;

import java.awt.Point;
import java.util.Arrays;

import com.shaayaan.colordash.board.State;

public class Fire extends AgentAI {

	int numFruits;
	int[] totalFruits; 
	double[] myFruits; 
	double[] oppFruits; 
	int[] remainFruits; 
	int[] deadFruits; 
	double[][] heatMap; 
	int width; 
	int height; 
	
	public Fire(Point pos, int marker, State state) {
		super(pos, marker, state, 4);
		
		numFruits = state.getNumFruits(); 
		totalFruits = new int[numFruits]; 
		
		for (int i = 0; i < numFruits; i++) {
			totalFruits[i] = state.getFruitCount(i); 
		}
		
		width = state.getWidth(); 
		height = state.getHeight(); 
	}
	
	public Move getNextMove(State state) {
		
		int myX = pos.x; 
		int myY = pos.y;
		
		//get Current board information
		myFruits = new double[numFruits];
		oppFruits = new double[numFruits]; 
		remainFruits = new int[numFruits]; 
		deadFruits = new int[numFruits]; 
		
		for (int i = 0; i < numFruits; i++) {
			myFruits[i] = state.getMyFruitCount(marker, i + 1); 
			oppFruits[i] = state.getOpponentFruitCount(marker, i + 1); 
			remainFruits[i] = (int)(totalFruits[i] - myFruits[i] - oppFruits[i]); 
			deadFruits[i] = myFruits[i] >= Math.ceil((double)totalFruits[i]/2) || oppFruits[i] >= Math.ceil((double)totalFruits[i]/2) || myFruits[i] + oppFruits[i] == totalFruits[i] ? 1 : 0; 
		}
		
		heatMap = new double[height][width]; 
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int fruit = state.hasItem(x, y); 
				if (fruit > 0 && deadFruits[fruit - 1] != 1) {
					addHeat(x, y, myX, myY, fruit); 
				}
			}
		}

		double n, s, e, w, t; 
		n = myY - 1 < 0 ? -1 : heatMap[myY - 1][myX]; 
		s = myY + 1 >= height ? -1 : heatMap[myY + 1][myX];
		e = myX + 1 >= width ? -1 : heatMap[myY][myX + 1];
		w = myX - 1 < 0 ? -1 : heatMap[myY][myX - 1];
		t = heatMap[myY][myX];
		
		double[] nums = {n, s, e, w, t}; 
		double max = nums[4]; 
		
		if (max == t && state.hasItem(myX, myY) > 0) {
			return Move.TAKE; 
		}
		else if (max == t) {
			nums[4] = -1; 
			Arrays.sort(nums);
			max = nums[4]; 
		}
		
		if (max == n)
			return Move.UP; 
		else if (max == s)
			return Move.DOWN; 
		else if (max == e)
			return Move.RIGHT; 
		else 
			return Move.LEFT; 
	}
	
	public void addHeat(int fx, int fy, int myX, int myY, int fruit) {
		
		fruit = fruit - 1; 
		
		// determines importance of fruit in relation to total of its type 
		// as c1 increases, less common fruits become more valuable
		double c1 = weights[0]; 

		// determines importance of fruits in close contention 
		// as c2 increases, fruits in close contention become more important
		double c2 = weights[1]; 
		
		// determines importance of fruits in relation to distance 
		// as c3 increases, importance dissipates more rapidly
		double c3 = weights[2];  
		
		// determines importance of picking a fruit at this spot 
		double c4 = weights[3]; 
		
		// large if fruit is rare, small if fruit is abundant
		double x1 = Math.pow(totalFruits[fruit], -c1); 
		double x2 = Math.pow(Math.abs(myFruits[fruit] - oppFruits[fruit]) + .5, -c2); 
		
		double para = x1*x2; 
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int dist = getManhattanDist(x, y, fx, fy); 
				if (dist == 0) {
					heatMap[y][x] += para*c4; 
				}
				else {
					heatMap[y][x] += Math.pow(dist, -c3)*para; 
				}
			}
		}
	}
	
	public int getManhattanDist(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2); 
	}
	
	public void printHeat(double[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int k = 0; k < map[0].length; k++) {
				System.out.printf("%.2f ", map[i][k]); 
			}
			System.out.println(); 
		}
	}
}

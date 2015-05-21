package com.shaayaan.colordash.board;

/**
 * 
 * @author Shaayaan Sayed
 * keeps track of the state of the board
 * 
 */
public class State {
	
	private Tile[][] tileBoard; 
	private double[][] score; 
	private int numFruits; 
	private int[] fruitCount; 
	private int width; 
	private int height; 
	
	public State(Board board) {
		tileBoard = board.getTileBoard(); 
		score = board.getScore(); 
		numFruits = board.getNumFruits(); 
		fruitCount = board.getFruitCounts(); 
		width = board.getWidth(); 
		height = board.getHeight(); 
	}
	
	public int hasItem(int x, int y) {
		return tileBoard[y][x].fruitType(); 
	}
	
	public int getNumFruits() {
		return numFruits; 
	}
	
	public int getFruitCount(int type) {
		return fruitCount[type]; 
	}
	
	public double getMyFruitCount(int marker, int type) {
		return score[type - 1][marker - 1]; 
	}
	
	public double getOpponentFruitCount(int marker, int type) {
		return score[type - 1][2 - marker]; 
	}
	
	public int getWidth() {
		return width; 
	}
	
	public int getHeight() {
		return height; 
	}
	
	public double getScore(int marker, int fruit) {
		return score[fruit][marker - 1]; 
	}

}

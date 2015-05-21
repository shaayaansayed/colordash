package com.shaayaan.colordash.board;

import java.awt.Point;
import java.util.ArrayList;

import com.shaayaan.agent.Agent.Move;

public class Board {
	
	private static final int SMALL_MIN_SIZE = 5; 
	private static final int SMALL_MAX_SIZE = 15; 
	private static final int MEDIUM_MIN_SIZE = 10; 
	private static final int MEDIUM_MAX_SIZE = 20; 
	private static final int LARGE_MIN_SIZE = 15; 
	private static final int LARGE_MAX_SIZE = 40; 
	
	private static final int SMALL_TILE_SIZE = 2025; 
	private static final int MEDIUM_TILE_SIZE = 1089; 
	private static final int LARGE_TILE_SIZE = 289;
	
	private static final int numPlayers = 2; 
	private static boolean takeCheck; 
	
	private int numFruits; 
	private int height, width; 
	private int tileSize; 
	private int[] fruits; 
	private double[][] score; 
	private Tile[][] tileBoard; 
	private Point initPoint; 
	
	private Tile prevTile; 
	private int prevFruit; 
	private int numItems; 
	
	private State state; 
	
	public Board(int tile_size, int min_size, int max_size) {
		
		this.tileSize = tile_size;  
		width = (int)(Math.random() * (max_size - min_size + 1) + min_size); 
		height = (int)(Math.random() * (max_size - min_size + 1) + min_size);  
		initialize();  
		state = new State(this);
	}
	
	private void initialize() {
		
		tileBoard = new Tile[height][width];  
		
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				tileBoard[r][c] = new Tile(); 
			}
		}
		
		do{
			numFruits = (int)(Math.random() * 3 + 3); 
		} while(Math.pow(numFruits, 2) >= width*height); 
		
		score = new double[numFruits][numPlayers]; 
		
		fruits = new int[numFruits];
		for (int i = 0; i < numFruits; i++) {
			fruits[i] = 2*i + 1; 
			numItems += fruits[i]; 
		}
		 
		int r, c; 
		//place fruits on board
		for (int i = 0; i < numFruits; i++) {
			r = 0; 
			c = 0; 
			for (int k = 0; k < fruits[i]; k++) {
				do {
					r = (int)(Math.random() * height); 
                    c = (int)(Math.random() * width);
                } while (!tileBoard[r][c].isEmpty());
                tileBoard[r][c].placeFruit(i + 1); 
			}
		}
		
		//place players
		do {
			r = (int)(Math.random() * height); 
			c = (int)(Math.random() * width);
		}while(!tileBoard[r][c].isEmpty()); 
		
		ArrayList<Integer> players = new ArrayList<Integer>(); 
		for (int i = 0; i < numPlayers; i++) {
			players.add(i + 1); 
			tileBoard[r][c].setPlayers(players); 
			initPoint = new Point(r, c); 
		}
		takeCheck = true; 
		prevTile = tileBoard[r][c]; 
	}
	
	public Tile update(int marker, Point pos, Point new_pos, Move mov) { 
		if (mov == Move.TAKE) { 
			int f = tileBoard[pos.x][pos.y].hasFruit();
			if (f != 0) { 
				//player x takes fruit and player y is on the same tile
				//fruit must stay on tile 
				if (!takeCheck && tileBoard[pos.x][pos.y].numAgents() == 2) {
					takeCheck = true;
					prevTile = tileBoard[pos.x][pos.y];  
					score[f - 1][marker - 1] += 1; 
				}
				//player x takes fruit and player y on next turn takes same fruit 
				//fruit must not stay 
				else if (takeCheck && tileBoard[pos.x][pos.y].equals(prevTile)) {
					takeCheck = false;
					tileBoard[pos.x][pos.y].takeFruit(); 
					score[prevFruit - 1][marker - 1]+=.5; 
					score[prevFruit - 1][2 - marker] -= .5;  
					numItems--; 
				}
				//player x takes fruit and player y takes fruit on another tile 
				//fruit must not stay 
				else {
					takeCheck = false; 
					tileBoard[pos.x][pos.y].takeFruit(); 
					score[f - 1][marker - 1] += 1; 
					numItems--; 
				}
				prevFruit = f; 
			}
		}
		else {
			takeCheck = false; 
			tileBoard[pos.x][pos.y].leave(marker); 
			tileBoard[new_pos.x][new_pos.y].enter(marker); 
		}
		return tileBoard[new_pos.x][new_pos.y]; 
	}
	
	/**
	 * returns 0 if no player has won
	 * returns 1 if player 1 has won
	 * returns 2 if player 2 has won
	 * @return
	 */
	public int winCondition() {
		if (!takeCheck || numItems == 0) {
			int[] cats = new int[numFruits]; 
			for (int i = 0; i < numFruits; i++) {
				double remaining = (2*(i) + 1) - score[i][0] - score[i][1];
				if (score[i][0] + remaining < score[i][1]) {
					cats[i] = 2; 
				}
				//player 1 won this category
				else if(score[i][1] + remaining < score[i][0]) {
					cats[i] = 1;  
				}
				else if (remaining == 0) {
					cats[i] = 4; 
				}
			}
			
			int p1 = 0; 
			int p2 = 0; 
			int draw = 0; 
			for (int i = 0; i < cats.length; i++) {
				if (cats[i] == 1)
					p1++; 
				else if(cats[i] == 2)
					p2++; 
				else if (cats[i] == 4) {
					draw++; 
				}
			}
			if (p1 > (numFruits - draw)/2) {
				return 1; 
			}
			else if(p2 > (numFruits - draw)/2) {
				return 2; 
			}
			else if (p1 == p2 && p1 + p2 + draw == numFruits) {
				return 3; 
			}
			else 
				return 0; 
		}
		return 0; 
	}
	
	public void printBoard() {
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				System.out.print(tileBoard[i][k].hasFruit() + " "); 
			}
			System.out.println(); 
		}
	}
	
	public boolean onBoard(Point pos) {
		if (pos.x < 0 || pos.x >= width || pos.y < 0 || pos.y >= height) {
			return false; 
		}
		return true; 
	}
	
	public static boolean getTakeCheck() {
		return takeCheck; 
	}
	
	public int getTileSize() {
		return tileSize; 
	}
	
	public int getWidth() {
		return width; 
	}
	
	public int getHeight() {
		return height; 
	}
	
	public Tile[][] getTileBoard() {
		return tileBoard; 
	}
	
	public int getNumFruits() {
		return numFruits; 
	}
	
	public int[] getFruitCounts() {
		return fruits; 
	}
	
	public Point getInitialPoint() {
		return initPoint; 
	}
	
	public double[][] getScore() {
		return score; 
	}
	
	public State getState() {
		return state; 
	}
	
	public static Board load(String size) {
		
		int min_size, max_size, tile_size; 
		switch(size) {
			case "small":
				min_size = SMALL_MIN_SIZE; 
				max_size = SMALL_MAX_SIZE; 
				tile_size = SMALL_TILE_SIZE; 
				break;
			case "medium":
				min_size = MEDIUM_MIN_SIZE; 
				max_size = MEDIUM_MAX_SIZE; 
				tile_size = MEDIUM_TILE_SIZE; 
				break;
			default: 
				min_size = LARGE_MIN_SIZE; 
				max_size = LARGE_MAX_SIZE; 
				tile_size = LARGE_TILE_SIZE; 
		}
		
		return new Board(tile_size, min_size, max_size); 
	}
}

package com.shaayaan.agent.AI;

import java.awt.Point;

import com.shaayaan.agent.Agent;
import com.shaayaan.colordash.board.State;

/**
 * 
 * @author Shaayaan Sayed
 * AI goes to the nearest fruit 
 * 
 */
public class NearAI extends Agent {

	int nearestFruit; 
	Point nearestCoord; 
	
	public NearAI(Point pos, int marker, State state) {
		super(pos, marker, state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Move getNextMove(State state) {
		int myX = pos.x; 
		int myY = pos.y; 
		if (nearestCoord == null || state.hasItem(nearestCoord.x, nearestCoord.y) == 0) {
			findNearestFruit(state, pos.x, pos.y); 
		}
		
		if (myX > nearestCoord.x) {
			return Move.LEFT; 
		}
		else if (myX < nearestCoord.x) {
			return Move.RIGHT; 
		}
		else if(myY < nearestCoord.y) {
			return Move.DOWN; 
		}
		else if (myY > nearestCoord.y) {
			return Move.UP; 
		}
		else{
			nearestCoord = null; 
			return Move.TAKE; 
		}
	}

	public void findNearestFruit(State state, int myX, int myY) {
		int boardWidth = state.getWidth(); 
		int boardHeight = state.getHeight();
		int bound = 1; 
		
		while (true) {
			int shortestDist = Integer.MAX_VALUE; 
			Point point = new Point(0, 0);  
			int fruit = 0; 
			for (int x = myX - bound; x < myX + bound; x++) {
				for (int y = myY - bound; y < myY + bound; y++) {
					if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) {
						continue; 
					}
					else {
						if (state.hasItem(x, y) >  0) {
							int dist = getManhattanDist(myX, myY, x, y); 
							if (dist < shortestDist) {
								shortestDist = dist; 
								point = new Point(x, y); 
								fruit = state.hasItem(x, y); 
							}
						}
					}
				}
			}
			
			if (fruit > 0) {
				nearestCoord = point; 
				nearestFruit = fruit; 
				break; 
			}
			bound++; 
		}
	}
	
	public int getManhattanDist(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2); 
	}
	
}

package com.shaayaan.agent;

import java.awt.Point;

import com.shaayaan.colordash.board.Board;
import com.shaayaan.colordash.board.State;

/**
 * Agent is the superclass for all movable bots on 
 * the board (Human or AI)
 * 
 * @author Shaayaan Sayed
 *
 */
public abstract class Agent{
	
	protected Point pos; 
	protected int marker; 
	State state; 
	
	public Agent(Point pos, int marker, State state) {
		this.pos = new Point(pos.y, pos.x); 
		this.marker = marker; 
		this.state = state; 
	}
	
	public boolean move(Board board) {
		Move next = getNextMove(state); 
		if (next != null){
			Point newPos; 
			switch(next){
				case UP:
					newPos = new Point(pos.x, pos.y - 1); 
					if (!board.onBoard(newPos)) {
						newPos = pos; 
					}
					break;
				case LEFT:
					newPos = new Point(pos.x - 1, pos.y);
					if (!board.onBoard(newPos)) {
						newPos = pos; 
					}
					break; 
				case DOWN: 
					newPos = new Point(pos.x, pos.y + 1);
					if (!board.onBoard(newPos)) {
						newPos = pos; 
					}
					break; 
				case RIGHT:
					newPos = new Point(pos.x + 1, pos.y);
					if (!board.onBoard(newPos)) {
						newPos = pos; 
					}
					break; 
				default:
					newPos = pos; 
					break; 				
			}
			board.update(marker, new Point(pos.y, pos.x), new Point(newPos.y, newPos.x), next); 
			pos = newPos; 
			return true; 
		}
		return false; 
	}
	
	abstract public Move getNextMove(State state); 
	
	public int getMarker() {
		return marker; 
	}
	
	public enum Move {
		UP, LEFT, DOWN, RIGHT, TAKE, STAY;
	}
}

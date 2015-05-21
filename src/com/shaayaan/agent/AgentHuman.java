package com.shaayaan.agent;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.shaayaan.colordash.board.State;

public class AgentHuman extends Agent implements KeyListener {
	
	private Move _next;
	
	public AgentHuman(Point pos, int marker, State state) {
		super(pos, marker, state); 
		_next = null; 
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_DOWN:	_next = Move.DOWN;  break;
			case KeyEvent.VK_UP:	_next = Move.UP;    break;
			case KeyEvent.VK_LEFT:	_next = Move.LEFT;  break;
			case KeyEvent.VK_RIGHT:	_next = Move.RIGHT; break;
			case KeyEvent.VK_SPACE: _next = Move.TAKE; break; 			
		}
	}
	
	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }

	@Override
	public Move getNextMove(State state) {
		Move tmp = _next; 
		_next = null; 
		return tmp; 
	}
}

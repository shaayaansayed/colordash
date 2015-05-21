package com.shaayaan.colordash;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.Timer;

import com.shaayaan.agent.Agent;
import com.shaayaan.agent.AgentHuman;
import com.shaayaan.agent.AI.Fire;
import com.shaayaan.agent.AI.Heat;
import com.shaayaan.agent.AI.NearAI;
import com.shaayaan.colordash.board.Board;
import com.shaayaan.colordash.board.State;
import com.shaayaan.genetic.Weights;


/**
 * Game class controls internal game components 
 * and handles behind the back operations 
 * 
 * 
 * @author Shaayaan Sayed
 *
 */
public class Game implements ActionListener {
	
	private static final int FRAMES_PER_SECOND = 30;
	private static final int numPlayers = 2; 
	private static int frames; 
	
	private Agent[] agents; 
	private Board board; 
	private State state; 
	
	//keeps track of turns 
	private int turnCounter; 
	private boolean isSwitch; 	
	
	private Timer gTimer; 
	private static int interval = 10*1000; 
	
	private int winner; 
	private int timer = interval; 
	private int counter = 0; 
	
	public Game(Board board, Agent[] agents, State state, Agent oponnent) {
		this(board, agents, state, FRAMES_PER_SECOND);
	}
	
	public Game(Board board, Agent[] agents, State state, int frames) {
		this.board  = board; 
		this.state = state; 
		this.agents = agents; 
		Game.frames = frames; 
		turnCounter = 0; 
		gTimer = new Timer(1000 / frames, this); 
	}
	

	public void actionPerformed(ActionEvent arg0) { 
		counter++; 
		if (counter == frames) {
			counter = 0; 
			timer -= 1000; 
			if (timer == 0) {
				newTurn(); 
			}
		}
		isSwitch = false; 
		boolean move = agents[turnCounter].move(board);
		if (move) { 
			int win = detectWinCondition(); 
			if (win > 0) {
				stop(); 
			}
			else {
				newTurn();
				gTimer.restart();  
			}
		}
	}
	
	public int detectWinCondition() {
		int win = board.winCondition(); 
		if (win == 1 || win == 2) {
			winner = win; 
		}
		else if (win == 3) {
			winner = 3; 
		}
		return win;  
	}
	
	public int getWinner() {
		return winner; 
	}
	
	public void newTurn() {
		isSwitch = true; 
		turnCounter = turnCounter == numPlayers - 1 ? 0 : turnCounter + 1; 
		timer = interval; 
		counter = 0;  
	}
	
	public void addActionListener(ActionListener listener) {
		gTimer.addActionListener(listener);
	}
	
	/** Start execution of the game. */
	public void start() { 
		gTimer.start();
	}
	
	/** Stop game timer */
	public void stop() {
		gTimer.stop();
	}
	
	public boolean isRunning() {
		return gTimer.isRunning(); 
	}
	
	public List<Agent> getAgents() {
		return Arrays.asList(agents); 
	}
	
	public Agent getMain() {
		return agents[0]; 
	}
	
	public Board getBoard() {
		return board; 
	}
	
	public State getState() {
		return state; 
	}
	
	public int getTime() {
		return timer;  
	}
	
	public int getTurn() {
		return turnCounter; 
	}

	public boolean isSwitch() {
		return isSwitch; 
	}

	public static final Game load(Properties props) throws IOException, URISyntaxException, ClassNotFoundException {
		int frames = Integer.valueOf(props.getProperty("game.fps"));
		props.load(new FileInputStream(new File(props.getProperty("game.properties"))));
		
		//create board
		String size = String.valueOf(props.getProperty("game.size")); 
		Board board  = Board.load(size); 
		
		Agent[] players = new Agent[numPlayers]; 
		
		// Initialize Agents
		int mode = Integer.valueOf(props.getProperty("game.mode"));
		
		State state = board.getState(); 
		Point point = board.getInitialPoint(); 
		
		ArrayList<Agent> AIs = new ArrayList<Agent>();  
		
		if (mode == 2) {
			AIs = new ArrayList<Agent>(); 
			AIs.add(getAI(String.valueOf(props.getProperty("game.ai1")), point, 2, state, props)); 
		}
		else if (mode == 3) {
			AIs = new ArrayList<Agent>(); 
			AIs.add(getAI(String.valueOf(props.getProperty("game.ai1")), point, 1, state, props)); 
			AIs.add(getAI(String.valueOf(props.getProperty("game.ai2")), point, 2, state, props)); 
		}
	
		switch (mode) {
			case 1: 
				for (int i = 0; i < numPlayers; i++) {
					players[i] = new AgentHuman(point, i + 1, state); 
				}
				break; 
			case 2: 
				players[0] = new AgentHuman(point, 1, state); 
				players[1] = AIs.get(0); 
				break; 
			case 3: 
				for (int i = 0; i < numPlayers; i++) {
					players[i] = AIs.get(i); 
				}
				break;				
		}	
		return new Game(board, players, state, frames);
	}
	
	private static Agent getAI(String name, Point point, int marker, State state, Properties props) throws ClassNotFoundException, IOException {  
		if (name.equals("Near")) {
			return new NearAI(point, marker, state); 
		}
		else if (name.equals("Heat")) {
			return new Heat(point, marker, state); 
		}
		else if (name.equals("Fire")) {
			Fire fire = new Fire(point, marker, state);
			Weights w; 
			if (marker == 1) {
				w = Weights.load(new File(props.getProperty("game.file1"))); 
			}
			else {
				w = Weights.load(new File(props.getProperty("game.file2"))); 
			}
			fire.setWeights(w.getWeights());
			return fire; 
		}
		else {
			return null; 
		}
	}
}

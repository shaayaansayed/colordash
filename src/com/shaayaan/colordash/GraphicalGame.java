package com.shaayaan.colordash;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.shaayaan.agent.Agent;
import com.shaayaan.agent.AgentHuman;
import com.shaayaan.colordash.board.Board;
import com.shaayaan.colordash.board.Tile;

/**
 * Graphical game class provides a GUI for the 
 * Game class 
 * 
 * 
 * @author Shaayaan Sayed
 *
 */
public class GraphicalGame extends JFrame {
	
	private static int num_players = 2; 
	
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("./colordash.properties")));
		Game game = Game.load(props); 
		new GraphicalGame(game);
		
	}
	
	public GraphicalGame(Game game) throws IOException, FontFormatException, URISyntaxException, InterruptedException {
		GamePanel gp = new GamePanel(game); 
		this.setContentPane(gp);
		this.setTitle("ColorDash - Shaayaan Sayed");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocation(200, 100);
		this.pack();
		this.setVisible(true);
	}
	
	private class GamePanel extends JPanel implements ActionListener {

		private static final String TITLE_FONT_FILE = "./assets/fonts/gamegirl.ttf";
		private static final String SCORE_FONT_FILE = "./assets/fonts/timeburner.ttf";
		
		private Font TITLE_FONT; 
		private Font SCORE_FONT;
		
		private Game game;
		private List<Agent> agents; 
		ArrayList<Color> fruitColors = new ArrayList<Color>(); 
		ArrayList<Color> playerColors = new ArrayList<Color>(); 
		
		private int gameWidth = 1000; 
		private int gameHeight = 800; 
		
		//for title and player information
		private double perOff = .15; 
		private int linearOff = (int)(gameHeight*perOff); 
		float c = 255f;  
		
		public GamePanel(Game game) throws IOException, FontFormatException, URISyntaxException {	
			
			setColors(); 
			TITLE_FONT = loadFont(55, TITLE_FONT_FILE); 
			SCORE_FONT = loadFont(20, SCORE_FONT_FILE); 
			
			this.game = game; 
			game.addActionListener(this);
			game.start();
			
			agents = game.getAgents(); 
			this.setPreferredSize(new Dimension(gameWidth, gameHeight));
			this.setBackground(Color.WHITE);

			
			if(game.getMain() instanceof AgentHuman) {
				this.addKeyListener((AgentHuman) game.getMain());
				this.setFocusable(true);
				this.requestFocusInWindow();
			}			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) { 
			if (game.isSwitch()) {
				int turn = game.getTurn(); 
				if (agents.get(turn) instanceof AgentHuman) {
					this.removeKeyListener(this.getKeyListeners()[0]);
					this.addKeyListener((AgentHuman) agents.get(turn)); 
					this.setFocusable(true);
					this.requestFocusInWindow();
				}
			}
			this.repaint(); 	
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Board b = game.getBoard(); 
			drawTitle(g, b, b.winCondition()); 
			drawBoard(g, b); 
			drawScoreBoard(g, b); 
		}
		
		public void drawTitle(Graphics g, Board board, int winner) {
			
			int sect = (int)((linearOff - 20)/num_players); 
			g.setFont(TITLE_FONT);
			
			for (int i = 1; i <= num_players; i++) {
				if (i - 1 == game.getTurn()) { 
					g.setColor(playerColors.get(i - 1));
				}
				else {
					g.setColor(new Color(0, 0, 0, .7f));
				}
				if (winner == 0)
					g.drawString("Player " + i, 0, sect*i);
				else if (winner == i)
					g.drawString("Player " + i + "- winner!", 0, sect*i);
				else if (winner == 3) {
					g.drawString("Player " + i + "- draw", 0, sect*i);
				}
			}
		}
		
		private void drawBoard(Graphics g, Board board) {
			
			int width = board.getWidth();  
			int height = board.getHeight();  
			int tileSide = (int)Math.sqrt(board.getTileSize()); 
			Tile[][] tile_board = board.getTileBoard(); 
			
			Graphics2D g2d = (Graphics2D) g.create();
			
			Color background = playerColors.get(game.getTurn() + 4); 
			g2d.setColor(background);
			g2d.fillRect(0, linearOff, tileSide*width, tileSide*height); 
			//set grid lines to color of the player whose turn it is
			g2d.setColor(playerColors.get(game.getTurn() + 2));
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			for(int r = 0; r < height; r++) {
				g2d.drawLine(0, linearOff + tileSide*r, tileSide*width, linearOff + tileSide*r);
			}
			g2d.drawLine(0, linearOff + tileSide*height, tileSide*width, linearOff + tileSide*height);
			for(int c = 0; c < width; c++) {
				g2d.drawLine(tileSide*c, linearOff, tileSide*c, linearOff + tileSide*height);
			}
			g2d.drawLine(tileSide*width, linearOff, tileSide*width, linearOff + tileSide*height);
			
			double per = .25; 
			for (int r = 0; r < height; r++) {
				for (int c = 0; c < width; c++) {
					Tile t = tile_board[r][c]; 
					if (t.isEmpty()) {
						continue; 
					}
					else {
						if(t.hasFruit() > 0) {
							int mid_x = (int)(((2*c + 1)*tileSide)/2); 
							int mid_y = (int)((2*linearOff + tileSide*(2*r + 1))/2);  
							g2d.setColor(fruitColors.get(t.fruitType() - 1));
							drawCircle(g2d, mid_x, mid_y, (int)(per*tileSide)); 
						}
						if (t.hasAgent()) {
							int mid_x = (int)(((2*c + 1)*tileSide)/2); 
							int mid_y = (int)((2*linearOff + tileSide*(2*r + 1))/2); 
							ArrayList<Integer> tileMarkers = t.getMarkers(); 
							for (int i = 0; i < t.numAgents(); i++) { 
								g2d.setColor(playerColors.get(tileMarkers.get(i) - 1));
								drawSquareOff(g2d, mid_x, mid_y, (int)(per*tileSide), tileMarkers.get(i)); 
							}
						}
					}
				}
			}
		}
		
		public void drawScoreBoard(Graphics g, Board board) {
			
			final double per = .25; 
			final int gridSide = 40; 
			int tileSide = (int)Math.sqrt(board.getTileSize()); 
			Graphics2D g2d = (Graphics2D)g; 
			
			//right offset from gameboard
			int padding = 20; 
			Point topLeft = new Point(board.getWidth()*tileSide + padding, linearOff); 
			int num_fruits = board.getNumFruits(); 
			int[] fCounts = board.getFruitCounts(); 
			double[][] score = board.getScore(); 
			
			g2d.setFont(SCORE_FONT);
			
			for (int r = 0; r < 1 + num_fruits; r++) {
				for (int c = 0; c < num_players + 2; c++) {
					//draw players on first row
					if (r == 0 && (c == 2 || c == 3)) {
						int mid_x = (((2*c + 1)*gridSide)/2) + topLeft.x; 
						int mid_y = (int)((2*topLeft.y + gridSide*(2*r + 1))/2);
						g2d.setColor(playerColors.get(c - 2));
						drawSquare(g2d, mid_x, mid_y, (int)(per*gridSide)); 
					}
					else if (r != 0) {
						if (c == 0) {
							int mid_x = (((2*c + 1)*gridSide)/2) + topLeft.x; 
							int mid_y = (int)((2*topLeft.y + gridSide*(2*r + 1))/2);
							g2d.setColor(fruitColors.get(r - 1));
							drawCircle(g2d, mid_x, mid_y, (int)(per*gridSide)); 
						}
						else if (c == 1) {
							g2d.setColor(new Color(0, 0, 0));
							g2d.drawString("(" +  fCounts[r - 1] + ")", topLeft.x + c*gridSide + 6, (r + 1)*gridSide + linearOff - 10);
						}
						else {
							g2d.drawString(""+ score[r - 1][c - 2], topLeft.x + c*gridSide + 6, (r + 1)*gridSide + linearOff - 10);
						}
					}
				}
			}
			
			int time = game.getTime(); 
			int minute = time / 60000; 
			int second = (time % 60000)/1000;
			String display = String.format("%d:%02d", minute, second);
			g2d.drawString(display, topLeft.x, topLeft.y + gridSide*(num_fruits + 1) + padding*2);
		}
		
		
		public void drawCircle(Graphics2D cg, int xCenter, int yCenter, int r) {
			cg.fillOval(xCenter-r, yCenter-r, 2*r + 5, 2*r + 5);
		}
		
		public void drawSquare(Graphics2D cg, int xCenter, int yCenter, int s) {
			cg.fillRect(xCenter - s, yCenter - s, 2*s, 2*s);
		}
		
		public void drawSquareOff(Graphics2D cg, int xCenter, int yCenter, int s, int marker) {
			int padding = 3; 
			int offset = (int)Math.pow(-1, marker)*padding; 
			cg.fillRect(xCenter - s + offset, yCenter - s + offset, 2*s - 2, 2*s - 2);
		}	
		
		public void setColors() {
			
			Color gold = new Color(227, 200, 0); 
			Color silver = new Color(168, 168, 168); 
			Color bronze = new Color(160, 82, 45);
//			Color bronze = new Color(150, 90, 56); 
			Color indigo = new Color(106, 0, 255); 
			Color emerald = new Color(0, 138, 0); 
			
			float fruitAlpha = .7f; 
			fruitColors.add(color(gold, fruitAlpha)); 
			fruitColors.add(color(silver, fruitAlpha)); 
			fruitColors.add(color(bronze, fruitAlpha)); 
			fruitColors.add(color(emerald, fruitAlpha));
			fruitColors.add(color(indigo, fruitAlpha));  
			
			Color sblue = new Color(70, 130, 180); 
			Color crimson = new Color(162, 0, 37); 

			float playerAlpha = .9f; 			
			playerColors.add(color(crimson, playerAlpha));
			playerColors.add(color(sblue, playerAlpha)); 
			
			float gridAlpha = .5f; 
			playerColors.add(color(crimson, gridAlpha)); 
			playerColors.add(color(sblue, gridAlpha + .2f)); 
			
			float bgAlpha = .05f; 
			playerColors.add(color(crimson, bgAlpha)); 
			playerColors.add(color(sblue, bgAlpha + .03f));  
		}
		
		private Color color(Color cl, float alpha) {
			return new Color(cl.getRed()/c, cl.getGreen()/c, cl.getBlue()/c, alpha); 
		}
		
		private Font loadFont(int fontSize, String fontFile) {
			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT, 
						new File(fontFile))
						.deriveFont((float)fontSize);
				return font;  
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null; 
		}
	}
	
	public static void load(Game game) throws IOException, FontFormatException, URISyntaxException, InterruptedException {
		new GraphicalGame(game); 
	}
}

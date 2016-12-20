package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AStarPathFinding {
	
	private final static int WIDTH = 6;
	private final static int HEIGHT = 6;
	private final static String RED = "\033[31m";
	private final static String GREEN = "\033[32m";
	private final static String YELLOW = "\033[33m";
	private final static String BLUE = "\033[34m";
	private final static String CYAN = "\033[36m";
	private final static String DEFAULT = "\033[0m";

	public void progressBar() throws InterruptedException, IOException {
		int progress = 0;

		while (progress <= 100) {

			StringBuffer progressBar = new StringBuffer();

			for (int x = 0; x < 10; x++) {
				int positionToOutputTo = (int) Math.ceil(progress / 10);

				if (x < positionToOutputTo) {
					progressBar.append("# ");
				} else {
					progressBar.append("- ");
				}
			}
			String output = "\r" + progressBar.toString() + progress + "%";
			System.out.write(output.getBytes());

			progress += Math.random() * 10;

			Thread.sleep((long) (Math.random() * 1000));
		}

		System.out.println("");
	}

	public void pathFinding() {

		GameBoard gameBoard = this.setupGameBoard();

		gameBoard.setPlayerCoOrds(4, 3);
		gameBoard.setTargetTileCoOrds(0, WIDTH - 1);
		
		gameBoard.getTileSafe(1, 3).setBlocked(true);
		gameBoard.getTileSafe(3, 5).setBlocked(true);
		gameBoard.getTileSafe(5, 3).setBlocked(true);

		gameBoard.getTileSafe(1, 4).setBlocked(true);
		gameBoard.getTileSafe(3, 1).setBlocked(true);
		gameBoard.getTileSafe(1, 5).setBlocked(true);

		System.out.println("The raw game board...");
		this.printGameBoard(gameBoard);

		gameBoard.aStarThisShit(false);
		
		System.out.println("Now with a calculated path...");
		this.printGameBoard(gameBoard);
		
		gameBoard.optimiseRoute();
		System.out.println("With an optimised route");
		this.printGameBoard(gameBoard);
		
		System.out.println("Final route taken...");
		this.printRouteTaken(gameBoard);
	}

	private void printRouteTaken(GameBoard gameBoard) {
		for (int x = gameBoard.open.size() - 1; x >= 0; x--) {
			Tile t = gameBoard.open.get(x);

			System.out.print(String.format("%d,%d", t.getX(), t.getY()));

			if (t != gameBoard.getTileSafe(gameBoard.getTargetX(), gameBoard.getTargetY())) {
				System.out.print(" ==> ");
			}
		}
		
		this.printNewLine();
	}

	private GameBoard setupGameBoard() {
		GameBoard gameBoard = new GameBoard(HEIGHT, WIDTH);

		return gameBoard;
	}
	
	private void printGameBoard(GameBoard gameBoard) {
		this.printHorizontalLine();
		this.printNewLine();

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				String leftBorder = (this.shouldPrintLeftBorder(x)) ? "|" : "";
				String rightBorder = (this.shouldPrintRightBorder(x)) ? "|" : "";
				Tile tile = gameBoard.getTileForXandY(x, y);

				String colour = DEFAULT;

				if (gameBoard.isPlayerInTile(x, y)) {
					colour = BLUE;
				} else if (tile.isBlocked) {
					colour = CYAN;
				} else if (gameBoard.isTileInOpenList(tile)) {
					colour = GREEN;
				} else if (gameBoard.isTileInClosedList(tile)) {
					colour = RED;
				} else if (gameBoard.isTargetTile(x, y)) {
					colour = YELLOW;
				}

				System.out.print(String.format("%s%s %02d, %02d, %02d %s%s", leftBorder, colour, tile.scoreFromPlayer,
						tile.heuristic, tile.score, DEFAULT, rightBorder));
			}

			this.printNewLine();
			this.printHorizontalLine();
			this.printNewLine();
		}

	}

	private void printNewLine() {
		System.out.println("");
	}

	private void printHorizontalLine() {
		for (int y = 0; y < HEIGHT; y++) {
			System.out.print("-------------");
		}
	}

	private boolean shouldPrintLeftBorder(int x) {
		return true;
	}

	private boolean shouldPrintRightBorder(int x) {
		return (x == (WIDTH - 1));
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		AStarPathFinding aStar = new AStarPathFinding();

		// aStar.progressBar();

		aStar.pathFinding();
	}
	
	public class GameBoard {
		private List<List<Tile>> gameBoard;
		private Integer playerX;
		private Integer playerY;
		private List<Tile> open;
		private List<Tile> closed;
		private Integer targetX;
		private Integer targetY;
		
		public GameBoard(int width, int height) {
			this.gameBoard = new ArrayList<List<Tile>>();
			
			for (int y = 0; y < height; y++) {
				this.gameBoard.add(y, new ArrayList<Tile>());
				
				for (int x = 0; x < width; x++) {
					this.gameBoard.get(y).add(x, new Tile(x, y));
				}
			}
			
			this.open = new ArrayList<Tile>();
			this.closed = new ArrayList<Tile>();
		}

		public boolean isPlayerAtTarget() {
			return (this.playerX == this.targetX && this.playerY == this.targetY);
		}
		
		public void optimiseRoute() {
			
			List<Tile> tilesToRemove = new ArrayList<Tile>();
			
			for (Tile tile : this.open) {
				
				if (!tilesToRemove.contains(tile)) {
				
					List<Tile> adjacentTiles = this.getAdjacentTiles(tile.getX(), tile.getY());
					
					if (adjacentTiles != null && adjacentTiles.size() > 0) {
						for (Tile candidateTile : adjacentTiles) {
							if (!candidateTile.isBlocked()) {
								if (this.open.contains(candidateTile)) {
									int candiateTileStepInRoute = this.open.indexOf(candidateTile);
									
									int actualStepInRoute = this.open.indexOf(tile);
									
									if (candiateTileStepInRoute == (actualStepInRoute - 1) || candiateTileStepInRoute == (actualStepInRoute + 1)) {
									}
									else {
										//they are not sequential in the route so can short circuit
									
										for(int j = actualStepInRoute + 1; j <= candiateTileStepInRoute - 1; j++) {
											tilesToRemove.add(this.open.get(j));
										}
									}
								}
							}
						}
					}
				}
				
			}

			List<Tile> newRoute = new ArrayList<Tile>();
			for (Tile tileInRoute : this.open) {
				if (!tilesToRemove.contains(tileInRoute)) {
					newRoute.add(tileInRoute);
				}
			}
			
			this.open = newRoute;
		}

		public void aStarThisShit(boolean shouldPrintAsCalculates) {
			
			this.clearClosedList();
			
			this.addPlayerToClosed();
			
			this.pathFind(this.getTileForXandY(this.playerX, this.playerY), 0);
		}
		
		private void clearClosedList() {
			for (int x = 0; x < this.closed.size(); x++) {
				this.closed.add(x, null);
			}
		}
		
		private void addPlayerToClosed() {
			this.closed.add(this.gameBoard.get(this.playerY).get(this.playerX));
		}
		
		private Tile pathFind(Tile currentTile, int timeToWaitBetweenSteps) {
			
			// if no tile passsed in
			if (currentTile == null) {
				return null;
			}
			
			printGameBoard(this);
			
			try {
				Thread.sleep(timeToWaitBetweenSteps);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			// if this is not the target tile
			if (!this.isTargetTile(currentTile.getX(), currentTile.getY())) {
				
				// get the adjacent tiles and see where we can go
				
				List<Tile> adjacentTiles = this.getCandidateAdjacentTiles(currentTile.getX(), currentTile.getY());
				
				if (adjacentTiles == null || adjacentTiles.size() == 0) {
					// could not go anywhere :-(
					// mark this tile as closed
					this.closed.add(currentTile);
					
					return null;
				}
				else {
					
					Tile tileToFollow = this.getLowestScoreTileNotClosed(adjacentTiles);
					
					while (tileToFollow != null) {
						this.closed.add(tileToFollow);
						Tile nextTile = this.pathFind(tileToFollow, timeToWaitBetweenSteps);
						
						if (nextTile != null) {
							this.open.add(currentTile);
							
							return currentTile;
						}
						else {
							tileToFollow = this.getLowestScoreTileNotClosed(adjacentTiles);
						}
					}
					
					return null;
				}
			}
			else {
				this.open.add(currentTile);
				
				return currentTile;
			}
		}
		
		private Tile getLowestScoreTileNotClosed(List<Tile> adjacentTiles) {
			Tile tileToFollow = null;
			
			for (Tile t : adjacentTiles) {
				if (t != null) {
					if (t == this.getTileSafe(this.targetX, this.targetY)) {
						return t;
					}
					else if (!this.closed.contains(t) 
							&& !t.isBlocked()
							&& (tileToFollow == null 
								|| (tileToFollow.getScore() == t.getScore() && t.getHeuristic() < tileToFollow.getHeuristic()) 
									|| (t.getScore() < tileToFollow.getScore()))) {
						tileToFollow = t;
					}
				
				 
				}
			}
			
			return tileToFollow;
		}

		private List<Tile> getCandidateAdjacentTiles(int fromX, int fromY) {
			List<Tile> candidateTiles = new ArrayList<Tile>();
			
			List<Tile> adjacenttiles = this.getAdjacentTiles(fromX, fromY);
			
			for (Tile t : adjacenttiles) {
				if (!this.closed.contains(t)) {
					candidateTiles.add(t);
				}
			}
			
			return candidateTiles;
			
		}
		private List<Tile> getAdjacentTiles(int fromX, int fromY) {
			List<Tile> tiles = new ArrayList<Tile>();
			
			// go one in each direction (+/- 1 square)
			
			Tile adjacentTile = null;
			
			//go up
			adjacentTile = this.getTileSafe(fromX, fromY - 1);
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			//go down
			adjacentTile = this.getTileSafe(fromX, fromY + 1);
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			
			//go left
			adjacentTile = this.getTileSafe(fromX - 1, fromY);
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			
			//go right
			adjacentTile = this.getTileSafe(fromX + 1, fromY);
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			
			return tiles;
		}
		
		private Tile getTileSafe(int x, int y) {
			if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
				return null;
			}
			else {
				return this.gameBoard.get(y).get(x);
			}
		}
		
		private void calculateHeuristicForTile(Tile tile) {
			tile.setHeuristic(Math.abs(this.targetX - tile.getX()) + Math.abs(this.targetY - tile.getY()));
		}

		private void calculateScoreFromPlayerForTile(Tile tile) {
			tile.setScoreFromPlayer(Math.abs(this.playerX - tile.getX()) + Math.abs(this.playerY - tile.getY()));
		}

//		private void calculateHeuristicAndScoreFromPlayerForTileAtCoOrd(int x, int y) {
//			Tile t = this.gameBoard.get(y).get(x);
//			
//			t.setHeuristic(Math.abs(this.targetX - x) + Math.abs(this.targetY - y));
//			t.setScoreFromPlayer(Math.abs(this.playerX - x) + Math.abs(this.playerY - y));
//		}
//		
//		public void calculateHeuristics() {
//			
//			for (int y = 0; y < this.gameBoard.length; y++) {
//				for (int x = 0; x < this.gameBoard[0].length; x++) {
//					Tile t = this.gameBoard[y][x];
//					
//					t.setHeuristic(Math.abs(this.targetX - x) + Math.abs(this.targetY - y));
//				}
//			}
//			
//		}
		public void setPlayerCoOrds(int playerX, int playerY) {
			this.setPlayerX(playerX);
			this.setPlayerY(playerY);
		}
		public List<List<Tile>> getGameBoard() {
			return gameBoard;
		}
		public void setGameBoard(List<List<Tile>> gameBoard) {
			this.gameBoard = gameBoard;
		}
		public Integer getPlayerX() {
			return playerX;
		}
		public void setPlayerX(Integer playerX) {
			this.playerX = playerX;
		}
		public Integer getPlayerY() {
			return playerY;
		}
		public void setPlayerY(Integer playerY) {
			this.playerY = playerY;
		}
		public Tile getTileForXandY(int x, int y) {
			return this.getTileSafe(x, y);
		}
		
		public boolean isTileInOpenList(Tile tile) {
			return this.isTileInList(tile, this.open);
		}
		public boolean isTileInClosedList(Tile tile) {
			return this.isTileInList(tile, this.closed);
		}
		private boolean isTileInList(Tile tile, List<Tile> list) {
			return list.contains(tile);
		}
		public boolean isPlayerInTile(int tileX, int tileY) {
			return (this.playerX == tileX && this.playerY == tileY);
		}
		public boolean isTargetTile(int tileX, int tileY) {
			return (this.targetX == tileX && this.targetY == tileY);
		}
		public void setTargetTileCoOrds(int targetX, int targetY) {
			this.targetX = targetX;
			this.targetY = targetY;
		}
		public List<Tile> getOpen() {
			return open;
		}
		public List<Tile> getClosed() {
			return closed;
		}
		public Integer getTargetX() {
			return targetX;
		}
		public Integer getTargetY() {
			return targetY;
		}
	}

	public class Tile {

		private int score = 0;
		private int scoreFromPlayer = 0;
		private int heuristic = 0;
		
		private boolean isBlocked = false;
		
		private int x;
		private int y;

		public Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean isBlocked() {
			return isBlocked;
		}

		public void setBlocked(boolean isBlocked) {
			this.isBlocked = isBlocked;
		}


		public int getScore() {
			return score;
		}

		public int getScoreFromStart() {
			return scoreFromPlayer;
		}

		public int getHeuristic() {
			return heuristic;
		}

		public void setScoreFromPlayer(int scoreFromPlayer) {
			this.scoreFromPlayer = scoreFromPlayer;

			this.calculateScore();
		}

		public void setHeuristic(int heuristic) {
			this.heuristic = heuristic;

			this.calculateScore();
		}

		private void calculateScore() {
			this.score = Math.abs(this.scoreFromPlayer + this.heuristic);
		}

		public int getScoreFromPlayer() {
			return scoreFromPlayer;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		@Override
		public String toString() {
			return String.format("{ X: %d, Y: %d, S: %d, P: %d, H: %d }", this.x, this.y, this.score, this.scoreFromPlayer, this.heuristic);
		}
		
		@Override
		public boolean equals(Object a) {
			return a.hashCode() == this.hashCode();
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.heuristic, this.isBlocked, this.scoreFromPlayer, this.scoreFromPlayer, this.x, this.y);
		}

	}
}

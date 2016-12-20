package sandpit;

import java.io.IOException;
import java.util.List;

public class AStarPathFinding {
	
	private final static int WIDTH = 6;
	private final static int HEIGHT = 6;
	private final static String RED = "\033[31m";
	private final static String GREEN = "\033[32m";
	private final static String YELLOW = "\033[33m";
	private final static String BLUE = "\033[34m";
	private final static String MAGENTA = "\033[35m";
	private final static String CYAN = "\033[36m";
	private final static String DEFAULT = "\033[0m";

	public void pathFinding() {

		GameBoard gameBoard = this.setupGameBoard();

		gameBoard.setPlayerCoOrds(4, 3);
		gameBoard.setGoalCoOrds(0, HEIGHT - 1);
		
		gameBoard.getTileSafe(1, 3).setBlocked(true);
		gameBoard.getTileSafe(3, 5).setBlocked(true);
		gameBoard.getTileSafe(5, 3).setBlocked(true);

		gameBoard.getTileSafe(1, 4).setBlocked(true);
		gameBoard.getTileSafe(3, 1).setBlocked(true);
		gameBoard.getTileSafe(1, 5).setBlocked(true);

		System.out.println(String.format("%sPLAYER %sOPEN_SET %sCLOSED_SET %sWALL %sGOAL%s", BLUE, GREEN, RED, CYAN, YELLOW, DEFAULT));
		
		System.out.println("The raw game board...");
		this.printGameBoard(gameBoard, null);

		List<Tile> pathGenerated = gameBoard.generatePathBetweenTiles(gameBoard.getPlayer(), gameBoard.getGoal());
		
		System.out.println(String.format("Now with a calculated path (in %sMAGENTA%s)...", MAGENTA, DEFAULT));
		this.printGameBoard(gameBoard, pathGenerated);

		System.out.println("Final route taken...");
		this.printPathTaken(pathGenerated);
		
		this.printNewLine();
	}

	private void printPathTaken(List<Tile> pathTaken) {
		
		if (pathTaken != null) {
			for (int x = pathTaken.size() - 1; x >= 0; x--) {
				Tile stepInRoute = pathTaken.get(x);

				System.out.print(String.format("%02d,%02d", stepInRoute.getX(), stepInRoute.getY()));
				
				if (x > 0) {
					System.out.print(" => ");
				}
			}
		}
		else {
			System.out.println("No path found");
		}
		
		this.printNewLine();
	}

	private GameBoard setupGameBoard() {
		GameBoard gameBoard = new GameBoard(WIDTH, HEIGHT);

		return gameBoard;
	}
	
	private void printGameBoard(GameBoard gameBoard, List<Tile> currentPath) {
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
				} else if (currentPath != null && currentPath.contains(tile)) {
					colour = MAGENTA;
				} else if (tile.isBlocked()) {
					colour = CYAN;
				} else if (gameBoard.isTileInOpenSet(tile)) {
					colour = GREEN;
				} else if (gameBoard.isTileInClosedSet(tile)) {
					colour = RED;
				} else if (gameBoard.isGoalTile(x, y)) {
					colour = YELLOW;
				}
				System.out.print(String.format("%s%s %02d, %02d, %02d %s%s", leftBorder, colour, tile.getGScore(),
						tile.getHScore(), tile.getFScore(), DEFAULT, rightBorder));
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

		aStar.pathFinding();
	}
}

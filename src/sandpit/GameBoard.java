package sandpit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameBoard {
		private List<List<Tile>> gameBoard;
		private Tile player;
		private Tile goal;
		private Set<Tile> openSet;
		private Set<Tile> closedSet;
		private int width = 0;
		private int height = 0;
		
		public GameBoard(int width, int height) {
			this.width = width;
			this.height = height;
			
			this.gameBoard = new ArrayList<List<Tile>>();
			
			for (int y = 0; y < height; y++) {
				this.gameBoard.add(y, new ArrayList<Tile>());
				
				for (int x = 0; x < width; x++) {
					this.gameBoard.get(y).add(x, new Tile(x, y));
				}
			}
			
			this.openSet = new HashSet<Tile>();
			this.closedSet = new HashSet<Tile>();
		}

		public boolean isGoalTile(int x, int y) {
			return this.goal.getX() == x && this.goal.getY() == y;
		}

		public boolean isPlayerInTile(int x, int y) {
			return this.player.getX() == x && this.player.getY() == y;
		}

		public List<Tile> generatePathBetweenTiles(Tile startTile, Tile goalTile) {
			
			// if no tile passsed in
			if (startTile == null || goalTile == null) {
				return null;
			}
			
			openSet.add(startTile);
			
			while(!openSet.isEmpty()) {
				
				Tile currentTileToExplore = this.getTileWithLowestFScore(openSet);
				
				if (currentTileToExplore.equals(goalTile)) {
					return this.generatePath(goalTile);
				}
				
				openSet.remove(currentTileToExplore);
				closedSet.add(currentTileToExplore);
				
				List<Tile> neighbouringTiles = this.getAdjacentTiles(currentTileToExplore);
				
				for(Tile neighbour : neighbouringTiles) {
					if (!neighbour.isBlocked() && !closedSet.contains(neighbour)) {
						
						if (!openSet.contains(neighbour)) {
							neighbour.setHScore(this.calculateHeuristicForTile(neighbour));
							neighbour.setGScore(currentTileToExplore.getGScore() + this.calculateDistanceBetweenTiles(currentTileToExplore, neighbour));
							neighbour.setParentInRoute(currentTileToExplore);
							
							openSet.add(neighbour);
						}
						else {
							int gScoreOfNeigbourFromHere = currentTileToExplore.getGScore() + this.calculateDistanceBetweenTiles(currentTileToExplore, neighbour);
							
							if (neighbour.getGScore() >= gScoreOfNeigbourFromHere) {
								neighbour.setParentInRoute(currentTileToExplore);
								neighbour.setGScore(gScoreOfNeigbourFromHere);
								
								openSet.add(neighbour);
							}
						}

					}
				}
			}
			
			return null;
		}
		
		private List<Tile> generatePath(Tile goalTile) {
			List<Tile> path = new ArrayList<Tile>();
			
			Tile currentStep = goalTile;
			
			path.add(currentStep);
			
			while(currentStep.getParentInRoute() != null) {
				Tile parent = currentStep.getParentInRoute();
				
				path.add(parent);
				currentStep = parent;
			}
			
			return path;
		}
		
		private Tile getTileWithLowestFScore(Set<Tile> setOfTiles) {
			if (setOfTiles == null || setOfTiles.isEmpty()) {
				return null;
			}
			else {
				Tile lowestFScoreTile = null;
				
				for(Tile tileToCheck : setOfTiles) {
					if (lowestFScoreTile == null) {
						lowestFScoreTile = tileToCheck;
					}
					else {
						if ((lowestFScoreTile.getFScore() > tileToCheck.getFScore())
								|| (lowestFScoreTile.getFScore() == tileToCheck.getFScore() 
									&& lowestFScoreTile.getHScore() > tileToCheck.getHScore())) {
							// if the F score of the iterated tile is better than found already
							// or it's the same but the heuristic is better
							
							lowestFScoreTile = tileToCheck;
						}
					}
				}
				
				return lowestFScoreTile;
			}
		}
		
		private List<Tile> getAdjacentTiles(Tile anchorTile) {
			List<Tile> tiles = new ArrayList<Tile>();
			
			// go one in each direction (+/- 1 square)
			
			Tile adjacentTile = null;
			
			//go up
			adjacentTile = this.getTileSafe(anchorTile.getX(), anchorTile.getY() - 1);
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			//go down
			adjacentTile = this.getTileSafe(anchorTile.getX(), anchorTile.getY() + 1);
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			
			//go left
			adjacentTile = this.getTileSafe(anchorTile.getX() - 1, anchorTile.getY());
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			
			//go right
			adjacentTile = this.getTileSafe(anchorTile.getX() + 1, anchorTile.getY());
			
			if (adjacentTile != null) {
				this.calculateHeuristicForTile(adjacentTile);
				this.calculateScoreFromPlayerForTile(adjacentTile);
				tiles.add(adjacentTile);
			}
			
			return tiles;
		}
		
		public Tile getTileSafe(int x, int y) {
			if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
				return null;
			}
			else {
				return this.gameBoard.get(y).get(x);
			}
		}
		private int calculateHeuristicForTile(Tile tile) {
			return this.calculateDistanceBetweenTiles(tile, this.goal);
		}
		private int calculateScoreFromPlayerForTile(Tile tile) {
			return this.calculateDistanceBetweenTiles(tile, this.player);
		}
		private int calculateDistanceBetweenTiles(Tile tileA, Tile tileB) {
			return Math.abs(tileA.getX() - tileB.getX()) + Math.abs(tileA.getY() - tileB.getY());
		}
		public void setPlayerCoOrds(int playerX, int playerY) {
			this.setPlayer(this.getTileSafe(playerX, playerY));
		}
		public void setGoalCoOrds(int goalX, int goalY) {
			this.setGoal(this.getTileSafe(goalX, goalY));
		}
		private void setGoal(Tile tile) {
			this.goal = tile;
		}
		public Tile getPlayer() {
			return player;
		}
		public void setPlayer(Tile player) {
			this.player = player;
		}
		public Tile getTileForXandY(int x, int y) {
			return this.getTileSafe(x, y);
		}
		public boolean isTileInOpenSet(Tile tile) {
			return this.openSet.contains(tile);
		}
		public boolean isTileInClosedSet(Tile tile) {
			return this.closedSet.contains(tile);
		}
		public Tile getGoal() {
			return goal;
		}
	}
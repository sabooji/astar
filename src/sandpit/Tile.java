package sandpit;

import java.util.Objects;

public class Tile {

		private int fScore = 0;
		private int gScore = 0;
		private int hScore = 0;
		
		private boolean isBlocked = false;
		
		private Tile parentInRoute;
		
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


		public int getFScore() {
			return fScore;
		}

		public int getHScore() {
			return hScore;
		}

		public void setGScore(int gScore) {
			this.gScore = gScore;

			this.calculateScore();
		}

		public void setHScore(int hScore) {
			this.hScore = hScore;

			this.calculateScore();
		}

		private void calculateScore() {
			this.fScore = Math.abs(this.gScore + this.hScore);
		}

		public int getGScore() {
			return gScore;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public Tile getParentInRoute() {
			return parentInRoute;
		}

		public void setParentInRoute(Tile parentInRoute) {
			this.parentInRoute = parentInRoute;
		}

		@Override
		public String toString() {
			return String.format("{ X: %d, Y: %d, S: %d, P: %d, H: %d }", this.x, this.y, this.fScore, this.gScore, this.hScore);
		}
		
		@Override
		public boolean equals(Object a) {
			return a.hashCode() == this.hashCode();
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.isBlocked, this.x, this.y);
		}

	}
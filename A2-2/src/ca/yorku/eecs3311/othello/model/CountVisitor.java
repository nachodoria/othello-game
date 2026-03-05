package ca.yorku.eecs3311.othello.model;

/**
 * Concrete Visitor for the Visitor Design Pattern.
 * Counts the number of tokens for a specific player on the board.
 */
public class CountVisitor implements Visitor {
	private char player;
	private int count = 0;

	public CountVisitor(char player) {
		this.player = player;
	}

	@Override
	public void visit(int row, int col, char token) {
		if (token == this.player) {
			this.count++;
		}
	}

	public int getCount() {
		return this.count;
	}
}

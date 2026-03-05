package ca.yorku.eecs3311.othello.model;

/**
 * Visitor interface for the Visitor Design Pattern.
 * Allows defining new operations on the OthelloBoard without changing its structure.
 */
public interface Visitor {
	public void visit(int row, int col, char token);
}

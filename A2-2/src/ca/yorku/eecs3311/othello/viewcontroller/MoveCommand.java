package ca.yorku.eecs3311.othello.viewcontroller;

import ca.yorku.eecs3311.othello.model.Othello;

/**
 * Concrete Command for the Command Design Pattern.
 * Encapsulates a move operation and its undo logic.
 * Stores a snapshot (memento) of the game state before the move to allow restoration.
 */
public class MoveCommand implements Command {
	private Othello othello;
	private int row, col;
	private Othello backup;

	public MoveCommand(Othello othello, int row, int col) {
		this.othello = othello;
		this.row = row;
		this.col = col;
	}

	@Override
	public void execute() {
		this.backup = this.othello.copy();
		this.othello.move(this.row, this.col);
	}

	@Override
	public void undo() {
		this.othello.restore(this.backup);
	}
}

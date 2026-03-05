package ca.yorku.eecs3311.othello.viewcontroller;

public interface Command {
	public void execute();
	public void undo();
}

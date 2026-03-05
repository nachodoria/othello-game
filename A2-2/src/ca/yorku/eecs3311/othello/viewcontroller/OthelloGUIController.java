package ca.yorku.eecs3311.othello.viewcontroller;

import ca.yorku.eecs3311.othello.model.*;
import java.io.*;
import java.util.Stack;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * The Controller in the MVC architecture.
 * Handles user interactions from the OthelloView and updates the Othello model.
 * 
 * Design Patterns:
 * - MVC: Acts as the Controller.
 * - Strategy: Manages Player strategies (Human, Random, Greedy) for P1 and P2.
 * - Command: Uses Command pattern (via MoveCommand and CommandManager logic) for Undo/Redo.
 */
public class OthelloGUIController {
    private Othello model;
    private OthelloView view;
    
    private Player player1;
    private Player player2;
    
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>(); // Optional, if we want redo
    
    private PauseTransition aiMovePause;

    public OthelloGUIController(Othello model) {
        this.model = model;
    }

    public void setView(OthelloView view) {
        this.view = view;
    }

    public void handleMove(int row, int col) {
        if (model.isGameOver()) return;

        // Check if it's human's turn
        char turn = model.getWhosTurn();
        Player currentPlayer = (turn == OthelloBoard.P1) ? player1 : player2;
        
        // If current player is NOT null (meaning it's a computer or specific strategy), 
        // we shouldn't allow manual board clicks to trigger moves, 
        // UNLESS we treat "Human" as null in player1/player2 fields.
        // Let's assume if playerX is null, it's Human.
        
        if (currentPlayer != null) {
            // It's computer's turn, ignore click
            return;
        }

        // Create command, execute it, and push to stack
        Command cmd = new MoveCommand(model, row, col);
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear(); // Clear redo stack on new move
        
        updateView();
        checkNextMove();
    }

    public void handleRestart() {
        if (aiMovePause != null) aiMovePause.stop();
        this.model = new Othello();
        this.undoStack.clear();
        this.redoStack.clear();
        view.setModel(this.model);
        
        // Initialize players based on selection
        String p1Sel = view.getP1Selection();
        String p2Sel = view.getP2Selection();
        
        player1 = createPlayer(p1Sel, OthelloBoard.P1);
        player2 = createPlayer(p2Sel, OthelloBoard.P2);
        
        updateView();
        checkNextMove();
    }
    
    public void handleUndo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
            updateView();
        }
    }
    
    public void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        File file = fileChooser.showSaveDialog(view.getRoot().getScene().getWindow());
        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        File file = fileChooser.showOpenDialog(view.getRoot().getScene().getWindow());
        if (file != null) {
            if (aiMovePause != null) aiMovePause.stop();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                this.model = (Othello) ois.readObject();
                view.setModel(this.model);
                undoStack.clear();
                redoStack.clear();
                
                // Re-initialize players with the new model
                String p1Sel = view.getP1Selection();
                String p2Sel = view.getP2Selection();
                player1 = createPlayer(p1Sel, OthelloBoard.P1);
                player2 = createPlayer(p2Sel, OthelloBoard.P2);
                
                updateView();
                checkNextMove();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Player createPlayer(String type, char player) {
        if ("Human".equals(type)) return null;
        if ("Random".equals(type)) return new PlayerRandom(model, player);
        if ("Greedy".equals(type)) return new PlayerGreedy(model, player);
        return null;
    }

    private void checkNextMove() {
        if (model.isGameOver()) {
            showGameOverAlert();
            return;
        }

        char turn = model.getWhosTurn();
        Player currentPlayer = (turn == OthelloBoard.P1) ? player1 : player2;

        if (currentPlayer != null) {
            // Computer move
            // Use PauseTransition to not block UI and give a delay
            aiMovePause = new PauseTransition(Duration.seconds(0.5));
            aiMovePause.setOnFinished(e -> {
                Move move = currentPlayer.getMove();
                if (move != null) {
                    // Execute computer move via Command
                    Command cmd = new MoveCommand(model, move.getRow(), move.getCol());
                    cmd.execute();
                    undoStack.push(cmd);
                    redoStack.clear();
                    
                    updateView();
                    checkNextMove(); // Recursive call for next turn
                } else {
                    // Should not happen if game is not over and player has moves
                    // But if no moves, model usually switches turn automatically?
                    // Othello.move() handles turn switching.
                    // If no moves available, Othello logic should handle it.
                    // But wait, Player.getMove() might return null if no moves?
                    // PlayerRandom returns a move from list.
                }
            });
            aiMovePause.play();
        }
    }
    
    private void updateView() {
        view.updateBoard();
        view.updateStatus();
    }

    private void showGameOverAlert() {
        char winner = model.getWinner();
        String message;
        if (winner == ' ') { 
            message = "It's a Draw!";
        } else {
            message = "Player " + (winner == OthelloBoard.P1 ? "1" : "2") + " Wins!";
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Platform.runLater(() -> alert.showAndWait());
    }
}

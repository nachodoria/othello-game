package ca.yorku.eecs3311.othello.viewcontroller;

import ca.yorku.eecs3311.othello.model.Othello;
import ca.yorku.eecs3311.othello.model.OthelloBoard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OthelloView {
    private Othello model;
    private OthelloGUIController controller;
    private BorderPane root;
    private GridPane boardGrid;
    private Label statusLabel;
    private Label p1ScoreLabel;
    private Label p2ScoreLabel;
    private ComboBox<String> p1Type;
    private ComboBox<String> p2Type;

    public OthelloView(Othello model, OthelloGUIController controller) {
        this.model = model;
        this.controller = controller;
        this.controller.setView(this);
        initUI();
    }

    public Parent getRoot() {
        return root;
    }
    
    public void setModel(Othello model) {
        this.model = model;
    }

    private void initUI() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: Status and Scores
        HBox topBox = new HBox(20);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        
        statusLabel = new Label();
        p1ScoreLabel = new Label();
        p2ScoreLabel = new Label();
        
        topBox.getChildren().addAll(statusLabel, p1ScoreLabel, p2ScoreLabel);
        root.setTop(topBox);

        // Center: Game Board
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        root.setCenter(boardGrid);

        // Bottom: Controls
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> controller.handleRestart());
        
        p1Type = new ComboBox<>();
        p1Type.getItems().addAll("Human", "Random", "Greedy");
        p1Type.setValue("Human");
        
        p2Type = new ComboBox<>();
        p2Type.getItems().addAll("Human", "Random", "Greedy");
        p2Type.setValue("Human");

        // Placeholders for other buttons (Undo, Save, Load)
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> controller.handleUndo());
        
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> controller.handleSave());
        
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> controller.handleLoad());
        
        bottomBox.getChildren().addAll(new Label("P1:"), p1Type, new Label("P2:"), p2Type, restartButton, undoButton, saveButton, loadButton);
        root.setBottom(bottomBox);

        updateBoard();
        updateStatus();
    }

    public void updateBoard() {
        boardGrid.getChildren().clear();
        for (int row = 0; row < Othello.DIMENSION; row++) {
            for (int col = 0; col < Othello.DIMENSION; col++) {
                Button cell = new Button();
                cell.setPrefSize(40, 40);
                
                char token = model.getToken(row, col);
                if (token == OthelloBoard.P1) {
                    cell.setStyle("-fx-background-color: black; -fx-background-radius: 20; -fx-min-width: 40px; -fx-min-height: 40px;");
                    cell.setText(""); 
                } else if (token == OthelloBoard.P2) {
                    cell.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 20; -fx-border-radius: 20; -fx-min-width: 40px; -fx-min-height: 40px;");
                    cell.setText(""); 
                } else {
                    cell.setStyle("-fx-background-color: green; -fx-border-color: darkgreen;");
                    cell.setText("");
                }

                final int r = row;
                final int c = col;
                cell.setOnAction(e -> controller.handleMove(r, c));
                
                boardGrid.add(cell, col, row);
            }
        }
    }

    public String getP1Selection() {
        return p1Type.getValue();
    }

    public String getP2Selection() {
        return p2Type.getValue();
    }

    public void updateStatus() {
        statusLabel.setText("Turn: " + (model.getWhosTurn() == OthelloBoard.P1 ? "Player 1 (Black)" : "Player 2 (White)"));
        p1ScoreLabel.setText("P1: " + model.getCount(OthelloBoard.P1));
        p2ScoreLabel.setText("P2: " + model.getCount(OthelloBoard.P2));
    }
}

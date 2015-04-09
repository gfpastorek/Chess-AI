package com.chess;

import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Greg Pastorek on 2/24/2015.
 */
public class Game {

    JFrame frame;
    GUI gui;
    Board board;
    boolean running;

    /* game state variables */
    int player1Score;
    int player2Score;
    String playerName[] = new String[2];
    int player_turn = 1;
    int highlighted_x, highlighted_y;
    boolean space_selected;

    Board previousBoard = null;

    public Game() {

    }

    public void setGUI(GUI gui_){
        gui = gui_;
    }

    public void setFrame(JFrame f){
        frame = f;
    }

    public void setPlayerNames(String player1Name_, String player2Name_){
        playerName[0] = player1Name_;
        playerName[1] = player2Name_;
        gui.setPlayerNames(playerName[0], playerName[1]);
    }

    /* check if any game ending conditions are met, and handle them appropriately */
    public boolean checkIfGameOver() throws Exception {

        if(board == null){
            return false;
        }

        if (board.isStalemate()) {
            gui.setStatusbar("Stalemate!");
            JOptionPane.showMessageDialog(frame, "Stalemate!");
            newGame();
            return true;
        }
        else if(board.isCheckmated(1)){
            giveVictory(2);
            newGame();
            return true;
        }
        else if (board.isCheckmated(2)) {
            giveVictory(1);
            newGame();
            return true;
        }

        return false;
    }

    /* victory for player 'player'. Updates scores and status bar */
    private void giveVictory(int player){
        if(player == 1) {
            player1Score++;
        } else {
            player2Score++;
        }
        gui.setScore(player1Score, player2Score);
        gui.setStatusbar(playerName[player-1] + " wins!");
    }

    /* clear game, wait for user to click 'start' */
    public void newGame(){
        gui.clearAllSpaces();
        board = null;  // delete board object
        running = false;
        gui.setStartButtonText("Start");
    }

    public boolean isRunning(){
        return running;
    }

    /* pop up prompt asking if user would like to restart. returns bool */
    private boolean promptUserForRestart(int player){
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(frame,
                playerName[player-1] + ", are you sure you want to restart this game?",
                "Restart Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        return n == 0;
    }

    /* makes the GUI match the board object */
    private void synchronizeGUIwithBoard() throws InvalidArgumentException {

        /* loop over entire board and fill spaces where there are pieces */
        for(int x = 0; x < board.getMaxX(); x++){
            for(int y = 0; y < board.getMaxY(); y++){
                Piece piece = board.getPiece(x, y);
                if(piece != null) {
                    gui.fillSpace(piece, x, y);
                }
            }
        }
    }

    /* pop up prompt asking if user would like to play classic chess or Chuck Norris Chess */
    /* returns true if they select classic chess                                           */
    private boolean promptGameMode(){
        Object[] options = {"Classic Chess", "Chuck Norris Chess"};
        int n = JOptionPane.showOptionDialog(frame,
                "Which game mode?",
                "Game Mode",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        return n == 0;
    }


    public class StartClickListener implements ActionListener {

        /* start's a new game. Does not reset the score. */
        public void actionPerformed(ActionEvent e){
            try
            {
                /* if we are restarting, prompt both users */
                if(running){
                    if(!(promptUserForRestart(1) && promptUserForRestart(2))){
                        return;
                    }
                }

                /* prompt user for game mode */
                boolean selectedClassicChess = promptGameMode();

                /* initialize board and GUI */
                gui.setStartButtonText("Restart");
                gui.clearAllSpaces();
                board = new Board(8,8);
                board.resetBoard(selectedClassicChess);
                running = true;

                synchronizeGUIwithBoard();

                gui.setStatusbar(playerName[player_turn-1] + "'s turn!");

            }
            catch (InvalidArgumentException e1)
            {
                e1.printStackTrace();
                throw new RuntimeException();
            }
        }
    }


    public class UndoClickListener implements ActionListener {

        /* Reset the game and the scores. */
        public void actionPerformed(ActionEvent e){

            if(previousBoard == null){
                return;
            }

            try {
                /* restore previous board and then update GUI to match */
                board = new Board(previousBoard);
                previousBoard = null;
                gui.clearAllSpaces();
                synchronizeGUIwithBoard();
                player_turn ^= 3;  //switch player turn between 1 and 2
                gui.setStatusbar(playerName[player_turn-1] + "'s turn!");
                gui.setUndoButtonEnabled(false);
            } catch (Exception e1) {
                e1.printStackTrace();
                throw new RuntimeException();
            }

        }
    }


    public class ForfeitClickListener implements ActionListener {

        /* Reset the game and the scores. */
        public void actionPerformed(ActionEvent e){
            /* give victory to opposing player, XOR with 3 inverts 1 and 2 */
            giveVictory(player_turn ^ 3);
            newGame();
        }
    }


    public class SpaceClickListener implements ActionListener {

        int loc_x, loc_y;

        public SpaceClickListener(int x, int y){
            loc_x = x;
            loc_y = y;
        }

        /* handler for clicking a space when no space is currently selected */
        /* highlights the space if all verifications pass                   */
        private void handleFirstClick(int loc_x, int loc_y){

            /* select a piece */
            Piece piece = board.getPiece(loc_x, loc_y);

            /* check if this space click was valid */
            if(piece == null){
                return;
            }

            /* make sure it is the player's turn */
            if(player_turn != piece.getPlayer()){
                return;
            }

            space_selected = true;
            highlighted_x = loc_x;
            highlighted_y = loc_y;

            gui.highlightSpace(highlighted_x, highlighted_y);
        }

        /* handler for clicking a space when another space is already highlighted */
        /* moves selected piece to space if all verifications pass                */
        private void handleSecondClick(int loc_x, int loc_y){

            /* piece was already select, select destination */
            space_selected = false;
            gui.unhighlightSpace(highlighted_x, highlighted_y);

            try {

                /* copy the board, if move succeeds store this is previousBoard for undo */
                Board potentialPreviousBoard = new Board(board);

                /* attempt to move piece, switch current player turn if the move was valid */
                int result = board.movePiece(highlighted_x, highlighted_y, loc_x, loc_y);

                switch(result) {

                    /* success - move the piece and switch turns, also checks for game ending conditions */
                    case 0:
                        gui.clearSpace(highlighted_x, highlighted_y);
                        gui.fillSpace(board.getPiece(loc_x, loc_y), loc_x, loc_y);
                        player_turn ^= 3;  //change player turn, bitwise XOR alternates between 1 and 2
                        gui.setStatusbar(playerName[player_turn-1] + "'s turn!");
                        checkIfGameOver();
                        previousBoard = potentialPreviousBoard;
                        gui.setUndoButtonEnabled(true);
                        break;

                    /* general invalid move */
                    case 1:
                        gui.setStatusbar("Invalid move");
                        break;

                    /* causes check */
                    case 2:
                        gui.setStatusbar("Invalid move: " + playerName[player_turn-1] + " would be in check!");
                        break;
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                throw new RuntimeException();
            }
        }

        /* Handle the space click */
        public void actionPerformed(ActionEvent e){

            if(!space_selected){
                handleFirstClick(loc_x, loc_y);
            } else{
                handleSecondClick(loc_x, loc_y);
            }
        }

    }



}

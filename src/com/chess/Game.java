package com.chess;

import OpeningLibrary.LibraryGraphBuilder;
import OpeningLibrary.OpeningGraph;
import OpeningLibrary.OpeningLibraryParser;
import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import ChessGUI.*;

/**
 * Created by Greg Pastorek on 2/24/2015.
 */
public class Game {

    final static int AI_MOVE_DELAY = 0;

    JFrame frame;
    ChessGUI gui;
    Board board;
    boolean running;
    boolean aiLock;

    int offset;
    int spacing;

    LinkedList<int []> movesQueue = new LinkedList<int []>();
    LinkedList<Boolean> responseQueue = new LinkedList<Boolean>();
    static final File libraryDirectory= new File(System.getProperty("user.dir"),"src\\OpeningLibrary" );
    OpeningGraph openingLibrary=null;

    /* game state variables */
    int player1Score;
    int player2Score;
    String playerName[] = new String[2];
    boolean playerIsAI[] = new boolean[2];
    ChessAI[] aiPlayers = new ChessAI[2];
    int player_turn = 1;
    int highlighted_x, highlighted_y;
    boolean space_selected;

    double[] evalWeights;
    boolean openingSequence=true;

    int last_winner = -1;

    int p1Difficulty = 1;
    int p2Difficulty = 1;

    Board previousBoard = null;
    Integer[] previousMove=null;

    public Game() {
        try {
            buildOpeningLibrary();
        }
        catch(Exception e){
            setOpeningLibrary(null);
        }

    }

    public Board getBoard() {
        return board;
    }

    public void setGUI(ChessGUI gui_){
        gui = gui_;
    }

    public void setFrame(JFrame f){
        frame = f;
    }

    public int getLastWinner() {
        return last_winner;
    }

    public void setAIDifficulty(int p1Difficulty_, int p2Difficulty_) {
        p1Difficulty = p1Difficulty_;
        p2Difficulty = p2Difficulty_;
    }

    public void setAiEvaluationWeights(double[] w) {
        evalWeights = w;
    }

    /* check if any game ending conditions are met, and handle them appropriately */
    public boolean checkIfGameOver() throws Exception {

        if(board == null){
            return false;
        }

        if (board.isDraw()) {
            if(gui != null) {
                gui.gameOver();
                //gui.setStatusbar("Draw!");
                JOptionPane.showMessageDialog(frame, "Draw!");
            }
            running=false;
            return true;
        }
        else if(board.isCheckmated(1)){
            giveVictory(2);
            if(gui != null)
                gui.gameOver();
            running=false;
            return true;
        }
        else if (board.isCheckmated(2)) {
            giveVictory(1);
            if(gui != null)
                gui.gameOver();
            running=false;
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
        last_winner = player;

        if(gui != null) {
            //gui.setScore(player1Score, player2Score);
            //gui.setStatusbar(playerName[player-1] + " wins!");
            JOptionPane.showMessageDialog(frame, playerName[player - 1] + " wins!");
        }
    }

    /* clear game, wait for user to click 'start' */
    public void newGame(int type) throws InvalidArgumentException {
        /*if(running){
            if(!(promptUserForRestart(1) && promptUserForRestart(2))){
                return;
            }
        }*/

        board = new Board(8,8);
        board.resetBoard();
        openingSequence=true;
        previousMove=null;

        if(gui != null)
            gui.updatePieces(board);

        running = true;
        player_turn=1;

        if(gui != null)
            gui.updatePieces(board);

        //gui.setStatusbar(playerName[player_turn-1] + "'s turn!");
        //TODO - refactor segment below into it's own method
        switch(type) {
            case 1:
                playerIsAI[0]=true;
                aiPlayers[0]= new ChessAI(p1Difficulty, board, board.player1Pieces, evalWeights, openingLibrary);
                playerIsAI[1]=false;
                break;
            case 2:
                playerIsAI[0]=false;
                playerIsAI[1]=false;
                break;
            case 3:
                playerIsAI[0]=true;
                aiPlayers[0]= new ChessAI(p1Difficulty, board, board.player1Pieces, evalWeights, openingLibrary);
                playerIsAI[1] = true;
                aiPlayers[1] = new ChessAI(p2Difficulty, board, board.player2Pieces, evalWeights, openingLibrary);
                break;
            case 4:
                playerIsAI[0]=true;
                aiPlayers[0]= new ChessAI(2, board, board.player1Pieces, evalWeights, openingLibrary);
                playerIsAI[1]=false;
                break;
        }
    }

    public boolean isRunning() {
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

    public void undoTrigger(){
            if(previousBoard == null){
                return;
            }
            try {
                /* restore previous board and then update GUI to match */
                board = new Board(previousBoard);
                previousBoard = null;
                gui.updatePieces(board);
                player_turn ^= 3;  //switch player turn between 1 and 2
            } catch (Exception e1) {
                e1.printStackTrace();
                throw new RuntimeException();
            }

    }
    public class SpaceClickListener implements ActionListener {

        int loc_x, loc_y;

        public SpaceClickListener(int x, int y){
            loc_x = x;
            loc_y = y;
        }

        /* Handle the space click */
        public void actionPerformed(ActionEvent e){
            /*
            if(!space_selected){
                handleFirstClick(loc_x, loc_y);
            } else{
                handleSecondClick(loc_x, loc_y);
            }*/
        }

    }

    /* AI polling mechanism, if the current player is an AI player, make an AI move */
    public void pollAI() throws Exception {
        if(playerIsAI[player_turn-1] && !aiLock){
            aiLock = true;
            try {
                makeAiMove(player_turn);
            } catch (Exception e) {
                e.printStackTrace();
            }
            aiLock = false;
        }
    }
    public void makeMove(){
        try {
            if (playerIsAI[player_turn - 1]) {
                pollAI();
            } else {
                Thread.sleep(50);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public int getPlayerTurn(){
        return player_turn;
    }


    /* make a move using the AI system for player 'player' */
    private void makeAiMove(int player) throws Exception {

        /* check for correct turn */
        if(player_turn != player) {
            throw new InvalidArgumentException(new String[] { "Wrong player turn." });
        }

        /* verify that the player is an AI player */
        if(!playerIsAI[player-1]) {
            throw new InvalidArgumentException(new String[] {"Player is not an AI player."});
        }

        /* retrieve the AI system */
        ChessAI ai = aiPlayers[player-1];

        if(ai == null){
            throw new IllegalStateException("Missing AI system.");
        }

        /* returns move as [src_x, src_y, dst_x, dst_y] */
        Integer move[]=null;
        if (openingSequence){
            move=ai.getMoveFromOpening(previousMove);
            if (move==null){
                openingSequence=false;
            }
        }
        if(!openingSequence) {
            move = ai.getMove();
        }
        if(move == null || move[0] == null || 0 != board.movePiece(move[0], move[1], move[2], move[3])) {
            move = ai.getMove(2);
            board.movePiece(move[0], move[1], move[2], move[3]);
        }
        previousMove= move;
        previousBoard = new Board(board);
        player_turn ^= 3;  //change player turn, bitwise XOR alternates between 1 and 2

        // TODO add this function
        //gui.setUndoButtonEnabled(true);

        /* make the move by activating the GUI */
        if(gui != null)
            gui.updatePieces(board);

    }
    public boolean moveReceiver(int [] possibleMove){
        int [] newMove=new int [4];
        newMove[0]= convertUnits(possibleMove[0], true);
        newMove[1]= convertUnits(possibleMove[1], true);
        newMove[2]= convertUnits(possibleMove[2], true);
        newMove[3]= convertUnits(possibleMove[3], true);
        movesQueue.add(newMove);
        handleInput();
        while(responseQueue.isEmpty()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } // sleep, my child
        return responseQueue.poll();
    }
    private int convertUnits(int oldUnits, boolean towards){
        if(towards) {
            return (oldUnits - offset) / spacing;
        }
        else{
            return (oldUnits*spacing)+offset;
        }
    }

    private void handleInput(){

        if (movesQueue.isEmpty()){
            return;
        }
        int [] curLoc= movesQueue.poll();
        int xInitial= curLoc[0];
        int yInitial= curLoc[1];
        int xEnd= curLoc[2];
        int yEnd= curLoc[3];
            /* select a piece */
        Piece piece = board.getPiece(xInitial, yInitial);

            /* check if this space click was valid */
        if(piece == null){
            return;
        }

            /* make sure it is the player's turn */
        if(player_turn != piece.getPlayer()){
            responseQueue.push(false);
            return;
        }
        try {

            /* copy the board, if move succeeds store this is previousBoard for undo */
            Board potentialPreviousBoard = new Board(board);

                /* attempt to move piece, switch current player turn if the move was valid */
            int result = board.movePiece(xInitial, yInitial, xEnd, yEnd);

            switch(result) {

                    /* success - move the piece and switch turns, also checks for game ending conditions */
                case 0:
                    player_turn ^= 3;  //change player turn, bitwise XOR alternates between 1 and 2
                    //gui.setStatusbar(playerName[player_turn-1] + "'s turn!");
                    previousBoard = potentialPreviousBoard;
                    previousMove= new Integer[]{xInitial, yInitial, xEnd, yEnd};
                    //gui.setUndoButtonEnabled(true);
                    responseQueue.push(true);
                    break;

                    /* general invalid move */
                case 1:
                    responseQueue.push(false);
                    //gui.setStatusbar("Invalid move");
                    break;

                    /* causes check */
                case 2:
                    responseQueue.push(false);
                    //gui.setStatusbar("Invalid move: " + playerName[player_turn-1] + " would be in check!");
                    break;
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        }
    }
    public void setPlayerNames(String player1Name_, String player2Name_){
        playerName[0] = player1Name_;
        playerName[1] = player2Name_;
        gui.setPlayerNames(player1Name_, player2Name_);
    }
    public int getPlayerScore(int Player){
        if (Player==1){
            return player1Score;
        }
        else{
            return player2Score;
        }

    }
    public  void buildOpeningLibrary() throws Exception{
        OpeningLibraryParser openingMoves= OpeningLibraryParser.getInstance();
        openingMoves.setDirectory(libraryDirectory);
        if(openingMoves.updateLibrary("http://www.chess.com/openings/")){
            openingMoves.parseOpeningGraph();
        }
        LibraryGraphBuilder graphBuilder= LibraryGraphBuilder.getInstance();
        graphBuilder.setDirectory(libraryDirectory);
        setOpeningLibrary(graphBuilder.buildGraphFromXML());
    }
    public void setOpeningLibrary(OpeningGraph library){
        openingLibrary= library;
    }
    public void getAIHelp() throws Exception{
        ChessAI helperAI;
        // gets the AI helper function
        if (player_turn==1) {
            helperAI = new ChessAI(2, board, board.player1Pieces, openingLibrary);
        }
        else{
            helperAI = new ChessAI(2, board, board.player2Pieces, openingLibrary);
        }
        Integer move[]=null;
        if (openingSequence){
                move=helperAI.getMoveFromOpening(previousMove);
                if (move==null){
                openingSequence=false;
            }
        }
        if(!openingSequence) {
            move = helperAI.getMove();
        }
        //highlights a move square
        gui.highlightHelperSquare(move);
    }
}

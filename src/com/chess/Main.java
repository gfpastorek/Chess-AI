package com.chess;

import com.chess.Game;
import com.chess.pieces.*;
import ChessGUI.*;
import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InvalidArgumentException {

        final Game game = new Game();
        final ChessGUI curGui= new ChessGUI();
        curGui.setModel(game);
        game.setGUI(curGui);
        /*game.running=true;
        game.board = new Board(8,8);
        game.board.resetBoard(true);
        game.playerIsAI[0] = true;
        game.aiPlayers[0] = new ChessAI(0, game.board.player1Pieces);
        game.playerIsAI[1] = false;
        curGui.updatePieces(game.board);
        */
        curGui.launchGUI();
        game.offset= curGui.getOffset();
        game.spacing= curGui.getSpacing();


        /*JFrame frame = new JFrame("Chess");
        frame.add(gui.getGui());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        frame.pack();

        // ensures the minimum size is enforced.
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);

        game.setFrame(frame);

        /* prompt user for player names*/
        String Player_one_name= curGui.getPlayerName(1);
        String Player_two_name= curGui.getPlayerName(2);
        game.setPlayerNames(Player_one_name, Player_two_name);
        Runnable r = new Runnable() {

            @Override
            public void run() {

                /* game loop */
                while(true){

                    /* wait for game to start */
                    while(!game.isRunning()){
                        try {
                            Thread.sleep(200);
                        } catch(InterruptedException e) {}
                    }

                    /* game is started, loop here */
                    while(game.isRunning()){
                        boolean gameOver=false;
                        try{
                            gameOver= game.checkIfGameOver();
                        }
                        catch (Exception e){

                        }
                        if (!gameOver) {
                            try {
                                Thread.sleep(10);
                                game.makeMove();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    //game.newGame();
                }

            }
        };

        r.run();

    }
}

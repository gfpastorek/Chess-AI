package com.chess;

import com.chess.Game;
import com.chess.pieces.*;
import ChessGUI.*;
import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {

        final Game game = new Game();
        final ChessGUI curGui= new ChessGUI();
        curGui.setModel(game);
        game.setGUI(curGui);

        game.setAIDifficulty(1, 0);

        curGui.launchGUI();
        game.offset= curGui.getOffset();
        game.spacing= curGui.getSpacing();

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

                    try {
                        /* game is started, loop here */
                        while (game.isRunning()) {
                            boolean gameOver = false;
                            gameOver = game.checkIfGameOver();
                            if (!gameOver) {
                                Thread.sleep(10);
                                game.makeMove();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        };

        r.run();

    }
}

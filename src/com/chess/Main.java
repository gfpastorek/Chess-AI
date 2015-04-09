package com.chess;

import com.chess.GUI;
import com.chess.Game;
import com.chess.pieces.*;
import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InvalidArgumentException {

        final Game game = new Game();
        final GUI gui = new GUI(8, 8, game);
        game.setGUI(gui);

        JFrame frame = new JFrame("Chess");
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

        /* prompt user for player names */
        String player1Name = JOptionPane.showInputDialog(frame, "Enter player 1's name.");
        String player2Name = JOptionPane.showInputDialog(frame, "Enter player 2's name.");
        game.setPlayerNames(player1Name, player2Name);

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
                        try {
                            Thread.sleep(200);
                        } catch(InterruptedException e) {}
                    }

                    game.newGame();
                }

            }
        };

        r.run();

    }
}

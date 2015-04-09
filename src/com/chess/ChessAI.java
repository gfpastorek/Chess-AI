package com.chess;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Greg Pastorek on 4/8/2015.
 */
public class ChessAI {

    private int player;
    private ArrayList<Piece> pieces;
    private int difficulty;

    public ChessAI(int difficulty_, int player_, ArrayList<Piece> pieces_) {
        player = player_;
        pieces = pieces_;
        difficulty = difficulty_;
    }

    public Integer[] getMove() throws Exception {


        switch(difficulty) {
            case 0:
                return getRandomMove();
            default:
                throw new InvalidArgumentException(new String[] { "Invalid difficulty value." });
        }

    }

    private Integer[] getRandomMove() throws Exception {

        /*  copy pieces list, shuffle order of pieces for random selection */
        ArrayList<Piece> potentialPieces = (ArrayList<Piece>) pieces.clone();
        Collections.shuffle(potentialPieces);

        while(!potentialPieces.isEmpty()) {
            Piece piece = potentialPieces.remove(0);
            List<Integer[]> moveSet = piece.validDestinationSet();
            if(!moveSet.isEmpty()) {
                Collections.shuffle(moveSet);
                return moveSet.remove(0);
            }
        }

        throw new IllegalStateException("No valid moves for AI player.");

    }



}

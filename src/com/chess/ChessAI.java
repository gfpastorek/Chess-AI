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

    private ArrayList<Piece> pieces;
    private int difficulty;

    public ChessAI(int difficulty_, ArrayList<Piece> pieces_) {
        pieces = pieces_;
        difficulty = difficulty_;
    }

    /* make an AI move, execution depends on the difficulty setting */
    /* returns a list of 4 numbers, src_x, src_y, dst_x, dst_y      */
    public Integer[] getMove() throws Exception {

        switch(difficulty) {
            case 0:
                return getRandomMove();
            default:
                throw new InvalidArgumentException(new String[] { "Invalid difficulty value." });
        }

    }

    /* random return a valid move for the AI             */
    /* output is a tuple of [src_x, src_y, dst_x, dst_y] */
    private Integer[] getRandomMove() throws Exception {

        /*  copy pieces list, shuffle order of pieces for random selection */
        ArrayList<Piece> potentialPieces = (ArrayList<Piece>) pieces.clone();
        Collections.shuffle(potentialPieces);

        /* select a random (valid) move from a random piece */
        while(!potentialPieces.isEmpty()) {
            Piece piece = potentialPieces.remove(0);
            List<Integer[]> moveSet = piece.validDestinationSet();
            if(!moveSet.isEmpty()) {
                Collections.shuffle(moveSet);
                return moveSet.remove(0);
            }
        }

        /* exception, checkmate should have occured */
        throw new IllegalStateException("No valid moves for AI player.");

    }



}

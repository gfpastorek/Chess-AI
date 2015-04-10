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
    private Board board;

    public ChessAI(int difficulty_, Board board_, ArrayList<Piece> pieces_) {
        pieces = pieces_;
        board = board_;
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

        return null;
    }

    /* rank moves from best to worst, look-ahead factor is 1 */
    private List<Integer[]> rankMoves(List<Integer[]> moveSet) throws Exception {

        //TODO - sort moves by score

        for(Integer[] move : moveSet) {
            Piece piece = board.getPiece(move[0], move[1]);
            Piece destPiece = board.getPiece(move[2], move[3]);
            boolean vulnerableAtSrc = board.canBeAttacked(piece);

            /* make copy of board to analyze 'what if' we moved piece to destination */
            Board testBoard = new Board(board);
            Piece testPiece = testBoard.getPiece(move[0], move[1]);
            testBoard.movePiece(move[0], move[1], move[2], move[3]);
            boolean vulnerableAtDst = testBoard.canBeAttacked(testPiece);

            int score = moveScore(piece, destPiece, vulnerableAtSrc, vulnerableAtDst);

        }

        return null;
    }

    /* return the numerical score (higher is better) of the move moving        */
    /* 'piece' to space 'destPiece' where vulnerableAtSrc and vulnerableAtDst  */
    /* describe 'piece''s vulnerability and the respective locations           */
    private int moveScore(Piece piece, Piece destPiece, boolean vulnerableAtSrc, boolean vulnerableAtDst) {
        int score = 0;
        if(vulnerableAtSrc){
            score -= PieceRank.getRank(piece);
        }
        if(vulnerableAtDst){
            score -= PieceRank.getRank(piece);
        }
        return PieceRank.getRank(destPiece) + score;
    }



}

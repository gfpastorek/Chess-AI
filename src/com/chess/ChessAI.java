package com.chess;

import com.chess.pieces.PawnPiece;
import com.sun.javaws.exceptions.InvalidArgumentException;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.*;

/**
 * Created by Greg Pastorek on 4/8/2015.
 */
public class ChessAI {

    private ArrayList<Piece> pieces;
    private int difficulty;
    private Board board;
    private int player;

    public ChessAI(int difficulty_, Board board_, ArrayList<Piece> pieces_) throws InvalidArgumentException {
        pieces = pieces_;
        board = board_;
        difficulty = difficulty_;

        if(!pieces.isEmpty()){
            player = pieces.get(0).getPlayer();
        } else {
            throw new InvalidArgumentException(new String[] { "Pieces are missing. "});
        }

    }

    /* make an AI move, execution depends on the difficulty setting */
    /* returns a list of 4 numbers, src_x, src_y, dst_x, dst_y      */
    public Integer[] getMove() throws Exception {

        switch(difficulty) {
            case 0:
                return getRandomMove();
            case 1:
                return getOptimalMove(1);
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

        if(!board.isCheckmated(player)) {
            System.out.println("the fuck?");
        }

        throw new InvalidStateException("No valid moves, but not checkmate!");

    }



    /* return the optimal move with lookahead of 1       */
    /* output is a tuple of [src_x, src_y, dst_x, dst_y] */
    private Integer[] getOptimalMove(int depth) throws Exception {

        return getOptimalMove(depth, player, board);

    }

    /* return the optimal move with lookahead of 1       */
    /* output is a tuple of [src_x, src_y, dst_x, dst_y] */
    private Integer[] getOptimalMove(int depth, int player, Board board) throws Exception {

        List<Integer[]> moveSet = getMoveSet(board.getPieces(player));

        SortedSet sortedMoves = rankMoves(moveSet, depth, player, board);

        Map.Entry<Integer[], Integer> bestEntry = (Map.Entry<Integer[], Integer>) sortedMoves.first();

        return bestEntry.getKey();

    }

    /* return the list of all valid moves for this player */
    private List<Integer[]> getMoveSet(List<Piece> pieces_) throws Exception {

        List<Integer[]> moveSet = new ArrayList<Integer[]>();

        /* select a random (valid) move from a random piece */
        for(Piece piece : pieces_) {
            moveSet.addAll(piece.validDestinationSet());
        }

        Collections.shuffle(moveSet);

        return moveSet;

    }


    /* rank moves from best to worst, look-ahead factor is 1 */
    private SortedSet rankMoves(List<Integer[]> moveSet, int depth, int player, Board board) throws Exception {

        HashMap<Integer[], Integer> moveScores = new HashMap<Integer[], Integer>();

        SortedSet sortedMoves = new TreeSet<Map.Entry<Integer[], Integer>>(new MoveComparator());

        for(Integer[] move : moveSet) {
            int score = moveScore(move, depth, player, board);
            moveScores.put(move, score);
        }

        sortedMoves.addAll(moveScores.entrySet());

        return sortedMoves;

    }

    private class MoveComparator implements Comparator<Map.Entry<Integer[], Integer>> {

        @Override
        public int compare(Map.Entry<Integer[], Integer> o1, Map.Entry<Integer[], Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /* return the numerical score (higher is better) of the move moving        */
    /* 'piece' to space 'destPiece' where vulnerableAtSrc and vulnerableAtDst  */
    /* describe 'piece''s vulnerability and the respective locations           */
    private int moveScore(Integer[] move, int depth, int player, Board board) throws Exception {

        /* make copy of board to analyze 'what if' we moved piece to destination */
        Board testBoard = new Board(board);
        testBoard.movePiece(move[0], move[1], move[2], move[3]);

        while(depth > 0) {
            player ^= 3;
            Integer[] oppMove = getOptimalMove(--depth, player, testBoard);
            testBoard.movePiece(oppMove[0], oppMove[1], oppMove[2], oppMove[3]);
        }

        return  scoreBoard(testBoard) - scoreBoard(board);

    }




    /*
    f(p) = 200(K-K')
            + 9(Q-Q')
            + 5(R-R')
            + 3(B-B' + N-N')
            + 1(P-P')
            - 0.5(D-D' + S-S' + I-I')
            + 0.1(M-M') + ...

    KQRBNP = number of kings, queens, rooks, bishops, knights and pawns
    D,S,I = doubled, blocked and isolated pawns
    M = Mobility (the number of legal moves)
    */
    private int scoreBoard(Board board) throws Exception {
        return scoreBoard(board, player) - scoreBoard(board, player ^ 3);
    }


    /* give the score of the current board for player 'player' */
    private int scoreBoard(Board board, int player) throws Exception {

        int score = 0;

        for(Piece examinedPiece : board.getPieces(player)) {
            if(!examinedPiece.isCaptured()) {
                score += 10*PieceRank.getRank(examinedPiece);
                score += examinedPiece.validDestinationSet().size();  //TODO - optimize this, causing slowdown
                if(examinedPiece.getClass() == PawnPiece.class) {
                    score -= 5*PieceRank.pawnIsDoubled((PawnPiece)examinedPiece, board);
                    score -= 5*PieceRank.pawnIsIsolated((PawnPiece)examinedPiece, board);
                    score -= 5*PieceRank.pawnIsBlocked((PawnPiece)examinedPiece);
                }
            }

        }

        return score;

    }



}

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

    final static int NUM_MOVES_THRESHOLD = 10;
    final static int PRUNING_SCORE = -1;

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
                return getOptimalMove(2);
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

    /* return the optimal move with lookahead of depth       */
    /* output is a tuple of [src_x, src_y, dst_x, dst_y]     */
    private Integer[] getOptimalMove(int depth, int player, Board board) throws Exception {

        Map.Entry<Integer[], Integer> bestEntry = getOptimalMoveEntry(depth, player, board);

        return bestEntry.getKey();
    }

    /* return the optimal move score with lookahead of depth       */
    /* output is an integer                                        */
    private Integer getOptimalMoveScore(int depth, int player, Board board) throws Exception {

        Map.Entry<Integer[], Integer> bestEntry = getOptimalMoveEntry(depth, player, board);

        /* checkmate was found, score -inf */
        if(bestEntry == null) {
            return -1000;
        }

        return bestEntry.getValue();
    }


    /* get the optimal entry<move, score> with lookahead of depth */
    /* returns null if no move found, signaling checkmate         */
    private Map.Entry<Integer[], Integer> getOptimalMoveEntry(int depth, int player, Board board) throws Exception {

        List<Integer[]> moveSet = getMoveSet(board.getPieces(player), depth);

        /* check if checkmate was found */
        if(moveSet.isEmpty()) {
            return null;
        }

        int numMoves = moveSet.size();

        SortedSet sortedMoves = rankMoves(moveSet, depth, player, board, numMoves);

        Map.Entry<Integer[], Integer> bestEntry = (Map.Entry<Integer[], Integer>) sortedMoves.first();

        return bestEntry;
    }

    /* return the list of all valid moves for this player */
    private List<Integer[]> getMoveSet(List<Piece> pieces_, int depth) throws Exception {

        List<Integer[]> moveSet = Collections.synchronizedList(new ArrayList<Integer[]>());

        /* select a random (valid) move from a random piece */
        for (Piece piece : pieces_) {
            moveSet.addAll(piece.validDestinationSet());
        }

        Collections.shuffle(moveSet);

        return moveSet;

    }


    /* rank moves from best to worst, look-ahead factor is 1 */
    private SortedSet rankMoves(List<Integer[]> moveSet, int depth, int player, Board board, int numMoves) throws Exception {

        Map<Integer[], Integer> moveScores = Collections.synchronizedMap(new HashMap<Integer[], Integer>());

        SortedSet sortedMoves = new TreeSet<Map.Entry<Integer[], Integer>>(new MoveComparator());

        if(depth > 1) {
            moveSet.parallelStream().forEach((move) -> {
                try {
                    int score = moveScore(move, depth, player, board, numMoves);
                    moveScores.put(move, score);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            for(Integer[] move : moveSet) {
                int score = moveScore(move, depth, player, board, numMoves);
                moveScores.put(move, score);
            }
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
    private int moveScore(Integer[] move, int depth, int player, Board board, int numMoves) throws Exception {

        /* make copy of board to analyze 'what if' we moved piece to destination */
        Board testBoard = new Board(board);
        testBoard.movePiece(move[0], move[1], move[2], move[3]);

        int score = scoreBoard(testBoard, player) - scoreBoard(board, player);

        int reponseScore = 0;

        if(--depth > 0 && (numMoves < NUM_MOVES_THRESHOLD || score > PRUNING_SCORE)) {
            reponseScore = getOptimalMoveScore(depth, player^3, testBoard);
        }

        return  score - reponseScore;

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
    private int scoreBoard(Board board, int player) throws Exception {
        return scoreBoardForPlayer(board, player) - scoreBoardForPlayer(board, player ^ 3);
    }


    /* give the score of the current board for player 'player' */
    private int scoreBoardForPlayer(Board board, int player) throws Exception {

        int score = 0;

        for(Piece examinedPiece : board.getPieces(player)) {
            if(!examinedPiece.isCaptured()) {
                score += 10*PieceRank.getRank(examinedPiece);
                score += examinedPiece.validDestinationSet().size();
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

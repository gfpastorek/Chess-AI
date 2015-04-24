package com.chess;

import com.chess.pieces.PawnPiece;
import com.sun.javaws.exceptions.InvalidArgumentException;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

/**
 * Created by Greg Pastorek on 4/8/2015.
 */
public class ChessAI {

    private ArrayList<Piece> pieces;
    private int difficulty;
    private Board board;
    private int player;

    final static int NUM_MOVES_THRESHOLD = 10;
    final static int PRUNING_SCORE = -30;

    double[] weights = {
            10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0,
            10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5,
            -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0,
            -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5
    };

    //greg
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

    //greg
    public ChessAI(int difficulty_, Board board_, ArrayList<Piece> pieces_, double[] weights_) throws InvalidArgumentException {
        pieces = pieces_;
        board = board_;
        difficulty = difficulty_;
        weights = weights_;

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
            case 2:
                return getMoveFromMinimax(2);
            case 3:
                return getMoveFromMinimax(3);
            case 4:
                return getMoveFromMinimax(4);
            case 5:
                return getMoveFromMinimax(5);
            case 6:
                return getOptimalMove(3);
            default:
                throw new InvalidArgumentException(new String[] { "Invalid difficulty value." });
        }

    }

    /* random return a valid move for the AI             */
    /* output is a tuple of [src_x, src_y, dst_x, dst_y] */
    //greg
    private Integer[] getRandomMove() throws Exception {

        /*  copy pieces list, shuffle order of pieces for random selection */
        ArrayList<Piece> potentialPieces = (ArrayList<Piece>) pieces.clone();
        Collections.shuffle(potentialPieces);

        /* select a random (valid) move from a random piece */
        while(!potentialPieces.isEmpty()) {
            Piece piece = potentialPieces.remove(0);
            List<Integer[]> moveSet = piece.validDestinationSet(false);
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
    //greg
    private Integer[] getOptimalMove(int depth) throws Exception {

        return getOptimalMove(depth, player, board);
    }

    /* return the optimal move with lookahead of depth       */
    /* output is a tuple of [src_x, src_y, dst_x, dst_y]     */
    //greg
    private Integer[] getOptimalMove(int depth, int player, Board board) throws Exception {

        Map.Entry<Integer[], Integer> bestEntry = getOptimalMoveEntry(depth, player, board);

        return bestEntry.getKey();
    }

    /* return the optimal move score with lookahead of depth       */
    /* output is an integer                                        */
    //greg
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
    //greg
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
    //greg
    private List<Integer[]> getMoveSet(List<Piece> pieces_, int depth) throws Exception {

        List<Integer[]> moveSet = Collections.synchronizedList(new ArrayList<Integer[]>());

        /* select a random (valid) move from a random piece */
        for (Piece piece : pieces_) {
            moveSet.addAll(piece.validDestinationSet(false));
        }

        Collections.shuffle(moveSet);

        return moveSet;

    }

    //yuriy
    private List<BoardAndMove> generateFrontier(Board startState, int currentPlayer) throws Exception {
        List<BoardAndMove> frontier= new ArrayList<BoardAndMove>();
        List<Piece> pieces_= startState.getPieces(currentPlayer);
        for (Piece piece : pieces_) {
            List<Integer[]> possibleMoves= new ArrayList<Integer[]>();

            possibleMoves = piece.validDestinationSet(false);

            for(Integer[] possibleMove: possibleMoves){
                Board possibleState = startState;
                possibleState = new Board(startState);
                possibleState.movePiece(possibleMove[0], possibleMove[1], possibleMove[2], possibleMove[3]);
                frontier.add(new BoardAndMove(possibleState,possibleMove));
            }
        }
        return frontier;
    }


    /* rank moves from best to worst, look-ahead factor is 1 */
    //greg
    private SortedSet rankMoves(List<Integer[]> moveSet, int depth, int player, Board board, int numMoves) throws Exception {

        Map<Integer[], Integer> moveScores = new HashMap<Integer[], Integer>();

        SortedSet sortedMoves = new TreeSet<Map.Entry<Integer[], Integer>>(new MoveComparator());

        /* compute score for each move */
        for(Integer[] move : moveSet) {
            int score = moveScore(move, depth, player, board, numMoves);
            moveScores.put(move, score);
        }

        /* sorts the moves by score */
        sortedMoves.addAll(moveScores.entrySet());

        return sortedMoves;

    }

    //greg
    private class MoveComparator implements Comparator<Map.Entry<Integer[], Integer>> {

        @Override
        public int compare(Map.Entry<Integer[], Integer> o1, Map.Entry<Integer[], Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /* return the numerical score (higher is better) of the move moving        */
    /* 'piece' to space 'destPiece' where vulnerableAtSrc and vulnerableAtDst  */
    /* describe 'piece''s vulnerability and the respective locations           */
    //greg
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
    //greg
    private int scoreBoard(Board board, int player) throws Exception {
        return scoreBoardForPlayer(board, player) - scoreBoardForPlayer(board, player ^ 3);
    }

    /* give the score of the current board for player 'player' */
    //greg
    private int scoreBoardForPlayer(Board board, int player) throws Exception {

        /* Heavily parallelized code for determining the score of a player */
        List<Piece> pieces = board.getPieces(player);
        List<Integer> scores = Collections.synchronizedList(new ArrayList<Integer>());

        pieces.parallelStream().forEach((examinedPiece) -> {
            try {
                /* ignore capture pieces, for each piece add its contribution to score */
                if (!examinedPiece.isCaptured()) {
                    int score = 0;
                    int possible_moves = examinedPiece.validDestinationSet(true).size();
                    score += 10 * PieceRank.getRank(examinedPiece);
                    score += possible_moves;
                    if (examinedPiece.getClass() == PawnPiece.class) {
                        score -= 5 * PieceRank.pawnIsDoubled((PawnPiece) examinedPiece, board);
                        score -= 5 * PieceRank.pawnIsIsolated((PawnPiece) examinedPiece, board);
                        score -= 5 * ((possible_moves == 0) ? 1 : 0);
                    }
                    scores.add(score);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        return sum(scores);

    }

    /* give the score of the current board for player 'player' */
    //greg
    private double scoreBoard(Board board) throws Exception {

        /* Heavily parallelized code for determining the score of a player */
        List<Piece> pieces = board.getPieces(player);
        pieces.addAll(board.getPieces(player ^ 3));

        List<?> scores =
                IntStream.range(0, pieces.size()).mapToObj(i -> {
                    try {
                        Piece examinedPiece = pieces.get(i);
                        double score = 0;

                        score += weights[4*i] * PieceRank.getRank(examinedPiece);
                        score += weights[4*i+1] * examinedPiece.validDestinationSet(true).size();

                        if (examinedPiece.getClass() == PawnPiece.class && !examinedPiece.isCaptured()) {
                            score += weights[4*i+2] * PieceRank.pawnIsDoubled((PawnPiece) examinedPiece, board);
                            score += weights[4*i+3] * PieceRank.pawnIsIsolated((PawnPiece) examinedPiece, board);
                        }

                        return score;

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return 0;
                    }
                }).collect(toList());

        return sumd((List<Double>)scores);

    }



    //yuriy
    private Integer[] getMoveFromMinimax(int depth) throws Exception {
        Integer [] bestMove= new Integer [4];
        MiniMax(board, depth, 1, player, -Integer.MAX_VALUE, Integer.MAX_VALUE, bestMove);
        return bestMove;
    }

    //yuriy
    private double MiniMax(Board boardState, int depth, int Maximizing, int currentPlayer, double alpha, double beta, Integer[] Move) throws Exception {

        // Switch the next action
        int nextAction=1;
        if (Maximizing==1) {
            nextAction = 0;
        }

        //Switch the next Player
        int playerNode=currentPlayer^3;
        if (Maximizing==1){
            playerNode=currentPlayer;
        }

        Board currentState = new Board(boardState);

        if (depth == 0 || currentState.checkEndState()) {
            //if we are at a leaf, we return the heuristic value
            try {
                return scoreBoard(currentState, player, weights);
            } catch (Exception e) {
                return 0;
            }
        }
        double bestValue;
        //otherwise we generate a frontier
        List<BoardAndMove> futureStates= generateFrontier(currentState, playerNode);
        if (Maximizing==1) {
            bestValue = -Double.MAX_VALUE;
            for (BoardAndMove state : futureStates) {
                double attemptValue = MiniMax(state.getBoard(), depth - 1, nextAction, currentPlayer, alpha, beta, new Integer[4]);
                if (attemptValue > bestValue) {
                    //if its better than the value we have so far, we update it
                    Integer[] bestMove= state.getMove();
                    Move[0]=bestMove[0];
                    Move[1]=bestMove[1];
                    Move[2]=bestMove[2];
                    Move[3]=bestMove[3];
                    bestValue = attemptValue;
                }
                alpha= Math.max(alpha, bestValue);
                if (beta<=alpha) {
                    break;
                }
            }
        }
        else {
            bestValue = Integer.MAX_VALUE;
            for (BoardAndMove state : futureStates) {
                double attemptValue = MiniMax(state.getBoard(), depth - 1, nextAction,currentPlayer,alpha, beta,  new Integer[4]);
                if (attemptValue < bestValue) {
                    //if its worse than the value we have so far, we update it
                    Integer[] bestMove= state.getMove();
                    Move[0]=bestMove[0];
                    Move[1]=bestMove[1];
                    Move[2]=bestMove[2];
                    Move[3]=bestMove[3];
                    bestValue = attemptValue;
                }
                beta = Math.min(beta, bestValue);
                if (beta<=alpha) {
                    break;
                }
            }
        }
        return bestValue;
    }

    private class BoardAndMove{
        private Board board;
        private Integer [] move;
        public BoardAndMove(Board new_board,Integer[] new_move){
            board= new_board;
            move= new_move;
        }
        public Board getBoard(){
            return board;
        }
        public Integer[] getMove(){
            return move;
        }
    }


    public static int sum(List<Integer> list) {
        int sum = 0;
        for (int i:list)
            sum += i;
        return sum;
    }

    //greg
    public static double sumd(List list) {
        double sum = 0.0;
        for (Object i: list) {
            try {
                sum += (double) ((Number)i).floatValue();
            } catch (Exception e) {
                System.out.println("FUCK");
            }
        }
        return sum;
    }


    /* give the score of the current board for player 'player' */
    //greg
    public static double scoreBoard(Board board, int player, double[] weights) throws Exception {

        /* Heavily parallelized code for determining the score of a player */
        List<Piece> pieces = new ArrayList<Piece>();
        pieces.addAll(board.getPieces(player));
        pieces.addAll(board.getPieces(player ^ 3));

        List<?> scores =
                IntStream.range(0, pieces.size()).mapToObj(i -> {
                    try {
                        Piece examinedPiece = pieces.get(i);
                        double score = 0;

                        score += weights[4*i] * PieceRank.getRank(examinedPiece);

                        if(weights[4*i+1] != 0)
                            score += weights[4*i+1] * examinedPiece.validDestinationSet(true).size();

                        if (examinedPiece.getClass() == PawnPiece.class && !examinedPiece.isCaptured()) {
                            score += weights[4*i+2] * PieceRank.pawnIsDoubled((PawnPiece) examinedPiece, board);
                            score += weights[4*i+3] * PieceRank.pawnIsIsolated((PawnPiece) examinedPiece, board);
                        }

                        return score;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }).collect(toList());

        return sumd(scores);

    }


}

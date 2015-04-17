package com.chess.pieces;

import com.chess.Board;
import com.chess.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public class RookPiece extends Piece {
    boolean movedRook=false;

    public RookPiece(Board parent, int player_){
        super(parent, player_);
    }

    public RookPiece(RookPiece other, Board parent){
        super(other, parent);
    }

    /* checks if a move is valid, DOES NOT check if check is caused */
    public boolean isValidMove(int src_x, int src_y, int dest_x, int dest_y){

        /* check boundaries on destination and check if src != dest */
        if(!isValidDestination(src_x, src_y, dest_x, dest_y)){
            return false;
        }

        /* get values (1, -1, or 0) of the direction to travel in each coordinate */
        int dir_x = Integer.signum(dest_x - src_x);
        int dir_y = Integer.signum(dest_y - src_y);

        /* check that we only move in one direction - dir_x*dir_y should be 0 */
        if(dir_x*dir_y != 0){
            return false;
        }
        /*if (castlingWorks()){
            return true;
        }*/
        /* iterate through the spaces in the path and check that we are not blocked */
        while(src_x != dest_x || src_y != dest_y){

            /* move piece one unit */
            src_x += dir_x;
            src_y += dir_y;

            /* check the space for a piece */
            Piece space = board.getPiece(src_x, src_y);
            if(space != null){
                /* check if we are blocked */
                /* return false if we are not at the destination and saw a piece */
                if(src_x != dest_x || src_y != dest_y){
                    return false;
                }
                /* return false if the destination contains one of our own pieces*/
                else if (space.getPlayer() == player){
                    return false;
                }
            }
        }

        return true;
    }


    /* retrieve the set of all valid moves for this piece as a tuple of [src_x, src_y, dst_x, dst_y] */
    protected List<Integer[]> retrieveValidDestinationSet(boolean allowCheck) throws Exception {

        List<Integer[]> validMoves = new ArrayList<Integer[]>();

        for(int n = 1; n < 8; n++) {
            addMoveIfValid(validMoves, loc_x + n, loc_y    , allowCheck);
            addMoveIfValid(validMoves, loc_x - n, loc_y    , allowCheck);
            addMoveIfValid(validMoves, loc_x,     loc_y + n, allowCheck);
            addMoveIfValid(validMoves, loc_x,     loc_y - n, allowCheck);
        }

        return validMoves;

    }
    protected boolean castlingWorks(){
        //TODO implement castling
        if(board.hasCastled(player)){
            return false;
        }
        int curRow=1;
        if (player==2){
            curRow=8;
        }
        if (loc_x==1 && loc_y==curRow && board.getPiece(4,curRow).getClass()==KingPiece.class && board.getPiece(3,curRow)==null &&
                board.getPiece(2,curRow)==null){
            if (!hasMoved && !board.getPiece(4,curRow).getMoved()){
                try {
                    Board testAttacked = new Board(board);
                    PawnPiece attackTest1 = new PawnPiece(board, player);
                    PawnPiece attackTest2 = new PawnPiece(board, player);
                    testAttacked.addPiece(attackTest1, 2, curRow);
                    testAttacked.addPiece(attackTest2, 3, curRow);
                    try {
                        if (!(testAttacked.canBeAttacked(attackTest1) || testAttacked.canBeAttacked(attackTest2) || testAttacked.canBeAttacked(this))) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
        if (loc_x==8 && loc_y==curRow && board.getPiece(4,curRow).getClass()==KingPiece.class && board.getPiece(5,curRow)==null &&
                board.getPiece(6,curRow)==null && board.getPiece(7,curRow)==null){
            if (!hasMoved && !board.getPiece(4,curRow).getMoved()){
                try {
                    Board testAttacked = new Board(board);
                    PawnPiece attackTest1 = new PawnPiece(board, player);
                    PawnPiece attackTest2 = new PawnPiece(board, player);
                    PawnPiece attackTest3 = new PawnPiece(board, player);
                    testAttacked.addPiece(attackTest1, 5, curRow);
                    testAttacked.addPiece(attackTest2, 6, curRow);
                    testAttacked.addPiece(attackTest3, 7, curRow);
                    try {
                        if (!(testAttacked.canBeAttacked(attackTest1) || testAttacked.canBeAttacked(attackTest2) ||testAttacked.canBeAttacked(attackTest3) || testAttacked.canBeAttacked(this))) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
        return false;
    }

}

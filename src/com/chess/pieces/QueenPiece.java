package com.chess.pieces;

import com.chess.Board;
import com.chess.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public class QueenPiece extends Piece {

    public QueenPiece(Board parent, int player_){
        super(parent, player_);
    }

    public QueenPiece(QueenPiece other, Board parent){
        super(other, parent);
    }

    public QueenPiece(PawnPiece other, Board parent){
        super(other, parent);
    }

    /* checks if a move is valid, DOES NOT check if check is caused */
    public boolean isValidMove(int src_x, int src_y, int dest_x, int dest_y){

        /* check boundaries on destination and check if src != dest */
        if(!isValidDestination(src_x, src_y, dest_x, dest_y)){
            return false;
        }


        /* get movement values, x and y distances */
        int move_x = dest_x - src_x;
        int move_y = dest_y - src_y;

        /* get values (1, -1, or 0) of the direction to travel in each coordinate */
        int dir_x = Integer.signum(move_x);
        int dir_y = Integer.signum(move_y);

        /* check that we move either all in our direction, or diagonally */
        if((Math.abs(move_x) != Math.abs(move_y)) && (move_x*move_y != 0)){
            return false;
        }

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
                /* return false if we try capturing a king */
                else if (space.getClass() == KingPiece.class){
                    return false;
                }
            }
        }

        return true;
    }


    /* retrieve the set of all valid moves for this piece as a tuple of [src_x, src_y, dst_x, dst_y] */
    protected List<Integer[]> retrieveValidDestinationSet() throws Exception {

        List<Integer[]> validMoves = new ArrayList<Integer[]>();

        for(int n = 1; n < 8; n++) {
            addMoveIfValid(validMoves, loc_x + n, loc_y + n);
            addMoveIfValid(validMoves, loc_x + n, loc_y - n);
            addMoveIfValid(validMoves, loc_x - n, loc_y + n);
            addMoveIfValid(validMoves, loc_x - n, loc_y - n);
            addMoveIfValid(validMoves, loc_x + n, loc_y    );
            addMoveIfValid(validMoves, loc_x - n, loc_y    );
            addMoveIfValid(validMoves, loc_x,     loc_y + n);
            addMoveIfValid(validMoves, loc_x,     loc_y - n);
        }

        return validMoves;

    }

}
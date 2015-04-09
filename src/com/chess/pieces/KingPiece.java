package com.chess.pieces;

import com.chess.Board;
import com.chess.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public class KingPiece extends Piece {

    public KingPiece(Board parent, int player_){
        super(parent, player_);
    }

    public KingPiece(KingPiece other, Board parent){
        super(other, parent);
    }

    public boolean isValidMove(int src_x, int src_y, int dest_x, int dest_y){

        /* check boundaries on destination and check if src != dest */
        if(!isValidDestination(src_x, src_y, dest_x, dest_y)){
            return false;
        }

        /* get values (1, -1, or 0) of the direction to travel in each coordinate */
        int dir_x = Integer.signum(dest_x - src_x);
        int dir_y = Integer.signum(dest_y - src_y);

        /* verify that this is a one step move */
        if(Math.abs(dest_x - src_x) != 1 && Math.abs(dest_y - src_y) != 1){
            return false;
        }

        /* move piece one unit */
        src_x += dir_x;
        src_y += dir_y;

        /* return false if we are not at the destination and saw a piece */
        if(src_x != dest_x || src_y != dest_y){
            return false;
        }

        /* check the space for a piece */
        Piece space = board.getPiece(src_x, src_y);

        if(space != null){
            /* check if we are blocked */
            /* return false if the destination contains one of our own pieces*/
            if (space.getPlayer() == player){
                return false;
            }
            /* return false if we try capturing a king */
            else if (space.getClass() == KingPiece.class){
                return false;
            }
        }


        return true;
    }


    /* retrieve the set of all valid moves for this piece as a tuple of [src_x, src_y, dst_x, dst_y] */
    protected List<Integer[]> retrieveValidDestinationSet() throws Exception {

        List<Integer[]> validMoves = new ArrayList<Integer[]>();

        addMoveIfValid(validMoves, loc_x + 1, loc_y + 1);
        addMoveIfValid(validMoves, loc_x + 1, loc_y - 1);
        addMoveIfValid(validMoves, loc_x - 1, loc_y + 1);
        addMoveIfValid(validMoves, loc_x - 1, loc_y - 1);
        addMoveIfValid(validMoves, loc_x + 1, loc_y    );
        addMoveIfValid(validMoves, loc_x - 1, loc_y    );
        addMoveIfValid(validMoves, loc_x,     loc_y + 1);
        addMoveIfValid(validMoves, loc_x,     loc_y - 1);

        return validMoves;

    }

}
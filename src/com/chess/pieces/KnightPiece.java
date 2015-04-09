package com.chess.pieces;

import com.chess.Board;
import com.chess.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public class KnightPiece extends Piece {

    public KnightPiece(Board parent, int player_){
        super(parent, player_);
    }

    public KnightPiece(KnightPiece other, Board parent){
        super(other, parent);
    }

    /* checks if a move is valid, DOES NOT check if check is caused */
    public boolean isValidMove(int src_x, int src_y, int dest_x, int dest_y){

        /* check boundaries on destination and check if src != dest */
        if(!isValidDestination(src_x, src_y, dest_x, dest_y)){
            return false;
        }

        /* get coordinates of direction */
        int dir_x = dest_x - src_x;
        int dir_y = dest_y - src_y;

        /* knight move must be 2 in one direction and 1 in another */
        if( (Math.abs(dir_x) != 1 && Math.abs(dir_x) != 2) ||
            (Math.abs(dir_y) != 1 && Math.abs(dir_y) != 2) ||
            (Math.abs(dir_y) == Math.abs(dir_x))              ){
            return false;
        }

        /* get piece at destination space */
        Piece space = board.getPiece(dest_x, dest_y);

        if(space != null){
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


    protected List<Integer[]> retrieveValidDestinationSet() throws Exception {

        List<Integer[]> validMoves = new ArrayList<Integer[]>();

        addMoveIfValid(validMoves, loc_x + 2, loc_y + 1);
        addMoveIfValid(validMoves, loc_x + 2, loc_y - 1);
        addMoveIfValid(validMoves, loc_x - 2, loc_y + 1);
        addMoveIfValid(validMoves, loc_x - 2, loc_y - 1);
        addMoveIfValid(validMoves, loc_x + 1, loc_y + 2);
        addMoveIfValid(validMoves, loc_x + 1, loc_y - 2);
        addMoveIfValid(validMoves, loc_x - 1, loc_y + 2);
        addMoveIfValid(validMoves, loc_x - 1, loc_y - 2);

        return validMoves;

    }


}
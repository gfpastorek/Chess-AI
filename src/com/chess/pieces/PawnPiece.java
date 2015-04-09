package com.chess.pieces;

import com.chess.Board;
import com.chess.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public class PawnPiece extends Piece {



    public PawnPiece(Board parent, int player_){
        super(parent, player_);
    }

    public PawnPiece(PawnPiece other, Board parent){
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

        /* check if move is invalid formation */
        if(Math.abs(dir_y) > 2 || Math.abs(dir_x) > 1){
            return false;
        }

        /* pawn can only move two spaces on first move, no blockage, and it is not a capture */
        if(Math.abs(dir_y) == 2 &&
                (hasMoved || Math.abs(dir_x) > 0 || board.getPiece(dest_x, (int)(dest_y - Math.signum(dir_y))) != null)){
            return false;
        }

        /* players can only move away from start in y direction */
        if(player == 1 && dir_y < 0 || player == 2 && dir_y > 0){
            return false;
        }

        /* get piece at destination space */
        Piece space = board.getPiece(dest_x, dest_y);

        /* vertical move, no capture */
        if(dir_x == 0){
            /* must be empty */
            if(space != null){
                return false;
            }
        }
        /* diagonal capture move */
        else {
            /* diagonal move must be a capture */
            if(space == null){
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

        return true;
    }


    protected List<Integer[]> retrieveValidDestinationSet() throws Exception {

        List<Integer[]> validMoves = new ArrayList<Integer[]>();

        /* maps 2->-1 and 1->1 */
        int dir = 3 - 2*player;

        addMoveIfValid(validMoves, loc_x + 1, loc_y + dir);
        addMoveIfValid(validMoves, loc_x - 1, loc_y + dir);
        addMoveIfValid(validMoves, loc_x,     loc_y + dir);
        addMoveIfValid(validMoves, loc_x,     loc_y + 2*dir);

        return validMoves;

    }

}

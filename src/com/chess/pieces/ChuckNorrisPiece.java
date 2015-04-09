package com.chess.pieces;
/**
 * Created by Greg Pastorek on 2/17/2015.
 */

import com.chess.Board;
import com.chess.Piece;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public class ChuckNorrisPiece extends Piece {

    public ChuckNorrisPiece(Board parent, int player_){
        super(parent, player_);
    }

    public ChuckNorrisPiece(ChuckNorrisPiece other, Board parent){
        super(other, parent);
    }

    /* checks if a move is valid, DOES NOT check if check is caused */
    public boolean isValidMove(int src_x, int src_y, int dest_x, int dest_y){

        /* check boundaries on destination and check if src != dest */
        if(!isValidDestination(src_x, src_y, dest_x, dest_y)){
            return false;
        }

        /* check that we only jump 3 spaces at most */
        if(Math.abs(dest_x - src_x) > 2 || Math.abs(dest_y - src_y) > 2){
            return false;
        }

        /* check the space for a piece */
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

        return validMoves;

    }

}

package com.chess;


import java.util.List;

/**
 * Created by Greg Pastorek on 2/6/2015.
 */
public abstract class Piece {

    protected Board board;
    protected int player;
    protected boolean captured;
    protected int loc_x, loc_y;
    protected boolean hasMoved;

    public Piece(Board parent, int player_){
        board = parent;
        player = player_;
        captured = false;
        hasMoved = false;
    }

    public Piece(Piece other, Board parent){
        board = other.board;
        player = other.player;
        captured = other.captured;
        hasMoved = other.hasMoved;
        loc_x = other.loc_x;
        loc_y = other.loc_y;
        board = parent;
    }

    public abstract boolean isValidMove(int src_x, int src_y, int dest_x, int dest_y);

    public abstract List<Integer[]> validDestinationSet() throws Exception;

    /* return int 1 or 2, corresponding to controlling player */
    public int getPlayer(){
        return player;
    }

    /* check if piece was already captured */
    public boolean isCaptured() {
        return captured;
    }

    /* set captured flag of piece */
    public void setCaptured(boolean value) {
        captured = value;
    }

    /* set hasMoved flag */
    public void setHasMoved(boolean value){
        hasMoved = value;
    }

    /* set the location of the coordinates contained in this class */
    public void setLocation(int loc_x, int loc_y){
        this.loc_x = loc_x;
        this.loc_y = loc_y;
    }

    public int getLocX(){
        return loc_x;
    }

    public int getLocY(){
        return loc_y;
    }

    /* checks if destination is in bounds and that the src and dest are different */
    /* helper function to the extending implementations of isValidMove            */
    protected boolean isValidDestination(int src_x, int src_y, int dest_x, int dest_y){

        /* check that dest space chosen is in the bounds of the board */
        if((dest_x < 0) || (dest_y < 0) || (dest_x >= board.getMaxX()) || (dest_y >= board.getMaxY())){
            return false;
        }

        /* check that the src and dest spaces are different */
        if((dest_x == src_x) && (dest_y == src_y)){
            return false;
        }

        return true;
    }

    /* add a move n spaces in dir_x and dir_y to the validMoves set */
    protected void addMoveIfValid(List<Integer[]> validMoves, int dst_x, int dst_y) throws Exception {
        if(isValidMove(loc_x, loc_y, dst_x, dst_y)
                && board.causesCheck(this, dst_x, dst_y)) {
            validMoves.add(new Integer[]{loc_x, loc_y, dst_x, dst_y});
        }
    }

}

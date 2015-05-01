package OpeningLibrary;

import com.chess.pieces.*;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Yuriy on 4/23/2015.
 */
public class MoveNode{
    //move node in the opening moves graph
    // unique identifier
    private String identifier;
    // end position
    private int dest_x;
    private int dest_y;
    // piece type
    private Class piece;
    // piece location hint in case of ambiguity
    private int pieceLocHint;
    // all possible following moves
    ArrayList<MoveNode> nextMoves;
    public MoveNode(Node curNode){
        nextMoves=new ArrayList<MoveNode>();
        //converts XML node to MoveNode
        convertToMoveNode(curNode);
    }
    public void addNextMove(MoveNode next){
        nextMoves.add(next);
    }
    public void convertToMoveNode(Node curNode){
        //sets the identifier
        identifier= curNode.getAttributes().item(0).getNodeValue();
        int moveLength= identifier.length();
        //different ways to parse the algebraic notation based on its length
        if (moveLength<2){
            return;
        }
        else if (moveLength==2){
            setPieceClass('P');
            setCoordinates(identifier);
        }
        else if (moveLength==3){
            setPieceClass(identifier.charAt(0));
            setCoordinates(identifier.substring(1));
        }
        else if (moveLength==4){
            if (identifier.charAt(1)=='x'){
                if(identifier.charAt(0)>= 'a' && identifier.charAt(0) <= 'z'){
                    setPieceClass('P');
                }
                else{
                    setPieceClass(identifier.charAt(0));
                }
            }
            else{
                setPieceClass(identifier.charAt(0));
                setHintLoc(identifier.charAt(1));
            }
            setCoordinates(identifier.substring(2));
        }
        else{
            setPieceClass(identifier.charAt(0));
            setHintLoc(identifier.charAt(1));
            setCoordinates(identifier.substring(3));
        }
    }
    public void setCoordinates(String pos){
        // sets the coordinate from alphanumeric notation
        dest_x= 7-(pos.charAt(0)-'a');
        dest_y= Character.getNumericValue(pos.charAt(1))-1;
    }
    public void setHintLoc(char loc){
        // sets the location hint
        pieceLocHint= 7-(loc-'a');
    }
    public Integer[] getNextMove(){
        // gets the next move for this node as an integer
        Integer [] nextMove= new Integer[2];
        nextMove[0]=dest_x;
        nextMove[1]=dest_y;
        return nextMove;
    }
    public MoveNode getRandomNextMoveNode(){
        // gets a random move that can follow this movenode
        MoveNode randomOpeningMove=null;

        Random randomGenerator = new Random();
        int nextMoveSize= nextMoves.size();
        if(nextMoveSize>0) {
            int randomMove = randomGenerator.nextInt(nextMoveSize);
            randomOpeningMove = nextMoves.get(randomMove);
        }
        return randomOpeningMove;
    }
    public Class getPieceClass(){
        return piece;
    }
    public int getPieceLocHint(){
        return pieceLocHint;
    }
    public void setPieceClass(char curpiece){
        switch(curpiece){
            case 'P':
                piece=PawnPiece.class;
                break;
            case 'N':
                piece=KnightPiece.class;
                break;
            case 'Q':
                piece=QueenPiece.class;
                break;
            case 'K':
                piece=KingPiece.class;
                break;
            case 'B':
                piece=BishopPiece.class;
                break;
            case 'R':
                piece=RookPiece.class;
                break;
        }
    }
    public int getX(){
        return dest_x;
    }
    public int getY(){
        return dest_y;
    }
    public String getIdentifier(){
        return identifier;
    }
    public MoveNode findNextMove(Integer[] location){
        // checks if a location follows this movenode
        MoveNode findMove=null;
        for(MoveNode nextMove:nextMoves){
            if((nextMove.getX()==location[2])&&(nextMove.getY()==location[3])){
                findMove=nextMove;
                break;
            }
        }
        return findMove;
    }

}

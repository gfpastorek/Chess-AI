package tests;

import com.chess.Board;
import com.chess.Piece;
import com.chess.pieces.KingPiece;
import com.chess.pieces.QueenPiece;
import org.junit.Test;

import static org.junit.Assert.*;

public class KingPieceTest {

    @Test
    public void testIsValidMoveCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new KingPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new QueenPiece(testBoard, 2), 4, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 6, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 6, 5);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 5, 4);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 5, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 6, 5);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 5, 4);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 4, 5);

        assertEquals("King can capture NW", true, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("King can capture NE", true, testPiece.isValidMove(5, 5, 6, 6));
        assertEquals("King can capture SE", true, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("King can capture SW", true, testPiece.isValidMove(5, 5, 4, 4));
        assertEquals("King can capture N", true, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("King can capture E", true, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("King can capture S", true, testPiece.isValidMove(5, 5, 5, 4));
        assertEquals("King can capture W", true, testPiece.isValidMove(5, 5, 4, 5));
    }

    @Test
    public void testIsValidMoveNoCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new KingPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        assertEquals("King can move NW", true, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("King can move NE", true, testPiece.isValidMove(5, 5, 6, 6));
        assertEquals("King can move SE", true, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("King can move SW", true, testPiece.isValidMove(5, 5, 4, 4));
        assertEquals("King can move N", true, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("King can move E", true, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("King can move S", true, testPiece.isValidMove(5, 5, 5, 4));
        assertEquals("King can move W", true, testPiece.isValidMove(5, 5, 4, 5));
    }

    @Test
    public void testIsValidMoveBlocked() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new KingPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new QueenPiece(testBoard, 1), 4, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 6, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 4, 4);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 6, 4);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 5, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 6, 5);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 4, 5);
        testBoard.addPiece(new KingPiece(testBoard, 2), 5, 4);

        assertEquals("King cannot stay put", false, testPiece.isValidMove(5, 5, 5, 5));
        assertEquals("King is blocked by own NW", false, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("King is blocked by own NE", false, testPiece.isValidMove(5, 5, 6, 6));
        assertEquals("King is blocked by own SE", false, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("King is blocked by king SW", false, testPiece.isValidMove(5, 5, 4, 4));
        assertEquals("King is blocked by own N", false, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("King is blocked by own E", false, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("King is blocked by own S", false, testPiece.isValidMove(5, 5, 4, 5));
        assertEquals("King is blocked by King W", false, testPiece.isValidMove(5, 5, 5, 4));
    }

}
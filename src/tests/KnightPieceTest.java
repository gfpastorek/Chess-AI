package tests;

import com.chess.Board;
import com.chess.Piece;
import com.chess.pieces.KingPiece;
import com.chess.pieces.KnightPiece;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnightPieceTest {

    @Test
    public void testIsValidMoveCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new KnightPiece(testBoard, 2);
        testBoard.addPiece(testPiece, 4, 4);

        testBoard.addPiece(new KnightPiece(testBoard, 1), 6, 5);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 6, 3);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 2, 5);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 2, 3);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 3, 6);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 4, 6);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 3, 2);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 4, 2);

        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 6, 5));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 6, 3));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 2, 5));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 2, 3));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 3, 6));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 5, 6));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 3, 2));
        assertEquals("Knight can capture", true, testPiece.isValidMove(4, 4, 5, 2));
    }

    @Test
    public void testIsValidMoveNoCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new KnightPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 4, 4);

        testBoard.addPiece(new KnightPiece(testBoard, 1), 5, 5);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 5, 3);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 3, 5);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 3, 3);

        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 6, 5));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 6, 3));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 2, 5));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 2, 3));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 3, 6));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 5, 6));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 3, 2));
        assertEquals("Knight can move", true, testPiece.isValidMove(4, 4, 5, 2));
    }

    @Test
    public void testIsValidMoveBlocked() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new KnightPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 4, 4);

        testBoard.addPiece(new KnightPiece(testBoard, 1), 6, 5);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 6, 3);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 2, 5);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 2, 3);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 3, 6);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 5, 6);
        testBoard.addPiece(new KnightPiece(testBoard, 1), 3, 2);
        testBoard.addPiece(new KingPiece(testBoard, 2), 5, 2);

        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 6, 5));
        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 6, 3));
        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 2, 5));
        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 2, 3));
        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 3, 6));
        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 5, 6));
        assertEquals("Knight is blocked", false, testPiece.isValidMove(4, 4, 3, 2));
        assertEquals("Knight cannot stay put", false, testPiece.isValidMove(4, 4, 4, 4));
        assertEquals("Knight blocked by King", false, testPiece.isValidMove(4, 4, 5, 2));
    }
    
}
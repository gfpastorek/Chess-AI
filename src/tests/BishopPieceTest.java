package tests;

import com.chess.pieces.BishopPiece;
import com.chess.pieces.KingPiece;
import org.junit.Test;
import com.chess.Board;
import com.chess.Piece;

import static org.junit.Assert.*;

public class BishopPieceTest {

    @Test
    public void testIsValidMoveCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new BishopPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new BishopPiece(testBoard, 2), 1, 1);
        testBoard.addPiece(new BishopPiece(testBoard, 2), 7, 7);
        testBoard.addPiece(new BishopPiece(testBoard, 2), 4, 6);
        testBoard.addPiece(new BishopPiece(testBoard, 2), 6, 4);

        assertEquals("Bishop can capture NW", true, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("Bishop can capture NE", true, testPiece.isValidMove(5, 5, 7, 7));
        assertEquals("Bishop can capture SE", true, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("Bishop can capture SW", true, testPiece.isValidMove(5, 5, 1, 1));
    }

    @Test
    public void testIsValidMoveNoCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new BishopPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        assertEquals("Bishop can move NW", true, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("Bishop can move NE", true, testPiece.isValidMove(5, 5, 7, 7));
        assertEquals("Bishop can move SE", true, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("Bishop can move SW", true, testPiece.isValidMove(5, 5, 1, 1));
    }

    @Test
    public void testIsValidMoveBlocked() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new BishopPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new BishopPiece(testBoard, 1), 1, 1);
        testBoard.addPiece(new BishopPiece(testBoard, 1), 7, 7);
        testBoard.addPiece(new BishopPiece(testBoard, 1), 4, 6);
        testBoard.addPiece(new KingPiece(testBoard, 2), 6, 4);

        assertEquals("Bishop cannot stay put", false, testPiece.isValidMove(5, 5, 5, 5));
        assertEquals("Bishop is blocked by own NW", false, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("Bishop is blocked by own NE", false, testPiece.isValidMove(5, 5, 7, 7));
        assertEquals("Bishop is blocked by own SE", false, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("Bishop is blocked by king SW", false, testPiece.isValidMove(5, 5, 1, 1));
    }
}
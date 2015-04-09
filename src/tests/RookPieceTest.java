package tests;

import com.chess.Board;
import com.chess.Piece;
import com.chess.pieces.KingPiece;
import com.chess.pieces.RookPiece;
import org.junit.Test;

import static org.junit.Assert.*;

public class RookPieceTest {

    @Test
    public void testIsValidMoveCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new RookPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new RookPiece(testBoard, 2), 5, 6);
        testBoard.addPiece(new RookPiece(testBoard, 2), 6, 5);
        testBoard.addPiece(new RookPiece(testBoard, 2), 5, 1);
        testBoard.addPiece(new RookPiece(testBoard, 2), 1, 5);

        assertEquals("Rook can capture N", true, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("Rook can capture E", true, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("Rook can capture S", true, testPiece.isValidMove(5, 5, 5, 1));
        assertEquals("Rook can capture W", true, testPiece.isValidMove(5, 5, 1, 5));
    }

    @Test
    public void testIsValidMoveNoCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new RookPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        assertEquals("Rook can move N", true, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("Rook can move E", true, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("Rook can move S", true, testPiece.isValidMove(5, 5, 5, 1));
        assertEquals("Rook can move W", true, testPiece.isValidMove(5, 5, 1, 5));
    }

    @Test
    public void testIsValidMoveBlocked() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new RookPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new RookPiece(testBoard, 1), 5, 6);
        testBoard.addPiece(new RookPiece(testBoard, 1), 6, 5);
        testBoard.addPiece(new RookPiece(testBoard, 1), 5, 1);
        testBoard.addPiece(new KingPiece(testBoard, 2), 1, 5);

        assertEquals("Rook cannot stay put", false, testPiece.isValidMove(5, 5, 5, 5));
        assertEquals("Rook is blocked by own N", false, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("Rook is blocked by own E", false, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("Rook is blocked by own S", false, testPiece.isValidMove(5, 5, 5, 1));
        assertEquals("Rook is blocked by King", false, testPiece.isValidMove(5, 5, 1, 5));
    }

}
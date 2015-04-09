package tests;

import com.chess.Board;
import com.chess.Piece;
import com.chess.pieces.KingPiece;
import com.chess.pieces.KnightPiece;
import com.chess.pieces.QueenPiece;
import com.chess.pieces.ChuckNorrisPiece;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChuckNorrisPieceTest {

    @Test
    public void testIsValidMoveCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new ChuckNorrisPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new ChuckNorrisPiece(testBoard, 2), 1, 1);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 7, 7);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 4, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 6, 4);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 5, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 6, 5);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 5, 1);
        testBoard.addPiece(new QueenPiece(testBoard, 2), 1, 5);

        assertEquals("Queen can capture NW", true, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("Queen can capture NE", true, testPiece.isValidMove(5, 5, 7, 7));
        assertEquals("Queen can capture SE", true, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("Queen can capture N", true, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("Queen can capture E", true, testPiece.isValidMove(5, 5, 6, 5));
    }

    @Test
    public void testIsValidMoveNoCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece = new ChuckNorrisPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        assertEquals("Queen can move NW", true, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("Queen can move NE", true, testPiece.isValidMove(5, 5, 7, 7));
        assertEquals("Queen can move SE", true, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("Queen can move N", true, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("Queen can move E", true, testPiece.isValidMove(5, 5, 6, 5));

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
        Piece testPiece = new ChuckNorrisPiece(testBoard, 1);
        testBoard.addPiece(testPiece, 5, 5);

        testBoard.addPiece(new QueenPiece(testBoard, 1), 1, 1);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 7, 7);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 4, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 5, 6);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 6, 5);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 5, 1);
        testBoard.addPiece(new QueenPiece(testBoard, 1), 1, 5);
        testBoard.addPiece(new KingPiece(testBoard, 2), 6, 4);

        assertEquals("Queen cannot stay put", false, testPiece.isValidMove(5, 5, 5, 5));
        assertEquals("Queen is blocked by own NW", false, testPiece.isValidMove(5, 5, 4, 6));
        assertEquals("Queen is blocked by own NE", false, testPiece.isValidMove(5, 5, 7, 7));
        assertEquals("Queen is blocked by own SE", false, testPiece.isValidMove(5, 5, 6, 4));
        assertEquals("Queen is blocked by king SW", false, testPiece.isValidMove(5, 5, 1, 1));
        assertEquals("Queen is blocked by own N", false, testPiece.isValidMove(5, 5, 5, 6));
        assertEquals("Queen is blocked by own E", false, testPiece.isValidMove(5, 5, 6, 5));
        assertEquals("Queen is blocked by own S", false, testPiece.isValidMove(5, 5, 5, 1));
        assertEquals("Queen is blocked by own W", false, testPiece.isValidMove(5, 5, 1, 5));
    }

}
package tests;

import com.chess.Board;
import com.chess.Piece;
import com.chess.pieces.KingPiece;
import com.chess.pieces.PawnPiece;
import org.junit.Test;

import static org.junit.Assert.*;

public class PawnPieceTest {

    @Test
    public void testIsValidMoveCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece1 = new PawnPiece(testBoard, 1);
        Piece testPiece2 = new PawnPiece(testBoard, 2);
        testBoard.addPiece(testPiece1, 1, 1);
        testBoard.addPiece(testPiece2, 6, 6);

        testBoard.addPiece(new PawnPiece(testBoard, 2), 0, 2);
        testBoard.addPiece(new PawnPiece(testBoard, 2), 2, 2);
        testBoard.addPiece(new PawnPiece(testBoard, 1), 5, 5);
        testBoard.addPiece(new PawnPiece(testBoard, 1), 7, 5);

        assertEquals("Player 1 Pawn can capture NW", true, testPiece1.isValidMove(1, 1, 0, 2));
        assertEquals("Player 1 Pawn can capture NE", true, testPiece1.isValidMove(1, 1, 2, 2));
        assertEquals("Player 2 Pawn can capture SW", true, testPiece2.isValidMove(6, 6, 5, 5));
        assertEquals("Player 2 Pawn can capture SE", true, testPiece2.isValidMove(6, 6, 7, 5));
    }

    @Test
    public void testIsValidMoveNoCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece1 = new PawnPiece(testBoard, 1);
        Piece testPiece2 = new PawnPiece(testBoard, 2);
        testBoard.addPiece(testPiece1, 1, 1);
        testBoard.addPiece(testPiece2, 6, 6);

        assertEquals("Player 1 Pawn can move N", true, testPiece1.isValidMove(1, 1, 1, 2));
        assertEquals("Player 2 Pawn can move S", true, testPiece2.isValidMove(6, 6, 6, 5));
    }

    @Test
    public void testIsValidMoveBlocked() throws Exception {
        Board testBoard = new Board(8, 8);
        Piece testPiece1 = new PawnPiece(testBoard, 1);
        Piece testPiece2 = new PawnPiece(testBoard, 2);
        testBoard.addPiece(testPiece1, 1, 1);
        testBoard.addPiece(testPiece2, 6, 6);
        testBoard.addPiece(new PawnPiece(testBoard, 1), 1, 2);
        testBoard.addPiece(new PawnPiece(testBoard, 1), 6, 5);
        testBoard.addPiece(new KingPiece(testBoard, 2), 2, 2);

        assertEquals("Pawn cannot stay put", false, testPiece1.isValidMove(1, 1, 1, 1));
        assertEquals("Pawn blocked by King", false, testPiece1.isValidMove(1, 1, 2, 2));
        assertEquals("Player 1 Pawn blocked N", false, testPiece1.isValidMove(1, 1, 1, 2));
        assertEquals("Player 2 Pawn blocked S", false, testPiece2.isValidMove(6, 6, 6, 5));
    }

}
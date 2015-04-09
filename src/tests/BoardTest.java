package tests;

import com.chess.Board;
import com.chess.Piece;
import com.chess.pieces.*;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BoardTest {

    @Test
     public void testMovePieceBoundViolation() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard = spy(testBoard);

        Piece testPiece = mock(Piece.class);
        when(testPiece.isValidMove(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(testBoard.getPiece(0, 1)).thenReturn(testPiece);
        testBoard.addPiece(testPiece, 0, 1);

        when(testBoard.causesCheck(any(Piece.class), anyInt(), anyInt())).thenReturn(false);

        assertEquals("Cannot move from below X bounds", 1, testBoard.movePiece(-1, 0, 0, 2));
        assertEquals("Cannot move from below Y bounds", 1, testBoard.movePiece(0, -1, 0, 2));
        assertEquals("Cannot move from above X bounds", 1, testBoard.movePiece(8, 0, 0, 2));
        assertEquals("Cannot move from above Y bounds", 1, testBoard.movePiece(0, 8, 0, 2));
        assertEquals("Can move from valid location", 0, testBoard.movePiece(0, 1, 0, 2));
    }

    @Test
    public void testMovePiecePlacement() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard = spy(testBoard);

        Piece testPiece = mock(Piece.class);
        when(testPiece.isValidMove(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(testBoard.causesCheck(any(Piece.class), anyInt(), anyInt())).thenReturn(false);

        testBoard.addPiece(testPiece, 0, 1);
        testBoard.movePiece(0, 1, 1, 1);

        assertEquals("New space contains the piece", testPiece, testBoard.getPiece(1, 1));
        assertEquals("Old space does not contain the piece", null, testBoard.getPiece(0, 1));
}

    @Test
    public void testMovePieceInvalid() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard = spy(testBoard);

        Piece testPiece = mock(Piece.class);
        when(testPiece.isValidMove(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(false);

        testBoard.addPiece(testPiece, 0, 1);

        assertEquals("movePiece returns false on invalid move", 1, testBoard.movePiece(0, 1, 1, 1));
        assertEquals("New space does contain the piece", null, testBoard.getPiece(1, 1));
        assertEquals("Old space does contains the piece", testPiece, testBoard.getPiece(0, 1));
    }

    @Test
    public void testMovePieceCapture() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard = spy(testBoard);
        when(testBoard.causesCheck(any(KingPiece.class), anyInt(), anyInt())).thenReturn(false);
        PawnPiece testPiece1 = spy(new PawnPiece(testBoard, 1));
        PawnPiece testPiece2 = spy(new PawnPiece(testBoard, 2));

        testBoard.addPiece(testPiece1, 0, 1);
        testBoard.addPiece(testPiece2, 1, 2);
        testBoard.movePiece(0, 1, 1, 2);

        assertEquals("Space contains the new piece", testPiece1, testBoard.getPiece(1, 2));
        assertEquals("Captured piece has flag set to true", true, testPiece2.isCaptured());
        assertEquals("Capturing piece has flag set to false", false, testPiece1.isCaptured());
    }

    @Test
    public void testBoardDims() throws Exception {
        Board testBoard = new Board(8, 8);
        assertEquals("Correct height", 8, testBoard.getMaxY());
        assertEquals("Correct width", 8, testBoard.getMaxX());
    }


    @Test
    public void testResetBoardConfigurationClasses() throws Exception {
        Board testBoard = new Board(8, 8);

        testBoard.resetBoard(true);

        assertEquals("Correct rook placement at (0, 0)", RookPiece.class, testBoard.getPiece(0, 0).getClass());
        assertEquals("Correct knight placement at (1, 0)", KnightPiece.class, testBoard.getPiece(1, 0).getClass());
        assertEquals("Correct bishop placement at (2, 0)", BishopPiece.class, testBoard.getPiece(2, 0).getClass());
        assertEquals("Correct king placement at (3, 0)", KingPiece.class, testBoard.getPiece(3, 0).getClass());
        assertEquals("Correct queen placement at (4, 0)", QueenPiece.class, testBoard.getPiece(4, 0).getClass());
        assertEquals("Correct bishop placement at (5, 0)", BishopPiece.class, testBoard.getPiece(5, 0).getClass());
        assertEquals("Correct knight placement at (6, 0)", KnightPiece.class, testBoard.getPiece(6, 0).getClass());
        assertEquals("Correct rook placement at (7, 0)", RookPiece.class, testBoard.getPiece(7, 0).getClass());

        assertEquals("Correct rook placement at (0, 7)", RookPiece.class, testBoard.getPiece(0, 7).getClass());
        assertEquals("Correct knight placement at (1, 7)", KnightPiece.class, testBoard.getPiece(1, 7).getClass());
        assertEquals("Correct bishop placement at (2, 7)", BishopPiece.class, testBoard.getPiece(2, 7).getClass());
        assertEquals("Correct king placement at (3, 7)", KingPiece.class, testBoard.getPiece(3, 7).getClass());
        assertEquals("Correct queen placement at (4, 7)", QueenPiece.class, testBoard.getPiece(4, 7).getClass());
        assertEquals("Correct bishop placement at (5, 7)", BishopPiece.class, testBoard.getPiece(5, 7).getClass());
        assertEquals("Correct knight placement at (6, 7)", KnightPiece.class, testBoard.getPiece(6, 7).getClass());
        assertEquals("Correct rook placement at (7, 7)", RookPiece.class, testBoard.getPiece(7, 7).getClass());

        assertEquals("King knows its coordinates", 3, testBoard.getPiece(3, 0).getLocX());
        assertEquals("King knows its coordinates", 0, testBoard.getPiece(3, 0).getLocY());

        for(int x = 0; x < 8; x++) {
            assertEquals("Correct pawn placement at (" + x + ", 1)", PawnPiece.class, testBoard.getPiece(x, 1).getClass());
        }

        for(int x = 0; x < 8; x++) {
            assertEquals("Correct pawn placement at (" + x + ", 6)", PawnPiece.class, testBoard.getPiece(x, 6).getClass());
        }

        for(int x = 0; x < 8; x++){
            for(int y = 2; y < 6; y++) {
                assertEquals("Other spaces do not contain pieces", null, testBoard.getPiece(x, y));
            }
        }

    }


    @Test
    public void testResetBoardConfigurationPlayer() throws Exception {
        Board testBoard = new Board(8, 8);

        testBoard.resetBoard(true);

        for(int x = 0; x < 8; x++) {
            assertEquals("Correct player placement at (" + x + ", 0)", 1, testBoard.getPiece(x, 0).getPlayer());
            assertEquals("Correct player placement at (" + x + ", 1)", 1, testBoard.getPiece(x, 1).getPlayer());
        }

        for(int x = 0; x < 8; x++) {
            assertEquals("Correct player placement at (" + x + ", 6)", 2, testBoard.getPiece(x, 6).getPlayer());
            assertEquals("Correct player placement at (" + x + ", 7)", 2, testBoard.getPiece(x, 7).getPlayer());
        }

    }

    @Test
    public void testIsCheckmated() throws Exception {
        Board testBoard = spy(new Board(8, 8));

        Piece kingPiece = new KingPiece(testBoard, 1);
        Piece oppKingPiece = new KingPiece(testBoard, 2);
        Piece oppPiece1 = new QueenPiece(testBoard, 2);
        Piece oppPiece2 = new QueenPiece(testBoard, 2);
        Piece oppPiece3 = new QueenPiece(testBoard, 2);

        when(testBoard.getPlayerKing(1)).thenReturn(kingPiece);
        when(testBoard.getPlayerKing(2)).thenReturn(oppKingPiece);

        testBoard.addPiece(kingPiece, 3, 3);
        testBoard.addPiece(oppKingPiece, 7, 7);
        assertEquals("Not checkmated", false, testBoard.isCheckmated(1));

        testBoard.addPiece(oppPiece1, 2, 5);
        testBoard.addPiece(oppPiece2, 3, 5);
        testBoard.addPiece(oppPiece3, 4, 5);
        assertEquals("Checkmated", true, testBoard.isCheckmated(1));

    }

    @Test
    public void testCausesCheck() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard = spy(testBoard);
        Piece piece = spy(new KingPiece(testBoard, 1));
        Piece piece2 = spy(new KingPiece(testBoard, 2));
        piece.setLocation(3, 3);
        when(testBoard.getPlayerKing(1)).thenReturn(piece);
        when(testBoard.getPlayerKing(2)).thenReturn(piece2);
        Piece queenPiece = new QueenPiece(testBoard, 1);
        testBoard.addPiece(piece, 3, 3);
        testBoard.addPiece(piece2, 0, 1);
        testBoard.addPiece(queenPiece, 4, 4);
        queenPiece.setLocation(4, 4);
        Piece oppQueenPiece = new QueenPiece(testBoard, 2);
        oppQueenPiece.setLocation(5, 5);
        testBoard.addPiece(oppQueenPiece, 5, 5);
        assertEquals("Causes check", true, testBoard.causesCheck(queenPiece, 4, 6, testBoard));
        assertEquals("Does not cause check", false, testBoard.causesCheck(queenPiece, 5, 5, testBoard));
    }


    @Test
    public void testIsStalemate() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard = spy(testBoard);
        Piece piece = mock(KingPiece.class, CALLS_REAL_METHODS);
        testBoard.addPiece(piece, 3, 3);
        when(piece.getLocX()).thenReturn(3);
        when(piece.getLocY()).thenReturn(3);
        doReturn(piece).when(testBoard).getPlayerKing(anyInt());
        doReturn(piece).when(testBoard).getPiece(anyInt(), anyInt());
        doReturn(false).when(testBoard).isInCheck(anyInt());
        doReturn(false).when(piece).isValidMove(anyInt(), anyInt(), anyInt(), anyInt());

        assertEquals("Stalemate", true, testBoard.isStalemate());

    }

    @Test
    public void testCopyConstructor() throws Exception {
        Board testBoard = new Board(8, 8);
        testBoard.resetBoard(true);

        Board copyBoard = new Board(testBoard);

        /* check that dims are the same */
        assertEquals("X dims equal", testBoard.getMaxX(), copyBoard.getMaxX());
        assertEquals("Y dims equal", testBoard.getMaxY(), copyBoard.getMaxY());

        /* check that each space is identical, has same type of piece on each */
        for(int x = 0; x < copyBoard.getMaxX(); x++){
            for(int y = 0; y < copyBoard.getMaxY(); y++){
                Piece origPiece = testBoard.getPiece(x, y);
                Piece copyPiece = copyBoard.getPiece(x, y);
                if(origPiece == null || copyPiece == null){
                    assertEquals("Empty spaces are same", origPiece, copyPiece);
                } else {
                    assertEquals("Same piece class on each space", origPiece.getClass(), copyPiece.getClass());
                    assertEquals("Same player", origPiece.getPlayer(), copyPiece.getPlayer());
                }
            }
        }


    }


}
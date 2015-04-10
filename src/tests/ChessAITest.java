package tests;

import com.chess.Board;
import com.chess.ChessAI;
import com.chess.Piece;
import com.chess.pieces.KingPiece;
import com.chess.pieces.PawnPiece;
import com.chess.pieces.QueenPiece;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;

public class ChessAITest extends TestCase {

    @Test
    public void testMovePiece_isValidMove() throws Exception {

        Board testBoard =  new Board(8, 8);
        Piece whiteKing = new KingPiece(testBoard,1);
        Piece blackKing = new KingPiece(testBoard,2);
        Piece blackQueen = new QueenPiece(testBoard, 2);
        testBoard.addPiece(whiteKing, 4, 4);
        testBoard.addPiece(blackQueen, 7, 5);
        testBoard.addPiece(blackKing, 7, 7);

        ArrayList<Piece> ai_pieces = new ArrayList<Piece>();
        ai_pieces.add(whiteKing);
        ChessAI ai = new ChessAI(0, testBoard, ai_pieces);

        Integer[] move = ai.getMove();

        int src_x = move[0];
        int src_y = move[1];
        int dst_x = move[2];
        int dst_y = move[3];

        boolean movedKing = (src_x == 4 && src_y == 4);
        boolean validMove = whiteKing.isValidMove(src_x, src_y, dst_x, dst_y);

        assertEquals("AI move was valid.", true, movedKing && validMove);

    }

    @Test
    public void testMovePiece_stopsCheck() throws Exception {

        Board testBoard =  new Board(8, 8);
        Piece whiteKing = new KingPiece(testBoard,1);
        Piece whitePawn1 = new PawnPiece(testBoard,1);
        Piece whitePawn2 = new PawnPiece(testBoard,1);
        Piece blackKing = new KingPiece(testBoard,2);
        Piece blackQueen = new QueenPiece(testBoard, 2);
        testBoard.addPiece(whiteKing, 4, 4);
        testBoard.addPiece(whitePawn1, 3, 4);
        testBoard.addPiece(whitePawn2, 4, 3);
        testBoard.addPiece(blackQueen, 5, 5);
        testBoard.addPiece(blackKing, 7, 7);

        ArrayList<Piece> ai_pieces = new ArrayList<Piece>();
        ai_pieces.add(whiteKing);
        ChessAI ai = new ChessAI(0, testBoard, ai_pieces);

        Integer[] move = ai.getMove();

        int src_x = move[0];
        int src_y = move[1];
        int dst_x = move[2];
        int dst_y = move[3];

        boolean movedKing = (src_x == 4 && src_y == 4);
        boolean killedQueen = (dst_x == 5 && dst_y == 5);
        boolean validMove = whiteKing.isValidMove(src_x, src_y, dst_x, dst_y);

        assertEquals("AI move was valid.", true, movedKing && killedQueen && validMove);

    }

}
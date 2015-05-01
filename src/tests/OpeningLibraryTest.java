package tests;

import OpeningLibrary.LibraryGraphBuilder;
import OpeningLibrary.OpeningLibraryParser;
import com.chess.Board;
import com.chess.ChessAI;
import com.chess.Piece;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuriy on 4/24/2015.
 */
public class OpeningLibraryTest {
    @Test
    public void testOpeningMoveResponse() throws Exception {
        OpeningLibraryParser openingMoves = OpeningLibraryParser.getInstance();
        if (openingMoves.updateLibrary("http://www.chess.com/openings/")) {
            openingMoves.parseOpeningGraph();
        }
        LibraryGraphBuilder graphBuilder = LibraryGraphBuilder.getInstance();
        Board testBoard = new Board(8, 8);
        testBoard.resetBoard();
        ArrayList<Piece> ai_pieces = (ArrayList) testBoard.getPieces(2);
        ChessAI ai = new ChessAI(2, testBoard, ai_pieces, null, graphBuilder.buildGraphFromXML());
        testBoard.movePiece(0, 1, 2, 2);
        Integer[] lastMove = new Integer[4];
        lastMove[0] = 0;
        lastMove[1] = 1;
        lastMove[2] = 2;
        lastMove[3] = 2;
        Integer[] move = ai.getMoveFromOpening(lastMove);
        int src_x = move[0];
        int src_y = move[1];
        int dst_x = move[2];
        int dst_y = move[3];

        boolean movedKnight = (src_x == 4 && src_y == 6);
        boolean correctPosition = (dst_x == 4 && dst_y == 4);

        assertEquals(true, movedKnight && correctPosition);
    }

    @Test
    public void testOpeningMoveStart() throws Exception {
        OpeningLibraryParser openingMoves = OpeningLibraryParser.getInstance();
        if (openingMoves.updateLibrary("http://www.chess.com/openings/")) {
            openingMoves.parseOpeningGraph();
        }
        LibraryGraphBuilder graphBuilder = LibraryGraphBuilder.getInstance();
        Board testBoard = new Board(8, 8);
        testBoard.resetBoard();

        ArrayList<Piece> ai_pieces = (ArrayList) testBoard.getPieces(1);
        ChessAI ai = new ChessAI(2, testBoard, ai_pieces, null, graphBuilder.buildGraphFromXML());
        Integer[] lastMove = null;
        Integer[] move = ai.getMoveFromOpening(lastMove);
        int src_x = move[0];
        int src_y = move[1];
        int dst_x = move[2];
        int dst_y = move[3];
        int k;

        boolean correctPosition = ((dst_x == 4 || dst_x==3) && dst_y == 3);

        assertEquals(true, correctPosition);
    }
}

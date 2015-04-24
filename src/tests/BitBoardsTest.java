package tests;

import com.chess.BitBoards;
import com.chess.Board;
import junit.framework.TestCase;
import org.junit.Test;

public class BitBoardsTest extends TestCase {

    @Test
    public void testInitializeBitBoards() throws Exception {
        Board board = new Board(8, 8);
        board.resetBoard();

        BitBoards bitboard = new BitBoards(board);

        assertEquals("all pieces correct", 0b1111111111111111000000000000000000000000000000000000000000000000L, bitboard.getAllPieces(2));
        assertEquals("all pieces correct", 0b0000000000000000000000000000000000000000000000001111111111111111L, bitboard.getAllPieces(1));
    }

}
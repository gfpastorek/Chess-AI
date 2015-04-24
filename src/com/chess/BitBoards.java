package com.chess;

import com.chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Pastorek on 4/23/2015.
 */
public class BitBoards {

    private final long LEFT_FILE = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    private final long RIGHT_FILE = 0b1000000010000000100000001000000010000000100000001000000010000000L;

    private long[] allPieces = new long[2];
    private long[] allPiecesRotated = new long[2];
    private long[][] leftDiagonals = new long[2][16];
    private long[][] rightDiagonals = new long[2][16];
    private long[] pawns = new long[2];
    private long[] rooks = new long[2];
    private long[] knights = new long[2];
    private long[] bishops = new long[2];
    private long[] queens = new long[2];
    private long[] kings = new long[2];

    private long[][] knightMoves = new long[8][8];
    private long[][] kingMoves = new long[8][8];

    long[][] horizontalAttackBoards = new long[8][256];
    long[][] diagonalAttackBoards = new long[16][256];

    private Board board;
    
    public BitBoards(Board board) {

        this.board = board;

        generateKingMoves();
        generateKnightMoves();
        generateHorizontalAttackBoards();
        generateDiagonalAttackBoards();

        updateBoard();
        
    }


    public void updateBoard() {

        for(int y = 0; y < board.getMaxY(); y++) {

            for(int x = 0; x < board.getMaxX(); x++) {

                Piece piece = board.getPiece(x, y);

                if(piece != null) {
                    updateBitBoards(piece, x, y);
                }

            }

        }

    }


    private void generateKnightMoves() {

        for(int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {

                if(x+1 < 8 && y+2 < 8)   knightMoves[x][y] |= getPosBinary(x+1, y+2);
                if(x+1 < 8 && y-2 >= 0)  knightMoves[x][y] |= getPosBinary(x+1, y-2);
                if(x+2 < 8 && y+1 < 8)   knightMoves[x][y] |= getPosBinary(x+2, y+1);
                if(x+2 < 8 && y-1 >= 0)  knightMoves[x][y] |= getPosBinary(x+2, y-1);
                if(x-1 >= 0 && y+2 < 8)  knightMoves[x][y] |= getPosBinary(x-1, y+2);
                if(x-1 >= 0 && y-2 >= 0) knightMoves[x][y] |= getPosBinary(x-1, y-2);
                if(x-2 >= 0 && y+1 < 8)  knightMoves[x][y] |= getPosBinary(x-2, y+1);
                if(x-2 >= 0 && y-1 >= 0) knightMoves[x][y] |= getPosBinary(x-2, y-1);

            }
        }

    }

    private void generateKingMoves() {

        for(int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {

                if(x+1 < 8 && y+1 < 8)   kingMoves[x][y] |= getPosBinary(x+1, y+1);
                if(x+1 < 8)              kingMoves[x][y] |= getPosBinary(x+1, y);
                if(x+1 < 8 && y-1 >= 0)  kingMoves[x][y] |= getPosBinary(x+1, y-1);
                if(x < 8 && y+1 < 8)     kingMoves[x][y] |= getPosBinary(x, y+1);
                if(x >= 0 && y-1 >= 0)   kingMoves[x][y] |= getPosBinary(x, y-1);
                if(x-1 >= 0 && y+1 < 8)  kingMoves[x][y] |= getPosBinary(x-1, y+1);
                if(x-1 >= 0)             kingMoves[x][y] |= getPosBinary(x-1, y);
                if(x-1 >= 0 && y-1 >= 0) kingMoves[x][y] |= getPosBinary(x-1, y-1);

            }
        }

    }

    private long getPosBinary(int x, int y) {
        return 1L << (x + y*8);
    }


    /* add piece to bitboard at position 'posBinary' */
    private void updateBitBoards(Piece piece, int x, int y) {

        int player_index = piece.getPlayer() - 1;

        long posBinary = getPosBinary(x, y);
        long posBinaryRotated = getPosBinary(y, x);

        allPieces[player_index] += posBinary;
        allPiecesRotated[player_index] += posBinaryRotated;

        leftDiagonals[player_index][x+y] |= ((x+y) > 7) ? (1L << x) : (1L << y);
        rightDiagonals[player_index][y-x+7] |= ((y-x+7) > 7) ? (1L << y) : (1L << x);

        if(piece.getClass() == RookPiece.class)
            rooks[player_index] += posBinary;
        else if (piece.getClass() == RookPiece.class)
            knights[player_index] += posBinary;
        else if (piece.getClass() == BishopPiece.class)
            bishops[player_index] += posBinary;
        else if (piece.getClass() == QueenPiece.class)
            queens[player_index] += posBinary;
        else if (piece.getClass() == KingPiece.class)
            kings[player_index] += posBinary;
        else if (piece.getClass() == PawnPiece.class)
            pawns[player_index] += posBinary;
    }

    public long getAllPieces(int player) {
        return allPieces[player-1];
    }


    public long findPawnMoves(int player) {

        int player_index = player - 1;

        long sgn = (player_index == 0) ? 1 : -1;

        long shiftedPawns = pawns[player_index] << sgn*8;
        long emptySpaces  = ~(allPieces[0] | allPieces[1]);
        long validOneSpaceMoves = shiftedPawns & emptySpaces;
        long unmovedPawns = pawns[player_index] & (255 << ((player_index == 0) ? 48 : 8));
        long validTwoSpaceMoves = (unmovedPawns << sgn*16) & (unmovedPawns << sgn*8) & emptySpaces;
        long diagMoves = ((shiftedPawns << 1) & (~LEFT_FILE)) | ((shiftedPawns >> 1) & (~RIGHT_FILE));
        long validAttacks = diagMoves & allPieces[player_index ^ 1];
        long validMoves = validOneSpaceMoves | validTwoSpaceMoves | validAttacks;

        return validMoves;

    }

    public long findLeftPawnAttacks(int player) {
        int player_index = player - 1;
        long sgn = (player_index == 0) ? 1 : -1;
        long shiftedPawns = pawns[player_index] << sgn*8;
        long diagMoves = (shiftedPawns << 1) & (~LEFT_FILE);
        long validAttacks = diagMoves & allPieces[player_index ^ 1];
        return validAttacks;
    }

    public long findRightPawnAttacks(int player) {
        int player_index = player - 1;
        long sgn = (player_index == 0) ? 1 : -1;
        long shiftedPawns = pawns[player_index] << sgn*8;
        long diagMoves = (shiftedPawns >> 1) & (~RIGHT_FILE);
        long validAttacks = diagMoves & allPieces[player_index ^ 1];
        return validAttacks;
    }

    public long findKnightMoves(int player, int x, int y){

        int player_index = player - 1;
        long validMoves = knightMoves[x][y] & ~(allPieces[player_index]);

        return validMoves;
    }

    public long findKingMoves(int player, int x, int y){

        int player_index = player - 1;
        long validMoves = kingMoves[x][y] & ~(allPieces[player_index]);

        return validMoves;
    }

    public List<Integer[]> findQueenMoves(int player, int x, int y) {

        long horizontalMoves = findHorizontalMoves(player, x, y);
        long verticalMoves = findVerticalMoves(player, x, y);
        long leftDiagonalMoves = findLeftDiagonalMoves(player, x, y);
        long rightDiagonalMoves = findRightDiagonalMoves(player, x, y);

        List<Integer[]> moves = new ArrayList<Integer[]>();

        int dl = x + y;
        int dr = y - x + 7;

        for(int i = 0; i < 8; i++) {

            if((horizontalMoves & 1L) == 1)
                moves.add(new Integer[] { x, y, i, y });

            if((verticalMoves & 1L) == 1)
                moves.add(new Integer[] { x, y, x, i });

            if((leftDiagonalMoves & 1L) == 1) {
                if(dl < 8)
                    moves.add(new Integer[]{x, y, i, dl-i});
                else
                    moves.add(new Integer[]{x, y, i+dl-7, 7-i});
            }

            if((rightDiagonalMoves & 1L) == 1) {
                if (dr < 8)
                    moves.add(new Integer[]{x, y, 7 - i, dr - i});
                else
                    moves.add(new Integer[]{x, y, 14 - i - dr, 7 - i});
            }

            horizontalMoves = horizontalMoves >> 1;
            verticalMoves = verticalMoves >> 1;
            leftDiagonalMoves = leftDiagonalMoves >> 1;
            rightDiagonalMoves = rightDiagonalMoves >> 1;

        }

        return moves;

    }

    public List<Integer[]> findRookMoves(int player, int x, int y) {

        long horizontalMoves = findHorizontalMoves(player, x, y);
        long verticalMoves = findVerticalMoves(player, x, y);

        List<Integer[]> moves = new ArrayList<Integer[]>();

        int dl = x + y;
        int dr = y - x + 7;

        for(int i = 0; i < 8; i++) {

            if((horizontalMoves & 1L) == 1)
                moves.add(new Integer[] { x, y, i, y });

            if((verticalMoves & 1L) == 1)
                moves.add(new Integer[] { x, y, x, i });

            horizontalMoves = horizontalMoves >> 1;
            verticalMoves = verticalMoves >> 1;

        }

        return moves;

    }

    public List<Integer[]> findBishopMoves(int player, int x, int y) {

        long leftDiagonalMoves = findLeftDiagonalMoves(player, x, y);
        long rightDiagonalMoves = findRightDiagonalMoves(player, x, y);

        List<Integer[]> moves = new ArrayList<Integer[]>();

        int dl = x + y;
        int dr = y - x + 7;

        for(int i = 0; i < 8; i++) {

            if((leftDiagonalMoves & 1L) == 1) {
                if(dl < 8)
                    moves.add(new Integer[]{x, y, i, dl-i});
                else
                    moves.add(new Integer[]{x, y, i+dl-7, 7-i});
            }

            if((rightDiagonalMoves & 1L) == 1) {
                if(dr < 8)
                    moves.add(new Integer[]{x, y, 7-i, dr-i});
                else
                    moves.add(new Integer[]{x, y, 14-i-dr, 7-i});
            }

            leftDiagonalMoves = leftDiagonalMoves >> 1;
            rightDiagonalMoves = rightDiagonalMoves >> 1;

        }

        return moves;

    }

    public long findHorizontalMoves(int player, int x, int y) {

        /* horizontal slides */
        long occupancy = computeRankOccupancy(y);
        long potentialTargets = horizontalAttackBoards[x][(int)occupancy];
        long validMoves = (~allPieces[player-1]) & potentialTargets;

        return validMoves;
    }

    public long findVerticalMoves(int player, int x, int y) {

        /* vertical slides */
        long occupancy = computeFileOccupancy(x);
        long potentialTargets = horizontalAttackBoards[y][(int)occupancy];
        long validMoves = (~allPiecesRotated[player-1]) & potentialTargets;

        return validMoves;
    }

    public long findLeftDiagonalMoves(int player, int x, int y) {

        long occupancy = leftDiagonals[0][x+y] | leftDiagonals[1][x+y];
        long potentialTargets = diagonalAttackBoards[x+y][(int)occupancy];
        long validMoves = (~leftDiagonals[player-1][x+y]) & potentialTargets;

        return validMoves;
    }

    public long findRightDiagonalMoves(int player, int x, int y) {

        long occupancy = rightDiagonals[0][y-x+7] | rightDiagonals[1][y-x+7];
        long potentialTargets = diagonalAttackBoards[y-x+7][(int)occupancy];
        long validMoves = (~rightDiagonals[player-1][y-x+7]) & potentialTargets;

        return validMoves;
    }

    private long computeRankOccupancy(int rank) {
        return ((allPieces[0] | allPieces[1]) >> (rank*8)) & 255;
    }

    private long computeFileOccupancy(int file) {
        return ((allPiecesRotated[0] | allPiecesRotated[1]) >> (file*8)) & 255;
    }

    /* generate 8x255 array of horizontal attacks valid at location x when occupancy is 'i' */
    private void generateHorizontalAttackBoards() {

        for(int x = 0; x < 8; x++) {

            for(long i = 0L; i < 255L; i++){

                long targetBinary = 0L;

                for(int x_ = x+1; x_ < 8; x_++){
                    long p = (1L << (long)x_);
                    targetBinary |= p;
                    if((i & p) == 1)
                        break;
                }

                for(int x_ = x-1; x_ >= 0; x_--){
                    long p = (1L << (long)x_);
                    targetBinary |= p;
                    if((i & p) == 1)
                        break;
                }

                horizontalAttackBoards[x][(int)i] = targetBinary;

            }
        }

    }

    private void generateDiagonalAttackBoards() {

        for(int d = 0; d < 8; d++) {

            for(int i = 0; i < (2L << d)-1; i++) {

                long targetBinary = 0L;

                for(int d_ = d+1; d_ < 8; d_++){
                    long p = (1L << (long)d_);
                    targetBinary |= p;
                    if((i & p) == 1)
                        break;
                }

                for(int d_ = d-1; d_ >= 0; d_--){
                    long p = (1L << (long)d_);
                    targetBinary |= p;
                    if((i & p) == 1)
                        break;
                }

                diagonalAttackBoards[d][i] = targetBinary;
                diagonalAttackBoards[14-d][i] = targetBinary;

            }

        }

    }







}

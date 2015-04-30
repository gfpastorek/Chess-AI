package com.chess;

import com.chess.pieces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Greg Pastorek on 4/23/2015.
 */
public class BitBoards {

    private static final long LEFT_FILE = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    private static final long RIGHT_FILE = 0b1000000010000000100000001000000010000000100000001000000010000000L;

    private long[] allPieces = new long[2];
    private long[] allPiecesRotated = new long[2];
    private long[][] leftDiagonals = new long[2][15];
    private long[][] rightDiagonals = new long[2][15];
    private long[] pawns = new long[2];
    private long[] rooks = new long[2];
    private long[] knights = new long[2];
    private long[] bishops = new long[2];
    private long[] queens = new long[2];
    private long[] kings = new long[2];

    private static long[][] knightMoves = new long[8][8];
    private static long[][] kingMoves = new long[8][8];

    static long[][] horizontalAttackBoards = new long[8][256];
    static long[][][] diagonalAttackBoards = new long[16][256][8];

    private Board board;
    
    public BitBoard realBitBoard;

    public static class BitBoard {
        public long[] allPieces;
        public long[] allPiecesRotated;
        public long[][] leftDiagonals = new long[2][15];
        public long[][] rightDiagonals = new long[2][15];
        public long[] pawns;
        public long[] rooks;
        public long[] knights;
        public long[] bishops;
        public long[] queens;
        public long[] kings;

        public HashMap<Piece, Integer[]> locations = new HashMap<Piece, Integer[]>();

        /* clear all bitBoards */
        public void reset() {
            this.knights = new long[2];
            this.allPieces = new long[2];
            this.allPiecesRotated = new long[2];
            this.leftDiagonals = new long[2][15];
            this.rightDiagonals = new long[2][15];
            this.pawns = new long[2];
            this.rooks = new long[2];
            this.bishops = new long[2];
            this.queens = new long[2];
            this.kings = new long[2];
        }

        /* visually outout the bitboard of 'bits'*/
        public static String represent(long bits) {

            String output = "\n";

            for(int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    if((bits & getPosBinary(x, y)) != 0L) {
                        output += "1 ";
                    } else {
                        output += "0 ";
                    }
                }
                output += "\n";
            }

            return output;

        }

        public BitBoard() {
            this.knights = new long[2];
            this.allPieces = new long[2];
            this.allPiecesRotated = new long[2];
            this.leftDiagonals = new long[2][15];
            this.rightDiagonals = new long[2][15];
            this.pawns = new long[2];
            this.rooks = new long[2];
            this.bishops = new long[2];
            this.queens = new long[2];
            this.kings = new long[2];
        }


        /* add piece at its position */
        public void addPiece(Piece piece) {
            int x = piece.getLocX();
            int y = piece.getLocY();
            locations.put(piece, new Integer[] {x,y});
        }

        /* copy constructor, clones all bitboards */
        public BitBoard(BitBoard other) {
            this.knights = other.knights.clone();
            this.allPieces = other.allPieces.clone();
            this.allPiecesRotated = other.allPiecesRotated.clone();
            this.leftDiagonals[0] = other.leftDiagonals[0].clone();
            this.rightDiagonals[0] = other.rightDiagonals[0].clone();
            this.leftDiagonals[1] = other.leftDiagonals[1].clone();
            this.rightDiagonals[1] = other.rightDiagonals[1].clone();
            this.pawns = other.pawns.clone();
            this.rooks = other.rooks.clone();
            this.bishops = other.bishops.clone();
            this.queens = other.queens.clone();
            this.kings = other.kings.clone();
            this.locations = (HashMap<Piece, Integer[]>)other.locations.clone();
        }
        
    }

    public BitBoard getBitBord() {
        return realBitBoard;
    }


    /* create a list of bitboards from making each move in 'target' */
    public static List<BitBoard> makeMoves(BitBoard startBoard, long targets, int x, int y, int player, Piece piece) {

        int player_index = player - 1;

        List<BitBoard> moves = new ArrayList<BitBoard>();

        /* scan the targets for valid locations */
        for(int i = 0; i < 64; i++) {

            long target = (1L << i);

            /* check if valid target */
            if((targets & target) != 0) {

                int tx = 7 - (i % 8);
                int ty = 7 - i / 8;

                int dr = tx + ty;
                int dl = ty - tx + 7;

                /* make new bitboard, and check all the appropriate bitboards to make the move */
                BitBoard moveBoard = new BitBoard(startBoard);

                moveBoard.locations.put(piece, new Integer[] {x,y});
                
                moveBoard.allPieces[player-1] = (startBoard.allPieces[player-1] | target) & (~getPosBinary(x, y));
                moveBoard.allPiecesRotated[player-1] = (startBoard.allPiecesRotated[player-1] | getPosBinary(ty, tx)) & (~getPosBinary(y, x));

                moveBoard.rightDiagonals[player-1][dr] = (startBoard.rightDiagonals[player-1][dl] | (1L << ((dl < 8) ? tx : 7-ty)));
                moveBoard.rightDiagonals[player-1][x+y] &=  (~(1L << ((x+y < 8) ? x : 7-y)));

                moveBoard.leftDiagonals[player-1][dl] = (startBoard.leftDiagonals[player-1][dr] | (1L << ((dr < 8) ? 7-tx : 7-ty)));
                moveBoard.leftDiagonals[player-1][y-x+7] &= (~(1L << ((y-x+7 < 8) ? 7-x : 7-y)));

                if(piece.getClass() == RookPiece.class)
                    moveBoard.rooks[player_index] = (startBoard.rooks[player-1] | target) & (~getPosBinary(x, y));
                else if (piece.getClass() == KnightPiece.class)
                    moveBoard.knights[player_index] = (startBoard.knights[player-1] | target) & (~getPosBinary(x, y));
                else if (piece.getClass() == BishopPiece.class)
                    moveBoard.bishops[player_index] = (startBoard.bishops[player-1] | target) & (~getPosBinary(x, y));
                else if (piece.getClass() == QueenPiece.class)
                    moveBoard.queens[player_index] = (startBoard.queens[player-1] | target) & (~getPosBinary(x, y));
                else if (piece.getClass() == KingPiece.class)
                    moveBoard.kings[player_index] = (startBoard.kings[player-1] | target) & (~getPosBinary(x, y));
                else if (piece.getClass() == PawnPiece.class)
                    moveBoard.pawns[player_index] = (startBoard.pawns[player-1] | target) & (~getPosBinary(x, y));

                moves.add(moveBoard);
            }
        }

        return moves;

    }

    /* copy constructor */
    public BitBoards(Board board) {

        this.board = board;

        generateKingMoves();
        generateKnightMoves();
        generateHorizontalAttackBoards();
        generateDiagonalAttackBoards();

        realBitBoard = new BitBoard();

        updateBoard(realBitBoard);
        
    }

    /* update the realbitboard to match the real board */
    public void updateBoard() {
        updateBoard(realBitBoard);
    }

    /* update the bitboard to match the real board */
    public void updateBoard(BitBoard bitBoard) {

        bitBoard.reset();

        /* scan the bitBoard for pieces */
        for(int y = 0; y < board.getMaxY(); y++) {

            for(int x = 0; x < board.getMaxX(); x++) {

                Piece piece = board.getPiece(x, y);

                /* add piece to bitBoard */
                if(piece != null) {
                    updateBitBoards(bitBoard, piece, x, y);
                }

            }

        }

    }


    /* generate knight move array, all targets are pre-computed for each space */
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

    /* generate king move array, all targets are pre-computed for each space */
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

    /* get the bitboard of the position x,y */
    private static long getPosBinary(int x, int y) {
        return 1L << ((7-x) + (7-y)*8);
    }

    /* add piece to bitboard at position 'posBinary' */
    private void updateBitBoards(BitBoard bitBoard, Piece piece, int x, int y) {

        int player_index = piece.getPlayer() - 1;

        long posBinary = getPosBinary(x, y);
        long posBinaryRotated = getPosBinary(y, x);

        bitBoard.allPieces[player_index] |= posBinary;
        bitBoard.allPiecesRotated[player_index] |= posBinaryRotated;

        bitBoard.rightDiagonals[player_index][x+y] |= ((x+y) < 8) ? (1L << x) : (1L << 7 - y);
        bitBoard.leftDiagonals[player_index][y-x+7] |= ((y-x+7) < 8) ? (1L << 7-x) : (1L << 7-y);

        bitBoard.addPiece(piece);

        /* update piece specific bitboards */
        if(piece.getClass() == RookPiece.class)
            bitBoard.rooks[player_index] |= posBinary;
        else if (piece.getClass() == KnightPiece.class)
            bitBoard.knights[player_index] |= posBinary;
        else if (piece.getClass() == BishopPiece.class)
            bitBoard.bishops[player_index] |= posBinary;
        else if (piece.getClass() == QueenPiece.class)
            bitBoard.queens[player_index] |= posBinary;
        else if (piece.getClass() == KingPiece.class)
            bitBoard.kings[player_index] |= posBinary;
        else if (piece.getClass() == PawnPiece.class)
            bitBoard.pawns[player_index] |= posBinary;
    }


    public long getAllPieces(BitBoard bitBoard, int player) {
        return bitBoard.allPieces[player-1];
    }


    /* return bitboard of pawn move targets for piece at x,y */
    public static long findPawnMoves(BitBoard bitBoard, int player, int x, int y) {

        int player_index = player - 1;

        long sgn = (player_index == 0) ? -1 : 1;

        long posBinary = getPosBinary(x, y);

        long shiftedPawns = posBinary << sgn*8;
        long emptySpaces  = ~(bitBoard.allPieces[0] | bitBoard.allPieces[1]);
        long validOneSpaceMoves = shiftedPawns & emptySpaces;
        long unmovedPawns = posBinary & (255 << ((player_index == 0) ? 48 : 8));
        long validTwoSpaceMoves = (unmovedPawns << sgn*16) & (unmovedPawns << sgn*8) & emptySpaces;
        long diagMoves = ((shiftedPawns << 1) & (~LEFT_FILE)) | ((shiftedPawns >> 1) & (~RIGHT_FILE));
        long validAttacks = diagMoves & bitBoard.allPieces[player_index ^ 1];
        long validMoves = validOneSpaceMoves | validTwoSpaceMoves | validAttacks;

        return validMoves;
    }

    /* return bitboard of knight move targets for piece at x,y */
    public static long findKnightMoves(BitBoard bitBoard, int player, int x, int y){

        int player_index = player - 1;
        long validMoves = knightMoves[x][y] & ~(bitBoard.allPieces[player_index]);

        return validMoves;
    }

    /* return bitboard of king move targets for piece at x,y */
    public static long findKingMoves(BitBoard bitBoard, int player, int x, int y){

        int player_index = player - 1;
        long validMoves = kingMoves[x][y] & ~(bitBoard.allPieces[player_index]);

        return validMoves;
    }

    /* return bitboard of queen move targets for piece at x,y */
    public static long findQueenMoves(BitBoard bitBoard, int player, int x, int y) {

        long diagonalTarget = getDiagonalMoves(bitBoard, player, x, y);
        long straightTargets = getStraightMoves(bitBoard, player, x, y);

        return diagonalTarget | straightTargets;
    }

    /* return bitboard of rook move targets for piece at x,y */
    public static long findRookMoves(BitBoard bitBoard, int player, int x, int y) {

        long targets = getStraightMoves(bitBoard, player, x, y);

        return targets;
    }

    /* return bitboard of horizontal+vertical moves for piece at x,y */
    private static long getStraightMoves(BitBoard bitBoard, int player, int x, int y) {
        long horizontalMoves = findHorizontalMoves(bitBoard, player, x, y);
        long verticalMoves = findVerticalMoves(bitBoard, player, x, y);

        long targets = horizontalMoves << 8*(7-y);

        for(int i = 0; i < 8; i++) {
            targets |= ((verticalMoves >> i) & 1L) << (8*i+7-x);
        }

        return targets;
    }

    /* return bitboard of bishop move targets for piece at x,y */
    public static long findBishopMoves(BitBoard bitBoard, int player, int x, int y) {

        long target = getDiagonalMoves(bitBoard, player, x, y);

        return target;
    }

    /* return bitboard of diagonal move targets for piece at x,y */
    private static long getDiagonalMoves(BitBoard bitBoard, int player, int x, int y) {
        long leftDiagonalMoves = findLeftDiagonalMoves(bitBoard, player, x, y);
        long rightDiagonalMoves = findRightDiagonalMoves(bitBoard, player, x, y);

        long target = 0L;

        /* compute left/right diagonal */
        int dr = x + y;
        int dl = y - x + 7;

        /* convert diagonal bitboard to square bitboard */
        for(int i = 0; i < 8; i++) {
            if(((leftDiagonalMoves >> i) & 1L) == 1L) {
                int xl = (dl < 8) ? 7-i : 14-i-dl;
                int yl = (dl < 8) ? dl-i : 7-i;
                target |= getPosBinary(xl, yl);
            }
            if(((rightDiagonalMoves >> i) & 1L) == 1L) {
                int xr = (dr < 8) ? i : dr+i-7;
                int yr = (dr < 8) ? dr-i : 7 - i;
                target |= getPosBinary(xr, yr);
            }
        }
        return target;
    }

    /* return bitboard of horizontal move targets for piece at x,y */
    public static long findHorizontalMoves(BitBoard bitBoard, int player, int x, int y) {

        /* horizontal slides */
        long occupancy = computeRankOccupancy(bitBoard, 7-y);
        long potentialTargets = horizontalAttackBoards[x][(int)occupancy];
        long validMoves = ((~bitBoard.allPieces[player-1]) >> 8*(7-y)) & potentialTargets;

        return validMoves;
    }

    /* return bitboard of vertical move targets for piece at x,y */
    public static long findVerticalMoves(BitBoard bitBoard, int player, int x, int y) {

        /* vertical slides */
        long occupancy = computeFileOccupancy(bitBoard, 7 - x);
        long potentialTargets = horizontalAttackBoards[y][(int)occupancy];
        long validMoves = ((~bitBoard.allPiecesRotated[player-1]) >> 8*(7-x)) & potentialTargets;

        return validMoves;
    }

    /* return bitboard of right diagonal move targets for piece at x,y */
    public static long findRightDiagonalMoves(BitBoard bitBoard, int player, int x, int y) {

        int d_pos = (x+y < 8) ? x : 7 - y;

        long occupancy = bitBoard.rightDiagonals[0][x+y] | bitBoard.rightDiagonals[1][x+y];
        long potentialTargets = diagonalAttackBoards[x+y][(int)occupancy][d_pos];
        long validMoves = (~bitBoard.rightDiagonals[player-1][x+y]) & potentialTargets;

        return validMoves;
    }

    /* return bitboard of left diagonal move targets for piece at x,y */
    public static long findLeftDiagonalMoves(BitBoard bitBoard, int player, int x, int y) {

        int d_pos = (y-x+7 < 8) ? 7 - x : 7 - y;

        long occupancy = bitBoard.leftDiagonals[0][y-x+7] | bitBoard.leftDiagonals[1][y-x+7];
        long potentialTargets = diagonalAttackBoards[y-x+7][(int)occupancy][d_pos];
        long validMoves = (~bitBoard.leftDiagonals[player-1][y-x+7]) & potentialTargets;

        return validMoves;
    }

    /* compute the row bitboard for rank */
    private static long computeRankOccupancy(BitBoard bitBoard, int rank) {
        return ((bitBoard.allPieces[0] | bitBoard.allPieces[1]) >> (rank*8)) & 255;
    }

    /* compute the column bitboard for file */
    private static long computeFileOccupancy(BitBoard bitBoard, int file) {
        return ((bitBoard.allPiecesRotated[0] | bitBoard.allPiecesRotated[1]) >> (file*8)) & 255;
    }

    /* generate 8x255 array of horizontal attacks valid at location x when occupancy is 'i' */
    private void generateHorizontalAttackBoards() {

        for(int x = 0; x < 8; x++) {

            for(long i = 0L; i < 255L; i++){

                long targetBinary = 0L;

                for(int x_ = x+1; x_ < 8; x_++){
                    long p = (1L << (long)x_);
                    targetBinary |= p;
                    if((i & p) != 0)
                        break;
                }

                for(int x_ = x-1; x_ >= 0; x_--){
                    long p = (1L << (long)x_);
                    targetBinary |= p;
                    if((i & p) != 0)
                        break;
                }

                horizontalAttackBoards[7-x][(int)i] = targetBinary;

            }
        }

    }

    /* precompute diagonal attack board targets */
    private void generateDiagonalAttackBoards() {

        for(int dd = 0; dd < 15; dd++) {

            int d = (dd < 8) ? dd : 14-dd;

            for(int i = 0; i < (2L << d)-1; i++) {

                for(int j = 0; j < d; j++) {

                    long targetBinary = 0L;

                    for(int k = j+1; k < d; k++){
                        long p = (1L << (long)k);
                        targetBinary |= p;
                        if((i & p) != 0L)
                            break;
                    }

                    for(int k = j-1; k >= 0; k--){
                        long p = (1L << (long)k);
                        targetBinary |= p;
                        if((i & p) != 0L)
                            break;
                    }

                    diagonalAttackBoards[d][i][j] = targetBinary;
                }

            }

        }

    }


    /* get all moves for piece at x,y as a target bitboard */
    public static long getMoves(BitBoard bitBoard, Piece piece, int player, int x, int y) {

        if(piece.getClass() == RookPiece.class) {
            return findRookMoves(bitBoard, player, x, y) & (~getPosBinary(x, y));
        } else if (piece.getClass() == KnightPiece.class) {
            return findKnightMoves(bitBoard, player, x, y) & (~getPosBinary(x, y));
        } else if (piece.getClass() == BishopPiece.class) {
            return findBishopMoves(bitBoard, player, x, y) & (~getPosBinary(x, y));
        } else if (piece.getClass() == QueenPiece.class) {
            return findQueenMoves(bitBoard, player, x, y) & (~getPosBinary(x, y));
        } else if (piece.getClass() == KingPiece.class) {
            return findKingMoves(bitBoard, player, x, y) & (~getPosBinary(x, y));
        } else if (piece.getClass() == PawnPiece.class) {
            return findPawnMoves(bitBoard, player, x, y) & (~getPosBinary(x, y));
        }

        return 0L;

    }




}

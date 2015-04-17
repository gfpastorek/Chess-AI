package com.chess;

import com.chess.pieces.*;

import java.util.HashMap;

/**
 * Created by Greg Pastorek on 4/9/2015.
 */
public class PieceRank {

    static HashMap<Class, Integer> rankMap = new HashMap<Class, Integer>();

    static boolean initialized = false;

    private static void initialize() {
        rankMap.put(PawnPiece.class, 1);
        rankMap.put(KnightPiece.class, 3);
        rankMap.put(BishopPiece.class, 3);
        rankMap.put(RookPiece.class, 5);
        rankMap.put(QueenPiece.class, 9);
        rankMap.put(KingPiece.class, 200);
        initialized = true;
    }

    /* return the rank of piece 'piece' as a number value, higher is more valuable */
    public static int getRank(Piece piece) {

        if(!initialized){
            initialize();
        }

        if(piece == null) {
            return 0;
        }

        if(piece.isCaptured()) {
            return 0;
        }

        return rankMap.get(piece.getClass());
    }

    /* check if a pawn is doubled, that is, check if another pawn lays on the same column */
    public static int pawnIsDoubled(PawnPiece pawn, Board board) {

        int x = pawn.getLocX();

        for(int y = 0; y < board.getMaxY(); y++) {
            Piece piece = board.getPiece(x, y);
            if(piece != null && piece.getClass() == PawnPiece.class
                    && piece.getPlayer() == pawn.getPlayer()) {
                return 1;
            }
        }

        return 0;

    }

    /* check if a pawn is isolated, that is, there is no pawn on an adjacent column */
    public static int pawnIsIsolated(PawnPiece pawn, Board board) {

        int x = pawn.getLocX();

        for(int y = 0; y < board.getMaxY(); y++) {
            Piece pieceLeft = board.getPiece(x-1, y);
            Piece pieceRight = board.getPiece(x-1, y);
            if( (pieceLeft != null && pieceLeft.getClass() == PawnPiece.class && pieceLeft.getPlayer() == pawn.getPlayer()) ||
                    (pieceRight != null && pieceRight.getClass() == PawnPiece.class && pieceRight.getPlayer() == pawn.getPlayer())) {
                return 0;
            }
        }

        return 1;

    }


    /* check if a pawn is isolated, that is, there is no pawn on an adjacent column */
    public static int pawnIsBlocked(PawnPiece pawn) throws Exception {

        if(pawn.validDestinationSet().size() == 0) {
            return 1;
        } else {
            return 0;
        }

    }



}

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
        rankMap.put(KingPiece.class, 100);
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

        return rankMap.get(piece.getClass());
    }



}

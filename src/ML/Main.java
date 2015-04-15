package ML;

import com.chess.Board;
import com.chess.Game;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Greg Pastorek on 4/14/2015.
 */
public class Main {

    //Score: 14 - 17 :::::::::: 0 vs 0
    public static void main(String[] args) throws Exception {
        final Game game = new Game();

        /* parameters */
        int iterations = 100;
        game.setAIDifficulty(0, 0);

        while(iterations-- > 0) {

            List<Board> boards = new LinkedList<Board>();

            game.newGame(3);

            boards.add(game.getBoard());

            /* game is started, loop here */
            while (game.isRunning()) {
                game.makeMove();
                boards.add(game.getBoard());
                game.checkIfGameOver();
            }

            int winner = game.getLastWinner();

            if(winner != -1) {
                System.out.println("" + winner + " won");
            }

        }

        int p1score = game.getPlayerScore(1);
        int p2score = game.getPlayerScore(2);

        System.out.println("Score: " + p1score + " - " + p2score);


    }

}

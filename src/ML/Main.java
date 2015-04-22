package ML;

import com.chess.Board;
import com.chess.ChessAI;
import com.chess.Game;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Greg Pastorek on 4/14/2015.
 */
public class Main {



    static double[] weights = {
            10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0, 10, 1, 0, 0,
            10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5, 10, 1, 5, 5,
            -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0, -10, -1, 0, 0,
            -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5, -10, -1, -5, -5
    };
    /*

     */
    //greg
    public static void main(String[] args) throws Exception {
        final Game game = new Game();

        /* parameters */
        int iterations = 100;
        game.setAIDifficulty(6, 3);
        int max_turns = 200;

        /* evaulation function weights set */
        game.setAiEvaluationWeights(weights);

        double[] playerSpeed = new double[2];

        while(iterations-- > 0) {

            System.out.println("Starting game");

            int turn = 0;
            List<Board> boards = new LinkedList<Board>();

            game.newGame(3);

            boards.add(game.getBoard());

            /* game is started, loop here */
            while (game.isRunning()) {
                //System.out.println("666");
                if(turn >= max_turns) break;
                turn++;
                int player = game.getPlayerTurn();

                long t1 = System.currentTimeMillis();
                game.makeMove();
                long t2 = System.currentTimeMillis();

                long dt = t2 - t1;
                playerSpeed[player-1] = (playerSpeed[player-1] * (turn-1) + dt) / (double)turn;

                //System.out.println("Move took " + (t2-t1) + " milliseconds for player " + player);
                System.out.println("Average move time for player " + player + " = " + playerSpeed[player-1]);

                boards.add(game.getBoard());

                game.checkIfGameOver();
            }

            int winner;
            if(turn >= max_turns) {
                winner = -1;
            } else {
                winner = game.getLastWinner();
            }

            int player = 1;
            for(int i = boards.size()-1; i > 0; i--) {
                Board board = boards.get(i);
                Board prev_board = boards.get(i-1);
                double d = ChessAI.scoreBoard(board, player, weights) - ChessAI.scoreBoard(prev_board, player, weights);
            }

            if(winner != -1) {
                System.out.println("" + winner + " won");
            }

            System.out.println("Average move time for player 1 = " + playerSpeed[0]);
            System.out.println("Average move time for player 2 = " + playerSpeed[1]);

        }

        int p1score = game.getPlayerScore(1);
        int p2score = game.getPlayerScore(2);

        System.out.println("Score: " + p1score + " - " + p2score);


    }

}

package ML;

import com.chess.Board;
import com.chess.ChessAI;
import com.chess.Game;
import com.sun.javaws.exceptions.InvalidArgumentException;

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

            double lambda = 0.7;
            double alpha = 1.0;

            updateWeights(boards, winner, lambda, alpha);

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

    /* update weight vector using temporal difference learning update rule */
    private static void updateWeights(List<Board> boards, int winner, double lambda, double alpha) throws Exception {
        int player = 1;

        int N = boards.size();
        double[] new_weights = weights.clone();
        double[] d = new double[N-1];

        /* N-1th temporal difference is 1000 for win, -1000 for loss, and 0 for tie */
        d[N-1] = (winner == player) ? 1000 : (winner == (player ^ 3) ? -1000 : 0);

        for(int i = N-2; i > 0; i--) {
            Board board = boards.get(i+1);
            Board prev_board = boards.get(i);
            d[i] = ChessAI.scoreBoard(board, player, weights) - ChessAI.scoreBoard(prev_board, player, weights);
            double[] gradient = gradient_scoreBoard(board, player);
            double s = computeTdScalar(lambda, N, d[i], i);
            vectorSum(new_weights, gradient, alpha*s);
        }
    }

    /* compute the scalar for the ith temporal difference update rule */
    private static double computeTdScalar(double lambda, int n, double d, int i) {
        double s = 0;
        for(int j=i; j < n -1; j++)
            s+= Math.pow(lambda, j-i) * d;
        return s;
    }

    /* compute vector sum result = result + scalar*vector */
    /* added to 'result' and manipulates array            */
    private static void vectorSum(double[] result, double[] vector, double scalar) throws InvalidArgumentException {

        if(result.length != vector.length) {
            throw new InvalidArgumentException(new String[]{"Dimension mismatch"});
        }

        for(int i = 0; i < result.length; i++) {
            result[i] += vector[i] * scalar;
        }

    }


    /* compute gradient vector for scoring function */
    private static double[] gradient_scoreBoard(Board board, int player) throws Exception {

        double[] gradient = new double[weights.length];

        for(int i = 0; i < weights.length; i++) {
            double[] w = new double[weights.length];
            w[i] = 1;
            gradient[i] = ChessAI.scoreBoard(board, player, w);
        }

        return gradient;
    }

}

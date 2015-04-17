package ML;

import com.chess.Board;
import com.chess.Game;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Greg Pastorek on 4/14/2015.
 */
public class Main {

    /*

     */
    public static void main(String[] args) throws Exception {
        final Game game = new Game();

        /* parameters */
        int iterations = 100;
        game.setAIDifficulty(2, 1);
        double [] player1speed= new double[10];
        double [] player2speed= new double[10];
        while(iterations-- > 0) {
            List<Board> boards = new LinkedList<Board>();

            game.newGame(3);

            boards.add(game.getBoard());
            int turns=100;
            /* game is started, loop here */
            while (game.isRunning()) {
                System.out.println(turns);
                turns--;
                int curPlayer= game.getPlayerTurn();
                long t1 = System.currentTimeMillis();
                game.makeMove();
                long t2 = System.currentTimeMillis();
                if (turns>80) {
                    if (curPlayer == 1) {
                        player1speed[(100-turns)/2]=t2-t1;
                    } else {
                        player2speed[(100-turns)/2]=t2-t1;
                    }
                }
                System.out.println("Move took " + (t2-t1) + " milliseconds for player"+curPlayer);
                if (turns<80){
                    double sum = 0;
                    double average=0;
                    for(int i=0; i < 10; i++){
                        sum = sum + player1speed[i];
                    }
                    average = sum / 10.0;
                    System.out.println("Player 1 took "+((Double)average).toString());
                    sum = 0;
                    for(int i=0; i < 10; i++){
                        sum = sum + player2speed[i];
                    }
                    average = sum / 10.0;
                    System.out.println("Player 2 took "+((Double)average).toString());

                }
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

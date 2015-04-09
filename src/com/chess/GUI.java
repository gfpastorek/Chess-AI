package com.chess;

/**
 * Created by Greg Pastorek on 2/17/2015.
 */


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.chess.pieces.*;
import com.sun.javaws.exceptions.InvalidArgumentException;


/*
    Test plan:
    Run and make sure all pieces are in correct place


 */

/*
 * GUI design inspired by: http://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel
 */
public class GUI {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSpaces;
    private JPanel mainWindow;

    private int boardDimX;
    private int boardDimY;
    private int windowDimX;
    private int windowDimY;

    private final Image BLANK_SPACE = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    private Image[][] pieceIcons = new Image[2][8];

    /* used for referencing the pieces in the array "gamePieceIcons" */
    public static final int KING = 0, QUEEN = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5, KILLERQUEEN = 6, CHUCKNORRIS = 7;
    public static final int BLACK = 0, WHITE = 1;

    private final File workingDirectory = new File(System.getProperty("user.dir"));
    private final File imageDirectory = new File(workingDirectory, "img");

    private JLabel player1NameLabel = new JLabel("Player 1:");
    private JLabel player1ScoreLabel = new JLabel("0");
    private JLabel player2NameLabel = new JLabel("Player 2:");
    private JLabel player2ScoreLabel = new JLabel("0");

    private JButton startButton;
    private JButton undoButton;
    private JLabel statusLabel;

    Game controller;

    /* constructor loads images and sets up the board, but does not place the pieces */
    GUI(int dimX, int dimY, Game controller_) throws IOException {
        controller = controller_;
        boardDimX = dimX;
        boardDimY = dimY;
        windowDimX = dimX + 2;
        windowDimY = dimY + 2;
        chessBoardSpaces = new JButton[boardDimX][boardDimY];
        gui.setBorder(new EmptyBorder(6, 6, 6, 6));
        initializeToolbar();
        initializeBoard();
        initializeStatusbar();
        loadPieceImages();
    }

    /* set the start button text, we alternate between "Start" and "Restart" */
    public void setStartButtonText(String text){
        startButton.setText(text);
    }

    public void setUndoButtonEnabled(boolean value){
        undoButton.setEnabled(value);
    }

    public void highlightSpace(int x, int y){
        chessBoardSpaces[x][y].setBorder(new LineBorder(Color.YELLOW));
    }

    public void unhighlightSpace(int x, int y){
        chessBoardSpaces[x][y].setBorder(new LineBorder(Color.BLACK));
    }

    /* initialize the board spaces */
    private final void initializeBoard() {

        mainWindow = new JPanel(new GridLayout(0, 10));
        mainWindow.setBorder(new LineBorder(Color.BLACK));
        gui.add(mainWindow);

        /* create the chess board Spaces */
        for (int x = 0; x < boardDimX; x++) {
            for (int y = 0; y < boardDimY; y++) {
                JButton space = new JButton();
                space.setMargin(new Insets(0,0,0,0));
                space.setIcon(new ImageIcon(BLANK_SPACE));
                if (y % 2 == x % 2) {
                    space.setBackground(Color.WHITE);
                } else {
                    space.setBackground(Color.BLACK);
                }
                space.addActionListener(controller.new SpaceClickListener(x, y));
                chessBoardSpaces[x][y] = space;
            }
        }

        /* fill the top row */
                for (int x = 0; x < windowDimX; x++) {
                    switch(x){
                        case 2:
                            mainWindow.add(player1NameLabel);
                            break;
                        case 5:
                            mainWindow.add(new JLabel("Score: "));
                            break;
                        case 6:
                            mainWindow.add(player1ScoreLabel);
                            break;
                        default:
                            mainWindow.add(new JLabel(""));
                            break;
            }
        }

        /* fill the black non-pawn piece row */
        for (int y = 0; y < boardDimY; y++) {
            for (int x = 0; x < windowDimX; x++) {
                if(x == 0 || x == 9) {
                    mainWindow.add(new JLabel(""));
                } else {
                    mainWindow.add(chessBoardSpaces[x-1][y]);
                }
            }
        }

        /* fill the bottom row */
        for (int x = 0; x < windowDimX; x++) {
            switch(x){
                case 2:
                    mainWindow.add(player2NameLabel);
                    break;
                case 5:
                    mainWindow.add(new JLabel("Score: "));
                    break;
                case 6:
                    mainWindow.add(player2ScoreLabel);
                    break;
                default:
                    mainWindow.add(new JLabel(""));
                    break;
            }
        }
    }


    private final void initializeToolbar(){

        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);

        startButton = new JButton("Start");
        startButton.addActionListener(controller.new StartClickListener());
        tools.add(startButton);

        JButton forfeitButton = new JButton("Forfeit");
        forfeitButton.addActionListener(controller.new ForfeitClickListener());
        tools.add(forfeitButton);

        tools.addSeparator();

        undoButton = new JButton("Undo");
        undoButton.addActionListener(controller.new UndoClickListener());
        tools.add(undoButton);

        tools.addSeparator();
        tools.add(player1NameLabel);
        tools.add(player1ScoreLabel);
        tools.addSeparator();
        tools.add(player2NameLabel);
        tools.add(player2ScoreLabel);
    }

    private final void initializeStatusbar(){

        // create the status bar panel and shove it down the bottom of the frame
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        gui.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(gui.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("Ready!");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
    }

    /* set text at bottom of app */
    public void setStatusbar(String text){
        statusLabel.setText(text);
    }

    /* set the names of the players on the top toolbar */
    public void setPlayerNames(String player1Name, String player2Name) {
        player1NameLabel.setText(player1Name);
        player2NameLabel.setText(player2Name);
    }

    /* set the scores of players 1 and 2, altering the score display */
    public void setScore(int player1Score, int player2Score){
        player1ScoreLabel.setText(Integer.toString(player1Score));
        player2ScoreLabel.setText(Integer.toString(player2Score));
    }

    /* load chess piece images to image array */
    private void loadPieceImages() throws IOException {
        pieceIcons[BLACK][KING] = ImageIO.read(new File(imageDirectory, "KingBlack.png"));
        pieceIcons[BLACK][QUEEN] = ImageIO.read(new File(imageDirectory, "QueenBlack.png"));
        pieceIcons[BLACK][ROOK] = ImageIO.read(new File(imageDirectory, "RookBlack.png"));
        pieceIcons[BLACK][BISHOP] = ImageIO.read(new File(imageDirectory, "BishopBlack.png"));
        pieceIcons[BLACK][KNIGHT] = ImageIO.read(new File(imageDirectory, "KnightBlack.png"));
        pieceIcons[BLACK][PAWN] = ImageIO.read(new File(imageDirectory, "PawnBlack.png"));
        pieceIcons[BLACK][KILLERQUEEN] = ImageIO.read(new File(imageDirectory, "KillerQueenBlack.png"));
        pieceIcons[BLACK][CHUCKNORRIS] = ImageIO.read(new File(imageDirectory, "ChuckNorrisBlack.png"));
        pieceIcons[WHITE][KING] = ImageIO.read(new File(imageDirectory, "KingWhite.png"));
        pieceIcons[WHITE][QUEEN] = ImageIO.read(new File(imageDirectory, "QueenWhite.png"));
        pieceIcons[WHITE][ROOK] = ImageIO.read(new File(imageDirectory, "RookWhite.png"));
        pieceIcons[WHITE][BISHOP] = ImageIO.read(new File(imageDirectory, "BishopWhite.png"));
        pieceIcons[WHITE][KNIGHT] = ImageIO.read(new File(imageDirectory, "KnightWhite.png"));
        pieceIcons[WHITE][PAWN] = ImageIO.read(new File(imageDirectory, "PawnWhite.png"));
        pieceIcons[WHITE][KILLERQUEEN] = ImageIO.read(new File(imageDirectory, "KillerQueenWhite.png"));
        pieceIcons[WHITE][CHUCKNORRIS] = ImageIO.read(new File(imageDirectory, "ChuckNorrisWhite.png"));
    }


    public final JComponent getMainWindow() {
        return mainWindow;
    }

    public final JComponent getGui() {
        return gui;
    }


    /* remove piece icon from space (x,y) */
    public void clearSpace(int x, int y){
        chessBoardSpaces[x][y].setIcon(new ImageIcon(BLANK_SPACE));
    }

    /* remove pieces from every space on board */
    public void clearAllSpaces(){
        for(int x = 0; x < boardDimX; x++){
            for(int y = 0; y < boardDimY; y++){
                clearSpace(x, y);
            }
        }
    }

    /* fill space at (x,y) with icon for "piece" */
    public void fillSpace(Piece piece, int x, int y) throws InvalidArgumentException {
        int player = piece.getPlayer() - 1;  //get player number, offset by 1
        int pieceId = getPieceId(piece);
        ImageIcon icon = new ImageIcon(pieceIcons[player][pieceId]);
        chessBoardSpaces[x][y].setIcon(icon);
    }

    private int getPieceId(Piece piece) throws InvalidArgumentException {
        Class pieceClass = piece.getClass();
        if(pieceClass == KingPiece.class){
            return KING;
        }
        else if (pieceClass == QueenPiece.class) {
            return QUEEN;
        }
        else if (pieceClass == RookPiece.class) {
            return ROOK;
        }
        else if (pieceClass == BishopPiece.class) {
            return BISHOP;
        }
        else if (pieceClass == KnightPiece.class) {
            return KNIGHT;
        }
        else if (pieceClass == PawnPiece.class) {
            return PAWN;
        }
        else if (pieceClass == KillerQueenPiece.class) {
            return KILLERQUEEN;
        }
        else if (pieceClass == ChuckNorrisPiece.class) {
            return CHUCKNORRIS;
        }
        else {
            String[] s = {"Invalid piece class"};
            throw new InvalidArgumentException(s);
        }
    }

}
package ChessGUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.LineBorder;
import com.chess.*;
import com.sun.javaws.exceptions.InvalidArgumentException;


/**
 * Created by Yuriy on 2/19/2015.
 */
    public class ChessGUI implements ActionListener, MouseListener, MouseMotionListener {
        JFrame window;
        boolean gameRunning=false;
        Game curGame;
        JPanel testref;
        int offset;
        int panelDim;
        int chessBoardWidth=8;
        int chessBoardHeight=8;
        Dimension chessDimension= new Dimension(chessBoardWidth*60,chessBoardHeight*60);
        private JPanel chessBoard;
        private JLayeredPane chessLayers;
        private ImageIcon whiteKnightImage;
        private ImageIcon whiteRookImage;
        private ImageIcon whiteKingImage;
        private ImageIcon whiteQueenImage;
        private ImageIcon whitePawnImage;
        private ImageIcon whiteBishopImage;
        private ImageIcon whiteMountedBishopImage;
        private ImageIcon whiteMountedRookImage;
        private ImageIcon blackKnightImage;
        private ImageIcon blackRookImage;
        private ImageIcon blackKingImage;
        private ImageIcon blackQueenImage;
        private ImageIcon blackPawnImage;
        private ImageIcon blackBishopImage;
        private ImageIcon blackMountedBishopImage;
        private ImageIcon blackMountedRookImage;
        private Clip moveClick;
        private AudioInputStream audioIn;
        private String p1_name;
        private String p2_name;
        private Color tile_one;
        private Color tile_two;

        static final File workingDirectory = new File(System.getProperty("user.dir"));
        static final File imageDirectory = new File(workingDirectory, "src\\ChessGUI\\ChessPiecePNG");
    
        private JLabel curMovedPiece;
        private JPanel originalTile;
        public ChessGUI() {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                //silently ignore
            }
            chessLayers = new JLayeredPane();
            chessLayers.addMouseListener(this);
            chessLayers.addMouseMotionListener(this);
            chessLayers.setMinimumSize(chessDimension);
            setSourceFiles();   // loads all the images
            initializeBoard();  // sets up board square images
         //   initializePieces(); // sets up piece images
        }
        public void launchGUI(){
            window = new JFrame("Yuriy Chess");
            window.setSize(63 * 8, 70 * 8); //hardcoded window size
            window.getContentPane().add(chessLayers);
            setUpMenu(window);
            window.setVisible(true);
            setDimensions();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        }

        /*private void initializeButton(JPanel myPanel) {
            JButton button = new JButton("Click me");
            button.addActionListener(this);
            myPanel.add(button, BorderLayout.SOUTH);
        }*/

        private void initializeBoard() {
            chessBoard = new JPanel();
            chessBoard.setName("chessBoard");
            chessLayers.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
            chessBoard.setMinimumSize(chessDimension);
            chessBoard.setBorder(new LineBorder(Color.BLACK));
            chessBoard.setBounds(0, 0, chessBoardWidth*60,chessBoardHeight*60);
            chessBoard.setLayout(new GridLayout(8,8));
            chessBoard.getLayout();
            tile_one=new Color(209, 139, 71);
            tile_two=new Color(255, 206, 158);
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {               // square subdivision
                    JPanel curPiece = new JPanel(new BorderLayout());
                    curPiece.setName("chessTile");
                    if ((x % 2 == 1 && y % 2 == 1)|| (x % 2 == 0 && y % 2 == 0)) {
                        curPiece.setBackground(tile_one);
                    }
                    else {
                        curPiece.setBackground(tile_two);
                    }
                    chessBoard.add(curPiece);
                }
            }
        }
        private void setUpMenu(JFrame window) {
            JMenuBar menubar = new JMenuBar();
            JMenu options = new JMenu("Game");
            JMenuItem newGame = new JMenuItem("New Game against easy AI");
            newGame.addActionListener(this);
            options.add(newGame);
            JMenuItem newMediumGame = new JMenuItem("New Game against medium AI");
            newMediumGame.addActionListener(this);
            options.add(newMediumGame);
            JMenuItem newPlayer = new JMenuItem("New Game against Player");
            newPlayer.addActionListener(this);
            options.add(newPlayer);
            JMenuItem AIDance = new JMenuItem("Watch AI dance");
            AIDance.addActionListener(this);
            options.add(AIDance);
            JMenuItem undoButton = new JMenuItem("Undo");
            undoButton.addActionListener(this);
            options.add(undoButton);
            JMenuItem curScore = new JMenuItem("Get Score");
            curScore.addActionListener(this);
            options.add(curScore);
            JMenuItem helperAI = new JMenuItem("Get Help From AI Overlords");
            helperAI.addActionListener(this);
            options.add(helperAI);
            menubar.add(options);
            window.setJMenuBar(menubar);
        }
        private void setSourceFiles(){
            whiteKnightImage=  new ImageIcon(imageDirectory  + "\\whiteKnight.png");
            whiteRookImage=  new ImageIcon(imageDirectory  + "\\whiteRook.png");
            whiteKingImage=  new ImageIcon(imageDirectory  + "\\whiteKing.png");
            whiteQueenImage= new ImageIcon(imageDirectory  + "\\whiteQueen.png");
            whitePawnImage=  new ImageIcon(imageDirectory  + "\\whitePawn.png");
            whiteBishopImage=  new ImageIcon(imageDirectory  + "\\whiteBishop.png");
            whiteMountedBishopImage=  new ImageIcon(imageDirectory  + "\\whiteMountedBishop.png");
            whiteMountedRookImage=  new ImageIcon(imageDirectory  + "\\whiteMountedRook.png");
            blackKnightImage=  new ImageIcon(imageDirectory  + "\\blackKnight.png");
            blackRookImage=  new ImageIcon(imageDirectory  + "\\blackRook.png");
            blackKingImage=  new ImageIcon(imageDirectory  + "\\blackKing.png");
            blackQueenImage= new ImageIcon(imageDirectory  + "\\blackQueen.png");
            blackPawnImage=  new ImageIcon(imageDirectory  + "\\blackPawn.png");
            blackBishopImage=  new ImageIcon(imageDirectory  + "\\blackBishop.png");
            blackMountedBishopImage=  new ImageIcon(imageDirectory  + "\\blackMountedBishop.png");
            blackMountedRookImage=  new ImageIcon(imageDirectory  + "\\blackMountedRook.png");
            try {
                audioIn = AudioSystem.getAudioInputStream(new File(imageDirectory  + "\\ChessMove.wav"));
                moveClick = AudioSystem.getClip();
                moveClick.open(audioIn);
            }
            catch (Exception e){
                moveClick=null;
            }
        }

        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getActionCommand()=="Undo"){
                    curGame.undoTrigger();
                }
                else if (e.getActionCommand()=="New Game against easy AI"){
                    gameRunning=true;
                    curGame.newGame(1);
                }
                else if (e.getActionCommand()=="New Game against medium AI"){
                    gameRunning=true;
                    curGame.newGame(4);
                }
                else if (e.getActionCommand()=="New Game against Player"){
                    gameRunning=true;
                    curGame.newGame(2);
                }
                else if (e.getActionCommand()=="Watch AI dance"){
                    gameRunning=true;
                    curGame.newGame(3);
                }
                else if (e.getActionCommand()=="New Custom"){
                    //curGame.newCustom();
                }
                else if (e.getActionCommand()=="Get Score"){
                    int player_one_score= curGame.getPlayerScore(1);
                    int player_two_score= curGame.getPlayerScore(2);
                    String curScore= p1_name+": "+Integer.toString(player_one_score)+" "+p2_name+": "+Integer.toString(player_two_score);
                    JOptionPane.showMessageDialog(null,curScore,"Current Score",JOptionPane.WARNING_MESSAGE);
                }
                else if (e.getActionCommand()=="Get Help From AI Overlords"){
                    try {
                        curGame.getAIHelp();
                    }
                    catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (InvalidArgumentException e1) {
                e1.printStackTrace();
            }
        }
        public void setPlayerNames(String player_one, String player_two){
            p1_name= player_one;
            p2_name= player_two;
        }
        public void mousePressed(MouseEvent e){
            if (gameRunning) {
                curMovedPiece = null;
                Component c = chessBoard.findComponentAt(e.getX(), e.getY());

                if (c.getName() == "chessTile") {
                    return;
                } else if (c instanceof JLabel) {
                    curMovedPiece = (JLabel) c;
                    originalTile = (JPanel) curMovedPiece.getParent();
                } else {
                    return;
                }

            /*Point parentLocation = c.getParent().getLocation();
            xAdjustment = parentLocation.x - e.getX();
            yAdjustment = parentLocation.y - e.getY();
            chessPiece = (JLabel)c;

            chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());*/
                curMovedPiece.setLocation(e.getX() - 30, e.getY() - 30);
                chessLayers.add(curMovedPiece, JLayeredPane.DRAG_LAYER);
            }
        }

        public void mouseClicked(MouseEvent e) {

        }
        public void mouseDragged(MouseEvent e){
            if(gameRunning) {
                if (curMovedPiece == null) {
                    return;
                }
                curMovedPiece.setLocation(e.getX() - 30, e.getY() - 30);
            }
        }
        public void mouseReleased(MouseEvent e){
            if (gameRunning) {
                if (curMovedPiece == null) {
                    return;
                }

                curMovedPiece.setVisible(false);
                Component newLoc = chessBoard.findComponentAt(e.getX(), e.getY());
                if (newLoc == null) {
                    originalTile.add(curMovedPiece);
                    curMovedPiece.setVisible(true);
                    return;
                }
                Container newTile = (Container) newLoc;
                int[] moveSet = new int[4];
                moveSet[0] = originalTile.getX();
                moveSet[1] = originalTile.getY();
                moveSet[2] = newLoc.getX();
                moveSet[3] = newLoc.getY();

                if (newLoc.getName() == "chessTile") {
                    if ((curGame.moveReceiver(moveSet))) {
                        newTile.add(curMovedPiece);
                        if (moveClick != null) {
                            moveClick.start();
                            moveClick.setFramePosition(0);
                        }
                        curMovedPiece.setVisible(true);
                        return;
                    }
                } else if (newLoc instanceof JLabel) {
                    JPanel parentTile = (JPanel) newLoc.getParent();
                    moveSet[2] = parentTile.getX();
                    moveSet[3] = parentTile.getY();
                    if ((curGame.moveReceiver(moveSet))) {
                        parentTile.remove(0);
                        parentTile.add(curMovedPiece);
                        if (moveClick != null) {
                            moveClick.start();
                            moveClick.setFramePosition(0);
                        }
                        curMovedPiece.setVisible(true);
                        return;
                    }
                }
                originalTile.add(curMovedPiece);
                curMovedPiece.setVisible(true);
            }
            return;
        }
        public void mouseMoved(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e){

        }
        public void mouseExited(MouseEvent e) {

        }
        public void setDimensions(){
            testref= (JPanel)chessBoard.getComponent(0);
            offset= testref.getX();
            testref= (JPanel)chessBoard.getComponent(1);
            panelDim= testref.getX()-offset;
        }
        public int getOffset(){
            return offset;
        }
        public int getSpacing(){
            return panelDim;
        }
        public void setModel(Game newModel){
            curGame=newModel;
        }
        public void gameOver(){
            gameRunning=false;
        }
        public void highlightHelperSquare(Integer [] move){
            JPanel curSquare= (JPanel)chessBoard.getComponent(8*move[1]+move[0]);
            curSquare.setBackground(new Color(50,50,150));
            curSquare= (JPanel)chessBoard.getComponent(8*move[3]+move[2]);
            curSquare.setBackground(new Color(50,50,150));
            chessBoard.revalidate();
            chessBoard.repaint();
        }
        public void updatePieces(Board curBoard){
            int width= curBoard.getMaxX();
            int length= curBoard.getMaxY();
            JPanel curPanel;
            JLabel curLabel;
            Piece curPiece;
            for (int i=0; i< width; i++){
                for (int j=0; j< length; j++){
                    curPanel= (JPanel)chessBoard.getComponent(i*width+j);
                    if (curPanel.getComponentCount()>0) {
                        curPanel.remove(0);
                    }
                    curPiece= curBoard.getPiece(j,i);
                    if (curPiece!=null) {
                        curLabel = new JLabel();
                        if (curPiece.getPlayer() == 1) {
                            if (curPiece.getClass() == com.chess.pieces.KingPiece.class) {
                                curLabel.setIcon(whiteKingImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.BishopPiece.class) {
                                curLabel.setIcon(whiteBishopImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.KnightPiece.class) {
                                curLabel.setIcon(whiteKnightImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.RookPiece.class) {
                                curLabel.setIcon(whiteRookImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.PawnPiece.class) {
                                curLabel.setIcon(whitePawnImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.QueenPiece.class) {
                                curLabel.setIcon(whiteQueenImage);
                            }
                        } else {
                            if (curPiece.getClass() == com.chess.pieces.KingPiece.class) {
                                curLabel.setIcon(blackKingImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.BishopPiece.class) {
                                curLabel.setIcon(blackBishopImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.KnightPiece.class) {
                                curLabel.setIcon(blackKnightImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.RookPiece.class) {
                                curLabel.setIcon(blackRookImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.PawnPiece.class) {
                                curLabel.setIcon(blackPawnImage);
                            }
                            if (curPiece.getClass() == com.chess.pieces.QueenPiece.class) {
                                curLabel.setIcon(blackQueenImage);
                            }
                        }
                        curPanel.add(curLabel);
                    }

                }
            }
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {               // square subdivision
                    JPanel curTile= (JPanel)chessBoard.getComponent(y*8+x);
                    if ((x % 2 == 1 && y % 2 == 1)|| (x % 2 == 0 && y % 2 == 0)) {
                        curTile.setBackground(tile_one);
                    }
                    else {
                        curTile.setBackground(tile_two);
                    }
                }
            }
            chessBoard.revalidate();
            chessBoard.repaint();
        }
public String getPlayerName(int number){
    return JOptionPane.showInputDialog(window, "Enter player "+((Integer)number).toString()+"'s name.");
}
}

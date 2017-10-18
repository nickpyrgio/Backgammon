package backgammon;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Nikos
 */
public class EventHandler extends JFrame implements ActionListener,MouseListener , MouseMotionListener {
           
   
    private int x;
    private int y;
    private JButton cancelButton;
    private JButton playButton; 
    private JButton restart; 
    private JButton StartNewGameButton; 
    private JButton rollDice;      
    private Board board;        
    private State state;
    private Piece selectedPiece;
    private GamePlayerFather player;
    
    public EventHandler() 
    {         
        super("Backgammon");            
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);           
        super.setResizable(true);
        super.setVisible(false);    
        
    }
    
    private JPanel mainPanel()
    {           
        JPanel mainComponent;
        
        this.state = new State();
        this.state.boardSetUp();
        this.board = new Board();
        this.board.boardSetUp();        
        this.board.addMouseListener(this);
        this.board.addMouseMotionListener(this);
        
        mainComponent = new JPanel();          
        BorderLayout b1 = new BorderLayout();
        mainComponent.setLayout(b1);                
        mainComponent.add(this.buttonPanel(),BorderLayout.SOUTH);        
        mainComponent.add(this.board,BorderLayout.CENTER);                         
        mainComponent.setPreferredSize(new Dimension(1024, 690));
        mainComponent.setVisible(true);                
        return mainComponent;
    }    
    
    private JPanel buttonPanel()
    {
        JPanel buttonPanel = new JPanel();        
        GridLayout g1 = new GridLayout(1, 2,0, 0);
        buttonPanel.setLayout(g1);
        
        this.cancelButton = new JButton("Cancel");        
        this.playButton = new JButton("Play ");
        this.StartNewGameButton = new JButton("Start Game !!!");
        this.restart = new JButton("Restart !");
        this.rollDice = new JButton("Roll Dice!");
                
        this.StartNewGameButton.setEnabled(true);
        this.restart.setEnabled(false);
        this.cancelButton.setEnabled(false);
        this.playButton.setEnabled(false);                         
        this.rollDice.setEnabled(false);                         
        
        this.cancelButton.addActionListener(this);
        this.playButton.addActionListener(this);        
        this.StartNewGameButton.addActionListener(this);        
        this.restart.addActionListener(this); 
        this.rollDice.addActionListener(this); 
        
        buttonPanel.add(StartNewGameButton);    
        buttonPanel.add(playButton);   
        buttonPanel.add(rollDice);
        buttonPanel.add(cancelButton);
        buttonPanel.add(restart);    
        return buttonPanel;
    }        
    
    protected void showFrame(boolean isVisible) 
    {                     
        this.add(mainPanel());      
        this.setMinimumSize(new Dimension(1040, 744));             
        this.setVisible(true);                 
        this.setPreferredSize(new Dimension(1040, 744));
        this.pack(); //uses preffered size field to adjust to screen        
        this.setLocationRelativeTo(null );        
    }
    
    private void computerPlays()
    {
        long startTime, duration;                                                                                                                           
        startTime = System.currentTimeMillis();        
        State max = player.ExpectiMiniMax(state);        
        duration = System.currentTimeMillis() - startTime;
        System.out.println("Children creation in : " + EventHandler.convertMllis(duration));   
         
        
        Stack<State.TempMove> stack = max.getAllMoves(true);            
        while(!stack.isEmpty())
        {                         
            State.TempMove temp = stack.pop();                       
            this.state.movePiece(temp.getPrevPos(), temp.getNewPos());
            this.board.movePiece(this.board.getArea(temp.getPrevPos()).getPiece(0), temp.getNewPos());
        }                                  
    }
    
    private void start(){
        Object answer;            
        Object selectionValues[];
        selectionValues = new Object[2];

        boolean alphBeta ;
        selectionValues[0] = "Mini max algorithm";
        selectionValues[1] = "AlphBeta MinMax Algoritm";            
        answer = (JOptionPane.showInputDialog(this, "Choose algorithm :", "Algorithm ‼", JOptionPane.PLAIN_MESSAGE, null, selectionValues, selectionValues[0]))/*.toString()*/;
        if(answer == null )  return;
        alphBeta = !answer.equals(selectionValues[0]);                        
        selectionValues = new Object[4];
        selectionValues[0] = "1";
        selectionValues[1] = "2";
        selectionValues[2] = "3";
        selectionValues[3] = "4";
        answer = (JOptionPane.showInputDialog(this, "Choose depth :", "Max depth ‼", JOptionPane.PLAIN_MESSAGE, null, selectionValues, selectionValues[1]))/*.toString()*/;
        if(answer == null )  return;
        if(alphBeta){
            this.player = new GamePlayerAlphaBeta(Integer.parseInt((String)answer));
        }else{
           this.player = new GamePlayer(Integer.parseInt((String)answer)); 
        }

        this.state.startGame();         
        this.board.startGame(state.getDicePair()[0], state.getDicePair()[1], this.state);
        if(!this.state.getIsWhiteTurn())
        {                
            this.playButton.setEnabled(true);                                                                       
        }            
        this.rollDice.setEnabled(false);                                                  
        this.StartNewGameButton.setEnabled(false);         
        this.restart.setEnabled(true);          
    }
           
    @Override
    public void actionPerformed(ActionEvent e) 
    {       
        if(e.getSource().equals(this.StartNewGameButton))
        {   
            start();                                   
        }
        if (e.getSource().equals(this.playButton))
        {                   
            this.playButton.setEnabled(false);
            this.cancelButton.setEnabled(false);
            if(this.state.getTimesHadBeenPlayed() == this.state.getTimesMustBePlayed())
            {
                this.playButton.setEnabled(false);
                this.rollDice.setEnabled(true); 
                return;
            }                                                  
            this.computerPlays();
            this.rollDice.setEnabled(true);            
            if(this.state.isTerminalState())
            {
                this.StartNewGameButton.setEnabled(true);
                this.playButton.setEnabled(false);
                this.restart.setEnabled(false);
                this.cancelButton.setEnabled(false);
                this.playButton.setEnabled(false);
                this.rollDice.setEnabled(false);               
            }               
         }
         if(e.getSource().equals(this.rollDice))
         {               
            if(this.state.isTerminalState())
            {
                this.StartNewGameButton.setEnabled(true);
                this.playButton.setEnabled(false);
                this.restart.setEnabled(false);
                this.cancelButton.setEnabled(false);
                this.playButton.setEnabled(false);
                this.rollDice.setEnabled(false);  
                return;
            }                   
            this.state.rollDice();
            this.board.setDicePos(this.state.getDicePair()[0], this.state.getDicePair()[1], this.state.getIsWhiteTurn());            
                        
            if(!this.state.getIsWhiteTurn())
            {
                this.playButton.setEnabled(true);
            }                 
            this.cancelButton.setEnabled(false);
            this.rollDice.setEnabled(false);       
            //System.out.println("CALC " + this.state.getTimesMustBePlayed());
            if(this.state.getTimesHadBeenPlayed() == this.state.getTimesMustBePlayed())
            {                   
                this.rollDice.setEnabled(true);     
                this.playButton.setEnabled(false);
            }                      
         }
         if(e.getSource().equals(this.cancelButton))
         {                                 
            this.state.cancelAllMoves();
            this.board.cancelAllMoves();            
            this.cancelButton.setEnabled(false);            
            this.rollDice.setEnabled(false);            
         }
         if(e.getSource().equals(this.restart))
         {
            this.playButton.setEnabled(false);
            start();
         }
    }

    @Override
    public void mousePressed(MouseEvent e) {       
        this.selectedPiece = null;
        if(this.rollDice.isEnabled() || !this.state.getIsWhiteTurn())
        {
            return;
        }
        Component c =  this.board.findComponentAt(e.getX(), e.getY());        
        if(c instanceof RedPiece)
        {       
                        
            if(this.state.getIsWhiteTurn()) {
                this.selectedPiece = null; 
                return;
            }                       
            this.selectedPiece = (RedPiece)c;         
            if(!this.board.getArea(25).isEmpty() &&  this.selectedPiece.getPosition() != 25)
            {                        
                this.selectedPiece = null;
                return;
            }
        }
        else if(c instanceof WhitePiece)
        {            
            if(!this.state.getIsWhiteTurn()) {
                this.selectedPiece = null; 
                return;
            }           
            this.selectedPiece = (Piece)c;  
            
            if(!this.board.getArea(24).isEmpty() &&  this.selectedPiece.getPosition() != 24)
            {               
                this.selectedPiece = null;
                return;
            }                        
        }
        else
        {
            return;
        }        
        x = selectedPiece.getX();
        y = selectedPiece.getY();                    
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {              
        
        if(this.state.getTimesHadBeenPlayed() == this.state.getTimesMustBePlayed() || !this.state.getIsWhiteTurn())
        {                            
            return;
        }                                
        if(this.rollDice.isEnabled())
        {
            return;
        }             
        if(this.state.movePiece(this.coordinatesToAreaPos(e.getX(), e.getY())))
        {            
            Stack<State.TempMove> stack = this.state.getAllMoves(false);                        
            State.TempMove temp = stack.pop();
            board.moveHitPieces(this.state.getAllHitOpCheckers());            
            board.movePiece(this.board.getArea(temp.getPrevPos()).getPiece(0), temp.getNewPos());     
        }
        if(this.state.getTimesHadBeenPlayed() >= 1) this.cancelButton.setEnabled(true);                  
        if(this.state.getTimesHadBeenPlayed() == this.state.getTimesMustBePlayed())
        {
            this.selectedPiece = null;
            this.rollDice.setEnabled(true);                
        }                                            
    }    
    @Override
    public void mouseDragged(MouseEvent e) {                           
         if(selectedPiece == null) return;        
         selectedPiece.setLocation(e.getX(), e.getY());         
    }

    @Override
    public void mouseReleased(MouseEvent e) {        
        if(this.selectedPiece == null) return;     
        if(this.coordinatesToAreaPos(e.getX(), e.getY())== -1)
        {
            this.selectedPiece.setLocation(x, y);
            return;            
        } 
        
        if(this.state.movePiece(selectedPiece.getPosition(), this.coordinatesToAreaPos(e.getX(), e.getY())))
        {       
            board.moveHitPieces(this.state.getAllHitOpCheckers());
            board.movePiece(selectedPiece, this.coordinatesToAreaPos(e.getX(), e.getY()));                 
        }
        else
        {
            //if player made an invalid move return
            //System.out.println("Invalid move");
            this.selectedPiece.setLocation(x, y);
            return;                        
        }                 
        if(this.state.getTimesHadBeenPlayed() >= 1) this.cancelButton.setEnabled(true);  
       
        if(this.state.getTimesHadBeenPlayed() == this.state.getTimesMustBePlayed())
        {
            this.selectedPiece = null;
            this.rollDice.setEnabled(true);                
        }                                       
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
   
    @Override
    public void mouseMoved(MouseEvent e) {
       
    }
        
    /**
     * 
     * @param x X coordinate of Gui Height
     * @param y Y coordinate of Gui Width
     * @return the position of the piece in the array , -1 if invalid pos
     */
    private int coordinatesToAreaPos(int x , int y)
    {        
        //System.out.println("X is :" + x + " , Y is :" + y);
        if(y >= CoordinatePos.LEFT_UP_CORNER_Y && y <= CoordinatePos.UP_LIMIT_AREA_0_TO_11)
        {
            for(int i = 0; i <= 13; i++)
            {
                //System.out.println("x >=" + (109 + i*62) + "&& x <= " + ( (109 + i*62) + 61) );
                if(x >= CoordinatePos.LEFT_UP_CORNER_X + i*CoordinatePos.PIECE_SIZE_X 
                && x <= (CoordinatePos.LEFT_UP_CORNER_X + i*CoordinatePos.PIECE_SIZE_X) + (CoordinatePos.PIECE_SIZE_X -1) )
                {
                    if(i == 13) return 26;
                    else if(i == 6) return 24;
                    else if(i >=7)  return i - 1;                  
                    else return i;                                    
                }
            }            
        }
        else if(y >= CoordinatePos.UP_LIMIT_AREA_23_TO_12 && y <= CoordinatePos.LEFT_DOWN_CORNER_Y + 50)
        {                       
            for(int i = 13; i >= 0; i--)
            {                
                //System.out.println("x >=" + (109 + i*62) + "&& x <= " + ( (109 + i*62) + 61) );
                if(x >= CoordinatePos.LEFT_UP_CORNER_X + i*CoordinatePos.PIECE_SIZE_X 
                && x <= (CoordinatePos.LEFT_UP_CORNER_X + i*CoordinatePos.PIECE_SIZE_X) + (CoordinatePos.PIECE_SIZE_X -1) )
                {
                    if(i == 13) return 27;
                    else if(i == 6) return 25;
                    else if(i <= 5) return 12 + (12 - i -1 );
                    else return 12 + (12 - i) ;
                }                    
            }            
        }
        else
        {        
            return -1;
        }
        return -1;
    }    
    
    public static String convertMllis(long millis)
    {
        long hour = millis/3600000;
        long minutes = (millis%3600000)/60000;
        long sec = ((millis%3600000)%60000)/1000;
        millis =  (((millis%3600000)%3600000)%60000)%1000;
        if(hour == 0 && minutes ==0 && sec == 0) return millis + " ms";
        else if(hour == 0 && minutes ==0) return sec + " seconds " + millis + " ms";
        else if((hour == 0 && minutes !=0)) return minutes + " minutes " + sec + " seconds " + millis + " ms";
        else   return hour + " hours " + minutes + " minutes " + sec + " seconds " + millis + " ms";
    }    
    
    public static void main(String[] args) { 
        
        EventHandler gui = new EventHandler();               
        gui.showFrame(true);
    }    
  
}

package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Nikos
 */
public class GamePlayerAlphaBeta implements GamePlayerFather {
    private int maxDepth;
    private int expandedNodes;
    
    GamePlayerAlphaBeta(int maxDepth)
    {        
        this.maxDepth = maxDepth;
    }    
    
    /**
     * Η μέθοδος ExpectiMiniMaxAlphaBeta προσομοιώνει τον αλγόριθμο MiniMax με πριόνισμα α-β.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει ο MiniMax.
     * @return Την προτεινόμενη κίνηση.
     */
    @Override
    public State ExpectiMiniMax(State state)
    {        
        this.expandedNodes = 0;
        double value;
        double a = Double.NEGATIVE_INFINITY ;              
        double b = Double.POSITIVE_INFINITY; //Ορίζουμε τα α και β.        
        //Ακολουθούμε τα ίδια βήματα με τον ExpectiMiniMax..
        //Αν είναι η σειρα του Max(κόκκινα πούλια).
        if(!state.getIsWhiteTurn())
        {                    
            State maxBoard = new State(Double.NEGATIVE_INFINITY);
            ArrayList<State> children = (state.getChildren()); 
            Collections.shuffle(children);
            for (State child : children) 
            {
               //..αλλά αυτή τη φορά καλούμε τον ExpectMinAlphaBeta
                value = ExpectMinAlphaBeta(child, this.maxDepth - 1, a, b);                
                //System.out.println("VALUE " + value);
                if (value >= maxBoard.getValue()) 
                {
                    maxBoard = child;
                    maxBoard.setValue(value);
                    a = value;
                }                
            }            
            System.out.println(maxBoard.getValue() + " EXPANDED NODES " + this.expandedNodes);
            return maxBoard;
        }
        else //Αν είναι η σειρα του Min(άσπρα πούλια).
        {                       
           State minBoard = new State(Double.POSITIVE_INFINITY);
            ArrayList<State> children = (state.getChildren());            
            for (State child : children) 
            {
               //..αλλά αυτή τη φορά καλούμε τον ExpectMinAlphaBeta
                value = ExpectMaxAlphaBeta(child, this.maxDepth - 1, a, b);
                //System.out.println(board.getValue());
                if (value <= minBoard.getValue()) 
                {
                    minBoard = child;
                    minBoard.setValue(value);
                    b = value;
                }
            }                
            return minBoard;
        }       
        
    }
           
    /**
     * Η μέθοδος ExpectMaxAlphaBeta βρίσκει την καλύτερη κίνηση για τον παίχτη Max με χρήση πριονίσματος α-β.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει ο Max.
     * @param depth Μέγιστο βάθος αναζήτησης.
     * @param a
     * @param b
     * @return Την προτεινόμενη κίνηση.
     */
    private double ExpectMaxAlphaBeta(State state , int depth, double a, double b)
    {            
        this.expandedNodes++;
        double value;
        double maxValue;
        //Τα βήματα είναι τα ίδια με τον απλό ExpectMax..
        if(depth == 0 || state.isTerminalState())
        {                     
           state.evaluate();
           maxValue = state.getValue();
           return maxValue;                                         
        }
        a = Double.NEGATIVE_INFINITY;        
        maxValue = Double.NEGATIVE_INFINITY;
        ArrayList<State> children = state.getChanceChildren();//new ArrayList<State>(state.getChanceChildren());
        for (State child : children)
        {                      
            value = diceAlphaBeta(child, depth,a , b);
            if(value >= maxValue)
            {                                                
                maxValue = value;
            }         
            //..με την διαφορά ότι ελέγχουμε και αν η αξία της κίνησης που επιστρέφει η 
            //diceAlphaBeta είναι μεγαλύτερη από το β.Αν ναι τότε δεν χρειάζεται να ψάξουμε
            //το δέντρο από κει και κάτω.Επιστρέφουμε κατευθείαν το maxBoard.
            if(value >= b) {
                maxValue = value;               
                return maxValue;
            }
            a = Math.max(value, a);            
        }                                  
        return maxValue;
    }
    
    /**
     * Η μέθοδος ExpectMinAlphaBeta βρίσκει την καλύτερη κίνηση για τον παίχτη Min με χρήση πριονίσματος α-β.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει ο Min.
     * @param depth Μέγιστο βάθος αναζήτησης.
     * @param a
     * @param b
     * @return Την προτεινόμενη κίνηση.
     */
    private double ExpectMinAlphaBeta(State state , int depth,double a, double b)
    {
        this.expandedNodes++;
        double value;
        double minValue;
        //Τα βήματα είναι τα ίδια με τον απλό ExpectMin..
        if(depth == 0 || state.isTerminalState())
        {                  
           state.evaluate();
           minValue = state.getValue();
           return minValue;                                         
        } 
        
        ArrayList<State> children = state.getChanceChildren();//new ArrayList<State>(state.getChanceChildren());        
        minValue = Double.POSITIVE_INFINITY;
        b = Double.POSITIVE_INFINITY;        
        for (State child : children)
        {                        
            value = diceAlphaBeta(child,depth,a , b);                
            if(value <= minValue)
            {                                                
                minValue = value;
            }    
            //..με την διαφορά ότι ελέγχουμε και αν η αξία της κίνησης που επιστρέφει η 
            //diceAlphaBeta είναι μικρότερη από το α.Αν ναι τότε δεν χρειάζεται να ψάξουμε
            //το δέντρο από κει και κάτω.Επιστρέφουμε κατευθείαν το minBoard.
            if(value <= a){
                minValue = value;
                return minValue;
            }
            b = Math.min(value, b);                        
        }
        return minValue;  
    }    
    
    /**
     * Η μέθοδος diceAlphaBeta βρίσκει την καλύτερη κίνηση για τον παίχτη-ζάρι με χρήση πριονίσματος α-β.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει η dice.
     * @param depth Μέγιστο βάθος αναζήτησης.
     * @param a
     * @param b
     * @return Την προτεινόμενη κίνηση.
     */
    private double diceAlphaBeta(State state ,int depth ,double a, double b)
    {      
        this.expandedNodes++;
        //Τα βήματα είναι τα ίδια με την απλή dice απλα καλούμε τους ExpectMaxAlphaBeta 
        //και ExpectMinAlphaBeta αντίστοιχα.        
        double value = 0;        
        //double d = 30;
        //double s = -30;        
        ArrayList<State> children = state.getChildren();//new ArrayList<State>(state.getChildren());       
        for (State child : children)
        {                    
            value += child.getIsWhiteTurn() 
                    ? ExpectMaxAlphaBeta(child, depth - 1 , a , b)                      
                    : ExpectMinAlphaBeta(child, depth - 1, a , b);
            //if(value > d) return value;
            //if(value < s) return value;
        } 
        value = value*state.probability();
        return value;
    }     
    
    @Override
    public int getExpandedNodes(){
        return this.expandedNodes;
    }        
    
}

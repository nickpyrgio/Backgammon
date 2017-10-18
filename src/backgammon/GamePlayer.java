package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

/**
 * Η κλάση GamePlayer αντιπροσωπεύει έναν αυτόνομο παίχτη του παιχνιδιού(παίχτης-υπολογιστής)
 * @author Nikos
 */
public class GamePlayer implements GamePlayerFather
{
    private int maxDepth;  
    private int expandedNodes;
    
    /**
     * Κατασκευαστής αντικειμένου GamePlayer.
     * @param maxDepth Μέγιστο βάθος αναζήτησης στο δέντρο.
     */
    GamePlayer(int maxDepth)
    {        
        this.maxDepth = maxDepth;
    }
                 
    //long startTime,duration = 0;    
    //static long total,sum;
    //Initiates the ExpectiMiniMax algorithm
	
    /**
     * Η μέθοδος ExpectiMiniMax προσομοιώνει τον αλγόριθμο MiniMax χωρίς πριόνισμα α-β.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει ο MiniMax.
     * @return Την προτεινόμενη κίνηση.
     */
    @Override
    public State ExpectiMiniMax(State state)
    {   
        this.expandedNodes = 0;
        //Αν είναι η σειρα του Max(κόκκινα πούλια).
        if(!state.getIsWhiteTurn())
        {                    
            State maxBoard = new State(Integer.MIN_VALUE); //Τοπικό αντικείμενο τύπου State στο οποίο δίνουμε την minimum αξία.
            
            //Αποθηκεύουμε τα παιδιά του State που πήραμε ως παράμετρο, με βάση τη ζαριά που ήρθε, σε μια ArrayList.
            ArrayList<State> children = new ArrayList<State> (state.getChildren());                                   
            for (State child : children)
            {               
                //Για κάθε παιδί στην ArrayList, καλώ τον Min να διαλέξει την καλύτερη κίνηση γι'αυτόν.
                State board = ExpectMin(child, this.maxDepth - 1);   
                
                //Αν η αξία της κίνησης που επέστρεψε ο Min είναι μεγαλύτερη της αξίας του τωρινού maxBoard,
                //τότε ορίζω ως maxBoard την κίνηση αυτή.
                if(board.getValue() > maxBoard.getValue())
                {                             
                    maxBoard = child;
                    maxBoard.setValue(board.getValue());
                }                                                
            }
            System.out.println(maxBoard.getValue() + " EXPANDED NODES " + this.expandedNodes);
           return maxBoard; //Επιστρέφω την καλύτερη κίνηση για τον Max(αυτή με το μεγαλύτερο value).
        }
        else //Αν είναι η σειρα του Min(άσπρα πούλια).
        {                       
            State minBoard = new State(Integer.MAX_VALUE);//Τοπικό αντικείμενο τύπου State στο οποίο δίνουμε την maximum αξία.
            
            //Αποθηκεύουμε τα παιδιά του State που πήραμε ως παράμετρο, με βάση τη ζαριά που ήρθε, σε μια ArrayList.
            ArrayList<State> children = new ArrayList<State> (state.getChildren());            
            for (State child : children)
            {   
                //Για κάθε παιδί στην ArrayList, καλώ τον Max να διαλέξει την καλύτερη κίνηση γι'αυτόν.
                State board = ExpectMax(child, this.maxDepth - 1); 
                
                 //Αν η αξία της κίνησης που επέστρεψε ο Max είναι μικρότερη της αξίας του τωρινού minBoard,
                //τότε ορίζω ως minBoard την κίνηση αυτή.
                if(board.getValue() < minBoard.getValue())
                {                             
                    minBoard = child; 
                    minBoard.setValue(board.getValue());
                }                                                
            }          
           return minBoard; //Επιστρέφω την καλύτερη κίνηση για τον Min(αυτή με το μικρότερο value).
        }       
    }
    
    /**
     * Η μέθοδος ExpectMax βρίσκει την καλύτερη κίνηση για τον παίχτη Max.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει ο Max.
     * @param depth Μέγιστο βάθος αναζήτησης.
     * @return Την προτεινόμενη κίνηση.
     */
    private State ExpectMax(State state , int depth)
    {   
        this.expandedNodes++;
        //Αν είμαστε σε βάθος 0 ή σε τελική κατάσταση,αξιολογούμε την κίνηση και την επιστρέφουμε.
        if(depth == 0 || state.isTerminalState())
        {                     
           state.evaluate();           
           return state;                                         
        }        
        
        State maxBoard = new State(Integer.MIN_VALUE);  //Τοπικό αντικείμενο τύπου State στο οποίο δίνουμε την minimum αξία.                                          
        
        //Αποθηκεύουμε τα παιδιά του παίχτη-ζάρι σε μια ArrayList(δηλαδή τις πιθανές ζαριές).
        ArrayList<State> children = new ArrayList<State>(state.getChanceChildren());
        
        //Για καθένα από τα παιδιά του ζαριού βρίσκω την καλύτερη κίνηση μέσω της μεθόδου dice.
        for (State child : children)
        {        
            State board = dice(child,depth);    
            
            //Αν η αξία της κίνησης που επέστρεψε η dice είναι μεγαλύτερη της αξίας του τωρινού maxBoard,
            //τότε ορίζω ως maxBoard την κίνηση αυτή.
            if(board.getValue() > maxBoard.getValue())
            {                                                
                maxBoard = board; 
                maxBoard.setValue(board.getValue());
            }               
        }                                  
        return maxBoard;  //Επιστρέφω την καλύτερη κίνηση για τον Max(αυτή με το μεγαλύτερο value).
    }
    
    /**
     * Η μέθοδος ExpectMin βρίσκει την καλύτερη κίνηση για τον παίχτη Min.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει ο Min.
     * @param depth Μέγιστο βάθος αναζήτησης.
     * @return Την προτεινόμενη κίνηση.
     */
    private State ExpectMin(State state , int depth)
    {
        this.expandedNodes++;
        //Αν είμαστε σε βάθος 0 ή σε τελική κατάσταση,αξιολογούμε την κίνηση και την επιστρέφουμε.
        if(depth == 0 || state.isTerminalState())
        {                  
           state.evaluate();
           return state;                                         
        }     
        
        //Αποθηκεύουμε τα παιδιά του παίχτη-ζάρι σε μια ArrayList(δηλαδή τις πιθανές ζαριές).
        ArrayList<State> children = new ArrayList<State>(state.getChanceChildren());
        
        State minBoard = new State(Integer.MAX_VALUE);  //Τοπικό αντικείμενο τύπου State στο οποίο δίνουμε την maximum αξία. 
        
        for (State child : children)
        {                        
            State board = dice(child,depth); 
            
            //Αν η αξία της κίνησης που επέστρεψε η dice είναι μικρότερη της αξίας του τωρινού minBoard,
            //τότε ορίζω ως minBoard την κίνηση αυτή.
            if(board.getValue() < minBoard.getValue())
            {                                                
                minBoard = board;  
                minBoard.setValue(board.getValue());
            }               
        }       
        return minBoard;  //Επιστρέφω την καλύτερη κίνηση για τον Min(αυτή με το μικρότερο value).
    }    
    
    /**
     * Η μέθοδος dice βρίσκει την καλύτερη κίνηση για τον παίχτη-ζάρι.
     * @param state Το στιγμιότυπο του παιχνιδιού για το οποίο θα τρέξει η dice.
     * @param depth Μέγιστο βάθος αναζήτησης.
     * @return Την προτεινόμενη κίνηση.
     */
    private State dice(State state ,int depth)
    {       
        this.expandedNodes++;
        State avg = new State(0);  //Τοπικό αντικείμενο τύπου State στο οποίο δίνουμε μηδενική αξία. 
        
        //Αποθηκεύουμε τα παιδιά του State που πήραμε ως παράμετρο, με βάση τη ζαριά που εξετάζουμε, σε μια ArrayList.
        ArrayList<State> children = new ArrayList<State>(state.getChildren());                
        for (State child : children)
        {           
            //Για κάθε παιδί στην ArrayList, καλώ τον Max ή τον Min(ανάλογα με το ποιός παίζει)να διαλέξει την καλύτερη κίνηση γι'αυτόν.
            State board = child.getIsWhiteTurn()?ExpectMax(child, depth - 1):ExpectMin(child, depth - 1);                        
            //Ορίζω την αξία του τοπικού αντικειμένου avg να είναι η αξία του συν την αξία της
            //κίνησης που επέστρεψε ο Max ή ο Min.
            avg.setValue(avg.getValue() + board.getValue());                        
        }        
        //Ορίζω την αξία του τοπικού αντικειμένου avg να είναι η αξία του επί την πιθανότητα να έρθει αυτή η ζαριά.
        avg.setValue(avg.getValue()*state.probability());          
        
        return avg; //Επιστρέφω την κίνηση αυτή.
    }                
    @Override
    public int getExpandedNodes(){
        return this.expandedNodes;
    }        
                              
}

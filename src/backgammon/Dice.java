package backgammon;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Η κλάση Dice αντιπροσωπεύει ένα ζάρι του backgammon.
 * @author Nikos
 */
public final class Dice extends JLabel {
    
    private   ImageIcon img[];  //Πίνακας από εικόνες που δείχνουν τις τιμές του ζαριού.
    private int randomNum;      //Ένας τυχαίος integer αριθμός.
    boolean isWhite;            //Boolean μεταβλητή που δείχνει αν το ζάρι είναι άσπρο η κόκκινο.
    
    private static final int DICE_WIDTH = 65;  //Τελικές μεταβλητές στις οποίες αποθηκεύουμε
    private static final int DICE_HEIGHT = 71; //τις διαστάσεις του ζαριού: πλάτος και ύψος.
  
    
    public Dice()
    {
        this(true);
    }
    
    public Dice(int num)
    {
        this.randomNum = num - 1;
    }
     public Dice(boolean isWhite)
     {
         this(0, 0, true);
     }
     public void setIsWhite(boolean isWhite)
     {
         this.isWhite = isWhite;
     }
     
     /**
      * Κατασκευαστής αντικειμένου Dice.
      * @param x Συντεταγμένη x της πάνω-αριστερά γωνίας.
      * @param y Συντεταγμένη y της πάνω-αριστερά γωνίας.
      * @param isWhite true αν το ζάρι ειναι άσπρο, false αν το ζάρι είναι κόκκινο
      */
    public Dice(int x, int y, boolean isWhite)
    {
        this.setBounds( x ,y );  //Ορίζουμε το μέγεθος του ζαριού και τη θέση του.
        this.isWhite= isWhite;   //Οριζουμε αν θα είναι άσπρο ή όχι.
        img = new ImageIcon[12];  //Δημιουργία του πίνακα εικόνων του ζαριού.(12 θέσεις: 
                                  //6 για τις άσπρες εικόνες,6 για τις κόκκινες)
        
        //Αρχικοποίηση των εικόνων του πίνακα.
        img[0] = new ImageIcon("backgammon\\leather\\whiteDice1.png");
        img[1] = new ImageIcon("backgammon\\leather\\whiteDice2.png");
        img[2] = new ImageIcon("backgammon\\leather\\whiteDice3.png");
        img[3] = new ImageIcon("backgammon\\leather\\whiteDice4.png");
        img[4] = new ImageIcon("backgammon\\leather\\whiteDice5.png");
        img[5] = new ImageIcon("backgammon\\leather\\whiteDice6.png");
        
        img[6] = new ImageIcon("backgammon\\leather\\redDice1.png");
        img[7] = new ImageIcon("backgammon\\leather\\redDice2.png");
        img[8] = new ImageIcon("backgammon\\leather\\redDice3.png");
        img[9] = new ImageIcon("backgammon\\leather\\redDice4.png");
        img[10]= new ImageIcon("backgammon\\leather\\redDice5.png");
        img[11]= new ImageIcon("backgammon\\leather\\redDice6.png");        
    }    
    
    
    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, Dice.DICE_WIDTH, Dice.DICE_HEIGHT);
    }        
    
    /**
     * Καθορισμός του μεγέθους και της θέσης του ζαριού.
     * @param x  Συντεταγμένη x της πάνω-αριστερά γωνίας.
     * @param y  Συντεταγμένη y της πάνω-αριστερά γωνίας.
     */
    public void setBounds(int x, int y)
    {
        super.setBounds(x, y, Dice.DICE_WIDTH, Dice.DICE_HEIGHT); 
    }        
     
    /**
     * Η μέθοδος throwDice προσομοιώνει τη ρίξη του ζαριού.
     * @return Την εικόνα που αντιστοιχεί στον αριθμό που έφερε το ζάρι.
     */
     public ImageIcon throwDice()             
     {
         Random random = new Random();         //"Ρίξη" ζαριού: επιλογή ενός τυχαίου 
         this.randomNum = random.nextInt(6);   //αριθμού μεταξύ των 1 και 6.
         
         //Επιλογή της αντοίστιχης εικόνας από τον πίνακα, ανάλογα με το αν 
         //το ζάρι είναι άσπρο ή κόκκινο.
         if(!this.isWhite)
         {
             this.setIcon(img[this.randomNum + 6]);
             return this.img[randomNum + 6];
         }
         this.setIcon(img[randomNum]);         
         return this.img[randomNum];
     }
     
     /**
      * Η μέθοδος setDice  καθορίζει την εικόνα του ζαριού με βάση τη νέα τιμή του.
      * @param num  Νέα τιμή του ζαριού.
      * @return   Την εικόνα που αντιστοιχεί στη νέα τιμή του ζαριού.
      */
     public ImageIcon setDice(int num)
     {
         //Επιλογή της αντοίστιχης εικόνας από τον πίνακα, ανάλογα με το αν 
         //το ζάρι είναι άσπρο ή κόκκινο.
         if(!this.isWhite)
         {
             this.setIcon(img[num + 5]);
             return this.img[num  + 5];
         }
         this.setIcon(img[num - 1]);         
         return this.img[num - 1];         
     }
     
     /**
      * @return Την τιμή του ζαριού.
      */
     public int getRandomNum()
     {
         return this.randomNum + 1;                 
     }
     
     /**
      * Η μέθοδος setRandomNum καθορίζει την τιμή του ζαριού.
      * @param i  Νέα τιμή του ζαριού.
      */
     void setRandomNum(int i)
     {
         this.randomNum = i - 1;
     }
    
}


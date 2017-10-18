package backgammon;



import javax.swing.ImageIcon;

/**
 * Η κλάση RedPiece είναι υποκλάση της Piece και αντιπροσωπεύει ένα άσπρο πούλι.
 * @author Nikos Pyrgiotis
 */
public class WhitePiece extends Piece{
    
    private ImageIcon img;  //Μεταβλητή που "κρατάει" την εικόνα για το άσπρο πούλι.
    
    public WhitePiece(int x,int y )
    {
           this(x, y, -1);
    }
    
    public WhitePiece(int row )
    {
        this(0,0, row);
    }
    
    /**
     * Κατασκευαστής αντικειμένου WhitePiece.
     * @param x  Συντεταγμένη x της πάνω-αριστερά γωνίας.
     * @param y  Συντεταγμένη y της πάνω-αριστερά γωνίας.
     * @param row  Θέση στην οποία θα βρίσκεται το πούλι.
     */
    public WhitePiece( int x, int y , int row )
    {
        super(x,y, row); //Κλήση του κατασκευαστή της Piece.
        this.img = new ImageIcon(PATH + "whiteChecker1.png"); //Ορισμός της εικόνας για το πούλι.
        this.setIcon(img);
    }
    
    /**
     * @return Την εικόνα για το πούλι.
     */
    public ImageIcon getImg()
    {        
        return img;
    }     
    
}

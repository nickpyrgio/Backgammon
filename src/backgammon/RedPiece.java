package backgammon;



import javax.swing.ImageIcon;

/**
 * Η κλάση RedPiece είναι υποκλάση της Piece και αντιπροσωπεύει ένα κόκκινο πούλι.
 * @author Nikos
 */
public class RedPiece extends Piece
{
    
    private ImageIcon img;  //Μεταβλητή που "κρατάει" την εικόνα για το κόκκινο πούλι.
    
    public RedPiece(int row )
    {
        this(0,0, row);
    }
    
    public RedPiece(int x,int y )
    {
           this(x, y, -1);
    }
    
    /**
     * Κατασκευαστής αντικειμένου RedPiece.
     * @param x  Συντεταγμένη x της πάνω-αριστερά γωνίας.
     * @param y  Συντεταγμένη y της πάνω-αριστερά γωνίας.
     * @param row  Θέση στην οποία θα βρίσκεται το πούλι.
     */
    public RedPiece( int x, int y , int row )
    {
        super(x,y, row); //Κλήση του κατασκευαστή της Piece.
        this.img = new ImageIcon(PATH + "redChecker2.png"); //Ορισμός της εικόνας για το πούλι.
        super.setIcon(img);
    }
    
    /**
     * @return Την εικόνα για το πούλι.
     */
    public ImageIcon getImg()
    {        
        return img;
    }     
    
}

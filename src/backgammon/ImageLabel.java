package backgammon;



import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Η κλάση ImageLabel αντιπροσωπεύει την εικόνα για το φόντο που θα έχει το τάβλι.
 * @author USER
 */
class ImageLabel extends JLabel {

   private Image image;
   private ImageIcon imgIcon;   

      
/**
 * Κατασκευαστής αντικειμένου ImageLabel.
 * @param filename Το μονοπάτι που οδηγεί στο αρχείο της εικόνας.
 */
   ImageLabel(String filename) 
   {
      //Αρχικοποίηση μεταβλητων.
      imgIcon = new ImageIcon(filename);
      image = imgIcon.getImage();
   } 

  
/**
 * Κατασκευαστής αντικειμένου ImageLabel.
 * @param icon Αντικείμενο icon με την εικόνα.
 */
   ImageLabel(ImageIcon icon) 
   {

      imgIcon = icon;
      image = imgIcon.getImage();     
   }
       
   /**
    * @return Το αντικείμενο icon με την εικόνα.
    */
   public Image getImgIcon()
   {
       return imgIcon.getImage();
   }
 

    @Override
   public void paint( Graphics g ) {
       super.paint( g);
       g.drawImage(image,  0 , 0 , this.getWidth() , getHeight() , null);       
   }
}

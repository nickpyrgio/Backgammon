package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Nikos
 */
public class Background extends JPanel implements ImageObserver {      
    private Dimension boardSize;
    private ImageLabel imgLabel;
    private JLabel label;
    
    public Background(int width ,int  height, String path)
    {        
        boardSize = new Dimension(width, height); 
        this.setPreferredSize(boardSize);          
        this.setBounds(0, 0, width, height); 
        imgLabel = new ImageLabel (path);
        label = new  JLabel();                                       
        label.setIcon(new ImageIcon(path));
        
        this.add(imgLabel);        
        this.setVisible(true);        
        
    }
   
     @Override
   public void paint( Graphics g ) {

       super.paint( g );
       g.drawImage(this.imgLabel.getImgIcon(),  0 , 0 , getWidth() , getHeight() , null);       
   }   
                  
}

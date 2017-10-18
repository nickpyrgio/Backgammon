package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Η κλάση Evaluator αποτελεί τον αξιολογητή των κινήσεων των παιχτών.
 * @author Nikos
 */
public final class Evaluator {
        
    private State  state; //Δείκτης στο αντικείμενο State(=κίνηση παίχτη-στιγμιότυπο 
                          //παιχνιδιού) προς αξιολόγηση.
    
    //Ακολουθούν τα δίαφορα βάρη που χρησιμοποιούνται 
    //στην αξιολόγηση της κίνησης.
    private double importantHouse; //Σημαντική πόρτα: στην homeArea και σε σημαντικές θέσεις.
    private double tower;          //Πόρτα με πάνω από 3 πούλια("πύργος").
    private double house;          //Απλή πόρτα με δύο πούλια.    
    private double exposed;        //Το πούλι είναι εκτεθειμένο,μπορεί να χτυπηθεί.  
    private double eaten;            //Χτυπάς ένα πούλι του αντιπάλου.
    private double stuck;            //Χτυπάς ένα πούλι του αντιπάλου.
    private double anchor;        
    private double win ;           //Νίκη.
    private double lose ;          //Ήττα.
    private int exposedInHomeArea; //Το πούλι είναι εκτεθειμένο στην homeArea.          
    private int spread;
    private int houseImp;
    /**
     * Κατασκευαστής αντικειμένου Evaluator.
     * @param mode Πόσο επιθετικός θέλουμε να είναι ο αξιολογητής.
     */
    Evaluator(int mode)
    {        
        switch(mode)
        {
            case 1 :
                this.setDefensive();
                break;
            case 2:
                this.setMiddleWay();
            default:
                this.setAggresive();
        }
    }
    
    /**
    * Η μέθοδος evaluate κάνει την αξιολόγηση της κίνησης.
    * @param state Ποιά κίνηση θα αξιολόγήσουμε.
    * @return Την αξία της κίνησης.
    */
    protected  double evaluate(State state)
    {                
         this.state = state;
         
        //Ανάλογα με το σε ποια κατάσταση είναι ο "πατέρας" της κίνησης μας,
        //επιλέγουμε την σωστή ευρετική.
        //Contact: Τουλάχιστον ένα πούλι ενός παίκτη είναι πίσω από ένα πούλι του άλλου παίκτη.
        switch (state.parent.getSituation()) {
        //Race: Το αντίθετο από την contact κατάσταση.
            case 0:
                return  contact();
        //Crashed: Ίδιο με την κατάσταση contact,αλλά με τoν επιπλέον περιορισμό ότι ο
        //παίκτης έχει έξι ή λιγότερα πούλια ακόμα στο παιχνίδι.
            case 2:
                return race();
            default:
                return crashed();
        }
    }
       
    private  void setDefensive()
    {
        
    }    
    
    private void setMiddleWay()
    {
        
    }        
    
    /**
     * Η μέθοδος setAggresive αρχικοποιεί τα βάρη για τον επιθετικό αξιολογητή.
     */
    private  void setAggresive()
    {                
        //this.win = 1000000;
        //this.lose = -1000000;                              
        this.exposed = -100;
        this.exposedInHomeArea = -39;        
        this.eaten = -160;
        this.house = 110;  
        this.houseImp = 50;
        this.spread = -10;
        this.anchor = -50;
        this.tower = -120;
    }
    
    private int portesCount(){
        int portesWhite = 0, portesRed = 0;
        for(int pos = 0 ;pos < 24; pos++){          
           CheckerArea area = state.getArea(pos);
           if(area.isEmpty()) continue;
           if( area.isOccupied()){
               if(area.isWhiteArea() && pos > 10)
                 portesWhite ++;
               else if(!area.isWhiteArea() && pos < 13)
                  portesRed++; 
           }           
        }
        return (portesRed - portesWhite);
    }
    
    private int exposedCount(){
        int exposedWhite = 0, exposedRed = 0;
        
        for(int pos = 0 ;pos < 24; pos++){
           CheckerArea area = state.getArea(pos);
           if(area.isEmpty()) continue;
           if(area.getSize() == 1){
               if(area.isWhiteArea())
                 exposedWhite ++;
               else
                  exposedRed++; 
           }           
        }
        return (exposedRed - exposedWhite);        
    }
    
    private int housesInHomeAreaCount(){
        int portesWhite = 0, portesRed = 0;
        for(int pos = 0 ;pos < 6; pos++){
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;
            if(area.isOccupied() && !area.isWhiteArea())
                portesRed++;               
        }
        for(int pos = 18 ;pos < 24; pos++){
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;
            if(area.isOccupied() && area.isWhiteArea())
                portesWhite++;               
        }        
        return (portesRed - portesWhite);
    } 
    
    private int h4(){
        int exposedInHomeAreaWhite = 0, exposedInHomeAreaRed = 0;
        for(int pos = 0 ;pos < 6; pos++){
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;
            if(area.getSize() == 1 && !area.isWhiteArea())
                exposedInHomeAreaRed++;               
        }
        for(int pos = 18 ;pos < 24; pos++){
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;
            if(area.getSize() == 1 && area.isWhiteArea())
                exposedInHomeAreaWhite++;               
        }        
        return (exposedInHomeAreaRed - exposedInHomeAreaWhite);
    }     
    
    private int h5(){
        CheckerArea red = this.state.getArea(State.RED_DAMAGED_AREA);
        CheckerArea white = this.state.getArea(State.WHITE_DAMAGED_AREA);
        return (red.getSize() - white.getSize());
    }
    
    private int h6(){
        int cntOutWhite = 0, cntOutRed = 0;
        for(int pos = 13 ;pos < 24; pos++)
       {           
            CheckerArea area = state.getArea(pos);   
            if(area.isEmpty()) continue;
            if(!area.isWhiteArea()) cntOutRed += area.getSize();
       }    
        for(int pos = 0 ;pos < 10; pos++)
       {
            CheckerArea area = state.getArea(pos); 
            if(area.isEmpty()) continue;
            if(area.isWhiteArea()) cntOutWhite += area.getSize();
       }   
        cntOutRed += this.state.getArea(State.RED_DAMAGED_AREA).getSize();
        cntOutWhite += this.state.getArea(State.WHITE_DAMAGED_AREA).getSize();
        return cntOutRed - cntOutWhite;
    }       
    
    private int h7(){
       int pipCountWhite= 0;
       int pipCountRed= 0; 
              
       for(int pos = 0 ;pos < 24; pos++)
       {
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;
            if(area.isWhiteArea()) 
                pipCountWhite += area.getSize()*(24 - pos);
            if(!area.isWhiteArea()) 
                pipCountRed += area.getSize()*(pos + 1);
       }         
       return pipCountRed - pipCountWhite;
    }
    
    private int h8(){
        int cntWhite = 0, cntRed = 0;
        for(int pos = 0 ;pos < 6; pos++)
        {
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;            
            if(!area.isWhiteArea()) 
                cntRed += area.getSize();
        }                       
        for(int pos = 18 ;pos < 24; pos++)
        {
            CheckerArea area = state.getArea(pos);
            if(area.isEmpty()) continue;
            if(area.isWhiteArea()) 
                cntWhite += area.getSize();               
        }   
        cntWhite += state.getArea(State.WHITE_FINAL_AREA).getSize();
        cntRed += state.getArea(State.RED_FINAL_AREA).getSize();
        return cntRed -  cntWhite;
    }
        
    /**
     * Η μέθοδος contact αποτελεί την ευρετική που χρησιμοποιούμε στην περίπτωση contact.
     * @return Την αξία της κίνησης.
     */
    private  double contact()
    {      
       double value = 0.0;
       value = this.portesCount()*this.house 
               + this.exposedCount()*this.exposed 
               + this.housesInHomeAreaCount()*this.importantHouse 
               + this.exposedInHomeArea*this.h4() 
               + this.h5()*this.eaten
               + this.h6()*this.anchor
               +  (this.h7())*spread
               ;
               
       
       return   value;
    }

    private  double race()
    {               
       if(!state.allPiecesToHomeArea()){                          
            return this.h8();
       }else{
         return state.getArea(State.RED_FINAL_AREA).getSize() - state.getArea(State.WHITE_FINAL_AREA).getSize();
       }      
    }
    private  double crashed()
    {
        return 0;
    }   
}

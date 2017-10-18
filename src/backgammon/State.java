package backgammon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * @author Nikos Pyrgiotis
 * Class State represents a state in backgammon game Portes
 */
public final class State {

    static final int RED_FINAL_AREA = 26;
    static final int WHITE_FINAL_AREA = 27;
    static final int RED_DAMAGED_AREA = 25;
    static final int WHITE_DAMAGED_AREA = 24;
    
    private Stack<TempMove> tempMoveStack;   //mia oloklhrh kinhsh    
    private Stack<TempMove> hitOpPieces;   //hit pieces of op   
    private int firstDice; // to prwto zari, to vazoume ws to megisto twn 2 zariwn afou riksoume ta zaria
    private int secondDice; // to deutero zari to vazoume ws to elaxisto twn 2 zariwn afou riksoume ta zaria    
    private int timesHadBeenPlayed; // arithmos kinhsen(Temp Move) pou exoume kanei se auto to guro enas paixths(min(Aspros) h max)Kokkinos))
    private int minPossibleMoves; //o elaxistos arithmos twn kinhsewn pou prepei na paiksei upoxrewtika se auto to guro o paixths
    private boolean firstDicePlayed; // boolean pou deixnei an paixthke to prwto zari xrhsimopoeitai otan den exoume ferei diples
    private boolean secDicePlayed; //antistoixa me firstDicePlayed
    private boolean isWhiteTurn;     //boolean pou deixnei poios pazei
    private boolean isDiples; //boolean pou deinei ean ta 2 zaria exoun ish timh, xrhsimopoeitai gia eukolia
    private double value; // heuristic value of state se kapoia stigmh. 
    State parent; // pointer(anafora) ston patera apo ton opoio proekupse mia katastash.Xrhsimopoeitai gia thn euretikh    
    private CheckerArea[] stateCheckerArray;  // pinakas pou periexei perioxes pou perioxoun poulia(se char morfh ta polia, se StringBuffer h perioxh)    
    private static Evaluator  evaluatorWhite = new Evaluator(0);
    private static Evaluator evaluatorRed = new Evaluator(0);
    
    /**
     * Class pou paristanei mia proswrinh kinhsh. Mia oloklhrh kinhsh einai mia stoiva apo proswrines kinhseis.
     * Ama theloume na tis paroume tis kinhseis me th seira h antistrofh ths stoivas mas dinei tis proswrines kinhseis 
     * me th seira pou eginan
     */
    protected  class TempMove
    {
        private int prevPos; // prohgoumenh thesh tou pouliou
        private int newPos; // epomenh thseh tou pouliou
        private final byte PLAYER; // poiou paixth einai to pouli

        //Profanes
        TempMove(int prevPos ,int newPos, byte player)
        {
            this.prevPos = prevPos;
            this.newPos = newPos;
            this.PLAYER = player;
        }

        protected byte getPlayer()
        {
            return this.PLAYER;
        }
        protected int getPrevPos()
        {
            return this.prevPos;
        }
        protected int getNewPos()
        {
            return this.newPos;
        }
    }
    
    /**
     * Constructs a new State of a backgammon game with initial value = 0 
     */
    public State()
    {        
        this(0);
    }

    /**
     * Constructs a new State with initial value the specified value
     * @param value the initial value of the board
     */
    public State(double value)
    {
        this.value = value;
        //arxikopoihsh pediwn ths classhs
        this.tempMoveStack = new Stack<TempMove>();           
        this.hitOpPieces = new Stack<TempMove>();
        this.stateCheckerArray = new CheckerArea[28];        
        for (int i = 0; i < stateCheckerArray.length ; i++)
        {            
            this.stateCheckerArray[i] = new CheckerArea(i);
            //0 - 23 , 24 - 25 , 26 - 27
        }
    }
    
    /**
     * Constructs a copy of the Board.All the values are copied by value NOT by reference.
     * @param state The state of which a copy is demanded
     */
    public State(State state)
    {        
        //copy all the primitive fields by value
        this.value = state.value;
        this.isWhiteTurn = state.isWhiteTurn;
        this.isDiples = state.isDiples;
        this.timesHadBeenPlayed = state.timesHadBeenPlayed;
        this.minPossibleMoves = state.minPossibleMoves;       
        this.firstDice = state.firstDice;
        this.secondDice = state.secondDice;        
        this.firstDicePlayed = state.firstDicePlayed;
        this.secDicePlayed = state.secDicePlayed;
        //dhmiourgoume nees domes dedomenwn gia ta pedia mas
        this.tempMoveStack = new Stack<TempMove>();   
        this.hitOpPieces = new Stack<TempMove>();
        this.stateCheckerArray = new CheckerArea[28];        
        //kai ta arxikopoioume antigrafontas BY VALUE
        for(int i = 0; i < 28 ; i ++ )
        {
            this.stateCheckerArray[i] = new CheckerArea(i);
            this.stateCheckerArray[i].appendPieces(state.stateCheckerArray[i].getSize(), state.stateCheckerArray[i].isWhiteArea());
        }
        for(int i = 0; i < state.tempMoveStack.size()  ; i ++)
        {            
            tempMoveStack.add(new TempMove(state.tempMoveStack.get(i).prevPos, state.tempMoveStack.get(i).newPos, state.tempMoveStack.get(i).getPlayer()));
        }
        /*
        for(int i = 0; i < state.hitOpPieces.size()  ; i ++)
        {            
            hitOpPieces.add(new TempMove(state.hitOpPieces.get(i).prevPos, state.hitOpPieces.get(i).newPos, state.hitOpPieces.get(i).getPlayer()));
        }
         * 
         */
     }
    
    /**
     * Cancel all TempMoves made in this round
     * 
     */
    protected void cancelAllMoves()
    {
        TempMove tempMove;         
        //douleuoume anapoda. Epeidh einai stoiva LIFO h teleutaia kinhsh epistrefei to pouli apo ekei pou prohlthe 
        //kai sunexizoume me thn epomenh prohgoumenh kinhsh mexri na adeiasei h stoiva
        while(!this.tempMoveStack.isEmpty())   
        {                    
            tempMove = this.tempMoveStack.pop();                                                         
            this.stateCheckerArray[tempMove.prevPos].appendPiece(this.stateCheckerArray[tempMove.newPos].removePieceFromTop());            
        }       
        //arxikopoiume ksana ta pedia pou deixnooun ti exei paixtei
        this.timesHadBeenPlayed = 0;
        this.firstDicePlayed = false;
        this.secDicePlayed = false;
        hitOpPieces.clear();
     }       
    
    /**
     * Ypologizei to minPossibleMoves sthn arxh kathe gurou(afou paixtoun ta zaria kai prin paiksei o paixths)
     * @return 
     */
    private int calcMinPossibleMoves() {
        
        if(this.isWhiteTurn) return calcMinPossibleMovesWhite();
        return calcMinPossibleMovesRed();
    }    
    
    private int calcMinPossibleMovesRed()
    {
        //dhmiourgoume antigrafo ths katastashs kai dokimazoume kinhseis.
        //An se opoidhpote shmeio ftasoume to elaxisto epistreofoume to elaxisto
        //Elaxisto einai ean diples 4 , 2 alliws.
        //Ama de vroume 4 h 2 antistoixa epistrefoume ton arithmo epitrepomnwn kinhsewn
        //ws ekshs timesMustBePlayed - timesPlayed.
         State temp = new State(this);
         int timesMustBePlayed;
         int timesPlayed;
         if(temp.isDiples)
         {
             timesMustBePlayed = 4;
             timesPlayed = 0;
             if(!temp.stateCheckerArray[State.RED_DAMAGED_AREA].isEmpty())
             {
                 if(!temp.isValidMove(State.RED_DAMAGED_AREA, 24 - firstDice ))
                 {
                     return 0;// den exoume kinhsh
                 }
                 temp.cancelAllMoves();                                 
                 while(!temp.stateCheckerArray[State.RED_DAMAGED_AREA].isEmpty())
                 {                         
                     temp.movePiece(State.RED_DAMAGED_AREA, 24 - firstDice);
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed ) break;
                 }
                 if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;                 
             }
             for(int area = 23; area >= 0 ; area--)
             {
                 if(temp.stateCheckerArray[area].isEmpty()) continue;
                 if(temp.stateCheckerArray[area].isWhiteArea()) continue;
                 if(temp.movePiece(area, area - firstDice))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1; //check again same area
                 }
             }
             for(int area = 5; area >= 0; area-- )
             {
                 if(temp.movePiece(area, State.RED_FINAL_AREA))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1;
                 }                 
             }
         }
         else
         {
              timesMustBePlayed = 2;
              timesPlayed = 0;
              if(!temp.stateCheckerArray[State.RED_DAMAGED_AREA].isEmpty())
              {
                 if(!temp.isValidMove(State.RED_DAMAGED_AREA, 24 - firstDice)  && !temp.isValidMove(State.RED_DAMAGED_AREA, 24 - secondDice))
                 {
                     return timesPlayed;// = 0
                 }
                 temp.cancelAllMoves();
                 if(temp.stateCheckerArray[State.RED_DAMAGED_AREA].getSize() >= 2)
                 {
                     if(temp.isValidMove(State.RED_DAMAGED_AREA, 24 - firstDice)  && temp.isValidMove(State.RED_DAMAGED_AREA, 24 - secondDice))
                     {
                         return timesMustBePlayed;
                     }
                     temp.cancelAllMoves();
                     if(temp.isValidMove(State.RED_DAMAGED_AREA, 24 - firstDice))
                     {                         
                         return 1;//return 1
                     }
                     temp.cancelAllMoves();
                     if(temp.isValidMove(State.RED_DAMAGED_AREA, 24 - secondDice))
                     {                         
                         return 1;//return 1
                     }
                 }
                 else
                 {
                    if(temp.movePiece(State.RED_DAMAGED_AREA, 24 - firstDice))
                    {
                      for(int area = 23; area >= 0 ; area--)
                      {
                         if(temp.stateCheckerArray[area].isEmpty()) continue;
                         if(temp.stateCheckerArray[area].isWhiteArea()) continue;                            
                         if(temp.movePiece(area, area - secondDice))
                         {                             
                             return timesMustBePlayed;                             
                         }                            
                      }                        
                    }
                    int max = timesPlayed;    
                    timesPlayed = 0;                    
                    if(temp.movePiece(State.RED_DAMAGED_AREA, 24 - secondDice))
                    {
                      for(int area = 23; area >= 0 ; area--)
                      {
                         if(temp.stateCheckerArray[area].isEmpty()) continue;
                         if(temp.stateCheckerArray[area].isWhiteArea()) continue;                            
                         if(temp.movePiece(area, area - firstDice))
                         {                          
                             return timesMustBePlayed;                          
                         }                            
                      }                          
                    }
                    max = Math.max(max, timesPlayed);
                    //timesPlayed = max;         
                    return timesMustBePlayed - timesPlayed;
                 }                 
              }
              for(int area = 23; area >= 0 ; area--)
              {
                 if(temp.stateCheckerArray[area].isEmpty()) continue;
                 if(temp.stateCheckerArray[area].isWhiteArea()) continue;
                 if(temp.movePiece(area, area - firstDice))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1; //check again same area
                 }
                 else if(temp.movePiece(area, area - secondDice))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1; //check again same area
                 }                            
              }
              for(int area = 5; area >= 0; area-- )
              {
                 if(temp.movePiece(area, State.RED_FINAL_AREA))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1;
                 }                 
              }                      
              int max = timesPlayed;    
              timesPlayed = 0;
              for(int area = 23; area >= 0 ; area--)
              {
                 if(temp.stateCheckerArray[area].isEmpty()) continue;
                 if(temp.stateCheckerArray[area].isWhiteArea()) continue;
                 if(temp.movePiece(area, area - secondDice))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1; //check again same area
                 }
                 else if(temp.movePiece(area, area - firstDice))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1; //check again same area
                 }                            
              }              
             for(int area = 5; area >= 0; area-- )
             {
                 if(temp.movePiece(area, State.RED_FINAL_AREA))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area += 1;
                 }                 
             } 
             max = Math.max(max, timesPlayed);
             timesPlayed = max;              
         }
         return timesPlayed;
     }
     
    private int calcMinPossibleMovesWhite()
    {
        //dhmiourgoume antigrafo ths katastashs kai dokimazoume kinhseis.
        //An se opoidhpote shmeio ftasoume to elaxisto epistreofoume to elaxisto
        //Elaxisto einai ean diples 4 , 2 alliws.
        //Ama de vroume 4 h 2 antistoixa epistrefoume ton arithmo epitrepomnwn kinhsewn
        //ws ekshs timesMustBePlayed - timesPlayed.        
         State temp = new State(this);
         int timesMustBePlayed ;
         int timesPlayed;
         if(temp.isDiples)
         {
             timesMustBePlayed = 4;
             timesPlayed = 0;
             if(!temp.stateCheckerArray[State.WHITE_DAMAGED_AREA].isEmpty())
             {
                 if(!temp.isValidMove(State.WHITE_DAMAGED_AREA, firstDice - 1 ))
                 {
                     return 0;// den exoume kinhsh
                 }
                 temp.cancelAllMoves();
                 while(timesPlayed != 4)
                 {
                     temp.movePiece(State.WHITE_DAMAGED_AREA, firstDice - 1);                         
                     timesPlayed++;
                     if(temp.stateCheckerArray[State.WHITE_DAMAGED_AREA].isEmpty()) break;
                 }
                 if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
             }
             for(int area = 0; area <= 23 ; area++)
             {
                 if(temp.stateCheckerArray[area].isEmpty()) continue;
                 if(!temp.stateCheckerArray[area].isWhiteArea()) continue;
                 if(temp.movePiece(area, area + firstDice))
                 {                                          
                     timesPlayed++;                     
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area -= 1; //check again same area
                 }
             }
             for(int area = 18; area <= 23; area++ )
             {
                 if(temp.movePiece(area, State.WHITE_FINAL_AREA))
                 {                                         
                     timesPlayed++;    
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area -= 1;
                 }                 
             }
         }
         else
         {
              timesMustBePlayed = 2;
              timesPlayed = 0;
              if(!temp.stateCheckerArray[State.WHITE_DAMAGED_AREA].isEmpty())
              {
                 if(!(temp.isValidMove(State.WHITE_DAMAGED_AREA,  firstDice - 1)  || temp.isValidMove(State.WHITE_DAMAGED_AREA, secondDice - 1)))
                 {
                     return timesPlayed;// = 0
                 }
                 temp.cancelAllMoves();
                 if(temp.stateCheckerArray[State.WHITE_DAMAGED_AREA].getSize() >= 2)
                 {                     
                     if(temp.isValidMove(State.WHITE_DAMAGED_AREA,  firstDice - 1)  && temp.isValidMove(State.WHITE_DAMAGED_AREA, secondDice - 1))
                     {                         
                         return timesMustBePlayed;
                     }
                     temp.cancelAllMoves();
                     if(temp.isValidMove(State.WHITE_DAMAGED_AREA,  firstDice - 1))
                     {                                      
                         return 1;//return 1
                     }
                     temp.cancelAllMoves();
                     if(temp.isValidMove(State.WHITE_DAMAGED_AREA,  secondDice - 1))
                     {                                    
                         return 1;//return 1
                     }
                 }
                 else
                 {                     
                    if(temp.movePiece(State.WHITE_DAMAGED_AREA, firstDice - 1))
                    {                      
                      for(int area = 0; area <= 23 ; area++)
                      {
                         if(temp.stateCheckerArray[area].isEmpty()) continue;
                         if(!temp.stateCheckerArray[area].isWhiteArea()) continue;                            
                         if(temp.movePiece(area, area + secondDice))
                         {                             
                             return timesMustBePlayed; //exw paiksei hdh mia
                         }                            
                      }                        
                    }                            
                    if(temp.movePiece(State.WHITE_DAMAGED_AREA, secondDice - 1))
                    {
                      for(int area = 0; area <= 23 ; area++)
                      {
                         if(temp.stateCheckerArray[area].isEmpty()) continue;
                         if(!temp.stateCheckerArray[area].isWhiteArea()) continue;                            
                         if(temp.movePiece(area, area + firstDice))
                         {                             
                             return timesMustBePlayed;                             
                         }                            
                      }                          
                    }
                    return 1;//den eixa allh kinhsh
                 }                 
              }//den exoume xtuphmena              
              for(int area = 0; area <= 23 ; area++)
              {                
                 if(temp.stateCheckerArray[area].isEmpty()) continue;
                 if(!temp.stateCheckerArray[area].isWhiteArea()) continue;
                 if(temp.movePiece(area, area + firstDice))
                 {                     
                     timesPlayed++;                     
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area -= 1; //check again same area
                 }
                 if(temp.movePiece(area, area + secondDice))
                 {                     
                     timesPlayed++;                    
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     
                     area -= 1; //check again same area                     
                 }                            
              }
             for(int area = 18; area <= 23 ; area++)
             {
                 if(temp.movePiece(area, State.WHITE_FINAL_AREA))
                 {
                     timesPlayed++;
                     if(timesPlayed == timesMustBePlayed) return timesMustBePlayed;
                     area -= 1;
                 }                 
             }                                     
         }         
         return timesPlayed;
     }     

    /**
     * Dhmiourgoume mia arrayList pou periexei 21 katastaseis (oles oi pithanes zaries sto tavli)
     * @return mia ArrayList me antigrafa ths twrinhs katastash opou to kathe paidi exei diaforetikh timh sta pedia firstDice, secondDIce
     * 
     */
    protected ArrayList<State> getChanceChildren()
    {
         ArrayList<State> children = new ArrayList();

            for(int i = 1; i <= 6; i++){
                for(int z = i; z <= 6; z++)
                {
                    State curState =  new State(this);
                    curState.setDice(i, z);
                    children.add(curState);
                }
            }
            return children;
     }

    /**
     * An arrayList is constructed which contains all the children of a state. A child of a state is a state which occurs when the player chooses <br>
     * a move from the possible moves he has with the current Board and current firstDice and secondDice number
     * @return an ArrayList with all the DISTINCT children of a state
     */    
    protected ArrayList<State> getChildren()
    {
         if(this.isWhiteTurn) return this.getChildrenWhite();
         return this.getChildrenRed();
     }
    
    private ArrayList<State> getChildrenWhite()
    {
        /**
         * Koitame poses kinhseis prepei na paikosume.
         * Gia oses exoume kanoume efoleumena for loop kai upologizoume ta paidia
         */
         ArrayList<State> children = new ArrayList();
         int damagedArea  =  State.WHITE_DAMAGED_AREA;
         State curState =  new State(this);
         int maximumNum = this.firstDice;
         int minimumNum = this.secondDice;
         State child1, child2, child3,child4;         
         if(curState.minPossibleMoves == 0) return children;
         if(curState.minPossibleMoves == 4)
         {
             if(!curState.stateCheckerArray[damagedArea].isEmpty())
             {
                 child1 = new State(curState);
                 while(!child1.stateCheckerArray[damagedArea].isEmpty() && child1.timesHadBeenPlayed < 4)
                 {                     
                     child1.movePiece(damagedArea,child1.firstDice - 1);
                 }
                 if(child1.timesHadBeenPlayed == 4)
                 {
                     children.add(child1);
                     child1.parent = this;
                     return children;
                 }
                 else
                 {
                     child1.minPossibleMoves = curState.minPossibleMoves - child1.timesHadBeenPlayed;
                     curState = new State(child1);                                           
                 }
             }
             else//ama den exoume xtuphmena poulia
             {
                 for(int firstPieceArea = 0; firstPieceArea <= 23; firstPieceArea ++)
                 {
                     if(curState.stateCheckerArray[firstPieceArea].isEmpty())  continue;                       
                     if(!curState.stateCheckerArray[firstPieceArea].isWhiteArea()) continue;
                     
                     child1 = new State(curState);     
                     
                     if(child1.movePiece(firstPieceArea,firstPieceArea + this.firstDice))                     
                     {                                
                         for(int secondPieceArea = firstPieceArea; secondPieceArea <= 23; secondPieceArea ++)
                         {
                             if(child1.stateCheckerArray[secondPieceArea].isEmpty()) continue;
                             if(!child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue; 
                             
                             child2 = new State(child1);                               
                             
                             if(child2.movePiece(secondPieceArea,secondPieceArea + this.firstDice ))                                  
                             {
                                 
                                 for(int thirdPieceArea = secondPieceArea; thirdPieceArea <= 23; thirdPieceArea ++)
                                 {                                     
                                     if(child2.stateCheckerArray[thirdPieceArea].isEmpty())continue;                                                                                                                                      
                                     if(!child2.stateCheckerArray[thirdPieceArea].isWhiteArea())continue;                                     
                                                                                     
                                     child3 = new State(child2);                                     
                                     if(child3.movePiece(thirdPieceArea, thirdPieceArea + this.firstDice))                                          
                                     {                                           
                                         for(int forthPieceArea = thirdPieceArea; forthPieceArea <= 23 ; forthPieceArea++ )
                                         {                                             
                                             if(child3.stateCheckerArray[forthPieceArea].isEmpty())continue;                                                                                                                                      
                                             if(!child3.stateCheckerArray[forthPieceArea].isWhiteArea())continue;
                                             
                                             child4 = new State(child3);                                             
                                             if(child4.movePiece(forthPieceArea,forthPieceArea + this.firstDice))                                                  
                                             {                                                                                                  
                                                 children.add(child4);
                                             }                                             
                                         }//end for loop 4th piece area
                                         if(child3.allPiecesToHomeArea())//3 kinhseis kai pernw 1
                                         {
                                             child4 = new State(child3);  
                                             int piecesHadBeenTaken = 0;
                                             for(int k = 18; k <= 23; k++)
                                             {
                                                 if(child4.stateCheckerArray[k].isEmpty())  continue;                                        
                                                 if(!child4.stateCheckerArray[k].isWhiteArea())continue;                                                                                                       
                                                 if(child4.movePiece(k, State.WHITE_FINAL_AREA))
                                                 {                                                                                                     
                                                     piecesHadBeenTaken++;
                                                     k--; ///check again same pos
                                                     if(piecesHadBeenTaken == 1)
                                                     {                                                               
                                                        children.add(child4);
                                                        break;     
                                                     }
                                                 }                                       
                                             }                                                                                           
                                         }//end check an mporw na parw ena sth finalarea                                         
                                     }//if check gia 3h kinhsh                                
                                  }//end for thirdPieceArea loop    
                                 if(child2.allPiecesToHomeArea())//2 kinhseis kai pernw
                                 {
                                    child3 = new State(child2);  
                                    int piecesHadBeenTaken = 0;

                                    for(int k = 18; k <= 23; k++)
                                    {
                                         if(child3.stateCheckerArray[k].isEmpty())  continue;                                        
                                         if(!curState.stateCheckerArray[k].isWhiteArea())continue;                                                                                                       
                                         if(child3.movePiece(k, State.WHITE_FINAL_AREA))
                                         {                                                                                                     
                                             piecesHadBeenTaken++;
                                             k--; ///check again same pos
                                             if(piecesHadBeenTaken == 2)
                                             {                                                      
                                                children.add(child3);
                                                break;     
                                             }
                                         }                                       
                                     }                                         
                                 }                                     
                             }
                        }//end for loop      
                        if(child1.allPiecesToHomeArea())//mia kinhsh pernw tria
                        {                            
                            child2 = new State(child1);                             
                            int piecesHadBeenTaken = 0;
                            for(int k = 18; k <= 23; k++)
                            {
                                if(child2.stateCheckerArray[k].isEmpty())  continue;                                        
                                if(!child2.stateCheckerArray[k].isWhiteArea())continue;                                                                                                       
                                if(child2.movePiece(k, State.WHITE_FINAL_AREA))
                                {
                                    piecesHadBeenTaken++;
                                    k--; ///check again same pos
                                    if(piecesHadBeenTaken == 3)
                                    {                                                  
                                       children.add(child2);
                                       break;                                                                                           
                                    }                                             
                                }                                       
                            }                                                       
                        }                         
                     }//end check for first move                    
                 }//end firstPIeceArea loop(big loop)                                 
                 if(curState.allPiecesToHomeArea())
                 {
                    child1 = new State(curState);
                    int piecesHadBeenTaken = 0;
                                                    
                     for(int k = 18; k  <= 23; k++)
                     {
                         if(child1.stateCheckerArray[k].isEmpty())continue;
                         if(!child1.stateCheckerArray[k].isWhiteArea())continue;                                                         
                         if(child1.movePiece(k, State.WHITE_FINAL_AREA))
                         {
                             piecesHadBeenTaken++;
                             k--; ///check again same pos
                             if(piecesHadBeenTaken == 4)
                             {                                       
                                children.add(child1);
                                break;     
                             }
                         }                                                                                                 
                      }                                                      
                 }                 
             }
         }
         if(curState.minPossibleMoves == 3)
         {
             //ama exoume xtuphmena poulia
             if(!curState.stateCheckerArray[damagedArea].isEmpty())
             {
                 child1 = new State(curState);
                 while(!child1.stateCheckerArray[damagedArea].isEmpty() && child1.timesHadBeenPlayed < 3)
                 {                     
                     child1.movePiece(damagedArea,child1.firstDice - 1);
                 }
                 if(child1.timesHadBeenPlayed == 3)
                 {
                     children.add(child1);
                     child1.parent = this;
                     return children;
                 }
                 else
                 {
                     child1.minPossibleMoves = curState.minPossibleMoves - child1.timesHadBeenPlayed;
                     curState = new State(child1); 
                 }
             }
             else//ama den exoume xtuphmena poulia
             {                 
                 for(int firstPieceArea = 0; firstPieceArea <= 23; firstPieceArea ++)
                 {
                     if(curState.stateCheckerArray[firstPieceArea].isEmpty())continue;                       
                     if(!curState.stateCheckerArray[firstPieceArea].isWhiteArea())continue;
                     
                     child1 = new State(curState);                     
                     if(child1.movePiece(firstPieceArea, firstPieceArea + this.firstDice))                     
                     {
                         for(int secondPieceArea = firstPieceArea; secondPieceArea <= 23; secondPieceArea ++)
                         {
                             if(child1.stateCheckerArray[secondPieceArea].isEmpty()) continue;
                             if(!child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue;                                                               
                             
                             child2 = new State(child1);     
                             
                             if(child2.movePiece(secondPieceArea, secondPieceArea + this.firstDice ))                                  
                             {
                                 for(int thirdPieceArea = 0; thirdPieceArea <= 23; thirdPieceArea ++)
                                 {
                                     if(child2.stateCheckerArray[thirdPieceArea].isEmpty())continue;                                                                                                                                      
                                     if(!child2.stateCheckerArray[thirdPieceArea].isWhiteArea())continue;                                     
                                                                                     
                                     child3 = new State(child2);
                                     
                                     if(child3.movePiece(thirdPieceArea, 
                                          thirdPieceArea + this.firstDice))
                                     {                                             
                                         children.add(child3);                             
                                     }                                   
                                 }//end thirdPieceArea loop                                     
                                 if(child2.allPiecesToHomeArea())
                                 {
                                     child3 = new State(child2);                              

                                     for(int k = 18; k <= 23; k++)
                                     {
                                         if(child3.stateCheckerArray[k].isEmpty())  continue;
                                         if(!child3.stateCheckerArray[k].isWhiteArea())continue;
                                         if(child3.movePiece(k, State.WHITE_FINAL_AREA))
                                         {                                                                                                                                                     
                                            children.add(child3);
                                            break;                                                                                                                                                                                             
                                         }                                       
                                     }                                   
                                 }                                   
                             }//end check for secondPieceArea move
                         }//end for loop   
                         if(child1.allPiecesToHomeArea())
                         {
                             child2 = new State(child1);                             
                             int piecesHadBeenTaken = 0;                                          
                             for(int k = 18; k <= 23; k++)
                             {
                                 if(child2.stateCheckerArray[k].isEmpty())  continue;                                        
                                 if(!child2.stateCheckerArray[k].isWhiteArea())continue;  
                                 
                                 if(child2.movePiece(k, State.WHITE_FINAL_AREA))
                                 {
                                     piecesHadBeenTaken++;
                                     k--; ///check again same pos
                                     if(piecesHadBeenTaken == 2)
                                     {                                             
                                        children.add(child2);
                                        break;                                                                                           
                                     }                                             
                                 }                                       
                             }                            
                         }                         
                     }//end check gia 1h kinhsh 
                 }//end big for                 
                 if(curState.allPiecesToHomeArea())
                 {
                    child1 = new State(curState);
                    int piecesHadBeenTaken = 0;
                     for(int k = 18; k  <= 23; k++)
                     {
                         if(child1.stateCheckerArray[k].isEmpty())continue;
                         if(!child1.stateCheckerArray[k].isWhiteArea())continue;                                                         
                         if(child1.movePiece(k, State.WHITE_FINAL_AREA))
                         {
                             piecesHadBeenTaken++;
                             k--; ///check again same pos
                             if(piecesHadBeenTaken == 3)
                             {                                    
                                children.add(child1);
                                break;     
                             }
                         }                                                                                                 
                      }
                 }
            }//end ama den exoume xtuphmena poulia
         }  
         if(curState.minPossibleMoves <= 2)
         {             
             if(!curState.stateCheckerArray[damagedArea].isEmpty())
             {                                   
                 child1 = new State(curState);                                   
                 if(child1.movePiece(damagedArea, child1.firstDice - 1))
                 {                                                      
                     if(minPossibleMoves == child1.timesHadBeenPlayed)//==1
                     {                        
                         children.add(child1);
                     }
                     else
                     {
                         if(!child1.stateCheckerArray[damagedArea].isEmpty())
                         {             
                             child1.movePiece(damagedArea,child1.secondDice - 1);
                             children.add(child1);
                             child1.parent = this;
                             return children;
                         }
                         else
                         {
                             for(int j = 0; j < 24; j ++)
                             {                        
                                if(child1.stateCheckerArray[j].isEmpty())continue;                              
                                if(!child1.stateCheckerArray[j].isWhiteArea())continue;
                                
                                child2 = new State(child1);
                                if(child2.movePiece(j,j + child2.secondDice))
                                {
                                    children.add(child2);                                
                                }
                            }
                        }
                     }
                  }
                  child1 = new State(curState);                                        
                  if(child1.movePiece(damagedArea,child1.secondDice - 1))
                  {
                     if(minPossibleMoves == child1.timesHadBeenPlayed)
                     {
                         children.add(child1);
                     }
                     else
                     {
                         if(!child1.stateCheckerArray[damagedArea].isEmpty())
                         {
                             child1.movePiece(damagedArea,child1.firstDice - 1);
                             children.add(child1);
                         }
                         else
                         {
                             for(int j = 0; j < 24; j ++)
                             {                                       
                                if(child1.stateCheckerArray[j].isEmpty())continue;                             
                                if(!child1.stateCheckerArray[j].isWhiteArea())  continue;
                                
                                child2 = new State(child1);
                                if(child2.movePiece(j, j + child2.firstDice))                                                                  
                                {                                                                            
                                    children.add(child2);
                                }
                            }
                         }      
                       }                     
                  }
             }//end damaged area check             
             else // ama den exoume xtuphmena poulia
             {
                 if(curState.minPossibleMoves == 1)
                 {
                     for(int firstPieceArea = 0; firstPieceArea <= 23; firstPieceArea ++)
                     {
                         if(curState.stateCheckerArray[firstPieceArea].isEmpty())continue;                      
                         if(!curState.stateCheckerArray[firstPieceArea].isWhiteArea())continue;
                         
                         child1 = new State(curState);
                         if(child1.movePiece(firstPieceArea,firstPieceArea + this.firstDice))
                         {
                            children.add(child1);                            
                         }                                       
                     }        
                     if(!(curState.isDiples))
                     {
                         for(int i = 0; i <= 23; i ++)
                         {        
                             if(curState.stateCheckerArray[i].isEmpty())continue;                           
                             if(!curState.stateCheckerArray[i].isWhiteArea())continue;
                             
                             child1 = new State(curState);
                             if(child1.movePiece(i, i + this.secondDice))                                   
                             {
                                children.add(child1);
                             }                                       
                         }    
                     }
                     if(curState.allPiecesToHomeArea())
                     {
                         child1 = new State(curState);
                                                                                   
                         for(int i = 18; i <= 23; i++)
                         {
                             if(child1.stateCheckerArray[i].isEmpty()) continue;
                             if(!child1.stateCheckerArray[i].isWhiteArea())continue;

                             if(child1.movePiece(i, State.WHITE_FINAL_AREA))
                             {
                                children.add(child1);
                                break;
                             }                                       
                         }                      
                     }
                 }
                 else //ama exoume 2 kinhseis
                 {                       
                    for(int firstPieceArea = 0; firstPieceArea <= 23; firstPieceArea ++)
                    {                               
                        if(curState.stateCheckerArray[firstPieceArea].isEmpty())continue;                       
                        if(!curState.stateCheckerArray[firstPieceArea].isWhiteArea())continue;
                        
                       child1 = new State(curState);                                                
                       if(child1.movePiece(firstPieceArea,firstPieceArea + minimumNum ))
                       {                                  
                            for(int secondPieceArea = firstPieceArea; secondPieceArea <= 23; secondPieceArea ++)
                            {                        
                                if(child1.stateCheckerArray[secondPieceArea].isEmpty())continue;                              
                                if(!child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue;
                                
                                child2 = new State(child1);
                                if(child2.movePiece(secondPieceArea, secondPieceArea + maximumNum))                                      
                                {                                                                        
                                    children.add(child2);                                    
                                }
                            }
                            if(child1.allPiecesToHomeArea())
                            {                               
                                for(int k = 18; k  <= 23; k++)
                                {
                                    if(child1.stateCheckerArray[k].isEmpty()) continue;                                    
                                    if(!child1.stateCheckerArray[k].isWhiteArea())continue;
                                    if(child1.movePiece(k, State.WHITE_FINAL_AREA))
                                    {                                                          
                                        children.add(child1);                                                 
                                        break;                                             
                                    }                                                                              
                                 }                                                 
                              }                            
                        }
                       child1 = new State(curState);                         
                       if(child1.movePiece(firstPieceArea, firstPieceArea + maximumNum))
                       {                           
                            for(int secondPieceArea = firstPieceArea; secondPieceArea <= 23; secondPieceArea ++)
                            {                        
                                if(child1.stateCheckerArray[secondPieceArea].isEmpty())continue;                              
                                if(!child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue;

                                child2 = new State(child1);
                                
                                if(child2.movePiece(secondPieceArea, secondPieceArea + minimumNum))                                                                  
                                {
                                    if(children.contains(child2)) continue;
                                    children.add(child2);                                    
                                }
                            }
                            if(child1.allPiecesToHomeArea())
                            {                               
                               
                                for(int k = 18; k  <= 23; k++)
                                {
                                    if(child1.stateCheckerArray[k].isEmpty()) continue;                                    
                                    if(!child1.stateCheckerArray[k].isWhiteArea())continue;
                                    if(child1.movePiece(k, State.WHITE_FINAL_AREA))
                                    {                                                
                                        children.add(child1);                                                 
                                        break;                                             
                                    }                                                                              
                                }                                                                 
                              }                            
                          }
                    }//end big for                                        
                    if(curState.allPiecesToHomeArea())
                    {                           
                        child1 = new State(curState);
                        int piecesHadBeenTaken = 0;
                                                
                        for(int j = 18; j  <= 23; j++)
                        {
                            if(child1.stateCheckerArray[j].isEmpty()) continue;                                    
                            if(!child1.stateCheckerArray[j].isWhiteArea())continue;                                

                            if(child1.movePiece(j, State.WHITE_FINAL_AREA))
                            {                                   
                                 j--;
                                 piecesHadBeenTaken++;                                     
                                 if(piecesHadBeenTaken == 2)
                                 {
                                     children.add(child1);
                                     break;
                                 }
                            }
                        }                                                                        
                    }//end check for children who go to final area                      
                }
             }
         }                  
         //long duration = System.currentTimeMillis() - startTime;    
         //System.out.println("Time is : " + convertMllis(duration));         
         for(int i = 0; i < children.size();i++)
         {             
             children.get(i).parent = this;
         }         
         return children;
    }

    private ArrayList<State> getChildrenRed()
    {
         ArrayList<State> children = new ArrayList();
         //MaxPQ<State> maxPriority = new MaxPQ(20);
         State curState =  new State(this);
         int damagedArea  =  State.RED_DAMAGED_AREA;                       
         int maximumNum = this.firstDice;
         int minimumNum = this.secondDice;
         State child1, child2, child3,child4;
                          
         if(curState.minPossibleMoves == 0) return children;
         if(curState.minPossibleMoves == 4)
         {
             if(!curState.stateCheckerArray[damagedArea].isEmpty())
             {
                 child1 = new State(curState);
                 while(!child1.stateCheckerArray[damagedArea].isEmpty() && child1.timesHadBeenPlayed < 4)
                 {                     
                     child1.movePiece(damagedArea,24 - child1.firstDice);
                 }
                 if(child1.timesHadBeenPlayed == 4)
                 {
                     children.add(child1);
                     child1.parent = this;
                     return children;
                 }
                 else
                 {
                     child1.minPossibleMoves = curState.minPossibleMoves - child1.timesHadBeenPlayed;
                     curState = new State(child1);                                           
                 }
             }
             else//ama den exoume xtuphmena poulia
             {
                 for(int firstPieceArea = 23; firstPieceArea >= 0; firstPieceArea --)
                 {
                     if(curState.stateCheckerArray[firstPieceArea].isEmpty())  continue;                       
                     if(curState.stateCheckerArray[firstPieceArea].isWhiteArea()) continue;
                     
                     child1 = new State(curState);     
                     
                     if(child1.movePiece(firstPieceArea, firstPieceArea - this.firstDice))
                     {                                
                         for(int secondPieceArea = firstPieceArea; secondPieceArea >= 0; secondPieceArea --)
                         {
                             if(child1.stateCheckerArray[secondPieceArea].isEmpty()) continue;
                             if(child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue; 
                             
                             child2 = new State(child1);                               
                             
                             if(child2.movePiece(secondPieceArea,  secondPieceArea - this.firstDice))
                             {
                                 
                                 for(int thirdPieceArea = secondPieceArea; thirdPieceArea >= 0; thirdPieceArea --)
                                 {                                     
                                     if(child2.stateCheckerArray[thirdPieceArea].isEmpty())continue;                                                                                                                                      
                                     if(child2.stateCheckerArray[thirdPieceArea].isWhiteArea())continue;                                     
                                                                                     
                                     child3 = new State(child2);                                     
                                     if(child3.movePiece(thirdPieceArea,thirdPieceArea - this.firstDice))
                                     {                                           
                                         for(int forthPieceArea = thirdPieceArea; forthPieceArea >= 0; forthPieceArea --)
                                         {                                             
                                             if(child3.stateCheckerArray[forthPieceArea].isEmpty())continue;                                                                                                                                      
                                             if(child3.stateCheckerArray[forthPieceArea].isWhiteArea())continue;
                                             
                                             child4 = new State(child3);    
                                             
                                             if(child4.movePiece(forthPieceArea, forthPieceArea - this.firstDice))
                                             {                                                                                                  
                                                 children.add(child4);
                                             }                                             
                                         }//end for loop 4th piece area
                                         if(child3.allPiecesToHomeArea())//3 kinhseis kai pernw 1
                                         {
                                             child4 = new State(child3);  
                                             int piecesHadBeenTaken = 0;
                                             
                                             for(int k = 5; k  >= 0; k--)
                                             {
                                                 if(child4.stateCheckerArray[k].isEmpty())  continue;                                        
                                                 if(child4.stateCheckerArray[k].isWhiteArea())continue;                                                                                                       
                                                 if(child4.movePiece(k, State.RED_FINAL_AREA))
                                                 {                                                   
                                                     piecesHadBeenTaken++;
                                                     k++; ///check again same pos
                                                     if(piecesHadBeenTaken == 1)
                                                     {                                                              
                                                        children.add(child4);
                                                        break;     
                                                     }
                                                 }                                      
                                             }                             
                                                  
                                         }//end check an mporw na parw ena sth finalarea                                         
                                     }//if check gia 3h kinhsh                                
                                  }//end for thirdPieceArea loop    
                                 if(child2.allPiecesToHomeArea())//2 kinhseis kai pernw
                                 {
                                     child3 = new State(child2);  
                                     int piecesHadBeenTaken = 0;
                                   
                                     for(int k = 5; k  >= 0; k--)
                                     {
                                         if(child3.stateCheckerArray[k].isEmpty())  continue;                                        
                                         if(child3.stateCheckerArray[k].isWhiteArea())continue;                                                                                                       
                                         if(child3.movePiece(k, State.RED_FINAL_AREA))
                                         {                                                   
                                             piecesHadBeenTaken++;
                                             k++; ///check again same pos
                                             if(piecesHadBeenTaken == 2)
                                             {                                                   
                                                children.add(child3);
                                                break;     
                                             }
                                         }                                      
                                     }                             
                                 }                            
                             }
                        }//end for loop      
                        if(child1.allPiecesToHomeArea())//mia kinhsh pernw tria
                        {                            
                            child2 = new State(child1);                             
                            int piecesHadBeenTaken = 0;                         
                            for(int k = 5; k  >= 0; k--)                                 {
                                if(child2.stateCheckerArray[k].isEmpty()) continue;                                        
                                if(child2.stateCheckerArray[k].isWhiteArea())continue;                                         
                                if(child2.movePiece(k, State.RED_FINAL_AREA))
                                {
                                    piecesHadBeenTaken++;
                                    k++; ///check again same pos
                                    if(piecesHadBeenTaken == 3)
                                    {                                                    
                                       children.add(child2);
                                       break;                                                                                           
                                    }                                             
                                }                                      
                            }                                                             
                        }                         
                     }//end check for first move                    
                 }//end firstPIeceArea loop(big loop)                                 
                 if(curState.allPiecesToHomeArea())
                 {
                    child1 = new State(curState);
                    int piecesHadBeenTaken = 0;
                  
                         for(int k = 5; k  >= 0; k--)
                         {
                             if(child1.stateCheckerArray[k].isEmpty())continue;
                             if(child1.stateCheckerArray[k].isWhiteArea())continue;      
                             
                             if(child1.movePiece(k, State.RED_FINAL_AREA))
                             {                                 
                                 piecesHadBeenTaken++;
                                 k++; ///check again same pos
                                 if(piecesHadBeenTaken == 4)
                                 {                                           
                                    children.add(child1);
                                    break;     
                                 }                                                           
                            }                                             
                         }
                 }                 
             }
         }
         if(curState.minPossibleMoves == 3)
         {
             //ama exoume xtuphmena poulia
             if(!curState.stateCheckerArray[damagedArea].isEmpty())
             {
                 child1 = new State(curState);
                 while(!child1.stateCheckerArray[damagedArea].isEmpty() && child1.timesHadBeenPlayed < 3)
                 {                     
                     child1.movePiece(damagedArea,24 - child1.firstDice);
                 }
                 if(child1.timesHadBeenPlayed == 3)
                 {
                     children.add(child1);
                     child1.parent = this;
                     return children;
                 }
                 else
                 {
                     child1.minPossibleMoves = curState.minPossibleMoves - child1.timesHadBeenPlayed;
                     curState = new State(child1); 
                 }
             }
             else//ama den exoume xtuphmena poulia
             {                 
                 for(int firstPieceArea = 23; firstPieceArea >= 0; firstPieceArea --)
                 {
                     if(curState.stateCheckerArray[firstPieceArea].isEmpty())continue;                       
                     if(curState.stateCheckerArray[firstPieceArea].isWhiteArea())continue;
                     
                     child1 = new State(curState);    
                     
                     if(child1.movePiece(firstPieceArea, firstPieceArea - this.firstDice))
                     {
                         for(int secondPieceArea = firstPieceArea; secondPieceArea >= 0; secondPieceArea --)
                         {
                             if(child1.stateCheckerArray[secondPieceArea].isEmpty()) continue;
                             if(child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue; 
                             
                             child2 = new State(child1);                               
                             if(child2.movePiece(secondPieceArea, secondPieceArea - this.firstDice))
                             {
                                 for(int thirdPieceArea = secondPieceArea; thirdPieceArea >= 0; thirdPieceArea --)
                                 {
                                     if(child2.stateCheckerArray[thirdPieceArea].isEmpty())continue;                                                                                                                                      
                                     if(child2.stateCheckerArray[thirdPieceArea].isWhiteArea())continue;                                     
                                                                                     
                                     child3 = new State(child2);
                                     if(child3.movePiece(thirdPieceArea,thirdPieceArea - this.firstDice))
                                     {                                             
                                         children.add(child3);                             
                                     }                                   
                                 }//end thirdPieceArea loop                                     
                                 if(child2.allPiecesToHomeArea())
                                 {
                                     child3 = new State(child2);                              
                                                                      
                                     for(int k = 5; k  >= 0; k--)
                                     {
                                         if(child3.stateCheckerArray[k].isEmpty())  continue;                                        
                                         if(child3.stateCheckerArray[k].isWhiteArea())continue;                                                                                                       
                                         if(child3.movePiece(k, State.RED_FINAL_AREA))
                                         {                                                                                                   
                                            children.add(child3);
                                            break;                                                                                                                                              
                                         }                                      
                                     }                                                                     
                                 }                                   
                             }//end check for secondPieceArea move
                         }//end for loop   
                         if(child1.allPiecesToHomeArea())
                         {
                             child2 = new State(child1);                             
                             int piecesHadBeenTaken = 0;                             
                             for(int k = 5; k  >= 0; k--)
                             {
                                 if(child2.stateCheckerArray[k].isEmpty()) continue;                                        
                                 if(child2.stateCheckerArray[k].isWhiteArea())continue;                                         
                                 if(child2.movePiece(k, State.RED_FINAL_AREA))
                                 {
                                     piecesHadBeenTaken++;
                                     k++; ///check again same pos
                                     if(piecesHadBeenTaken == 2)
                                     {                                             
                                        children.add(child2);
                                        break;                                                                                           
                                     }                                             
                                 }                                      
                             }                                                          
                         }                         
                     }//end check gia 1h kinhsh 
                 }//end big for                 
                 if(curState.allPiecesToHomeArea())
                 {
                    child1 = new State(curState);
                    int piecesHadBeenTaken = 0;
                                  
                     for(int k = 5; k  >= 0; k--)
                     {
                         if(child1.stateCheckerArray[k].isEmpty())continue;
                         if(child1.stateCheckerArray[k].isWhiteArea())continue;                                                         
                         if(child1.movePiece(k, State.RED_FINAL_AREA))
                         {
                             piecesHadBeenTaken++;
                             k++; ///check again same pos
                             if(piecesHadBeenTaken == 3)
                             {                                    
                                children.add(child1);
                                break;     
                             }                                                           
                        }                                                                         
                     }                 
                 }
            }//end ama den exoume xtuphmena poulia
         }  
         if(curState.minPossibleMoves <= 2)
         {
             if(!curState.stateCheckerArray[damagedArea].isEmpty())
             {                                   
                 child1 = new State(curState);                                   
                 if(child1.movePiece(damagedArea, 24 - child1.firstDice))
                 {                                                      
                     if(minPossibleMoves == child1.timesHadBeenPlayed)//==1
                     {                                  
                         children.add(child1);
                     }
                     else
                     {
                         if(!child1.stateCheckerArray[damagedArea].isEmpty())
                         {             
                             child1.movePiece(damagedArea,24 - child1.secondDice);
                             children.add(child1);
                             child1.parent = this;
                             return children;
                         }
                         else
                         {
                             for(int j = 23; j >= 0; j --)
                             {                        
                                if(child1.stateCheckerArray[j].isEmpty())continue;                              
                                if(child1.stateCheckerArray[j].isWhiteArea())continue;
                                
                                child2 = new State(child1);
                                if(child2.movePiece(j, j - child2.secondDice))                            
                                {
                                    children.add(child2);                                
                                }
                            }
                        }
                     }
                  }
                  child1 = new State(curState);                      
                  if(child1.movePiece(damagedArea, 24 - child1.secondDice))
                  {
                     if(minPossibleMoves == child1.timesHadBeenPlayed)
                     {
                         children.add(child1);
                     }
                     else
                     {
                         if(!child1.stateCheckerArray[damagedArea].isEmpty())
                         {
                             child1.movePiece(damagedArea, 24 - child1.firstDice);
                             children.add(child1);
                         }
                         else
                         {
                             for(int j = 23; j >= 0; j --)
                             {                                       
                                if(child1.stateCheckerArray[j].isEmpty())continue;                             
                                if(child1.stateCheckerArray[j].isWhiteArea())  continue;
                                
                                child2 = new State(child1);
                                if(child2.movePiece(j,  j - child2.firstDice))                            
                                {                                                                            
                                    children.add(child2);
                                }
                            }
                         }      
                     }                     
                  }
             }//end damaged area check             
             else // ama den exoume xtuphmena poulia
             {
                 if(curState.minPossibleMoves == 1)
                 {
                     for(int firstPieceArea = 23; firstPieceArea >= 0; firstPieceArea --)
                     {
                         if(curState.stateCheckerArray[firstPieceArea].isEmpty())continue;                      
                         if(curState.stateCheckerArray[firstPieceArea].isWhiteArea())continue;
                         
                         child1 = new State(curState);
                         if(child1.movePiece(firstPieceArea, firstPieceArea - this.firstDice))
                         {
                            children.add(child1);                            
                         }                                       
                     }        
                     if(!(curState.isDiples))
                     {
                         for(int i = 23; i >= 0; i --)
                         {        
                             if(curState.stateCheckerArray[i].isEmpty())continue;                           
                             if(curState.stateCheckerArray[i].isWhiteArea())continue;
                             
                             child1 = new State(curState);
                             if(child1.movePiece(i, i - this.secondDice))
                             {
                                children.add(child1);
                             }                                       
                         }    
                     }
                     if(curState.allPiecesToHomeArea())
                     {
                         child1 = new State(curState);                       
                             for(int i = 5; i  >= 0; i--)
                             {
                                 if(child1.stateCheckerArray[i].isEmpty())continue;
                                 if(child1.stateCheckerArray[i].isWhiteArea())continue;

                                 if(child1.movePiece(i, State.RED_FINAL_AREA))
                                 {
                                    children.add(child1);
                                    break;
                                 }                                       
                             }
                     }
                 }
                 else //ama exoume 2 kinhseis
                 {                     
                   for(int firstPieceArea = 23; firstPieceArea >= 0; firstPieceArea --)
                   {                               
                        if(curState.stateCheckerArray[firstPieceArea].isEmpty())continue;                       
                        if(curState.stateCheckerArray[firstPieceArea].isWhiteArea())continue;

                        child1 = new State(curState);                         
                        if(child1.movePiece(firstPieceArea,firstPieceArea - minimumNum))
                        {                       
                            for(int secondPieceArea = firstPieceArea; secondPieceArea >= 0; secondPieceArea --)
                            {                        
                                if(child1.stateCheckerArray[secondPieceArea].isEmpty())continue;                              
                                if(child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue;

                                child2 = new State(child1);
                                if(child2.movePiece(secondPieceArea, secondPieceArea - maximumNum))                            
                                {                                    
                                    children.add(child2);                                    
                                }
                            }
                            if(child1.allPiecesToHomeArea())
                            {                                                                           
                                for(int k = 5; k  >= 0; k--)
                                {
                                    if(child1.stateCheckerArray[k].isEmpty()) continue;                                    
                                    if( child1.stateCheckerArray[k].isWhiteArea())continue;
                                    if(child1.movePiece(k, State.RED_FINAL_AREA))
                                    {                                             
                                        children.add(child1);                                                 
                                        break;                                             
                                    }                                                                              
                                 }                     
                              }                            
                        }
                        child1 = new State(curState);                         
                        if(child1.movePiece(firstPieceArea, firstPieceArea - maximumNum ))
                        {                            
                            for(int secondPieceArea = firstPieceArea; secondPieceArea >= 0; secondPieceArea --)
                            {                        
                                if(child1.stateCheckerArray[secondPieceArea].isEmpty())continue;                              
                                if(child1.stateCheckerArray[secondPieceArea].isWhiteArea())continue;

                                child2 = new State(child1);
                                if(child2.movePiece(secondPieceArea,secondPieceArea - minimumNum))                            
                                {
                                    if(children.contains(child2)) continue;
                                    children.add(child2);                                    
                                }
                            }
                            if(child1.allPiecesToHomeArea())
                            {                               
                                                       
                                for(int k = 5; k  >= 0; k--)
                                {
                                    if(child1.stateCheckerArray[k].isEmpty()) continue;                                    
                                    if(child1.stateCheckerArray[k].isWhiteArea())continue;
                                    if(child1.movePiece(k, State.RED_FINAL_AREA))
                                    {                                             
                                        children.add(child1);                                                 
                                        break;                                             
                                    }                                                                              
                                }                                                     
                              }                            
                          }
                    }//end big for                                        
                    if(curState.allPiecesToHomeArea())
                    {                           
                        child1 = new State(curState);
                        int piecesHadBeenTaken = 0;
                        for(int j = 5; j  >= 0; j--)
                        {
                            if(child1.stateCheckerArray[j].isEmpty()) continue;                                    
                            if(child1.stateCheckerArray[j].isWhiteArea())continue;                                

                            if(child1.movePiece(j, State.RED_FINAL_AREA))
                            {                                   
                                 j++;
                                 piecesHadBeenTaken++;                                     
                                 if(piecesHadBeenTaken == 2)
                                 {
                                     children.add(child1);
                                     break;
                                 }
                            }                           
                         }                            
                    }//end check for children who go to final area                      
                 }
             }
         }                  
         //long duration = System.currentTimeMillis() - startTime;    
         //System.out.println("Time is : " + convertMllis(duration));         
         for(int i = 0; i < children.size();i++)
         {             
             children.get(i).parent = this;
         }         
         return children;    
    }

    /**
     * Returns the probality of [firstDice, secondDice] = [x,z] where Domain([x,z] = [1,6]X[1,6]
     * @return 1/36 an exoume ferei diples, 1/18 alliws
     */
    protected double probability()
    {
         return this.firstDice == secondDice ?(double)1/(double)36 : (double) 1/(double)18 ;
    }

    /**
     * Set up the board. 15 pieces/checkers are put to each player`s final area
     */
    protected void boardSetUp()
    {
        this.stateCheckerArray[State.WHITE_FINAL_AREA].appendPieces(15, true);        
        this.stateCheckerArray[State.RED_FINAL_AREA].appendPieces(15, false);
     }

    /**
     * Initialise the state. Paizoume portes opote vazoume ta 30 poulia sth thesh tous ama arxizoume apo thn arxh
     * Diaforetika ta vazoume opou theloume kai kataskeuazoume  mia tuxaia katastash,
     * H sunarthsh einai private kai kaleitai sthn arxh tou paixnidiou
     */
    private void initState()
    {
        //epanatopothethsh twn pouliwn sth telikh perioxh afoue p.x. teleiwsei ena paixnidi
        for (CheckerArea stateCheckerArray1 : this.stateCheckerArray) {
            if (!stateCheckerArray1.isWhiteArea()) {
                continue;
            }
            int tempOriginalSize = stateCheckerArray1.getSize();
            for (int k = 0; k < tempOriginalSize; k++) {
                this.stateCheckerArray[State.WHITE_FINAL_AREA].appendPiece(stateCheckerArray1.removePieceFromTop());
            }
        }        
        for (CheckerArea stateCheckerArray1 : this.stateCheckerArray) {
            if (stateCheckerArray1.isWhiteArea()) {
                continue;
            }
            int tempOriginalSize = stateCheckerArray1.getSize();
            for (int k = 0; k < tempOriginalSize; k++) {
                this.stateCheckerArray[State.RED_FINAL_AREA].appendPiece(stateCheckerArray1.removePieceFromTop());
            }
        }              
        
         //topothethsh 0 <= x <= 15 pouliwn apo th telkh perioxh kathe paixth sthn antistoixh 0 <= y <= 27 perioxh        
        appendPieces(2, State.WHITE_FINAL_AREA, 0);
        appendPieces(5, State.WHITE_FINAL_AREA, 11);
        appendPieces(3, State.WHITE_FINAL_AREA, 16);
        appendPieces(5, State.WHITE_FINAL_AREA, 18);        
        appendPieces(2, State.RED_FINAL_AREA, 23);
        appendPieces(5, State.RED_FINAL_AREA, 12);
        appendPieces(3, State.RED_FINAL_AREA, 7);
        appendPieces(5, State.RED_FINAL_AREA, 5);        
        /*
        appendPieces(2, State.RED_FINAL_AREA, 23);
        appendPieces(5, State.RED_FINAL_AREA, 12);
        appendPieces(5, State.WHITE_FINAL_AREA, 19);        
        appendPieces(5, State.WHITE_FINAL_AREA, 20);   
         * 
         */
     }
    
    /**
     * In Backgammon astate is terminal if  a player`s all checkers/pieces are in his final area
     * @return Returns if a state is terminal
     */
    protected boolean isTerminalState()
    {
        return this.stateCheckerArray[RED_FINAL_AREA].getSize() == 15 || this.stateCheckerArray[WHITE_FINAL_AREA].getSize() == 15;
    }    

    private void appendPieces(int times, int areaToTakeFrom, int areaToTakeTO)
    {
        for (int i = 0; i < times; i++)
        {
            this.stateCheckerArray[areaToTakeTO].appendPiece(this.stateCheckerArray[areaToTakeFrom].removePieceFromTop());
        }
     }
    
    CheckerArea getArea(int index)
    {
        return this.stateCheckerArray[index];
    }

    /**
     * Start the game. Hurray :P
     */
    protected void startGame()
    {
        this.initState();  //initialise the board      
        this.tempMoveStack.clear();   //clear all previous moves
        this.hitOpPieces.clear();
        //empty the stack of moves !!!                
        Random r = new Random(System.currentTimeMillis());
        this.firstDice = r.nextInt(6) + 1;
        this.secondDice = r.nextInt(6) + 1;        
        //this.firstDice = 4;
        //this.secondDice = 6;
        while(this.firstDice == this.secondDice)
        {
            this.firstDice = r.nextInt(6) + 1;
            this.secondDice = r.nextInt(6) + 1;
        }
        this.isWhiteTurn = this.firstDice >= this.secondDice;                                
        this.isDiples = false;        
        this.timesHadBeenPlayed = 0;        
        this.firstDicePlayed= false;
        this.secDicePlayed = false;           
        this.minPossibleMoves = this.calcMinPossibleMoves();
    }
    
    /**
     * Rixonoume ta zaria. Kanoume antistoixes kinhseis me th startGame method
     */
    protected void rollDice()
    {                
        this.changePlayer();
        this.hitOpPieces.clear();
        this.tempMoveStack.clear();  
        this.throwDice();
        //this.firstDice = 5;
        //this.secondDice = 5;
        this.isDiples = this.firstDice == this.secondDice;                
        this.firstDicePlayed= false;
        this.secDicePlayed = false;        
        this.timesHadBeenPlayed = 0;         
        this.minPossibleMoves = this.calcMinPossibleMoves();              
    }
    
    /**
     * Thetoume ta zaria me tous antistoixous arithmous. Den to afhnoume sth tuxh.
     * Auto ginetai eswterika otan dhmiourgoume ta paidia twn komvwn tuxhs
     * @param first firstDice value
     * @param second SecondDice value
     */
    private void setDice(int first, int second)
    {                                   
        this.hitOpPieces.clear();
        this.tempMoveStack.clear();
        //empty the stack of moves
        this.changePlayer();
        this.firstDice = Math.max(first, second);
        this.secondDice = Math.min(first, second);     
        this.isDiples = this.firstDice == this.secondDice;
        this.firstDicePlayed = false;
        this.secDicePlayed = false;
        this.timesHadBeenPlayed = 0;
        this.minPossibleMoves = this.calcMinPossibleMoves();        
     }    
    /**
     * Rixnoume ta zaria . Oi times twn zariwn einai tuxaies.
     * Afou ta rikosume dinoume th timh max twn 2 zariwn sto firstDice kai to min sto secondDice
     */            
    private void throwDice()
    {
        Random r = new Random(System.currentTimeMillis());
        this.firstDice = r.nextInt(6) + 1;
        this.secondDice = r.nextInt(6) + 1;
        int maximumNum = Math.max(this.firstDice, this.secondDice);
        int minimumNum = Math.min(this.firstDice, this.secondDice);      
        this.firstDice = maximumNum;
        this.secondDice = minimumNum;
    }
    
    /**
     * 
     * @return Returns the values of the two dices. 
     */
    protected int[] getDicePair()
    {
        return new int[]{firstDice, secondDice};
    }
    
    /**
     * Sets the value of the state to the specified value
     * @param value The value of the Board
     */
    protected void setValue(double value)
    {
        this.value = value;
    } 
         
    /**
     * Return a stack of the moves happened in  oredr happened if inOrderHappened = true. Otherwise ot return a stack of tempMoves made in reverse order(Last First)
     * @param inOrderHappened 
     * @return a stack of TempMoves made
     */
    protected Stack<TempMove> getAllMoves(boolean inOrderHappened)
    {
        Stack<TempMove> stack = new Stack<TempMove>();
        if(inOrderHappened)
        {
            for(int i =  this.tempMoveStack.size() - 1; i >= 0   ; i --)
            {
                stack.add(new TempMove(this.tempMoveStack.get(i).prevPos, this.tempMoveStack.get(i).newPos, this.tempMoveStack.get(i).getPlayer()));
            }                        
        }
        else
        {
            //last move first
            for(int i =  0; i < this.tempMoveStack.size()  ; i ++)
            {
                stack.add(new TempMove(this.tempMoveStack.get(i).prevPos, this.tempMoveStack.get(i).newPos, this.tempMoveStack.get(i).getPlayer()));
            }              
        }

        return stack;
    }
    
    void evaluate()
    {        
        this.value = this.isWhiteTurn? evaluatorWhite.evaluate(this): State.evaluatorRed.evaluate(this);                
    }
    
    /**
     * mode = 0 -> contact, crashed -> 1, race -> 2;
     * @return mode Returns mode = 0 if  contact, = 1 if crashed, = 2 if race
     */
    int getSituation()
    {
        int mode;
        int playerFinalArea =  this.isWhiteTurn ? State.WHITE_FINAL_AREA: State.RED_FINAL_AREA;
        int playerDamagedArea = this.isWhiteTurn ? State.WHITE_DAMAGED_AREA: State.RED_DAMAGED_AREA;
        int opDamagedArea = !this.isWhiteTurn ? State.WHITE_DAMAGED_AREA: State.RED_DAMAGED_AREA;
        int lastWhitePosition=-1;
        int lastRedPosition=-1;

        if(!this.stateCheckerArray[playerDamagedArea].isEmpty())
        {
            if(this.stateCheckerArray[playerFinalArea].getSize() >=9)
            {
                return 1;
            }
            return 0;
        }
        if(!this.stateCheckerArray[opDamagedArea].isEmpty())
        {
            if(this.stateCheckerArray[playerFinalArea].getSize() >=9)
            {
                return 1;
            }
            return 0;
        }
        for(int i=0; i<=23 ;i++)
        {
            if(this.stateCheckerArray[i].isEmpty())continue;

            if(this.stateCheckerArray[i].isWhiteArea())
            {
                lastWhitePosition=i;
                break;
            }
        }
        for(int i=23;i >= 0;i--)
        {
            if(this.stateCheckerArray[i].isEmpty())continue;

            if(!this.stateCheckerArray[i].isWhiteArea())
            {
                lastRedPosition=i;
                break;
            }
        }
        if(lastRedPosition > lastWhitePosition)
        {
            if(this.stateCheckerArray[playerFinalArea].getSize() >=9)
            {
                mode = 1;
            }
            else
            {
                mode = 0;
            }
        }
        else
        {
            mode =  2;
        }

        return mode;
    }  
    
   /**
   * 
   * @return true iff white are currently playing
   */
   protected boolean getIsWhiteTurn()
   {
        return this.isWhiteTurn;
    }    
    
   /**
     * 
     * @return A stack that contains the hit checkers we collide during a tempMove.
     */
   protected Stack<TempMove> getAllHitOpCheckers()
   {
        Stack<TempMove> stack = new Stack<TempMove>();
        for(int i = 0; i < this.hitOpPieces.size()  ; i ++)
        {            
            stack.add(new TempMove(this.hitOpPieces.get(i).prevPos, this.hitOpPieces.get(i).newPos, this.hitOpPieces.get(i).getPlayer()));
        }        
        return stack;
    }    
    
    /**
     * Returns the number of times we made TempMoves during this round
     * @return Tne number of temp moves we have made in a round
     */
    protected int getTimesHadBeenPlayed()
    {
        return this.timesHadBeenPlayed;
    }
    
    /**     
     * @return Returns the number of moves we must play in this round
     */
    protected int getTimesMustBePlayed() {
        return this.minPossibleMoves;
    }    

    /**
     * 
     * @return Returns the value of the current state
     */
    protected double getValue()
    {
        return this.value;
    }
      
    /**
     * 
     * @param diceNum H zaria pou mas leei poio pouli mporoume na paroume 
     * @return th thesh pou exei to pouli[18 - 23] gia sprous, [0 - 5] gia kokkinous
     */
    private int validAreaToPickFromChecker(int diceNum)
    {
        //ama ola ta poulia einai sthn telikh perioxh
        if(this.isWhiteTurn && this.allPiecesToHomeArea())
        {
            //ama to pouli einai sth thesh apo thn opoia mporoume na to paroume me th sugkekrhmenh zaria
            if(!this.stateCheckerArray[24 - diceNum].isEmpty() && this.stateCheckerArray[24 - diceNum].isWhiteArea())
            {
                return 24 - diceNum;
            }
            //ama phgainontas apo megalueterh se mikroterh perioxh mexri th perioxh 24 - diceNum gia tous asprous 
            //sunanthsoume pouli mas tote epistrefoume -1 pou einai to flag gia false
            for(int i = 18 ; i < 24 - diceNum ; i++)
            {
                if(!this.stateCheckerArray[i].isEmpty() && this.stateCheckerArray[i].isWhiteArea())
                {
                   return -1;
               }
            }
            for(int i = 24 - diceNum ; i <= 23 ; i++)
            {
                if(this.stateCheckerArray[i].isEmpty()) continue;             
                if(!this.stateCheckerArray[i].isWhiteArea()) continue;                
                return i; // area apo opou mporoume na paroume to pouli kai na to metakinhsoume sth finalArea
            }
        }
        //antistixa gia tous kokkinous
        else if(!this.isWhiteTurn && this.allPiecesToHomeArea())
        {
            if(!this.stateCheckerArray[diceNum - 1].isEmpty() && !this.stateCheckerArray[diceNum -1].isWhiteArea())
            {
                return diceNum - 1;
            }
            for(int i = 5 ; i >  diceNum - 1; i--)
            {
                if(!this.stateCheckerArray[i].isEmpty() && !this.stateCheckerArray[i].isWhiteArea())
                {
                   return -1;
               }
            }
            for(int i =  diceNum  - 1; i >= 0 ; i--)
            {
                if(this.stateCheckerArray[i].isEmpty())continue;               
                if(this.stateCheckerArray[i].isWhiteArea()) continue;
                return i;
            }
        }
        //flag gia invalid parse sth final area
        return -1;
    }
     
    /**
     * Moves a piece to the new position in the board specified if tempMove is valid
     * @param newPos the new position of the board
     * @return true iff a checker`s move is valid for the specified area, false otherwise
     */
    protected boolean movePiece(int newPos) 
    {    
        int timesBeenPlayed = this.timesHadBeenPlayed;        
        if(this.isWhiteTurn)
        {
            if(this.stateCheckerArray[State.WHITE_DAMAGED_AREA].isEmpty())
            {
                if(newPos == State.WHITE_FINAL_AREA)
                {                    
                    int area = this.validAreasToFinalArea();
                    
                    if(area == -1)
                    {
                        return false;
                    }
                    else
                    {                                                                        
                        this.tempMoveStack.add(new TempMove(area, newPos, stateCheckerArray[area].getPiece(0)));            
                        this.stateCheckerArray[newPos].appendPiece(this.stateCheckerArray[area].removePieceFromTop());                                            
                        return true;                                                                                                 
                    }
                }
                if(this.movePiece(newPos -  this.firstDice, newPos))                            
                {                              
                   return true;
                }                                  
                if(this.movePiece(newPos -  this.secondDice, newPos))
                {                        
                   return  true;
                }                                                          
                if(this.movePiece(newPos - (secondDice + this.firstDice), newPos))
                {                        
                   return true;
                }              
                if(this.movePiece(newPos -  3*firstDice, newPos))
                {                        
                   return true;
                }               

                if(this.movePiece(newPos -  4*firstDice, newPos))
                {

                   return true;
                }                             
            }
            else
            {
                if(this.movePiece(State.WHITE_DAMAGED_AREA, newPos))
                {
                    return true;
                }                    
            }                        
        }
        else
        {              
            if(this.stateCheckerArray[State.RED_DAMAGED_AREA].isEmpty())
            {                            
                if(this.movePiece(newPos +  this.firstDice, newPos))
                {
                   return  true;
                }                                     
                if( this.movePiece(newPos +  this.secondDice, newPos))                   
                {
                   return  true;
                }                
                if(this.movePiece(newPos +  secondDice + this.firstDice, newPos))
                {
                   return true;
                }                
                if(this.movePiece(newPos +  3*firstDice, newPos))
                    {
                       return   true;
                    }                                     
                if(this.movePiece(newPos +  4* firstDice, newPos))
                {
                   return true;
                }             
            }                          
            else
            {
                if(this.movePiece(State.RED_DAMAGED_AREA, newPos))
                {
                    return true;
                }                    
            }            
        }
        timesHadBeenPlayed = timesBeenPlayed;
        return false;
    }    
    
    /**
     * Gia to Gui interface
     * @param prevPos
     * @param newPos
     * @return
     */
    protected boolean movePiece(int prevPos, int newPos)
    {
        this.hitOpPieces.clear();
        int timesBeenPlayed = this.timesHadBeenPlayed;
        if(isValidMove(prevPos, newPos))
        {
            this.tempMoveStack.add(new TempMove(prevPos, newPos, stateCheckerArray[prevPos].getPiece(0)));            
            this.stateCheckerArray[newPos].appendPiece(this.stateCheckerArray[prevPos].removePieceFromTop());                                            
            return true;
        }
        this.timesHadBeenPlayed = timesBeenPlayed;
        return false;
    }   
      
    
    /**
     * Change the player
     */
    private void changePlayer()
    {              
         this.isWhiteTurn = !this.isWhiteTurn;             
     }          
   
    private int validAreasToFinalArea()
    {                
        State temp = new State(this);
        if(this.isWhiteTurn)
        {                        
            int tmp = temp.validAreaToPickFromChecker(this.firstDice);            
            if(tmp != -1 && !temp.firstDicePlayed) {                 
                if(!temp.isDiples)this.firstDicePlayed = true;                
                this.timesHadBeenPlayed++;
                return tmp;
            }                        
            tmp = temp.validAreaToPickFromChecker(this.secondDice);            
            if(tmp != -1 && !temp.secDicePlayed) {
                if(!temp.isDiples)this.secDicePlayed = true;                
                this.timesHadBeenPlayed ++;
                return tmp;
            }                        
            for(int i = 0; i < 24; i++)
            {          
                temp = new State(this);
                
                if(temp.movePiece(i, firstDice + i))
                {                                        
                    tmp = temp.validAreaToPickFromChecker(this.secondDice );
                    if(tmp != -1 && tmp == firstDice + i && !temp.secDicePlayed && temp.timesHadBeenPlayed < 4) {                 
                        if(!temp.isDiples)this.secDicePlayed = true;                        
                        this.secDicePlayed = true;
                        this.timesHadBeenPlayed += 2;
                        return i;
                    }                    
                }
                temp = new State(this);
                if(temp.movePiece(i, secondDice + i))
                {                    
                    tmp = temp.validAreaToPickFromChecker(this.firstDice);
                    if(tmp != -1 && tmp == secondDice + i && !temp.firstDicePlayed && temp.timesHadBeenPlayed < 4) {                        
                        if(!temp.isDiples){
                            this.secDicePlayed = true;
                            this.firstDicePlayed = true;
                        }
                        this.timesHadBeenPlayed+=2;
                        return i;
                    }                    
                }            
                temp = new State(this);
                if(temp.isDiples && temp.movePiece(i, 2*firstDice + i) && temp.timesHadBeenPlayed < 3)
                {                    
                    tmp = temp.validAreaToPickFromChecker(this.firstDice);
                    if(tmp != -1 && tmp == 2*firstDice + i) {                 
                        this.timesHadBeenPlayed += 3;
                        return i;
                    }                    
                }   
                temp = new State(this);
                if(temp.isDiples && temp.movePiece(i, 3*firstDice + i) && temp.timesHadBeenPlayed < 2)
                {
                    tmp = temp.validAreaToPickFromChecker(this.firstDice);
                    if(tmp != -1 && tmp == 3*firstDice + i) {                 
                        this.timesHadBeenPlayed = 4;
                        return i;
                    }                                        
                }                
            }
        }
        else
        {
            //kokkinoi
        }        
        return -1;
    }
    
    /**
     * Checks if a tempMove from previous position toa new position in the board is valid
     * @param prevPos
     * @param newPos
     * @return true iff the tempMove is valid, false otherwise
     */    
    private boolean isValidMove(int prevPos , int newPos)
    {       
        if(this.isWhiteTurn) return this.isValidMoveWhite(prevPos, newPos);
        else return this.isValidMoveRed(prevPos, newPos);
        
    }
    
    private boolean isValidMoveWhite(int prevPos, int newPos)
    {               
        if(prevPos < 0 || newPos < 0 || newPos > 27 || prevPos > 27 || newPos == State.RED_DAMAGED_AREA 
        || newPos == State.WHITE_DAMAGED_AREA || newPos == State.RED_FINAL_AREA || !this.stateCheckerArray[prevPos].isWhiteArea()
        || this.stateCheckerArray[prevPos].isEmpty())  {            
            return false;
        }
        if(newPos == State.WHITE_FINAL_AREA)
        {            
            if(this.isDiples && this.timesHadBeenPlayed < 4)
            {
                if(prevPos == this.validAreaToPickFromChecker(this.firstDice))
                {
                    this.timesHadBeenPlayed++;                             
                    return true;                                                                                                    
                }
            }
            else
            {
                if(!this.firstDicePlayed && prevPos == this.validAreaToPickFromChecker(this.firstDice))
                {
                    this.firstDicePlayed = true;                                                   
                    this.timesHadBeenPlayed++;                             
                    return true;                                                                                                                                                    
                }
                if(!this.secDicePlayed && prevPos == this.validAreaToPickFromChecker(this.secondDice))
                {
                    this.secDicePlayed = true;                                                   
                    this.timesHadBeenPlayed++;                             
                    return true;                                                                                                                                                    
                }
            }            
        }
        else 
        {            
            if(prevPos == State.WHITE_DAMAGED_AREA) prevPos = -1; 
                        
            if(this.isDiples && this.timesHadBeenPlayed < 4)
            {
                if(this.firstDice + prevPos == newPos && this.timesHadBeenPlayed < 4)
                {
                    if(this.isValidCollision(this.firstDice + prevPos, false))
                    {
                        this.timesHadBeenPlayed++;
                        return true;
                    }
                }
                else if(2*this.firstDice + prevPos == newPos && this.timesHadBeenPlayed < 3)
                {                   
                    if(this.isValidCollision(2*this.firstDice + prevPos, true) && this.isValidCollision(this.firstDice + prevPos ,true))
                    {
                        this.isValidCollision(2*this.firstDice + prevPos, false);
                        this.isValidCollision(this.firstDice + prevPos ,false);
                        this.timesHadBeenPlayed+= 2;
                        return true;
                    }
                }
                else if(3*this.firstDice + prevPos == newPos && this.timesHadBeenPlayed < 2)
                {
                    if(this.isValidCollision(3*this.firstDice + prevPos, true) && this.isValidCollision(2*this.firstDice + prevPos, true) 
                            && this.isValidCollision(this.firstDice + prevPos ,true))
                    {
                        this.isValidCollision(3*this.firstDice + prevPos, false);
                        this.isValidCollision(2*this.firstDice + prevPos, false);
                        this.isValidCollision(this.firstDice + prevPos ,false);
                        this.timesHadBeenPlayed+= 3;
                        return true;
                    }                  
                }
                else if(4*this.firstDice - prevPos == newPos && this.timesHadBeenPlayed < 1)
                {
                    if(this.isValidCollision(4*this.firstDice + prevPos, true) && this.isValidCollision(3*this.firstDice + prevPos, true) 
                    && this.isValidCollision(2*this.firstDice + prevPos, true) && this.isValidCollision(this.firstDice + prevPos ,true))
                    {
                        this.isValidCollision(4*this.firstDice + prevPos, false);
                        this.isValidCollision(3*this.firstDice + prevPos, false);
                        this.isValidCollision(2*this.firstDice + prevPos, false);
                        this.isValidCollision(this.firstDice + prevPos ,false);
                        this.timesHadBeenPlayed = 4;
                        return true;
                    }                  
                }                 
                //den einai egkurh kinhsh
            }//ama den einai diples
            else
            {
                if(!this.firstDicePlayed && this.firstDice + prevPos == newPos && this.timesHadBeenPlayed < 2)
                {
                    if(this.isValidCollision(this.firstDice + prevPos, false))
                    {
                        firstDicePlayed = true;
                        this.timesHadBeenPlayed++;
                        return true;
                    }
                }
                else if(!this.secDicePlayed && this.secondDice + prevPos == newPos && this.timesHadBeenPlayed < 2)
                {
                    if(this.minPossibleMoves == 1 )
                    {
                        //ama exoume mia epiloghmono elegxoume an mporoume na paiksoume to megaluero zari
                        //an nai upoxrewtika paizoume th megaluerh zaria
                        State temp = new State(this);
                        if(temp.isValidMove(prevPos, prevPos + temp.firstDice))return false;                        
                    }
                    if(this.isValidCollision(this.secondDice + prevPos, false))
                    {
                        secDicePlayed = true;
                        this.timesHadBeenPlayed++;
                        return true;
                    }
                }
                else if(!this.firstDicePlayed && !this.secDicePlayed && this.timesHadBeenPlayed < 1 && (this.firstDice + this.secondDice) + prevPos == newPos)
                {
                    if(this.isValidCollision(this.firstDice + prevPos, true) && this.isValidCollision((this.firstDice + this.secondDice) + prevPos ,true))
                    {
                        this.isValidCollision(this.firstDice + prevPos, false) ;
                        this.isValidCollision((this.firstDice + this.secondDice) + prevPos ,false);
                        this.firstDicePlayed = true;
                        this.secDicePlayed = true;
                        this.timesHadBeenPlayed+=2;
                        return true;                        
                    }
                    else if(this.isValidCollision(this.secondDice + prevPos, true) && this.isValidCollision((this.firstDice + this.secondDice) + prevPos ,true))
                    {
                        this.isValidCollision(this.secondDice + prevPos, false) ;
                        this.isValidCollision((this.firstDice + this.secondDice) + prevPos ,false);
                        this.firstDicePlayed = true;
                        this.secDicePlayed = true;
                        this.timesHadBeenPlayed+= 2;
                        return true;                             
                    }
                }
            }
            return false;
        }//ama eimaste mesa kai den phgainoume sth telikh perioxh                  
        return false;
    }

    private boolean isValidMoveRed(int prevPos, int newPos)
    {
        if(prevPos < 0 || newPos < 0 || newPos > 27||  prevPos > 27 ||  newPos == State.RED_DAMAGED_AREA 
        || newPos == State.WHITE_DAMAGED_AREA || newPos == State.WHITE_FINAL_AREA|| this.stateCheckerArray[prevPos].isWhiteArea()
        || this.stateCheckerArray[prevPos].isEmpty()) return false;
        if(newPos == State.RED_FINAL_AREA)
        {            
            if(this.isDiples && this.timesHadBeenPlayed < 4)
            {
                if(prevPos == this.validAreaToPickFromChecker(this.firstDice))
                {
                    this.timesHadBeenPlayed++;                             
                    return true;                                                                                                    
                }
            }
            else
            {
                if(!this.firstDicePlayed && prevPos == this.validAreaToPickFromChecker(this.firstDice))
                {
                    this.firstDicePlayed = true;                                                   
                    this.timesHadBeenPlayed++;                             
                    return true;                                                                                                                                                    
                }
                if(!this.secDicePlayed && prevPos == this.validAreaToPickFromChecker(this.secondDice))
                {
                    this.secDicePlayed = true;                                                   
                    this.timesHadBeenPlayed++;                             
                    return true;                                                                                                                                                    
                }
            }            
        }
        else 
        {
            if(prevPos == State.RED_DAMAGED_AREA) prevPos = 24;
            if(this.isDiples && this.timesHadBeenPlayed < 4)
            {
                if(prevPos - this.firstDice == newPos && this.timesHadBeenPlayed < 4)
                {
                    if(this.isValidCollision(prevPos - this.firstDice  , false))
                    {
                        this.timesHadBeenPlayed++;
                        return true;
                    }
                }
                else if(prevPos - 2*this.firstDice  == newPos && this.timesHadBeenPlayed < 3)
                {                   
                    if(this.isValidCollision(prevPos - 2*this.firstDice, true) && this.isValidCollision(prevPos - this.firstDice ,true))
                    {
                        this.isValidCollision(prevPos - 2*this.firstDice, false);
                        this.isValidCollision(prevPos - this.firstDice ,false);
                        this.timesHadBeenPlayed+= 2;
                        return true;
                    }
                }
                else if(prevPos - 3*this.firstDice  == newPos && this.timesHadBeenPlayed < 2)
                {
                    if(this.isValidCollision(prevPos - 3*this.firstDice, true) && this.isValidCollision(prevPos - 2*this.firstDice, true) 
                            && this.isValidCollision(prevPos - this.firstDice ,true))
                    {
                        this.isValidCollision(prevPos - 3*this.firstDice, false);
                        this.isValidCollision(prevPos - 2*this.firstDice, false);
                        this.isValidCollision(prevPos - this.firstDice ,false);
                        this.timesHadBeenPlayed+= 3;
                        return true;
                    }                  
                }
                else if(prevPos - 4*this.firstDice == newPos && this.timesHadBeenPlayed < 1)
                {
                    if(this.isValidCollision(prevPos - 4*this.firstDice, true) && this.isValidCollision(prevPos - 3*this.firstDice, true) 
                    && this.isValidCollision(prevPos - 2*this.firstDice, true) && this.isValidCollision(prevPos - this.firstDice ,true))
                    {
                        this.isValidCollision(prevPos - 4*this.firstDice, false);
                        this.isValidCollision(prevPos - 3*this.firstDice, false);
                        this.isValidCollision(prevPos - 3*this.firstDice, false);
                        this.isValidCollision(prevPos - 1*this.firstDice ,false);
                        this.timesHadBeenPlayed+= 4;
                        return true;
                    }                  
                }                 
                //den einai egkurh kinhsh
            }//ama den einai diples
            else
            {
                if(!this.firstDicePlayed && prevPos - this.firstDice == newPos && this.timesHadBeenPlayed < 2)
                {
                    if(this.isValidCollision(prevPos - this.firstDice, false))
                    {                        
                        this.firstDicePlayed = true;
                        this.timesHadBeenPlayed++;
                        return true;
                    }
                }
                else if(!this.secDicePlayed && prevPos - this.secondDice == newPos && this.timesHadBeenPlayed < 2)
                {
                    if(this.minPossibleMoves == 1 )
                    {
                        //ama exoume mia epilogh mono elegxoume an mporoume na paiksoume to megaluero zari
                        //an nai upoxrewtika paizoume th megaluerh zaria, h mikroterh einai invalid
                        State temp = new State(this);
                        if(temp.isValidMove(prevPos, prevPos - temp.firstDice))return false;                        
                    }                    
                    if(this.isValidCollision(prevPos - this.secondDice, false))
                    {                        
                        secDicePlayed = true;
                        this.timesHadBeenPlayed++;
                        return true;
                    }
                }
                else if(!this.firstDicePlayed && !this.secDicePlayed && this.timesHadBeenPlayed < 1 &&  prevPos -(this.firstDice + this.secondDice)  == newPos)
                {
                    if(this.isValidCollision(prevPos - this.firstDice , true) && this.isValidCollision(prevPos -(this.firstDice + this.secondDice) ,true))
                    {
                        this.isValidCollision(prevPos - this.firstDice, false) ;
                        this.isValidCollision(prevPos -(this.firstDice + this.secondDice) ,false);
                        this.firstDicePlayed = true;
                        this.secDicePlayed = true;
                        this.timesHadBeenPlayed+=2;
                        return true;                        
                    }
                    else if(this.isValidCollision(prevPos - this.secondDice , true) && this.isValidCollision(prevPos -(this.firstDice + this.secondDice)  ,true))
                    {
                        this.isValidCollision(prevPos -this.secondDice , false) ;
                        this.isValidCollision(prevPos -(this.firstDice + this.secondDice) ,false);
                        this.firstDicePlayed = true;
                        this.secDicePlayed = true;
                        this.timesHadBeenPlayed+= 2;
                        return true;                             
                    }
                }
            }            
        }//end if  ama eimaste mesa kai den phgainoume sth telikh perioxh                  
        return false;
    }    

    /**
     * 
     * @return true iff  player`s ALL pieces are in home Area. 
     * Home area is all positions in the board  ?: in  [18 ,23] for white , in [0, 5] for red 
     */
    protected boolean allPiecesToHomeArea()
    {
        int sum = 0;
        if(this.isWhiteTurn)
        {
            for(int i = 23; i >= 18;i--)
            {
                if(this.stateCheckerArray[i].isWhiteArea()) sum += this.stateCheckerArray[i].getSize();
            }
            sum += this.stateCheckerArray[WHITE_FINAL_AREA].getSize();
        }
        else
        {
            for(int i = 0; i <= 5;i++)
            {
                if(!this.stateCheckerArray[i].isWhiteArea()) sum += this.stateCheckerArray[i].getSize();
            }
            sum += this.stateCheckerArray[RED_FINAL_AREA].getSize();
        }
        return sum == 15;
     }
   /**
     * Check if a collision with an area is valid. Ama xtuphsoume ena antipalo automata to pouli tou metaferetai sth damaged area tou kai 
     * enhmerwnontai h stoiva twn TempMove kai twn xtuphmenw pouliwn se auto to guro gia auth th TempMOve tou pouliou tou antipalou
     * @param targetedPosition the position we want to check
     * @param isInternalCheck 
     * @return 
     */
    private boolean isValidCollision(int targetedPosition, boolean isInternalCheck)
    {               
        //if(!this.isWhiteTurn)System.out.println("GEIA");
        int damagedArea = this.isWhiteTurn?State.RED_DAMAGED_AREA:State.WHITE_DAMAGED_AREA;        
        if(!this.stateCheckerArray[targetedPosition].isEmpty())
        {                                
            if(this.isWhiteTurn != this.stateCheckerArray[targetedPosition].isWhiteArea())
            {
                if(this.stateCheckerArray[targetedPosition].isOccupied())
                {
                    return false;
                }
                else
                {            
                    if(!isInternalCheck){   
                        
                        this.hitOpPieces.add(new TempMove(targetedPosition, damagedArea, stateCheckerArray[targetedPosition].getPiece(0)));
                        this.tempMoveStack.add(new TempMove(targetedPosition, damagedArea, stateCheckerArray[targetedPosition].getPiece(0)));
                        this.stateCheckerArray[damagedArea].appendPiece(this.stateCheckerArray[targetedPosition].removePieceFromTop());
                    }                    
                    return true;
                }
            } 
            else
            {                                
                return true;
            }
        }                
        return true;               
    }

    @Override
    public boolean equals(Object b)
    {
        if(!(b instanceof State)) return false;
        State board = (State)b;
        for(int i = 0; i < this.stateCheckerArray.length; i ++)
        {
            if(stateCheckerArray[i].isWhiteArea() == board.stateCheckerArray[i].isWhiteArea() && stateCheckerArray[i].getSize() == board.stateCheckerArray[i].getSize()) {
            } else
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Arrays.deepHashCode(this.stateCheckerArray);
        return hash;
    }

    @Override
    public String toString()
    {
        //System.out.println(this.tempMoveStack.size());

        String str = "";
        /*
        for(int i = 0 ; i < this.tempMoveStack.size();i++)
        {
            if(this.isWhiteTurn && this.tempMoveStack.get(i).piece instanceof RedPiece)continue;
            if(!this.isWhiteTurn && this.tempMoveStack.get(i).piece instanceof WhitePiece) continue;
            str += String.valueOf(this.tempMoveStack.get(i).prevPos) + " " + String.valueOf(this.tempMoveStack.get(i).newPos + "\n");
        }
        str += "--------------------------\n";
         *
         */

        for(int i = 0 ; i < this.stateCheckerArray.length; i++)
        {
            if(stateCheckerArray[i].isEmpty()) continue;
            str += i + ")" + stateCheckerArray[i] + "\n";
        }
        str = str +  "Dices : " +  this.firstDice + " " + this.secondDice + "\r\n";
         
        return str;
    }  
}

package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Nikos
 */
public interface GamePlayerFather {
    
    public State ExpectiMiniMax(State state);    
    public int getExpandedNodes();             
}

package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;

public class MoveWood implements StripsAction{

     private int[] peasantIds;
     private int[] resourceIds;
     private int peasantsInvolved;
     
     public MoveWood(int k) {
          peasantsInvolved=k;
     }
     
     @Override
     public boolean preconditionsMet(GameState state) {
          //at least k peasants not next to forest and with empty hands
          //for(int i=0;i<state.)
          //TODO
          return false;
     }

     @Override
     public GameState apply(GameState state) {
          // TODO Auto-generated method stub
          return null;
     }

     @Override
     public Action toSepiaAction() {
          // TODO Auto-generated method stub
          return null;
     }

}

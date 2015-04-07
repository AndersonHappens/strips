package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;

public class CompoundGatherWood implements StripsAction {

     int peasantsInvolved;
     
     public CompoundGatherWood(int k){
          peasantsInvolved=k;
     }
     
     @Override
     public boolean preconditionsMet(GameState state) {
          MoveWood action=new MoveWood(peasantsInvolved);
          return action.preconditionsMet(state);
     }

     @Override
     public GameState apply(GameState state) {
          //Note that each action checks it's preconditions during apply(...)
          MoveWood moveWood=new MoveWood(peasantsInvolved);
          GameState moveWoodState=moveWood.apply(state);
          if(moveWoodState==null) {
               return null;
          }
          GatherWood gatherWood=new GatherWood(peasantsInvolved);
          GameState gatherWoodState=gatherWood.apply(moveWoodState);
          if(gatherWoodState==null) {
               return null;
          }
          MoveTownHall moveTownHall=new MoveTownHall(peasantsInvolved);
          GameState moveTownHallState=moveTownHall.apply(gatherWoodState);
          if(moveTownHallState==null) {
               return null;
          }
          DepositWood depositWood=new DepositWood(peasantsInvolved);
          GameState depositWoodState=depositWood.apply(moveTownHallState);
          if(depositWoodState==null) {
               return null;
          }
          return depositWoodState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          // TODO Auto-generated method stub
          return null;
     }

     @Override
     public Integer[] getPeasantIdsInvolved() {
          // TODO Auto-generated method stub
          return null;
     }

}

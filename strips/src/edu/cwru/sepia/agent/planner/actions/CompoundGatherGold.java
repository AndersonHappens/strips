package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;

public class CompoundGatherGold implements StripsAction {

     int peasantsInvolved;
     private Integer[] peasantIdsInvolved;
     private ArrayList<Action> actions = new ArrayList<Action>();
     
     public CompoundGatherGold(int k){
          peasantsInvolved=k;
     }
     
     @Override
     public boolean preconditionsMet(GameState state) {
          MoveGold action=new MoveGold(peasantsInvolved);
          return action.preconditionsMet(state);
     }

     @Override
     public GameState apply(GameState state) {
          //Note that each action checks it's preconditions during apply(...), returns null if preconditions are not met.
          MoveGold moveGold=new MoveGold(peasantsInvolved);
          peasantIdsInvolved = moveGold.getPeasantIdsInvolved();
          GameState moveGoldState=moveGold.apply(state);
          if(moveGoldState==null) {
               return null;
          }
          GatherGold gatherGold=new GatherGold(peasantsInvolved);
          GameState gatherGoldState=gatherGold.apply(moveGoldState);
          if(gatherGoldState==null) {
               return null;
          }
          MoveTownHall moveTownHall=new MoveTownHall(peasantsInvolved);
          GameState moveTownHallState=moveTownHall.apply(gatherGoldState);
          if(moveTownHallState==null) {
               return null;
          }
          DepositGold depositGold=new DepositGold(peasantsInvolved);
          GameState depositGoldState=depositGold.apply(moveTownHallState);
          if(depositGoldState==null) {
               return null;
          }
          return depositGoldState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          return actions;
     }

     @Override
     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved;
     }

}

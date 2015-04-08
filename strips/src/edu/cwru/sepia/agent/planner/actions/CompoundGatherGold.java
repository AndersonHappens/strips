package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

public class CompoundGatherGold implements StripsAction {

     int peasantsInvolved;
     private Integer[] peasantIdsInvolved;
     
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
     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved;
     }

}

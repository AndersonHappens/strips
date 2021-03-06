package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;


public class DepositGold implements StripsAction {
     
     private Integer[] peasantIdsInvolved;
     private Position[] peasantPositionsInvolved;
     private int peasantsInvolved;
     private Position townHallPosition;
     
     public DepositGold(int k) {
          peasantsInvolved=k;
          peasantIdsInvolved=new Integer[peasantsInvolved];
          peasantPositionsInvolved=new Position[peasantsInvolved];
          townHallPosition=null;
     }

     @Override
     public boolean preconditionsMet(GameState state) {
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          Position[] peasantPositions=state.getPeasantPositions().toArray(new Position[0]);
          Integer[] peasantCargo=state.getPeasantCargo().toArray(new Integer[0]);
          townHallPosition=state.getTownHallPosition();
          int peasantsAvailable=0;
          for(int i=0;i<peasantPositions.length && peasantsInvolved>peasantsAvailable;i++) {
               if(peasantPositions[i].isAdjacent(townHallPosition) && peasantCargo[i].intValue()==GameState.GOLD) {
                    peasantIdsInvolved[peasantsAvailable]=peasantIDs[i];
                    peasantPositionsInvolved[peasantsAvailable]=peasantPositions[i];
                    peasantsAvailable++;
               }
          }
          if(peasantsAvailable>=peasantsInvolved) {
               return true;
          } else {
               return false;
          }
     }

     @Override
     public GameState apply(GameState state) {
          GameState newState=state.copyOf();
          if(!preconditionsMet(newState)) {
               return null;
          }
          Integer[] peasantIDs=newState.getPeasantIds().toArray(new Integer[0]);
          ArrayList<Integer> peasantCargo=newState.getPeasantCargo();
          for(int i=0;i<peasantIDs.length;i++) {
               for(int j=0;j<peasantIdsInvolved.length;j++) {
                    if(peasantIDs[i].equals(peasantIdsInvolved[j])) {
                         peasantCargo.set(i, GameState.NONE);
                    }
               }
          }
          newState.setGoldAmount(newState.getGoldAmount()+peasantsInvolved*100);
          newState.setPeasantCargo(peasantCargo);
          newState.setParent(state);
          newState.setAction(this);
          return newState;
     }

     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved;
     }

     public Position[] getPeasantPositionsInvolved() {
          return peasantPositionsInvolved;
     }

     public Position getTownHallPosition() {
          return townHallPosition;
     }
     
     public String toString() {
          return "DepositGold("+Arrays.toString(peasantIdsInvolved)+")";
     }
}

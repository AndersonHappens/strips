package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;


public class GatherGold implements StripsAction {
     
     private Integer[] peasantIdsInvolved;
     private Position[] peasantPositionsInvolved;
     private Position[] minePositionsInvolved;
     private int[] newGoldAmounts;
     private int peasantsInvolved;
     
     public GatherGold(int k) {
          peasantsInvolved=k;
          peasantIdsInvolved=new Integer[peasantsInvolved];
          peasantPositionsInvolved=new Position[peasantsInvolved];
          minePositionsInvolved=new Position[peasantsInvolved];
     }

     @Override
     public boolean preconditionsMet(GameState state) {
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          Position[] peasantPositions=state.getPeasantPositions().toArray(new Position[0]);
          Integer[] peasantCargo=state.getPeasantCargo().toArray(new Integer[0]);
          Position[] minePositions=state.getGoldPositions();
          newGoldAmounts=Arrays.copyOf(state.getGoldAmounts(),state.getGoldAmounts().length);
          int peasantsAvailable=0;
          for(int i=0;i<peasantPositions.length && peasantsInvolved>peasantsAvailable;i++) {
               for(int j=0;j<minePositions.length;j++) {
                   if(peasantPositions[i].isAdjacent(minePositions[j]) && peasantCargo[i].intValue()==GameState.NONE && newGoldAmounts[j]>=100) {
                         peasantIdsInvolved[peasantsAvailable]=peasantIDs[i];
                         peasantPositionsInvolved[peasantsAvailable]=peasantPositions[i];
                         minePositionsInvolved[peasantsAvailable]=minePositions[j];
                         newGoldAmounts[j]-=100;
                         peasantsAvailable++;
                         break;
                    }
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
          ArrayList<Integer> peasantCargo=new ArrayList<Integer>(newState.getPeasantCargo());
          for(int i=0;i<peasantIDs.length;i++) {
               for(int j=0;j<peasantIdsInvolved.length;j++) {
                    if(peasantIDs[i].equals(peasantIdsInvolved[j])) {
                         peasantCargo.set(i, GameState.GOLD);
                    }
               }
          }
          newState.setGoldAmounts(newGoldAmounts);
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

     public Position[] getMinePositionsInvolved() {
          return minePositionsInvolved;
     }
     
     public String toString() {
          return "GatherGold("+Arrays.toString(peasantIdsInvolved)+", "+Arrays.toString(minePositionsInvolved)+")";
     }
}

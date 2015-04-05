package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;

import edu.cwru.sepia.action.Action;
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
          Position[] treePositions=state.getGoldPositions();
          newGoldAmounts=state.getGoldAmounts();
          int peasantsAvailable=0;
          for(int i=0;i<peasantPositions.length && peasantsInvolved>peasantsAvailable;i++) {
               for(int j=0;j<treePositions.length;j++) {
                    if(peasantPositions[i].isAdjacent(treePositions[j]) && peasantCargo[i].intValue()==GameState.NONE && newGoldAmounts[j]>=100) {
                         peasantIdsInvolved[peasantsAvailable]=peasantIDs[i];
                         peasantPositionsInvolved[peasantsAvailable]=peasantPositions[i];
                         minePositionsInvolved[peasantsAvailable]=treePositions[j];
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
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          ArrayList<Integer> peasantCargo=state.getPeasantCargo();
          for(int i=0;i<peasantIDs.length;i++) {
               for(int j=0;j<peasantIdsInvolved.length;j++) {
                    if(peasantIDs[i].equals(peasantIdsInvolved.length)) {
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

     @Override
     public ArrayList<Action> toSepiaAction() {
          ArrayList<Action> actions=new ArrayList<Action>();
          for(int i=0;i<peasantIdsInvolved.length;i++) {
               actions.add(Action.createPrimitiveGather(peasantIdsInvolved[i], peasantPositionsInvolved[i].getDirection(minePositionsInvolved[i])));   
          }
          return actions;
     }
}

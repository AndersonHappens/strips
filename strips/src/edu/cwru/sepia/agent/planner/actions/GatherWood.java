package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;


public class GatherWood implements StripsAction {
     
     private Integer[] peasantIdsInvolved;
     private Position[] peasantPositionsInvolved;
     private Position[] treePositionsInvolved;
     private int[] newWoodAmounts;
     private int peasantsInvolved;
     
     public GatherWood(int k) {
          peasantsInvolved=k;
          peasantIdsInvolved=new Integer[peasantsInvolved];
          peasantPositionsInvolved=new Position[peasantsInvolved];
          treePositionsInvolved=new Position[peasantsInvolved];
     }

     @Override
     public boolean preconditionsMet(GameState state) {
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          Position[] peasantPositions=state.getPeasantPositions().toArray(new Position[0]);
          Integer[] peasantCargo=state.getPeasantCargo().toArray(new Integer[0]);
          Position[] treePositions=state.getWoodPositions();
          newWoodAmounts=state.getWoodAmounts();
          int peasantsAvailable=0;
          for(int i=0;i<peasantPositions.length && peasantsInvolved>peasantsAvailable;i++) {
               for(int j=0;j<treePositions.length;j++) {
                    if(peasantPositions[i].isAdjacent(treePositions[j]) && peasantCargo[i].intValue()==GameState.NONE && newWoodAmounts[j]>=100) {
                         peasantIdsInvolved[peasantsAvailable]=peasantIDs[i];
                         peasantPositionsInvolved[peasantsAvailable]=peasantPositions[i];
                         treePositionsInvolved[peasantsAvailable]=treePositions[j];
                         newWoodAmounts[j]-=100;
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
          if(!preconditionsMet(state)) {
               return null;
          }
          GameState newState=state.copyOf();
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          ArrayList<Integer> peasantCargo=state.getPeasantCargo();
          for(int i=0;i<peasantIDs.length;i++) {
               for(int j=0;j<peasantIdsInvolved.length;j++) {
                    if(peasantIDs[i].equals(peasantIdsInvolved.length)) {
                         peasantCargo.set(i, GameState.WOOD);
                    }
               }
          }
          newState.setWoodAmounts(newWoodAmounts);
          newState.setPeasantCargo(peasantCargo);
          newState.setParent(state);
          newState.setAction(this);
          return newState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          ArrayList<Action> actions=new ArrayList<Action>();
          for(int i=0;i<peasantIdsInvolved.length;i++) {
               actions.add(Action.createPrimitiveGather(peasantIdsInvolved[i], peasantPositionsInvolved[i].getDirection(treePositionsInvolved[i])));   
          }
          return actions;
     }

     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved;
     }

     public Position[] getPeasantPositionsInvolved() {
          return peasantPositionsInvolved;
     }

     public Position[] getTreePositionsInvolved() {
          return treePositionsInvolved;
     }
}
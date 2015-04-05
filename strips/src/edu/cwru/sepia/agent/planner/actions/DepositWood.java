package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;


public class DepositWood implements StripsAction {
     
     private Integer[] peasantIdsInvolved;
     private Position[] peasantPositionsInvolved;
     private int peasantsInvolved;
     private Position townHallPosition;
     
     public DepositWood(int k) {
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
               if(peasantPositions[i].isAdjacent(townHallPosition) && peasantCargo[i].intValue()==GameState.WOOD) {
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
          if(!preconditionsMet(state)) {
               return null;
          }
          GameState newState=state.copyOf();
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          ArrayList<Integer> peasantCargo=state.getPeasantCargo();
          for(int i=0;i<peasantIDs.length;i++) {
               for(int j=0;j<peasantIdsInvolved.length;j++) {
                    if(peasantIDs[i].equals(peasantIdsInvolved.length)) {
                         peasantCargo.set(i, GameState.NONE);
                    }
               }
          }
          newState.setWoodAmount(newState.getWoodAmount()+peasantsInvolved*100);
          newState.setPeasantCargo(peasantCargo);
          newState.setParent(state);
          newState.setAction(this);
          return newState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          ArrayList<Action> actions=new ArrayList<Action>();
          for(int i=0;i<peasantIdsInvolved.length;i++) {
               actions.add(Action.createPrimitiveDeposit(peasantIdsInvolved[i], peasantPositionsInvolved[i].getDirection(townHallPosition)));   
          }
          return actions;
     }
}

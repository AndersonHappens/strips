package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;

public class BuildPeasant implements StripsAction {
     
     private int newPeasantId;

     @Override
     public boolean preconditionsMet(GameState state) {
          return state.isBuildPeasantsAvailable() && state.getPeasantIds().size()<3 && state.getGoldAmount()>=400;
     }

     @Override
     public GameState apply(GameState state) {
          if(!preconditionsMet(state)) {
               return null;
          }
          GameState newState=state.copyOf();
          newPeasantId=newState.getPeasantIds().size()+1;
          newState.getPeasantIds().add(newPeasantId);
          newState.getPeasantCargo().add(GameState.NONE);
          //the new peasants location is slightly off, this will be fixed after it's first move
          newState.getPeasantPositions().add(state.getTownHallPosition());
          newState.setGoldAmount(newState.getGoldAmount()-400);
          newState.setParent(state);
          newState.setAction(this);
          return newState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          // TODO Auto-generated method stub
          return null;
     }
     
     public int getNewPeasantId() {
          return newPeasantId;
     }

     @Override
     public Integer[] getPeasantIdsInvolved() {
          return new Integer[0];
     }
}

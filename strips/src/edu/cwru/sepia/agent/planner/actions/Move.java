package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;

public abstract class Move implements StripsAction {

     @Override
     public abstract boolean preconditionsMet(GameState state);
     @Override
     public abstract GameState apply(GameState state);

     @Override
     public abstract ArrayList<Action> toSepiaAction();
     
     public boolean isValid(Position p, GameState state, ArrayList<Position> newPeasantPositions) {
          if(!p.inBounds(state.getxSize(), state.getySize())) {
               return false;
          }
          if(p.equals(state.getTownHallPosition())) {
               return false;
          }
          for(Position position:state.getWoodPositions()) {
               if(p.equals(position)) {
                    return false;
               }
          }
          for(Position position:state.getGoldPositions()) {
               if(p.equals(position)) {
                    return false;
               }
          }
          for(Position position:newPeasantPositions) {
               if(p.equals(position)) {
                    return false;
               }
          }
          return true;
     }
}

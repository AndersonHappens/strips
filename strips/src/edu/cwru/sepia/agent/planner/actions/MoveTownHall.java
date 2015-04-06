package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.PriorityQueue;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;

public class MoveTownHall extends Move implements StripsAction{

     private ArrayList<Integer> peasantIdsInvolved;
     private ArrayList<Position> targetPositions;
     private ArrayList<Position> newPeasantPositions;
     private int peasantsInvolved;
     
     public MoveTownHall(int k) {
          peasantsInvolved=k;
          peasantIdsInvolved=new ArrayList<Integer>();
          newPeasantPositions=new ArrayList<Position>();
     }
     
     @Override
     public boolean preconditionsMet(GameState state) {
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          Position[] peasantPositions=state.getPeasantPositions().toArray(new Position[0]);
          newPeasantPositions=new ArrayList<Position>(state.getPeasantPositions());
          Integer[] peasantCargo=state.getPeasantCargo().toArray(new Integer[0]);
          Position townHallPosition=state.getTownHallPosition();
          PriorityQueue<CandidateMove> candidateMoves=new PriorityQueue<CandidateMove>();
          for(int i=0;i<peasantIDs.length;i++) {
               if(peasantCargo[i]==GameState.NONE) {
                    if(!peasantPositions[i].isAdjacent(townHallPosition)) {
                         for(Position p:townHallPosition.getAdjacentPositions()) {
                              candidateMoves.add(new CandidateMove(peasantIDs[i],i,p,p.chebyshevDistance(peasantPositions[i])));
                         }
                    }
               }
          }
          peasantIdsInvolved=new ArrayList<Integer>();
          targetPositions=new ArrayList<Position>();
          while(peasantIdsInvolved.size()<peasantsInvolved && !candidateMoves.isEmpty()) {
               CandidateMove current=candidateMoves.poll();
               if(!peasantIdsInvolved.contains(current.unitId) && isValid(current.targetLocation,state,newPeasantPositions)) {
                    peasantIdsInvolved.add(current.unitId);
                    targetPositions.add(current.targetLocation);
                    newPeasantPositions.set(current.unitIndex, current.targetLocation);
               }
          }
          if(peasantIdsInvolved.size()>=peasantsInvolved) {
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
          newState.setPeasantPositions(newPeasantPositions);
          newState.setParent(state);
          newState.setAction(this);
          return newState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          ArrayList<Action> moves=new ArrayList<Action>();
          for(int i=0;i<peasantIdsInvolved.size();i++) {
               moves.add(Action.createCompoundMove(peasantIdsInvolved.get(i),targetPositions.get(i).x,targetPositions.get(i).y));
          }
          return moves;
     }

     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved.toArray(new Integer[0]);
     }

     public ArrayList<Position> getTargetPositions() {
          return targetPositions;
     }
}

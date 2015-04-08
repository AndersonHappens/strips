package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.PriorityQueue;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;

public class MoveGold extends Move implements StripsAction{

     private ArrayList<Integer> peasantIdsInvolved;
     private ArrayList<Position> targetPositions;
     private ArrayList<Position> newPeasantPositions;
     private int peasantsInvolved;
     private int distanceMoved;
     
     public MoveGold(int k) {
          peasantsInvolved=k;
          peasantIdsInvolved=new ArrayList<Integer>();
          newPeasantPositions=new ArrayList<Position>();
     }
     
     @Override
     public boolean preconditionsMet(GameState state) {
          if(state.getGoldAmount() >= state.getGoalGoldAmount() + 400) {
         		 return false;
         	}
          Integer[] peasantIDs=state.getPeasantIds().toArray(new Integer[0]);
          Position[] peasantPositions=state.getPeasantPositions().toArray(new Position[0]);
          newPeasantPositions=new ArrayList<Position>(state.getPeasantPositions());
          Integer[] peasantCargo=state.getPeasantCargo().toArray(new Integer[0]);
          Position[] minePositions=state.getGoldPositions();
          int[] mineAmounts=state.getGoldAmounts();
          PriorityQueue<CandidateMove> candidateMoves=new PriorityQueue<CandidateMove>();
          for(int i=0;i<peasantIDs.length;i++) {
               if(peasantCargo[i]==GameState.NONE) {
                    for(int j=0;j<minePositions.length;j++) {
                         if(!peasantPositions[i].isAdjacent(minePositions[j]) && mineAmounts[j]>=100) {
                              for(Position p:minePositions[j].getAdjacentPositions()) {
                            	  distanceMoved = p.chebyshevDistance(peasantPositions[i]);
                                  candidateMoves.add(new CandidateMove(peasantIDs[i],i,p, distanceMoved));
                              }
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
          GameState newState=state.copyOf();
          if(!preconditionsMet(newState)) {
               return null;
          }
          newState.setPeasantPositions(newPeasantPositions);
          newState.setParent(state);
          newState.setAction(this);
          return newState;
     }

     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved.toArray(new Integer[0]);
     }

     public ArrayList<Position> getTargetPositions() {
          return targetPositions;
     }
     
     public int getDistanceMoved() {
    	 return distanceMoved;
     }
     
     public String toString() {
          return "MoveGold("+peasantIdsInvolved+", "+targetPositions+")";
     }
}

package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.actions.Move.CandidateMove;

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
          //System.out.println("called movegold preconditions met");
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
          /*if(state.getGoldAmount()>=100) {
               System.out.println(candidateMoves);
               System.out.println(Arrays.toString(mineAmounts));
          }*/
          peasantIdsInvolved=new ArrayList<Integer>();
          targetPositions=new ArrayList<Position>();
          while(peasantIdsInvolved.size()<peasantsInvolved && !candidateMoves.isEmpty()) {
               CandidateMove current=candidateMoves.poll();
               if(!peasantIdsInvolved.contains(current.unitId) && isValid(current.targetLocation,state,newPeasantPositions)) {
                    peasantIdsInvolved.add(current.unitId);
                    targetPositions.add(current.targetLocation);
                    newPeasantPositions.set(current.unitIndex, current.targetLocation);
               }
               //System.out.println("PeasantIds for move gold: "+peasantIdsInvolved);
          }
          //System.out.println("the new peasant positions "+newPeasantPositions);
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
          //System.out.println("the new peasant positions that are applied "+newPeasantPositions);
          //System.out.println("the new peasant positions that were applied "+newState.getPeasantPositions());
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
     
     public int getDistanceMoved() {
    	 return distanceMoved;
     }
}

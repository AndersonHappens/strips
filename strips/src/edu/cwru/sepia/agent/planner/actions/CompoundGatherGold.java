package edu.cwru.sepia.agent.planner.actions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.GameState;

public class CompoundGatherGold implements StripsAction {

     int peasantsInvolved;
     private Integer[] peasantIdsInvolved;
     private ArrayList<Action> actions = new ArrayList<Action>();
     
     public CompoundGatherGold(int k){
          peasantsInvolved=k;
     }
     
     @Override
     public boolean preconditionsMet(GameState state) {
          MoveGold action=new MoveGold(peasantsInvolved);
          return action.preconditionsMet(state);
     }

     @Override
     public GameState apply(GameState state) {
          //Note that each action checks it's preconditions during apply(...), returns null if preconditions are not met.
          GameState newState=state.copyOf();
          System.out.println("this is that line: "+newState+"  "+state);
          System.out.println(""+(newState == state));
          MoveGold moveGold=new MoveGold(peasantsInvolved);
          peasantIdsInvolved = moveGold.getPeasantIdsInvolved();
          GameState moveGoldState=moveGold.apply(newState);
          if(moveGoldState==null) {
               return null;
          }
          System.out.println("move gold "+moveGoldState.getPeasantPositions());
          System.out.println(moveGoldState.getParent()+"  "+state);
          System.out.println(moveGoldState.getAction());
          GatherGold gatherGold=new GatherGold(peasantsInvolved);
          GameState gatherGoldState=gatherGold.apply(moveGoldState);
          if(gatherGoldState==null) {
               return null;
          }
          System.out.println("cargo: "+Arrays.toString(gatherGoldState.getGoldAmounts()));
          System.out.println(gatherGoldState.getParent()+"  "+moveGoldState);
          System.out.println(gatherGoldState.getAction());
          
          MoveTownHall moveTownHall=new MoveTownHall(peasantsInvolved);
          GameState moveTownHallState=moveTownHall.apply(gatherGoldState);
          if(moveTownHallState==null) {
               return null;
          }
          System.out.println(moveTownHallState.getPeasantPositions());
          System.out.println(moveTownHallState.getParent()+"  "+gatherGoldState);
          System.out.println(moveTownHallState.getAction());

          DepositGold depositGold=new DepositGold(peasantsInvolved);
          GameState depositGoldState=depositGold.apply(moveTownHallState);
          if(depositGoldState==null) {
               return null;
          }
          System.out.println("final gold amount: "+depositGoldState.getGoldAmount());
          return depositGoldState;
     }

     @Override
     public ArrayList<Action> toSepiaAction() {
          return actions;
     }

     @Override
     public Integer[] getPeasantIdsInvolved() {
          return peasantIdsInvolved;
     }

}

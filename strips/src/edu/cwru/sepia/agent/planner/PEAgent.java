package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * This is an outline of the PEAgent. Implement the provided methods. You may add your own methods and members.
 */
public class PEAgent extends Agent {

    // The plan being executed
    private Stack<StripsAction> plan = null;

    // maps the real unit Ids to the plan's unit ids
    // when you're planning you won't know the true unit IDs that sepia assigns. So you'll use placeholders (1, 2, 3).
    // this maps those placeholders to the actual unit IDs.
    private Map<Integer, Integer> peasantIdMap;
    private int townhallId;
    private int peasantTemplateId;
    
    private int newPeasantId;
    private boolean newPeasantCreated;

    public PEAgent(int playernum, Stack<StripsAction> plan) {
        super(playernum);
        peasantIdMap = new HashMap<Integer, Integer>();
        this.plan = plan;

    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {
        // gets the townhall ID and the peasant ID
        for(int unitId : stateView.getUnitIds(playernum)) {
            Unit.UnitView unit = stateView.getUnit(unitId);
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if(unitType.equals("townhall")) {
                townhallId = unitId;
            } else if(unitType.equals("peasant")) {
                peasantIdMap.put(unitId, unitId);
            }
        }

        // Gets the peasant template ID. This is used when building a new peasant with the townhall
        for(Template.TemplateView templateView : stateView.getTemplates(playernum)) {
            if(templateView.getName().toLowerCase().equals("peasant")) {
                peasantTemplateId = templateView.getID();
                break;
            }
        }

        return middleStep(stateView, historyView);
    }

    /**
     * This is where you will read the provided plan and execute it. If your plan is correct then when the plan is empty
     * the scenario should end with a victory. If the scenario keeps running after you run out of actions to execute
     * then either your plan is incorrect or your execution of the plan has a bug.
     *
     * You can create a SEPIA deposit action with the following method
     * Action.createPrimitiveDeposit(int peasantId, Direction townhallDirection)
     *
     * You can create a SEPIA harvest action with the following method
     * Action.createPrimitiveGather(int peasantId, Direction resourceDirection)
     *
     * You can create a SEPIA build action with the following method
     * Action.createPrimitiveProduction(int townhallId, int peasantTemplateId)
     *
     * You can create a SEPIA move action with the following method
     * Action.createCompoundMove(int peasantId, int x, int y)
     *
     * these actions are stored in a mapping between the peasant unit ID executing the action and the action you created.
     *
     * For the compound actions you will need to check their progress and wait until they are complete before issuing
     * another action for that unit. If you issue an action before the compound action is complete then the peasant
     * will stop what it was doing and begin executing the new action.
     *
     * To check an action's progress you can call getCurrentDurativeAction on each UnitView. If the Action is null nothing
     * is being executed. If the action is not null then you should also call getCurrentDurativeProgress. If the value is less than
     * 1 then the action is still in progress.
     *
     * Also remember to check your plan's preconditions before executing!
     */
    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
         System.out.println("in the middle step");
         if(newPeasantCreated) {
              peasantIdMap.put(newPeasantId, stateView.getAllUnitIds().get(stateView.getAllUnitIds().size()-1));
              newPeasantCreated=false;
         }
         Map<Integer, Action> map = new HashMap<Integer, Action>();
         int currentTurn = stateView.getTurnNumber();
         //System.out.println(plan);
	   // System.out.println(historyView.getCommandFeedback(playernum, currentTurn-1).values());
	    for(ActionResult feedback : historyView.getCommandFeedback(playernum, currentTurn-1).values())
         {
               if(feedback.getFeedback() == ActionFeedback.INCOMPLETE) {
                    return map;
               }
         }
	    if(!plan.isEmpty()) {
     	    StripsAction act=plan.pop();
     	    ArrayList<Action> actions=createSepiaAction(act);
     	    for(Action action:actions) {
     	         map.put(action.getUnitId(), action);
     	        // System.out.println(action.getUnitId()+"  "+action);
     	    }
	    }
         return map;
    }

    /**
     * Returns a SEPIA version of the specified Strips Action.
     * @param action StripsAction
     * @return SEPIA representation of same action
     */
    private ArrayList<Action> createSepiaAction(StripsAction action) {
        ArrayList<Action> actions=new ArrayList<Action>();
       /* if(action instanceof CompoundGatherGold) {
        	CompoundGatherGold act=(CompoundGatherGold) action;
            Integer[] peasantIdsInvolved = act.getPeasantIdsInvolved();
            Position[] peasantPositionsInvolved=act.getPeasantPositionsInvolved();
            Position[] treePositionsInvolved=act.getTreePositionsInvolved();
            for(int i=0;i<peasantIdsInvolved.length;i++) {
                actions.add(Action.createCompoundMove(peasantIdMap.get(peasantIdsInvolved[i]),targetPositions.get(i).x,targetPositions.get(i).y));
           }
            for(int i=0;i<peasantIdsInvolved.length;i++) {
                actions.add(Action.createPrimitiveGather(peasantIdMap.get(peasantIdsInvolved[i]), peasantPositionsInvolved[i].getDirection(treePositionsInvolved[i])));   
           }            
            for(int i=0;i<peasantIdsInvolved.length;i++) {
                actions.add(Action.createCompoundMove(peasantIdMap.get(peasantIdsInvolved[i]),targetPositions.get(i).x,targetPositions.get(i).y));
           }
            for(int i=0;i<peasantIdsInvolved.length;i++) {
                actions.add(Action.createPrimitiveDeposit(peasantIdMap.get(peasantIdsInvolved[i]), peasantPositionsInvolved[i].getDirection(townHallPosition)));   
           }
        }*/
        if(action instanceof GatherWood) {
             GatherWood act=(GatherWood) action;
             Integer[] peasantIdsInvolved = act.getPeasantIdsInvolved();
             Position[] peasantPositionsInvolved=act.getPeasantPositionsInvolved();
             Position[] treePositionsInvolved=act.getTreePositionsInvolved();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createPrimitiveGather(peasantIdMap.get(peasantIdsInvolved[i]), peasantPositionsInvolved[i].getDirection(treePositionsInvolved[i])));   
             }
        } else if(action instanceof GatherGold) {
             GatherGold act=(GatherGold) action;
             Integer[] peasantIdsInvolved = act.getPeasantIdsInvolved();
             Position[] peasantPositionsInvolved=act.getPeasantPositionsInvolved();
             Position[] minePositionsInvolved=act.getMinePositionsInvolved();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createPrimitiveGather(peasantIdMap.get(peasantIdsInvolved[i]), peasantPositionsInvolved[i].getDirection(minePositionsInvolved[i])));   
             }
        } else if(action instanceof DepositWood) {
             DepositWood act=(DepositWood) action;
             Integer[] peasantIdsInvolved = act.getPeasantIdsInvolved();
             Position[] peasantPositionsInvolved=act.getPeasantPositionsInvolved();
             Position townHallPosition=act.getTownHallPosition();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createPrimitiveDeposit(peasantIdMap.get(peasantIdsInvolved[i]), peasantPositionsInvolved[i].getDirection(townHallPosition)));   
             } 
        } else if(action instanceof DepositGold) {
             DepositGold act=(DepositGold) action;
             Integer[] peasantIdsInvolved = act.getPeasantIdsInvolved();
             Position[] peasantPositionsInvolved=act.getPeasantPositionsInvolved();
             Position townHallPosition=act.getTownHallPosition();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createPrimitiveDeposit(peasantIdMap.get(peasantIdsInvolved[i]), peasantPositionsInvolved[i].getDirection(townHallPosition)));   
             }
        } else if(action instanceof MoveWood) {
             MoveWood act=(MoveWood) action;
             Integer[] peasantIdsInvolved=act.getPeasantIdsInvolved();
             ArrayList<Position> targetPositions=act.getTargetPositions();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createCompoundMove(peasantIdMap.get(peasantIdsInvolved[i]),targetPositions.get(i).x,targetPositions.get(i).y));
             }
        } else if(action instanceof MoveGold) {
             MoveGold act=(MoveGold) action;
             Integer[] peasantIdsInvolved=act.getPeasantIdsInvolved();
             ArrayList<Position> targetPositions=act.getTargetPositions();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createCompoundMove(peasantIdMap.get(peasantIdsInvolved[i]),targetPositions.get(i).x,targetPositions.get(i).y));
             }
        } else if(action instanceof MoveTownHall) {
             MoveTownHall act=(MoveTownHall) action;
             Integer[] peasantIdsInvolved=act.getPeasantIdsInvolved();
             ArrayList<Position> targetPositions=act.getTargetPositions();
             for(int i=0;i<peasantIdsInvolved.length;i++) {
                  actions.add(Action.createCompoundMove(peasantIdMap.get(peasantIdsInvolved[i]),targetPositions.get(i).x,targetPositions.get(i).y));
             }
        } else if(action instanceof BuildPeasant) {
             BuildPeasant act=(BuildPeasant) action;
             newPeasantId=act.getNewPeasantId();
             actions.add(Action.createPrimitiveProduction(townhallId, peasantTemplateId));
             newPeasantCreated=true;      
        } else {
             try {
                  throw new Exception("Error: Unexpected StripsAction");
             } catch (Exception e) {
                  System.err.println(e);
                  e.printStackTrace();
             }
        }
        return actions;
    }

    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView) {
         
    }

    @Override
    public void savePlayerData(OutputStream outputStream) {

    }

    @Override
    public void loadPlayerData(InputStream inputStream) {

    }
}

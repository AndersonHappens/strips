package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.actions.StripsAction;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class is used to represent the state of the game after applying one of the avaiable actions. It will also
 * track the A* specific information such as the parent pointer and the cost and heuristic function. Remember that
 * unlike the path planning A* from the first assignment the cost of an action may be more than 1. Specifically the cost
 * of executing a compound action such as move can be more than 1. You will need to account for this in your heuristic
 * and your cost function.
 *
 * The first instance is constructed from the StateView object (like in PA2). Implement the methods provided and
 * add any other methods and member variables you need.
 *
 * Some useful API calls for the state view are
 *
 * state.getXExtent() and state.getYExtent() to get the map size
 *
 * I recommend storing the actions that generated the instance of the GameState in this class using whatever
 * class/structure you use to represent actions.
 */
public class GameState implements Comparable<GameState> {
     
     private int goalWoodAmount;
     private int goalGoldAmount;
     private int playerNum;
     private int cost;
     private boolean buildPeasantsAvailable;
     
     private int xSize;
     private int ySize;
     
     private int[] woodIds;
     private Position[] woodPositions;
     private int[] woodAmounts;
     
     private int[] goldIds;
     private Position[] goldPositions;
     private int[] goldAmounts;
     
     private int townHallId;
     private Position townHallPosition;
     private int woodAmount;
     private int goldAmount;
     
     private ArrayList<Integer> peasantIds;
     private ArrayList<Position> peasantPositions;
     
     private GameState parent;
     private StripsAction action;
     
    /**
     * Construct a GameState from a stateview object. This is used to construct the initial search node. All other
     * nodes should be constructed from the another constructor you create or by factory functions that you create.
     *
     * @param state The current stateview at the time the plan is being created
     * @param playernum The player number of agent that is planning
     * @param requiredGold The goal amount of gold (e.g. 200 for the small scenario)
     * @param requiredWood The goal amount of wood (e.g. 200 for the small scenario)
     * @param buildPeasants True if the BuildPeasant action should be considered
     */
    public GameState(State.StateView state, int playernum, int requiredGold, int requiredWood, boolean buildPeasants) {
        goalGoldAmount=requiredGold;
        goalWoodAmount=requiredWood;
        playerNum=playernum;
        cost=0;
        buildPeasantsAvailable=buildPeasants;
        
        xSize=state.getXExtent();
        ySize=state.getYExtent();
        
        ResourceView[] woodNodes=state.getResourceNodes(ResourceNode.Type.TREE).toArray(new ResourceView[0]);
        woodIds=new int[woodNodes.length];
        woodPositions=new Position[woodNodes.length];
        woodAmounts=new int[woodNodes.length];
        for(int i=0;i<woodNodes.length;i++) {
             woodIds[i]=woodNodes[i].getID();
             woodPositions[i]=new Position(woodNodes[i].getXPosition(), woodNodes[i].getYPosition());
             woodAmounts[i]=woodNodes[i].getAmountRemaining();
        }
        
        ResourceView[] goldNodes=state.getResourceNodes(ResourceNode.Type.GOLD_MINE).toArray(new ResourceView[0]);
        goldIds=new int[goldNodes.length];
        goldPositions=new Position[goldNodes.length];
        goldAmounts=new int[goldNodes.length];
        for(int i=0;i<goldNodes.length;i++) {
             goldIds[i]=goldNodes[i].getID();
             goldPositions[i]=new Position(goldNodes[i].getXPosition(), goldNodes[i].getYPosition());
             goldAmounts[i]=goldNodes[i].getAmountRemaining();
        }
        
        Unit.UnitView[] units=state.getUnitIds(playerNum).toArray(new Unit.UnitView[0]);
        for(int i=0;i<units.length;i++) {
             if(units[i].getTemplateView().getName().equals("TownHall")) {
                  townHallId=units[i].getID();
                  townHallPosition=new Position(units[i].getXPosition(),units[i].getYPosition());
                  
             } else {
                  peasantIds.add(units[i].getID());
                  peasantPositions.add(new Position(units[i].getXPosition(),units[i].getYPosition()));
             }
        }
        
        woodAmount=state.getResourceAmount(playerNum, ResourceType.WOOD);
        goldAmount=state.getResourceAmount(playerNum, ResourceType.GOLD);
        
        parent=null;
        action=null;
    }

    /**
     * Unlike in the first A* assignment there are many possible goal states. As long as the wood and gold requirements
     * are met the peasants can be at any location and the capacities of the resource locations can be anything. Use
     * this function to check if the goal conditions are met and return true if they are.
     *
     * @return true if the goal conditions are met in this instance of game state.
     */
    public boolean isGoal() {
        return (goalWoodAmount<=woodAmount) && (goalGoldAmount<=goldAmount);
    }

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
    public List<GameState> generateChildren() {
        // TODO: Implement me!
        return null;
    }

    /**
     * Returns whether or not the gamestate is valid.
     * @return
     */
    private boolean isValid(GameState currentState) {
       //create a new temporary GameState to check against the enemy and resourcelocations
    	//GameState temp = (new GameState(x, y, null, 0)); 
    	for(Position position : currentState.peasantPositions) {
    		if(!(position.inBounds(currentState.xSize, currentState.ySize))) {
    			return false;
    		}
    		for(Position wood: currentState.woodPositions) {
    	    	if(wood!=null && wood.equals(position)) {
		              return false;
    		    }
    		}
    		for(Position gold: currentState.goldPositions) {
    	    	if(gold!=null && gold.equals(position)) {
		              return false;
		        }
    		}

    	}
    	return true;
    }
    
    /**
     * Write your heuristic function here. Remember this must be admissible for the properties of A* to hold. If you
     * can come up with an easy way of computing a consistent heuristic that is even better, but not strictly necessary.
     *
     * Add a description here in your submission explaining your heuristic.
     *
     * @return The value estimated remaining cost to reach a goal state from this state.
     */
    public double heuristic() {
        // TODO: Implement me!
        return 0.0;
    }

    /**
     *
     * Write the function that computes the current cost to get to this node. This is combined with your heuristic to
     * determine which actions/states are better to explore.
     *
     * @return The current cost to reach this goal
     */
    public double getCost() {
        return cost;
    }

    /**
     * This is necessary to use your state in the Java priority queue. See the official priority queue and Comparable
     * interface documentation to learn how this function should work.
     *
     * @param o The other game state to compare
     * @return 1 if this state costs more than the other, 0 if equal, -1 otherwise
     */
    @Override
    public int compareTo(GameState o) {
        if(getCost()-o.getCost()>0) {
             return 1;
        } else if(getCost()-o.getCost()<0) {
             return -1;
        } else {
             return 0;
        }
    }

    /**
     * This will be necessary to use the GameState as a key in a Set or Map.
     *
     * @param o The game state to compare
     * @return True if this state equals the other state, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        // TODO: Implement me!
        return false;
    }

    /**
     * This is necessary to use the GameState as a key in a HashSet or HashMap. Remember that if two objects are
     * equal they should hash to the same value.
     *
     * @return An integer hashcode that is equal for equal states.
     */
    @Override
    public int hashCode() {
        // TODO: Implement me!
        return 0;
    }
}

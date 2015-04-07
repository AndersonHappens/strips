package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.planner.actions.BuildPeasant;
import edu.cwru.sepia.agent.planner.actions.StripsAction;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;

import java.io.*;
import java.util.*;

/**
 * Created by Devin on 3/15/15.
 */
public class PlannerAgent extends Agent {

    final int requiredWood;
    final int requiredGold;
    final boolean buildPeasants;

    // Your PEAgent implementation. This prevents you from having to parse the text file representation of your plan.
    PEAgent peAgent;

    public PlannerAgent(int playernum, String[] params) {
        super(playernum);

        if(params.length < 3) {
            System.err.println("You must specify the required wood and gold amounts and whether peasants should be built");
        }

        requiredWood = Integer.parseInt(params[0]);
        requiredGold = Integer.parseInt(params[1]);
        buildPeasants = Boolean.parseBoolean(params[2]);


        System.out.println("required wood: " + requiredWood + " required gold: " + requiredGold + " build Peasants: " + buildPeasants);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {

        Stack<StripsAction> plan = AstarSearch(new GameState(stateView, playernum, requiredGold, requiredWood, buildPeasants));

        if(plan == null) {
            System.err.println("No plan was found");
            System.exit(1);
            return null;
        }

        // write the plan to a text file
        savePlan(plan);


        // Instantiates the PEAgent with the specified plan.
        peAgent = new PEAgent(playernum, plan);

        return peAgent.initialStep(stateView, historyView);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        if(peAgent == null) {
            System.err.println("Planning failed. No PEAgent initialized.");
            return null;
        }

        return peAgent.middleStep(stateView, historyView);
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
    public static void printIntArray(int[] arr) {
    	for(Integer in: arr) {
    		System.out.print(in + " ");
    	}
    	System.out.println();
    }

    /**
     * Perform an A* search of the game graph. This should return your plan as a stack of actions. This is essentially
     * the same as your first assignment. The implementations should be very similar. The difference being that your
     * nodes are now GameState objects not GameState objects.
     *
     * @param startState The state which is being planned from
     * @return The plan or null if no plan is found.
     */
    private Stack<StripsAction> AstarSearch(GameState startState) {
    	boolean goalFound = false;
    	//define a priority queue of the size of all open spaces on the map 
    	PriorityQueue<GameState> openset = new PriorityQueue<GameState>(startState.getxSize()*startState.getySize() - startState.getWoodPositions().length - startState.getGoldPositions().length - 1);
    	Hashtable<GameState, GameState> closedList = new Hashtable<GameState, GameState>();
    	// add initial start location
    	openset.add(startState);
    	GameState state = null;
    	GameState goal=null;
    	//while the openset has children to go through and the goal hasnt been found yet
    	while(!openset.isEmpty()) {
    	    state = openset.remove();
    	    //if we have found the goal, stop the search
    	    //System.out.println(state.getAction()+"  "+state.getCost()+"  "+state.heuristic()+"  "+(state.getCost()+state.heuristic()));
         	 if (state.isGoal()) {
                System.out.println("Goal found");
                goal=state;
                goalFound = true;
                break;
           } 
    	    //get all valid children
    		for(GameState child: state.generateChildren()) {
    			if(openset.contains(child)) {
    				//iterate through the openset until we found the child node we are looking for
    			     Iterator<GameState> iterator=openset.iterator();
    			     GameState temp=null;
    			     while(iterator.hasNext()) {
    			          temp=iterator.next();
    			          if(child.equals(temp)) {
         			          break;
    			          }
    			     }
    			     //if the cost of the node already in the openset is more expensive than the child, remove it and add the child
         			if(temp.getCost() >= child.getCost()) {
                         iterator.remove();
                         openset.offer(child);
                    }
    			} else if(closedList.containsKey(child)) {
    				//don't add it to the openset if it is already contained in the closedList
    				continue;
    			} else {
    				openset.add(child);
    			}
    		}
    		//add the state to the closed list after going through all of it's children
    		closedList.put(state, state);
    		
    		
    	}
    	if(!goalFound) {
    	     System.out.println("No available path");
    	     
    	  // return an empty path if no goal is found
    		return new Stack<StripsAction>();
    	}
    	     return calculateStack(goal);
    }
    
    /**
     * Given the ending node, calculates the path necessary to get from start to finish.
     * @param end
     * @return
     */
    private Stack<StripsAction> calculateStack(GameState end) {
    	Stack<StripsAction> endList = new Stack<StripsAction>();
    	GameState currentNode = end;
    	//while the parent exists, add the node to the endList
    	while(currentNode.getParent()!=null && currentNode.getAction() != null) {
    	     endList.push(currentNode.getAction());
    		currentNode = currentNode.getParent();
    	}
    	return endList;
    }

    /**
     * This has been provided for you. Each strips action is converted to a string with the toString method. This means
     * each class implementing the StripsAction interface should override toString. Your strips actions should have a
     * form matching your included Strips definition writeup. That is <action name>(<param1>, ...). So for instance the
     * move action might have the form of Move(peasantID, X, Y) and when grounded and written to the file
     * Move(1, 10, 15).
     *
     * @param plan Stack of Strips Actions that are written to the text file.
     */
    private void savePlan(Stack<StripsAction> plan) {
        if (plan == null) {
            System.err.println("Cannot save null plan");
            return;
        }

        File outputDir = new File("saves");
        outputDir.mkdirs();

        File outputFile = new File(outputDir, "plan.txt");

        PrintWriter outputWriter = null;
        try {
            outputFile.createNewFile();

            outputWriter = new PrintWriter(outputFile.getAbsolutePath());

            Stack<StripsAction> tempPlan = (Stack<StripsAction>) plan.clone();
            while(!tempPlan.isEmpty()) {
                outputWriter.println(tempPlan.pop().toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputWriter != null)
                outputWriter.close();
        }
    }
}

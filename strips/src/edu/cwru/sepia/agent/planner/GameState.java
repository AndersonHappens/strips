package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used to represent the state of the game after applying one of
 * the avaiable actions. It will also track the A* specific information such as
 * the parent pointer and the cost and heuristic function. Remember that unlike
 * the path planning A* from the first assignment the cost of an action may be
 * more than 1. Specifically the cost of executing a compound action such as
 * move can be more than 1. You will need to account for this in your heuristic
 * and your cost function.
 *
 * The first instance is constructed from the StateView object (like in PA2).
 * Implement the methods provided and add any other methods and member variables
 * you need.
 *
 * Some useful API calls for the state view are
 *
 * state.getXExtent() and state.getYExtent() to get the map size
 *
 * I recommend storing the actions that generated the instance of the GameState
 * in this class using whatever class/structure you use to represent actions.
 */
public class GameState implements Comparable<GameState> {
	public static final int NONE = 0;
	public static final int GOLD = 1;
	public static final int WOOD = 2;

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
	private ArrayList<Integer> peasantCargo;

	private GameState parent;
	private StripsAction action;

	/**
	 * Construct a GameState from a stateview object. This is used to construct
	 * the initial search node. All other nodes should be constructed from the
	 * another constructor you create or by factory functions that you create.
	 *
	 * @param state
	 *            The current stateview at the time the plan is being created
	 * @param playernum
	 *            The player number of agent that is planning
	 * @param requiredGold
	 *            The goal amount of gold (e.g. 200 for the small scenario)
	 * @param requiredWood
	 *            The goal amount of wood (e.g. 200 for the small scenario)
	 * @param buildPeasants
	 *            True if the BuildPeasant action should be considered
	 */
	public GameState(State.StateView state, int playernum, int requiredGold,
			int requiredWood, boolean buildPeasants) {
		goalGoldAmount = requiredGold;
		goalWoodAmount = requiredWood;
		playerNum = playernum;
		cost = 0;
		buildPeasantsAvailable = buildPeasants;
		peasantIds = new ArrayList<Integer>();
		peasantPositions = new ArrayList<Position>();
		peasantCargo = new ArrayList<Integer>();

		xSize = state.getXExtent();
		ySize = state.getYExtent();

		ResourceView[] woodNodes = state.getResourceNodes(
				ResourceNode.Type.TREE).toArray(new ResourceView[0]);
		woodIds = new int[woodNodes.length];
		woodPositions = new Position[woodNodes.length];
		woodAmounts = new int[woodNodes.length];
		for (int i = 0; i < woodNodes.length; i++) {
			woodIds[i] = woodNodes[i].getID();
			woodPositions[i] = new Position(woodNodes[i].getXPosition(),
					woodNodes[i].getYPosition());
			woodAmounts[i] = woodNodes[i].getAmountRemaining();
		}

		ResourceView[] goldNodes = state.getResourceNodes(
				ResourceNode.Type.GOLD_MINE).toArray(new ResourceView[0]);
		goldIds = new int[goldNodes.length];
		goldPositions = new Position[goldNodes.length];
		goldAmounts = new int[goldNodes.length];
		for (int i = 0; i < goldNodes.length; i++) {
			goldIds[i] = goldNodes[i].getID();
			goldPositions[i] = new Position(goldNodes[i].getXPosition(),
					goldNodes[i].getYPosition());
			goldAmounts[i] = goldNodes[i].getAmountRemaining();
		}

		Unit.UnitView[] units = state.getUnits(playerNum).toArray(
				new Unit.UnitView[0]);
		for (int i = 0; i < units.length; i++) {
			if (units[i].getTemplateView().getName().equals("TownHall")) {
				townHallId = units[i].getID();
				townHallPosition = new Position(units[i].getXPosition(),
						units[i].getYPosition());

			} else {
				peasantIds.add(units[i].getID());
				peasantPositions.add(new Position(units[i].getXPosition(),
						units[i].getYPosition()));
				if (units[i].getCargoAmount() > 0) {
					if (units[i].getCargoType() == ResourceType.WOOD) {
						peasantCargo.add(new Integer(WOOD));
					} else if (units[i].getCargoType() == ResourceType.GOLD) {
						peasantCargo.add(new Integer(GOLD));
					}
				} else {
					peasantCargo.add(new Integer(NONE));
				}
			}
		}

		woodAmount = state.getResourceAmount(playerNum, ResourceType.WOOD);
		goldAmount = state.getResourceAmount(playerNum, ResourceType.GOLD);

		parent = null;
		action = null;
	}

	private GameState(GameState original) {
		goalWoodAmount = original.goalWoodAmount;
		goalGoldAmount = original.goalGoldAmount;
		playerNum = original.playerNum;
		cost = original.cost + 1;
		buildPeasantsAvailable = original.buildPeasantsAvailable;

		xSize = original.xSize;
		ySize = original.ySize;

		woodIds = Arrays.copyOf(original.woodIds, original.woodIds.length);
		woodPositions = Arrays.copyOf(original.woodPositions,
				original.woodPositions.length);
		woodAmounts = Arrays.copyOf(original.woodAmounts,
				original.woodAmounts.length);

		goldIds = Arrays.copyOf(original.goldIds, original.goldIds.length);
		goldPositions = Arrays.copyOf(original.goldPositions,
				original.goldPositions.length);
		goldAmounts = Arrays.copyOf(original.goldAmounts,
				original.goldAmounts.length);

		townHallId = original.townHallId;
		townHallPosition = original.townHallPosition;
		woodAmount = original.woodAmount;
		goldAmount = original.goldAmount;

		peasantIds = new ArrayList<Integer>(original.peasantIds);
		peasantPositions = new ArrayList<Position>(original.peasantPositions);
		peasantCargo = new ArrayList<Integer>(original.peasantCargo);

		parent = original.parent;
		action = original.action;
	}

	/**
	 * Unlike in the first A* assignment there are many possible goal states. As
	 * long as the wood and gold requirements are met the peasants can be at any
	 * location and the capacities of the resource locations can be anything.
	 * Use this function to check if the goal conditions are met and return true
	 * if they are.
	 *
	 * @return true if the goal conditions are met in this instance of game
	 *         state.
	 */
	public boolean isGoal() {
		return (goalWoodAmount <= woodAmount) && (goalGoldAmount <= goldAmount);
	}

	/**
	 * The branching factor of this search graph are much higher than the
	 * planning. Generate all of the possible successor states and their
	 * associated actions in this method.
	 *
	 * @return A list of the possible successor states and their associated
	 *         actions
	 */
	public List<GameState> generateChildren() {
		ArrayList<GameState> children = new ArrayList<GameState>();
		int i=peasantIds.size()-1;
		//for (int i = peasantIds.size()-1; i < peasantIds.size(); i++) {
			DepositWood depositWood = new DepositWood(i + 1);
			GameState depositWoodState = depositWood.apply(this);
			if (depositWoodState != null) {
				children.add(depositWoodState);
			}
			DepositGold depositGold = new DepositGold(i + 1);
			GameState depositGoldState = depositGold.apply(this);
			if (depositGoldState != null) {
				children.add(depositGoldState);
			}
			GatherWood gatherWood = new GatherWood(i + 1);
			GameState gatherWoodState = gatherWood.apply(this);
			if (gatherWoodState != null) {
				children.add(gatherWoodState);
			}
			GatherGold gatherGold = new GatherGold(i + 1);
			GameState gatherGoldState = gatherGold.apply(this);
			if (gatherGoldState != null) {
				children.add(gatherGoldState);
			}
			MoveWood moveWood = new MoveWood(i + 1);
			GameState moveWoodState = moveWood.apply(this);
			if (moveWoodState != null) {
				children.add(moveWoodState);
			}
			MoveGold moveGold = new MoveGold(i + 1);
			GameState moveGoldState = moveGold.apply(this);
			// System.out.println(moveGold.apply(this));
			if (moveGoldState != null) {
				children.add(moveGoldState);
			}
			MoveTownHall moveTownHall = new MoveTownHall(i + 1);
			GameState moveTownHallState = moveTownHall.apply(this);
			if (moveTownHallState != null) {
				children.add(moveTownHallState);
			}
		//}
		BuildPeasant buildPeasant = new BuildPeasant();
		GameState buildPeasantState = buildPeasant.apply(this);
		if (buildPeasantState != null) {
			children.add(buildPeasantState);
		}
		//if (action != null && action instanceof MoveWood)
			//System.out.print("movewood ");
		System.out.print("Children: ");
		for (GameState child : children) {
			 System.out.print(child.getAction()+", ");
		}
		 System.out.println();
		return children;
	}

	/**
	 * Returns whether or not the gamestate is valid.
	 * 
	 * @return
	 */
	private boolean isValid(GameState currentState) {
		// create a new temporary GameState to check against the enemy and
		// resourcelocations
		// GameState temp = (new GameState(x, y, null, 0));
		for (Position position : currentState.peasantPositions) {
			if (!(position.inBounds(currentState.xSize, currentState.ySize))) {
				return false;
			}
			for (Position wood : currentState.woodPositions) {
				if (wood != null && wood.equals(position)) {
					return false;
				}
			}
			for (Position gold : currentState.goldPositions) {
				if (gold != null && gold.equals(position)) {
					return false;
				}
			}

		}
		return true;
	}

	/**
	 * Write your heuristic function here. Remember this must be admissible for
	 * the properties of A* to hold. If you can come up with an easy way of
	 * computing a consistent heuristic that is even better, but not strictly
	 * necessary.
	 *
	 * Add a description here in your submission explaining your heuristic.
	 *
	 * If is goal, return 0. Otherwise, go through all of the peasants and find
	 * the distance it would take to go to the closest necessary resource that
	 * isn't empty, and back, and add that to the current cost. Returns the sum
	 * total cost of all distances divided by the number of peasants sqaured.
	 * 
	 * @return The value estimated remaining cost to reach a goal state from
	 *         this state.
	 */
	// FIXME
	public double heuristic() {
		int heur = 0;
		if (isGoal()) {
			return heur;
		}
		int numPeasants = peasantPositions.size();
		/*
		 * for(int i = 0; i<numPeasants; i++) { heur +=
		 * getDistanceToClosestNeededResourceAndBack(peasantPositions.get(i)); }
		 */
		heur += Math.abs(getGoalGoldAmount() - getGoldAmount() - getCarriedGold()) + Math.abs(getGoalWoodAmount() - getWoodAmount() - getCarriedWood());
		if(getGoalGoldAmount() - getGoldAmount() - getCarriedGold()>getGoalWoodAmount() - getWoodAmount() - getCarriedWood() && action instanceof MoveWood) {
		     heur+=1000;
		} else if(getGoalGoldAmount() - getGoldAmount() - getCarriedGold()<getGoalWoodAmount() - getWoodAmount() - getCarriedWood() && action instanceof MoveGold) {
		     heur+=1000;
		}
		if(getGoldAmount()+getCarriedGold()>getGoalGoldAmount() || getGoalWoodAmount()<getWoodAmount()+getCarriedWood()) {
		     heur=heur*10;
		}
																				// shouldn't
																				// go
																				// negative
		//System.out.println("heuristic: " + heur);
		return heur; // Weighted towards more
													// peasants... still
													// necessary?
	}

	/**
	 * 
	 * @param pos
	 * @return the distance to the closest resource that is not empty and is
	 *         still needed, and back from the current peasant.
	 */
	private double getDistanceToClosestNeededResourceAndBack(Position pos) {
		double distance = Double.MAX_VALUE;
		double dist;
		Position post;
		for (int i = 0; i < goldPositions.length; i++) {
			post = goldPositions[i];
			if (goalGoldAmount <= goldAmount + getCarriedGold()) {
				break;
			}
			if (goldAmounts[i] < 100) {
				continue;
			}
			dist = pos.chebyshevDistance(post);
			if (dist < distance) {
				distance = dist - 1; // -1 because don't include mine space...
			}
		}
		for (int i = 0; i < woodPositions.length; i++) {
			post = woodPositions[i];
			if (goalWoodAmount <= woodAmount + getCarriedWood()) {
				break;
			}
			if (woodAmounts[i] < 100) {
				continue;
			}
			dist = pos.chebyshevDistance(post);
			if (dist < distance) {
				distance = dist - 1;// -1 because don't include forest space...
			}
		}
		// all wood and gold that is needed is gathered...
		if (distance == Double.MAX_VALUE) {
			distance = 0;
		}
		distance *= 2; // distance to forest or mine and back
		distance += pos.chebyshevDistance(townHallPosition); // and back to the
																// town hall...

		return distance;
	}

	// ignore for now?
	private Pair<Position, Integer> getClosestGold(final Position pos, int index) {
		ArrayList<Pair<Position, Integer>> post = new ArrayList<Pair<Position, Integer>>();
		for (int i = 0; i < goldPositions.length; i++) {
			post.add(new Pair<Position, Integer>(goldPositions[i],
					goldAmounts[i]));
		}
		Collections.sort(post, new Comparator<Pair<Position, Integer>>() {
			@Override
			public int compare(Pair<Position, Integer> o1,
					Pair<Position, Integer> o2) {
				return o1.a.chebyshevDistance(pos)
						- o2.a.chebyshevDistance(pos);
			}
		});
		return post.get(index);
	}

	// ignore for now?
	private Pair<Position, Integer> getClosestWood(final Position pos, int index) {
		ArrayList<Pair<Position, Integer>> post = new ArrayList<Pair<Position, Integer>>();
		for (int i = 0; i < woodPositions.length; i++) {
			post.add(new Pair<Position, Integer>(woodPositions[i],
					woodAmounts[i]));
		}
		Collections.sort(post, new Comparator<Pair<Position, Integer>>() {
			@Override
			public int compare(Pair<Position, Integer> o1,
					Pair<Position, Integer> o2) {
				return o1.a.chebyshevDistance(pos)
						- o2.a.chebyshevDistance(pos);
			}
		});
		return post.get(index);
	}

	/**
	 * 
	 * @return the amount of wood that is currently being carried by all
	 *         peasants
	 */
	public int getCarriedWood() {
		int wood = 0;
		for (int i = 0; i < peasantCargo.size(); i++) {
			if (peasantCargo.get(i) == WOOD) {
				wood += 100;
			}
		}
		return wood;
	}

	/**
	 * 
	 * @return the amount of gold that is currently being carried by all
	 *         peasants
	 */
	public int getCarriedGold() {
		int gold = 0;
		for (int i = 0; i < peasantCargo.size(); i++) {
			if (peasantCargo.get(i) == GOLD) {
				gold += 100;
			}
		}
		return gold;
	}

	/**
	 *
	 * Write the function that computes the current cost to get to this node.
	 * This is combined with your heuristic to determine which actions/states
	 * are better to explore.
	 *
	 * @return The current cost to reach this goal
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * This is necessary to use your state in the Java priority queue. See the
	 * official priority queue and Comparable interface documentation to learn
	 * how this function should work.
	 *
	 * @param o
	 *            The other game state to compare
	 * @return 1 if this state costs more than the other, 0 if equal, -1
	 *         otherwise
	 */
	@Override
	public int compareTo(GameState o) {
		double val = getCost() + heuristic() - o.getCost() - o.heuristic();
		if (val > 0) {
			return 1;
		} else if (val < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public int getGoalWoodAmount() {
		return goalWoodAmount;
	}

	public void setGoalWoodAmount(int goalWoodAmount) {
		this.goalWoodAmount = goalWoodAmount;
	}

	public int getGoalGoldAmount() {
		return goalGoldAmount;
	}

	public void setGoalGoldAmount(int goalGoldAmount) {
		this.goalGoldAmount = goalGoldAmount;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	public boolean isBuildPeasantsAvailable() {
		return buildPeasantsAvailable;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	public int[] getWoodIds() {
		return woodIds;
	}

	public void setWoodIds(int[] woodIds) {
		this.woodIds = woodIds;
	}

	public Position[] getWoodPositions() {
		return woodPositions;
	}

	public void setWoodPositions(Position[] woodPositions) {
		this.woodPositions = woodPositions;
	}

	public int[] getWoodAmounts() {
		return woodAmounts;
	}

	public void setWoodAmounts(int[] woodAmounts) {
		this.woodAmounts = woodAmounts;
	}

	public int[] getGoldIds() {
		return goldIds;
	}

	public void setGoldIds(int[] goldIds) {
		this.goldIds = goldIds;
	}

	public Position[] getGoldPositions() {
		return goldPositions;
	}

	public void setGoldPositions(Position[] goldPositions) {
		this.goldPositions = goldPositions;
	}

	public int[] getGoldAmounts() {
		return goldAmounts;
	}

	public void setGoldAmounts(int[] goldAmounts) {
		this.goldAmounts = goldAmounts;
	}

	public int getTownHallId() {
		return townHallId;
	}

	public void setTownHallId(int townHallId) {
		this.townHallId = townHallId;
	}

	public Position getTownHallPosition() {
		return townHallPosition;
	}

	public void setTownHallPosition(Position townHallPosition) {
		this.townHallPosition = townHallPosition;
	}

	public int getWoodAmount() {
		return woodAmount;
	}

	public void setWoodAmount(int woodAmount) {
		this.woodAmount = woodAmount;
	}

	public int getGoldAmount() {
		return goldAmount;
	}

	public void setGoldAmount(int goldAmount) {
		//System.out.println("Setting gold amount to: " + goldAmount);
		this.goldAmount = goldAmount;
	}

	public ArrayList<Integer> getPeasantIds() {
		return peasantIds;
	}

	public void setPeasantIds(ArrayList<Integer> peasantIds) {
		this.peasantIds = peasantIds;
	}

	public ArrayList<Position> getPeasantPositions() {
		return peasantPositions;
	}

	public void setPeasantPositions(ArrayList<Position> peasantPositions) {
		this.peasantPositions = peasantPositions;
	}

	public ArrayList<Integer> getPeasantCargo() {
		return peasantCargo;
	}

	public void setPeasantCargo(ArrayList<Integer> peasantCargo) {
		this.peasantCargo = peasantCargo;
	}

	public GameState getParent() {
		return parent;
	}

	public void setParent(GameState parent) {
		this.parent = parent;
	}

	public StripsAction getAction() {
		return action;
	}

	public void setAction(StripsAction action) {
		this.action = action;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * This will be necessary to use the GameState as a key in a Set or Map.
	 *
	 * @param o
	 *            The game state to compare
	 * @return True if this state equals the other state, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof GameState) {
			ArrayList<Position> poses = ((GameState) o).getPeasantPositions();
			if (poses.size() != peasantPositions.size()) {
				return false;
			}
			for (int i = 0; i < poses.size(); i++) {
				Position pos = poses.get(i);
				if (pos == null || !pos.equals(peasantPositions.get(i))) {
					return false;
				}
			}
			int[] amounts = ((GameState) o).getGoldAmounts();
			if (amounts.length != goldAmounts.length) {
				return false;
			}
			for (int i = 0; i < amounts.length; i++) {
				if (amounts[i] != (goldAmounts[i])) {
					return false;
				}
			}
			amounts = ((GameState) o).getWoodAmounts();
			if (amounts.length != woodAmounts.length) {
				return false;
			}
			for (int i = 0; i < amounts.length; i++) {
				if (amounts[i] != (woodAmounts[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * This is necessary to use the GameState as a key in a HashSet or HashMap.
	 * Remember that if two objects are equal they should hash to the same
	 * value.
	 *
	 * @return An integer hashcode that is equal for equal states.
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for (Position pos : peasantPositions) {
			hash = hash + (pos.x * 1024 + pos.y);
		}

		/*
		 * for(int i = 0; i< goldAmounts.length; i++) { hash = hash +
		 * goldAmounts[i]; }
		 * 
		 * for(int i = 0; i< woodAmounts.length; i++) { hash = hash +
		 * woodAmounts[i]; }
		 */
		hash = hash + goldAmount;
		hash = hash + woodAmount;
		//hash = hash + cost;
		return hash;
	}

	public GameState copyOf() {
		return new GameState(this);
	}
}

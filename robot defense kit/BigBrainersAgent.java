


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;

import jig.misc.rd.AirCurrentGenerator;
import jig.misc.rd.Direction;
import jig.misc.rd.RobotDefense;

/**
 *  A simple agent that uses reinforcement learning to direct the vacuum
 *  The agent has the following critical limitations:
 *  
 *  	- it only considers a small set of possible actions
 *  	- it does not consider turning the vacuum off
 *  	- it only reconsiders an action when the 'local' state changes  
 *         in some cases this may take a (really) long time
 *      - it uses a very simplisitic action selection mechanism
 *      - actions are based only on the cells immediately adjacent to a tower
 *      - action values are not dependent (at all) on the resulting state 
 */
public class BigBrainersAgent extends BaseLearningAgent {

	int stateCounter = 0;

	/**
	 * A Map of states to actions
	 * 
	 *  States are encoded in the StateVector objects
	 *  Actions are associated with a utility value and stored in the QMap
	 */
	HashMap<StateVector,QMap> actions = new HashMap<StateVector,QMap>();

	/**
	 * The agent's sensor system tracks /how many/ insects a particular generator
	 * captures, but here I want to know /when/ an air current generator just
	 * captured an insect so I can reward the last action. We use this captureCount
	 * to see when a new capture happens.
	 */
	HashMap<AirCurrentGenerator, Integer> captureCount;

	/**
	 * Keep track of the agent's last action so we can reward it
	 */
	HashMap<AirCurrentGenerator, AgentAction> lastAction;
	
	/**
	 * This stores the possible actions that an agent many take in any
	 * particular state.
	 */
	private static final AgentAction [] potentials;


	static {
		Direction [] dirs = Direction.values();
		potentials = new AgentAction[(dirs.length*2)+1];


		int i = 0;
		int power = 1;
		
		for(Direction d: dirs){
			// creates a new directional action with the power set to full
			// power can range from 1 ... AirCurrentGenerator.POWER_SETTINGS
			for(int j = 2; j < 5 ; j += 2){
				potentials[i] = new AgentAction(j, d);
				//System.out.println()
				//System.out.println(i + ":" + potentials[i].getDirection() + potentials[i].getPower());
				i++;
			}
			potentials[i] = new AgentAction(0,dirs[0]);
		}
	}
	
	public BigBrainersAgent() {
		captureCount = new HashMap<AirCurrentGenerator,Integer>();
		lastAction = new HashMap<AirCurrentGenerator,AgentAction>();		
	}
	
	/**
	 * Step the agent by giving it a chance to manipulate the environment.
	 * 
	 * Here, the agent should look at information from its sensors and 
	 * decide what to do.
	 * 
	 * @param deltaMS the number of milliseconds since the last call to step
	 * 
	 */
	public void step(long deltaMS) {
		StateVector state;
		QMap qmap;

		// This must be called each step so that the performance log is 
		// updated.
		updatePerformanceLog();

		for (AirCurrentGenerator acg : sensors.generators.keySet()) {

			//System.out.println(getMapContentsCode());
			

			boolean captured = false;

			if(stateCounter < 6)
			{
				if (!stateChanged(acg))
				{
					stateCounter++;
					continue;
				}
			}

			
			
			//CellContents.checkInsects();

			//System.out.println(getSensorySystem());

			stateCounter = 0;

			
			// Check the current state, and make sure member variables are
			// initialized for this particular state...
			state = thisState.get(acg);
			if (actions.get(state) == null) {
				actions.put(state, new QMap(potentials));
			}
			if (captureCount.get(acg) == null) captureCount.put(acg, 0);


			// Check to see if an insect was just captured by comparing our
			// cached value of the insects captured by each ACG with the
			// most up-to-date value from the sensors
			boolean justCaptured;
			justCaptured = (captureCount.get(acg) < sensors.generators.get(acg));
			

			// if this ACG has been selected by the user, we'll do some verbose printing
			boolean verbose = (RobotDefense.getGame().getSelectedObject() == acg);
			//System.out.println(verbose);

			// If we did something on the last 'turn', we need to reward it
			if (lastAction.get(acg) != null ) 
			{

				// get the action map associated with the previous state
				qmap = actions.get(lastState.get(acg));
				//System.out.println(actions.get(lastState.get(acg)));
				
				//System.out.println(lastAction.get(acg).getDirection());
				//lastAction.get(acg).getDirection());
				//lastAction.get(acg).getPower());

				

				if (justCaptured) {
					// capturing insects is good
					qmap.rewardAction(lastAction.get(acg), 20.0);
					captureCount.put(acg,sensors.generators.get(acg));
					captured = true;
				}

				if (verbose) {
					System.out.println("Last State for " + acg.toString() );
					System.out.println(lastState.get(acg).representation());
					System.out.println("Updated Last Action: " + qmap.getQRepresentation());
				}
			}
		
			// decide what to do now...
			// first, get the action map associated with the current state
			qmap = actions.get(state);
			AgentAction bestAction;

			if (verbose) {
				System.out.println("This State for Tower " + acg.toString() );
				System.out.println(thisState.get(acg).representation());
			}
			// If radius is empty turn off sucker
			if(thisState.get(acg).getEmptyRadius() == true)
			{
				//System.out.println("Hello this is a print statement to see if it works. ");
				//lastAction.put(acg, potentials[potentials.length-1]);
				//bestAction.doAction(acg);
				bestAction = potentials[potentials.length-1];
				bestAction.doAction(acg);
			}
			else{
				// find the 'right' thing to do, and do it.
				bestAction = qmap.findBestAction(lastAction.get(acg),verbose);
				bestAction.doAction(acg);
				//System.out.println(bestAction);
			}

			// finally, store our action so we can reward it later.
			lastAction.put(acg, bestAction);

			//reward the current action if a bug was caught from last action
			if(captured == true)
			{
				//this is current action
				qmap.rewardAction(lastAction.get(acg), 5.0);
			}
		}
			
	}


	/**
	 * This inner class simply helps to associate actions with utility values
	 */
	static class QMap {
		static Random RN = new Random();

		private double[] utility; 		// current utility estimate
		private int[] attempts;			// number of times action has been tried
		private AgentAction[] actions;  // potential actions to consider

		public QMap(AgentAction[] potential_actions) {

			actions = potential_actions.clone();
			int len = actions.length;

			utility = new double[len];
			attempts = new int[len];
			for(int i = 0; i < len; i++) {
				utility[i] = 0.0;
				attempts[i] = 0;
			}
		}

		/**
		 * Finds the 'best' action for the agent to take.
		 * 
		 * @param verbose
		 * @return
		 */
		public AgentAction findBestAction( AgentAction lastAction, boolean verbose) {
			int i,maxi,maxcount;
			maxi=0;
			maxcount = 1;
			int test;
			
			if (verbose)
				System.out.print("Picking Best Actions: " + getQRepresentation());

			for (i = 1; i < utility.length; i++) {
				if (utility[i] > utility[maxi]) {
					maxi = i;
					maxcount = 1;
				}
				else if (utility[i] == utility[maxi]) {
					maxcount++;
				}
			}
			if (RN.nextDouble() > .2) {
				int whichMax = RN.nextInt(maxcount);

				if (verbose)
					System.out.println( " -- Doing Best! #" + whichMax);

				for (i = 0; i < utility.length; i++) {
					if (utility[i] == utility[maxi]) {
						if(lastAction == actions[i])
						{
							return lastAction;
						}
						if (whichMax == 0) return actions[i];
						whichMax--;
					}
				}
				return actions[maxi];
			}
			else {
				int which = RN.nextInt(actions.length);
				if (verbose)
					System.out.println( " -- Doing Random (" + which + ")!!");

				return actions[which];
			}
		}

		/**
		 * Modifies an action value by associating a particular reward with it.
		 * 
		 * @param a the action performed 
		 * @param value the reward received
		 */
		public void rewardAction(AgentAction a, double value) {
			int i;
			

			//if(a.getDirection() == "south")
			for (i = 0; i < actions.length; i++) {
				if (a == actions[i]){
					//System.out.println("Action " + actions[i+1].getDirection() + actions[i+1].getPower());
					break;
				}
			}
			if (i >= actions.length) {
				System.err.println("ERROR: Tried to reward an action that doesn't exist in the QMap. (Ignoring reward)");
				return;
			}

			int sideAction1 = i-2;
			int sideAction2 = i+2;

			if(i <= 1)
			{
				sideAction1 = sideAction1 + 16;
			}
			if(i >= 15)
			{
				sideAction2 = sideAction2 - 16;
			}

			
			//action behind
			utility[sideAction1] = (utility[sideAction1] * attempts[sideAction1]) + value/2;
			attempts[sideAction1] = attempts[sideAction1] + 1;
			utility[sideAction1] = utility[sideAction1]/attempts[sideAction1];
			//System.out.println("Behind Action " + actions[sideAction1].getDirection() + actions[sideAction1].getPower());
			
			//main action
			utility[i] = (utility[i] * attempts[i]) + value;
			attempts[i] = attempts[i] + 1;
			utility[i] = utility[i]/attempts[i];
			//System.out.println("Main Action " + actions[i].getDirection() + actions[i].getPower());

			//action ahead
			utility[sideAction2] = (utility[sideAction2] * attempts[sideAction2]) + value/2;
			attempts[sideAction2] = attempts[sideAction2] + 1;
			utility[sideAction2] = utility[sideAction2]/attempts[sideAction2];
			//System.out.println("Ahead Action " + actions[sideAction2].getDirection() + actions[sideAction2].getPower());
			//System.out.println(utility[i]);
		}
		/**
		 * Gets a string representation (for debugging).
		 * 
		 * @return a simple string representation of the action values
		 */
		public String getQRepresentation() {
			StringBuffer sb = new StringBuffer(80);

			for (int i = 0; i < utility.length; i++) {
				sb.append(String.format("%.2f  ", utility[i]));
			}
			return sb.toString();

		}

	}
}
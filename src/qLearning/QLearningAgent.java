package qLearning;

import java.util.ArrayList;
import java.util.Hashtable;

import qLearning.model.Action;
import qLearning.model.Reward;
import qLearning.model.State;
import qLearning.model.StateActionPair;

public class QLearningAgent {
	final static double alpha = 0.5;
	final static double gamma = 0.5;
	static double epsilon = 0.99;

	private State currentState;
	private Action lastAction;
	private Reward lastReward;

	private Action nextAction;

	static ArrayList<Action> availableActions = new ArrayList<>();
	static Hashtable<StateActionPair, Double> QLearningTable = new Hashtable<>();

	public QLearningAgent(State currentState, Action lastAction) {
		this.currentState = currentState;
		this.lastAction = lastAction;
		setLastReward(lastAction);
	}

	public void setCurrentState(State state) {
		currentState = state;
	}

	public void setLastAction(Action action) {
		lastAction = action;
	}

	public void setLastReward(Action action) {
		lastReward = new Reward(new StateActionPair(currentState, action));
	}

	public Action getAction() {
		StateActionPair stateActionPair = new StateActionPair(currentState, lastAction);

		LearnFrom(stateActionPair, lastReward);

		return nextAction;
	}

	public ArrayList<Action> getAvailableActions() {
		return availableActions;
	}

	private void LearnFrom(StateActionPair stateActionPair, Reward reward) {

		if (QLearningTable.get(stateActionPair) == null) {
			QLearningTable.put(stateActionPair, 0.0);
		}

		// Reinforcement
		double newQValue = QLearningTable.get(stateActionPair) + alpha * (reward.getValue()
				+ gamma * maxQValue(stateActionPair.getState()) - QLearningTable.get(stateActionPair));
		QLearningTable.put(stateActionPair, newQValue);
		System.out.println(newQValue);

		double p = Math.random();
		if (p < epsilon) {
			// Exploration
			System.out.println("Random Action!");
			nextAction = (Action) Action.getRandomAction(availableActions);
		}
		epsilon = epsilon * 0.99;
	}

	private double maxQValue(State state) {
		double maxQValue = -Double.MAX_VALUE;
		if (availableActions.size() > 0) {
			for (Action action : availableActions) {
				StateActionPair s = new StateActionPair(state, action);
				if (QLearningTable.get(s) == null) {
					QLearningTable.put(s, 0.0);
				}
			}

			for (StateActionPair stateActionPair : QLearningTable.keySet()) {
				if (stateActionPair.getState().equals(state)) {
					System.out.println("equals state OK");
					if (availableActions.contains(stateActionPair.getAction())) {
						System.out.println("Possible action OK");
						System.out.println("maxQValue : " + maxQValue);
						System.out.println("Current QValue : " + QLearningTable.get(stateActionPair));
						if (QLearningTable.get(stateActionPair) > maxQValue) {
							maxQValue = QLearningTable.get(stateActionPair);
							System.out.println("Max OK");
							// Exploitation
							nextAction = stateActionPair.getAction();
							System.out.println("next action OK : " + nextAction);
						}
					}
				}
			}
		}
		if (maxQValue == -Double.MAX_VALUE) {
			nextAction = null;
		}

		System.out.println("salut!");
		return maxQValue;
	}

	public void setAvailableActions(ArrayList<Action> availableActions) {
		QLearningAgent.availableActions = availableActions;
	}
}

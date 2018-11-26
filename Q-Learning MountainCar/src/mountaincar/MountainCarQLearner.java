package mountaincar;

public class MountainCarQLearner implements Runnable {

	public static final boolean DISPLAY = true;
	public static final int EPISODES = 100;
	public static final int POSITION_BINS = 30;
	public static final int VELOCITY_BINS = 14;
	public static final double gamma = 0.99;
	
	private MountainCar mc;
	private MountainCarWindow window;
	
	private double velocity_1 = -100;
//	private double velocity_2 = -100;
	private double position_1 = -100;
	private double total_reward = 0.0;
	private Action action_1 = null;
	private double[][][] QValues;
	
	public MountainCarQLearner() {
		mc = new MountainCar();
		window = new MountainCarWindow(mc);
		
		//Attempted QValue table = velocity(t), velocity(t-1), action
		QValues = new double[POSITION_BINS][VELOCITY_BINS][Action.values().length];
	}
	
	@Override
	public void run() {
		for(int j = 0; j < EPISODES; j++) {
			total_reward = 0.0;
			mc.randomInit();
			int stepcounter = 0;
			
			while(!mc.isDone()) {
				if(DISPLAY)
					if(stepcounter % 100 == 0) {
//						System.out.println(stepcounter);
						window.repaintCar();
					}
					Action action = getAction(mc);
				mc.apply(action.ordinal());
				lastUpdate(mc, action);
				stepcounter++;
			}
			System.out.println("Episode " + j + " took " + stepcounter + " steps." + total_reward);
		}
		window.dispose();
	}
	
	private enum Action{
		decel, roll, accel;
	}
	
	
	private double normalize(double value, double min, double max) {
		return (value - min) / (max - min);
	}
	
	private int clamp(int value, int min, int max) {
		if (value < min)
			value = min;
		if (value > max)
			value = max;
		return value;
	}
	
	private int discretize(double value, double min, double max, int binCount) {
		int discreteValue = (int) (binCount * normalize(value, min, max));
		return clamp(discreteValue, 0, binCount -1);
	}
	
	/**
	 * This method gets called to decide which action to take next. Since the MountainCar object contains
	 * the entire state, a deep-clone is passed as argument.
	 * @param car MountainCar object that describes the current state
	 * @return Action of what next to do {decel, roll, accel};
	 */
	public Action getAction(MountainCar car) {
		//If there was no previous timestep, make a random action
//		if(velocity_1 < -0.07) {
//			velocity_1 = car.getVelocity();
//			action_1 = Action.values()[(int) (Math.random() * 3)];
//			return action_1;
//		} else if(velocity_2 < -0.07) {
//			velocity_2 = velocity_1;
//			velocity_1 = car.getVelocity();
//			action_1 = Action.values()[(int) (Math.random() * 3)];
//			return action_1;
//		} else {
		if(action_1 != null) {
			
			total_reward += car.getReward();
								
			//Q-Value update:
			double max_Q = Integer.MIN_VALUE;
			for(int i = 0; i < Action.values().length; i++) {
				if(QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][i] > max_Q) {
					max_Q = QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][i];
				}
			}
			QValues[discretize(position_1, -1.2, 0.6, POSITION_BINS)][discretize(velocity_1, -0.07, 0.07, VELOCITY_BINS)][action_1.ordinal()] = car.getReward() + gamma * max_Q;
		}
		Action max_action = null;
		double max_Q = Integer.MIN_VALUE;
		for(int i = 0; i < Action.values().length; i++) {
			if(QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][i] > max_Q) {
				max_action = Action.values()[i];
				max_Q = QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][i];
			}
		}

		velocity_1 = car.getVelocity();
		position_1 = car.getPosition();
		action_1 = max_action;
		
		return max_action;
//		return Action.values()[(int) (Math.random() * 3)];
	}
	
//	public void updateQValue(MountainCar car, MountainCar car_1, Action action) {
//		Action max_action = null;
//		double max_Q = Integer.MIN_VALUE;
//		for(int i = 0; i < Action.values().length; i++) {
//			if(QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][i] > max_Q) {
//				max_action = Action.values()[i];
//				max_Q = QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][i];
//			}
//		}
//		
//		QValues[discretize(car_1.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car_1.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][action.ordinal()] =
//				car_1.getReward() + gamma 
//	}
	
	public void lastUpdate(MountainCar car, Action action) {
		QValues[discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS)][discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][action.ordinal()] = car.getReward();
	}
	
	public static void main(String[] args) {
		MountainCarQLearner qlearn = new MountainCarQLearner();
		qlearn.run();
	}
}

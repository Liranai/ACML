package mountaincar;

public class MountainCarQLearner implements Runnable {

	public static final boolean DISPLAY = true;
	public static final int EPISODES = 100;
	public static final int POSITION_BINS = 30;
	public static final int VELOCITY_BINS = 10;
	
	private MountainCar mc;
	private MountainCarWindow window;
	
	private double velocity_1 = -100;
	private Action action_1;
	private double total_reward = 0.0;
	private double gamma = 0.99;
	private double[][][] QValues;
	
	public MountainCarQLearner() {
		mc = new MountainCar();
		window = new MountainCarWindow(mc);
		
		//Attempted QValue table = velocity(t), velocity(t-1), action
		QValues = new double[VELOCITY_BINS][VELOCITY_BINS][Action.values().length];
	}
	
	@Override
	public void run() {
		for(int i = 0; i < EPISODES; i++) {
			mc.randomInit();
			int stepcounter = 0;
			
			while(!mc.isDone()) {
				if(DISPLAY)
					if(stepcounter % 100 == 0)
						window.repaintCar();
				mc.apply(getAction(mc.clone()).ordinal());
				stepcounter++;
			}
			System.out.println("Episode " + i + " took " + stepcounter + " steps.");
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
	
	private int getDiscretizedValue(MountainCar car) {
		int position = discretize(car.getPosition(), -1.2, 0.6, POSITION_BINS);
		int velocity = discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS);
		return velocity;
	}
	
	/**
	 * This method gets called to decide which action to take next. Since the MountainCar object contains
	 * the entire state, a deep-clone is passed as argument.
	 * @param car MountainCar object that describes the current state
	 * @return Action of what next to do {decel, roll, accel};
	 */
	public Action getAction(MountainCar car) {
		//If there was no previous timestep, make a random action
		if(velocity_1 < -0.07) {
			velocity_1 = car.getVelocity();
			action_1 = Action.values()[(int) (Math.random() * 3)];
			return action_1;
		} else {
			total_reward += car.getReward();
			double reward = car.getReward();
			double velocity = car.getVelocity();
			
			Action max_action = null;
			double max_Q = Integer.MIN_VALUE;
			for(int i = 0; i < Action.values().length; i++) {
				if(QValues[discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][discretize(velocity_1, -0.07, 0.07, VELOCITY_BINS)][i] > max_Q) {
					max_action = Action.values()[i];
					max_Q = QValues[discretize(velocity, -0.07, 0.07, VELOCITY_BINS)][discretize(velocity_1, -0.07, 0.07, VELOCITY_BINS)][i];
				}
			}
			
			car.apply(max_action.ordinal());
			
			double velocity_p_1 = car.getVelocity();
			
			//Q-Value update:
			max_Q = Integer.MIN_VALUE;
			for(int i = 0; i < Action.values().length; i++) {
				if(QValues[discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][discretize(velocity_1, -0.07, 0.07, VELOCITY_BINS)][i] > max_Q) {
					max_Q = QValues[discretize(velocity, -0.07, 0.07, VELOCITY_BINS)][discretize(velocity_1, -0.07, 0.07, VELOCITY_BINS)][i];
				}
			}
			QValues[discretize(car.getVelocity(), -0.07, 0.07, VELOCITY_BINS)][discretize(velocity_1, -0.07, 0.07, VELOCITY_BINS)][max_action.ordinal()] = reward + gamma * max_Q;
//			System.out.println(max_Q);
			return max_action;
		}
//		if(velocity_1 > -100) {
//			if(velocity_1 > car.getVelocity())
//				return Action.decel;
//			else
//				return Action.accel;
//		}
//		else {
//			velocity_1 = car.getVelocity();
//			return Action.accel;
//		}
//		return Action.values()[(int) (Math.random() * 3)];
	}
	
	public static void main(String[] args) {
		MountainCarQLearner qlearn = new MountainCarQLearner();
		qlearn.run();
	}
}

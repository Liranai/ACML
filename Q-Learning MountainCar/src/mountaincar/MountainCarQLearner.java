package mountaincar;

public class MountainCarQLearner implements Runnable {

	public static final boolean DISPLAY = true;
	public static final int EPISODES = 100;
	
	private MountainCar mc;
	private MountainCarWindow window;
	
	private double velocity_1 = -100;
	
	public MountainCarQLearner() {
		mc = new MountainCar();
		window = new MountainCarWindow(mc);
	}
	
	@Override
	public void run() {
		for(int i = 0; i < EPISODES; i++) {
			mc.randomInit();
			int stepcounter = 0;
			
			while(!mc.isDone()) {
				if(DISPLAY)
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
	
	public Action getAction(MountainCar car) {
		if(velocity_1 > -100) {
			if(velocity_1 > car.getVelocity())
				return Action.decel;
			else
				return Action.accel;
		}
		else {
			velocity_1 = car.getVelocity();
			return Action.accel;
		}
	}
	
	public static void main(String[] args) {
		MountainCarQLearner qlearn = new MountainCarQLearner();
		qlearn.run();
	}
}

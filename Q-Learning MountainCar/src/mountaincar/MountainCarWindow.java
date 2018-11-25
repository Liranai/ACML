package mountaincar;

import java.awt.BorderLayout;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MountainCarWindow extends JFrame {
	
	private MountainCar mc;
	private MountainCarViewer view;

	public MountainCarWindow(MountainCar mc) {
		super("MountainCar Experiment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Resizable set to false so AwesomeWM doesn't mess with it
		setResizable(false);
		setUndecorated(true);
		setLocationRelativeTo(null);
		
		this.mc = mc;
		// Add a MountainCarViewer
		view = new MountainCarViewer(mc);
		add(view, BorderLayout.CENTER);
		
		pack();
		if(MountainCarQLearner.DISPLAY)
			setVisible(true);
	}

	public void repaintCar() {
		view.repaint();
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) {
		MountainCar mc = new MountainCar();
		MountainCarWindow pw = new MountainCarWindow(mc);
		for (int i = 0; i < 10; i++) {
			mc.randomInit();
			int stepcounter = 0;
			while (!mc.isDone()) {
				if(MountainCarQLearner.DISPLAY)
					pw.repaintCar();
				// mc.apply(0);
				mc.apply((int) (Math.random() * 3));
				stepcounter++;
			}
			System.out.println("Episode " + i + " took " + stepcounter + " steps.");
		}
		pw.dispose();
	}

}

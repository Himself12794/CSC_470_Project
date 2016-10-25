package edu.uncfsu.softwaredesign.f16.r2;

import javax.swing.SwingWorker;

public class GenericWorker extends SwingWorker<Integer, Integer> {

	private final Runnable process;
	
	public GenericWorker(Runnable run) {
		process = run;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		process.run();
		return -1;
	}

}

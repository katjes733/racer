package com.katjes.racer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controls extends KeyAdapter implements Runnable {

	// actualisation time for key handling thread
	private static final int ACTTIME = 50;
	// Turning steps
	private static final float TURNSTEP = 2.5f;

	// needed for analysis of multiple pressed directional keys
	private boolean UP;
	private boolean DOWN;
	private boolean LEFT;
	private boolean RIGHT;

	private final Thread t;

	private final WrapRacer wr;
	private final float turnAngle = 0.0f;

	// game is not paused by default
	private final boolean paused = false;

	public Controls(final WrapRacer wr) {
		// makes new thread with this class
		t = new Thread(this);
		// starts execution of thread functionality specified in function 'run'
		t.start();
		this.wr = wr;
	}

	// registers key-down events
	@Override
	public void keyPressed(final KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP: {
			UP = true;
			break;
		}
		case KeyEvent.VK_DOWN: {
			DOWN = true;
			break;
		}
		case KeyEvent.VK_RIGHT: {
			RIGHT = true;
			break;
		}
		case KeyEvent.VK_LEFT: {
			LEFT = true;
			break;
		}
		}
	}

	// registers key-released events
	@Override
	public void keyReleased(final KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP: {
			UP = false;
			break;
		}
		case KeyEvent.VK_DOWN: {
			DOWN = false;
			break;
		}
		case KeyEvent.VK_RIGHT: {
			RIGHT = false;
			break;
		}
		case KeyEvent.VK_LEFT: {
			LEFT = false;
			break;
		}
		}
	}

	// yet only text. real moving procedures need to be implemented as soon as course and car
	// are placed in world
	private void execMove() {
		if (UP == true && DOWN == false && RIGHT == false && LEFT == false) { // UP
			wr.car01.spinWheels(0.36f);
			System.out.println("UP" + wr.car01.getWheelSpinAngle());
		} else if (UP == true && DOWN == false && RIGHT == true
				&& LEFT == false) { // UP-RIGHT
			System.out.println("UP-RIGHT");
		} else if (UP == true && DOWN == false && RIGHT == false
				&& LEFT == true) { // UP-LEFT
			System.out.println("UP-LEFT");
		} else if (UP == false && DOWN == true && RIGHT == false
				&& LEFT == false) {// DOWN
			wr.car01.spinWheels(-0.72f);
			System.out.println("DOWN" + wr.car01.getWheelSpinAngle());
		} else if (UP == false && DOWN == true && RIGHT == false
				&& LEFT == true) { // DOWN-LEFT
			System.out.println("DOWN-LEFT");
		} else if (UP == false && DOWN == true && RIGHT == true
				&& LEFT == false) { // DOWN_RIGHT
			System.out.println("DOWN-RIGHT");
		} else if (UP == false && DOWN == false && RIGHT == false
				&& LEFT == true) {// LEFT
			wr.car01.turnFrontWheels(wr.car01.getFrontWheelAngle() - TURNSTEP);
			System.out.println("LEFT" + wr.car01.getFrontWheelAngle());

		} else if (UP == false && DOWN == false && RIGHT == true
				&& LEFT == false) {// RIGHT
			wr.car01.turnFrontWheels(wr.car01.getFrontWheelAngle() + TURNSTEP);
			System.out.println("RIGHT" + wr.car01.getFrontWheelAngle());
		}
	}

	public void run() {
		while (true && !paused) {
			// System.out.println("-----");
			execMove();
			try {
				t.sleep(ACTTIME);
			} catch (final InterruptedException e) {
			}
		}
	}

	public void stop() {
		t.stop();
	}
}
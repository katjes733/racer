package com.katjes.racer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
/**
 * @(#)WrapRacer.java
 *
 *
 * @Martin Macecek
 * @version 1.00 2007/6/25
 */
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class WrapRacer extends JPanel {
	private static final int BOUNDSIZE = 100; // larger than world

	private static final Point3d USERPOSN = new Point3d(0, 5, 10); // initial user position

	private final Canvas3D canvas3D;
	private final SimpleUniverse su;
	private BranchGroup sceneBG;
	private BoundingSphere bounds; // for environment nodes

	private final Racer topLevel; // required at quit time

	public SimpleCar car01;

	public WrapRacer(final Racer top, final boolean fullscreen) {
		topLevel = top;

		setLayout(new BorderLayout());

		/* the size of the panel is dictated by the isFullScreen, width and height properties */
		if (fullscreen) {
			setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize()); // full-screen
		} else { // not full-screen
			setPreferredSize(new Dimension(800, 600));
		}

		final GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		add("Center", canvas3D);

		canvas3D.setFocusable(true);
		canvas3D.requestFocus(); // the canvas now has focus, so receives key events

		su = new SimpleUniverse(canvas3D);

		createSceneGraph();

		initUserPosition(); // set user's viewpoint
		orbitControls(canvas3D); // controls for moving the viewpoint

		// depth-sort transparent objects on a per-geometry basis
		final View view = su.getViewer().getView();
		view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

		su.addBranchGraph(sceneBG);
	} // end of WrapRacer()

	private void createSceneGraph() {
		sceneBG = new BranchGroup();
		bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

		addKeyHandling();
		lightScene(); // add the lights
		addBackground(); // add the sky
		addCar(); // add the car
		// add course and car

		sceneBG.compile(); // fix the scene
	} // end of createSceneGraph()

	// adds keyhandling procedures to the canvas
	private void addKeyHandling() {
		canvas3D.addKeyListener(new KeyAdapter() {
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			@Override
			public void keyPressed(final KeyEvent e) {
				final int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_Q
						|| keyCode == KeyEvent.VK_END
						|| keyCode == KeyEvent.VK_C && e.isControlDown()) {
					topLevel.dispose();
					System.exit(0); // exit() alone isn't sufficient most of the time
				} else if (keyCode == KeyEvent.VK_SPACE) {
					initUserPosition(); // resets view to standardview, very usefull, if orbitcontrols was used
				}
			}
		});

		canvas3D.addKeyListener(new Controls(this));
	}

	private void lightScene() {
		// only on ambient light for now. Later dynamic directional light will be implemented
		// Color of light
		final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

		// Sets up white ambient light
		final AmbientLight ambientLightNode = new AmbientLight(white);
		ambientLightNode.setInfluencingBounds(bounds);
		sceneBG.addChild(ambientLightNode);
	}

	private void addBackground() {
		// sets up a sky blue background
		final Background back = new Background();
		back.setApplicationBounds(bounds);
		back.setColor(0.17f, 0.65f, 0.92f); // sky blue colour
		// adds background as Chilld to sceneBG
		sceneBG.addChild(back);
	}

	private void addCar() {
		car01 = new SimpleCar(new Point3d(0.0, 0.0, 0.0));
		sceneBG.addChild(car01);
	}

	private void orbitControls(final Canvas3D c) {
		/*
		 * OrbitBehaviour allows the user to rotate around the scene, and to zoom in and out - leave in for first steps
		 * as for developing purposes
		 */
		final OrbitBehavior orbit = new OrbitBehavior(c,
				OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);

		final ViewingPlatform vp = su.getViewingPlatform();
		vp.setViewPlatformBehavior(orbit);
	} // end of orbitControls()

	private void initUserPosition() {
		/* Set the user's initial viewpoint using lookAt() */
		final ViewingPlatform vp = su.getViewingPlatform();
		final TransformGroup steerTG = vp.getViewPlatformTransform();

		final Transform3D t3d = new Transform3D();
		steerTG.getTransform(t3d);

		// args are: viewer posn, where looking, up direction
		t3d.lookAt(USERPOSN, new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
		t3d.invert();

		steerTG.setTransform(t3d);
	} // end of initUserPosition()
}
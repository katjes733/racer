package com.katjes.racer;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;

public class SimpleCar extends BranchGroup implements Runnable {

	// actualisation time for key handling thread
	private static final int ACTTIME = 10;
	private final Thread t;

	// some colors
	private final static Color3f RED = new Color3f(1.0f, 0.0f, 0.0f);
	private final static Color3f ORANGE = new Color3f(1.0f, 0.5f, 0.0f);
	private final static Color3f YELLOW = new Color3f(1.0f, 1.0f, 0.0f);
	private final static Color3f GREEN = new Color3f(0.0f, 1.0f, 0.0f);
	private final static Color3f BLUE = new Color3f(0.0f, 0.0f, 1.0f);

	private final static Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
	private final static Color3f GRAY50 = new Color3f(0.5f, 0.5f, 0.5f);
	private final static Color3f GRAY25 = new Color3f(0.75f, 0.75f, 0.75f);
	private final static Color3f WHITE = new Color3f(0.9f, 0.9f, 0.9f);

	private final static float MAXangle = 35.0f;
	private final static float MAXspinangle = 36.0f; // ca. 113km/h or 31.4m/s @10ms actualisation time

	private float FrontWheelAngle = 0.0f;
	private float WheelSpinAngle = 0.0f;
	private final float WheelSpeed = 0.0f;
	private final float CarSpeed = 0.0f;

	// appearance elements
	private Appearance appCar, appTires, appRims, appAxis;
	private TransparencyAttributes transAtt;
	private Material matCar, matTires, matRims, matAxis;

	// Car elements
	private Box mainBody, trunkBox, engineBox;
	private Cylinder tireFR, tireFL, tireBR, tireBL, axisF, axisB, rimFR, rimFL,
			rimBR, rimBL;

	// Transformgroups
	private TransformGroup carTG, bodyTG, trunkTG, engineTG, wheelsRearTG,
			wheelsFrontTG, axisRTG, axisRtempTG, axisFTG, wheelFRTG,
			wheelFRtempTG, wheelFLTG, wheelFLtempTG, wheelRRTG, wheelRRtempTG,
			wheelRLTG, wheelRLtempTG;

	private Transform3D t3dFront;
	private final Transform3D t3dRear;

	public SimpleCar(final Point3d position) {
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		makeMaterial();
		putTogetherCar(position);

		t3dFront = new Transform3D();
		t3dRear = new Transform3D();

		// makes new thread with this class
		t = new Thread(this);
		// starts execution of thread functionality specified in function 'run'
		t.start();

	}

	private void makeMaterial() {
		/*
		 * Make a coloured material, which is originally blue. The ambient and diffuse components of the material can be
		 * changed at run time.
		 */

		// maybe save the material variables, and just use one, which is newly initialized every time

		appCar = new Appearance();
		appTires = new Appearance();
		appAxis = new Appearance();
		appRims = new Appearance();

		// set material for Car
		matCar = new Material(BLUE, BLACK, WHITE, WHITE, 100.f);
		// sets ambient, emissive, diffuse, specular, shininess
		matCar.setLightingEnable(true);
		appCar.setMaterial(matCar);

		// set material for Tires
		matTires = new Material(BLACK, BLACK, WHITE, WHITE, 100.f);
		// sets ambient, emissive, diffuse, specular, shininess
		matTires.setLightingEnable(true);
		appTires.setMaterial(matTires);

		// set material for rims
		matRims = new Material(GRAY25, BLACK, WHITE, WHITE, 100.f);
		// sets ambient, emissive, diffuse, specular, shininess
		matRims.setLightingEnable(true);
		appRims.setMaterial(matRims);

		// set material for Axis
		matAxis = new Material(GRAY50, BLACK, WHITE, WHITE, 100.f);
		// sets ambient, emissive, diffuse, specular, shininess
		matAxis.setLightingEnable(true);
		appAxis.setMaterial(matAxis);
	} // end of makeMaterial()

	private void putTogetherCar(final Point3d position) {
		// assembles car at given position
		carTG = new TransformGroup();

		// mainBody part
		mainBody = new Box(1.5f, 0.75f, 1.0f, Box.GENERATE_NORMALS, appCar);

		bodyTG = new TransformGroup();
		bodyTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		bodyTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(0.0, 0.25, 0.0));
		bodyTG.setTransform(t3d);
		bodyTG.addChild(mainBody);
		// end mainBody part

		// trunkBox part
		trunkBox = new Box(1.5f, 0.5f, 0.5f, Box.GENERATE_NORMALS, appCar);

		trunkTG = new TransformGroup();
		trunkTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		trunkTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(0.0, 0.0, 1.5));
		trunkTG.setTransform(t3d);
		trunkTG.addChild(trunkBox);
		// end trunkBox part

		// engineBox part
		engineBox = new Box(1.2f, 0.5f, 1.0f, Box.GENERATE_NORMALS, appCar);

		engineTG = new TransformGroup();
		engineTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		engineTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(0.0, 0.0, -2.0));
		engineTG.setTransform(t3d);
		engineTG.addChild(engineBox);
		// end engineBox part

		// rearWheel parts
		wheelsRearTG = new TransformGroup();
		wheelsRearTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelsRearTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		axisB = new Cylinder(0.05f, 3.5f, appAxis);

		axisRTG = new TransformGroup();
		axisRTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		axisRTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotZ(Math.PI / 2);
		t3d.setTranslation(new Vector3d(0.0, -0.25, 1.5));
		axisRTG.setTransform(t3d);

		axisRtempTG = new TransformGroup();
		axisRtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		axisRtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		axisRtempTG.addChild(axisB);
		axisRTG.addChild(axisRtempTG);
		wheelsRearTG.addChild(axisRTG);

		tireBL = new Cylinder(0.5f, 0.5f, appTires);
		rimBL = new Cylinder(0.35f, 0.52f, appRims);

		wheelRLTG = new TransformGroup();
		wheelRLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelRLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotZ(Math.PI / 2);
		t3d.setTranslation(new Vector3d(-1.75, -0.25, 1.5));
		wheelRLTG.setTransform(t3d);

		wheelRLtempTG = new TransformGroup();
		wheelRLtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelRLtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wheelRLtempTG.addChild(tireBL);
		wheelRLtempTG.addChild(rimBL);
		wheelRLTG.addChild(wheelRLtempTG);
		wheelsRearTG.addChild(wheelRLTG);

		tireBR = new Cylinder(0.5f, 0.5f, appTires);
		rimBR = new Cylinder(0.35f, 0.52f, appRims);

		wheelRRTG = new TransformGroup();
		wheelRRTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelRRTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotZ(Math.PI / 2);
		t3d.setTranslation(new Vector3d(1.75, -0.25, 1.5));
		wheelRRTG.setTransform(t3d);

		wheelRRtempTG = new TransformGroup();
		wheelRRtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelRRtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wheelRRtempTG.addChild(tireBR);
		wheelRRtempTG.addChild(rimBR);
		wheelRRTG.addChild(wheelRRtempTG);
		wheelsRearTG.addChild(wheelRRTG);
		// end rearWheel parts

		// FrontWheel parts
		wheelsFrontTG = new TransformGroup();

		axisF = new Cylinder(0.05f, 3.5f, appAxis);

		axisFTG = new TransformGroup();
		axisFTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		axisFTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotZ(Math.PI / 2);
		t3d.setTranslation(new Vector3d(0.0, -0.25, -2.0));
		axisFTG.setTransform(t3d);
		axisFTG.addChild(axisF);

		wheelsFrontTG.addChild(axisFTG);

		tireFL = new Cylinder(0.5f, 0.4f, appTires);
		rimFL = new Cylinder(0.35f, 0.42f, appRims);

		wheelFLTG = new TransformGroup();
		wheelFLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelFLTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotZ(Math.PI / 2);
		t3d.setTranslation(new Vector3d(-1.75, -0.25, -2.0));
		wheelFLTG.setTransform(t3d);

		wheelFLtempTG = new TransformGroup();
		wheelFLtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelFLtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wheelFLtempTG.addChild(tireFL);
		wheelFLtempTG.addChild(rimFL);
		wheelFLTG.addChild(wheelFLtempTG);
		wheelsFrontTG.addChild(wheelFLTG);

		tireFR = new Cylinder(0.5f, 0.4f, appTires);
		rimFR = new Cylinder(0.35f, 0.42f, appRims);

		wheelFRTG = new TransformGroup();
		wheelFRTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelFRTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotZ(Math.PI / 2);
		t3d.setTranslation(new Vector3d(1.75, -0.25, -2.0));
		wheelFRTG.setTransform(t3d);

		wheelFRtempTG = new TransformGroup();
		wheelFRtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wheelFRtempTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wheelFRtempTG.addChild(tireFR);
		wheelFRtempTG.addChild(rimFR);
		wheelFRTG.addChild(wheelFRtempTG);
		wheelsFrontTG.addChild(wheelFRTG);
		// end Frontwheels

		// bring body parts together in bodyTG
		carTG.addChild(bodyTG);
		carTG.addChild(trunkTG);
		carTG.addChild(engineTG);
		carTG.addChild(wheelsRearTG);
		carTG.addChild(wheelsFrontTG);

		// add bodyTG to BranchGroup (this class)
		this.addChild(carTG);

	}

	public void moveCar(final Vector3d dir) {
		// moves the car into a certain direction with a certain distance
	}

	public void rotateCar() {
		// rotates the car around a virtual axis (necessary for turnings)
	}

	public void turnFrontWheels(final float angle) {
		// turns Front wheels of car to make turning more realistic
		// does not work in combination with spinning
		// Problem is that rotation axis spins with wheel
		if (angle > MAXangle) {
			FrontWheelAngle = MAXangle;
		} else if (angle < -MAXangle) {
			FrontWheelAngle = -MAXangle;
		} else {
			FrontWheelAngle = angle;
		}

		final Transform3D t3d = new Transform3D();
		t3d.rotX(Math.PI / -180 * FrontWheelAngle);
		t3dFront = new Transform3D(); // returns wheel to standard position, optical issue
		t3dFront.mul(t3d);
		wheelFLtempTG.setTransform(t3dFront);
		wheelFRtempTG.setTransform(t3dFront);
	}

	public float getFrontWheelAngle() {
		// returns current angle of wheels
		return FrontWheelAngle;
	}

	public void spinWheels(final float angle) {
		// spins all wheels
		WheelSpinAngle = WheelSpinAngle + angle;
		if (WheelSpinAngle > MAXspinangle) {
			WheelSpinAngle = MAXangle;
		} else if (WheelSpinAngle < -MAXspinangle) {
			WheelSpinAngle = -MAXangle;
		} else if (WheelSpinAngle > -angle / 36.0f
				&& WheelSpinAngle < angle / 36.0f) {
			WheelSpinAngle = 0.0f;
		}
	}

	public float getWheelSpinAngle() {
		return WheelSpinAngle;
	}

	private void doSpinWheels() {
		// does actual wheel spinning, rework needed, does not actual spinning, rotation needs to be relativ
		// currently absolut spinning
		// global rot-var, reset everytime > 2*PI or < -2*PI could be solution
		final Transform3D t3d = new Transform3D();
		t3d.rotY(Math.PI / 180 * WheelSpinAngle); // correct spinning direction
		t3dFront.mul(t3d);
		// t3d.rotX(Math.PI/(180)*WheelSpinAngle);
		t3dRear.mul(t3d);
		wheelFLtempTG.setTransform(t3dFront);
		wheelFRtempTG.setTransform(t3dFront);
		wheelRLtempTG.setTransform(t3dRear);
		wheelRRtempTG.setTransform(t3dRear);

		// rear Axis needs to be added soon
	}

	public void run() {
		// Pause mode needs to be added later...
		while (true /* && (!paused) */) {
			// System.out.println("-----");
			doSpinWheels();
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
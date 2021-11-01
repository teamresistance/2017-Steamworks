package org.teamresistance.frc.auto;

import java.util.ArrayList;

import org.teamresistance.frc.Robot;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Vector2d;
import org.teamresistance.frc.util.AutoTargetFollow;
import org.teamresistance.frc.util.Time;
import org.teamresistance.frc.util.MecanumDrive.DriveType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 *
 */
public class AutoTimedShoot implements AutoMode {
	
	private DriveToHopper driveToHopper = new DriveToHopper();
	private AutoTargetFollow follower = new AutoTargetFollow();
	private DriveToHopperAcceleration driveToHopperAccel = new DriveToHopperAcceleration();
	
	private int currentState = 0;
	 
	private final double MAX_RUN_TIME_TO_HOPPER = 5.0;
	private final double MAX_HOPPER_RAM_TIME = 0.25;
	private final double MAX_DRIVE_TO_BALLS = 0.25;
	private final double MAX_SIT_FOR_BALLS = 0.5;
	
	private double initialTime = Time.getTime();
	
	private ArrayList<AutoDrivePath> drivePaths = new ArrayList<>();
	private int path = 0;
	
	private int driveMode = 1;
	
	public void init() {
		//SmartDashboard.putNumber("Acceleration Threshhold", 0.48);
		path = (int) SmartDashboard.getNumber("Drive Path", path);
		AutoDrivePath redPath = new AutoDrivePath();
		redPath.startingPosition = new Vector2d(-9, 1.7);
		redPath.speed = 0.85;
		redPath.orientation = 0.0;
		redPath.targetList = new ArrayList<>();
		redPath.targetList.add(new Vector2d(-0.25, 9));
		drivePaths.add(redPath);
		
		AutoDrivePath bluePath = new AutoDrivePath();
		bluePath.startingPosition = new Vector2d(9, 1.7);
		bluePath.speed = 0.85;
		bluePath.orientation = 0.0;
		bluePath.targetList = new ArrayList<>();
		bluePath.targetList.add(new Vector2d(0.25, 9));
		drivePaths.add(bluePath);
		
	    follower.init(0.08, 0.0, 0.0);
	    follower.initDistance(100, 0.0071, 0, 0.00325);
	    follower.start();
	    currentState = 0;
	}
	
	public boolean update() {
		boolean done = false;
		//SmartDashboard.putNumber("Auto State", currentState);
		double acceleration = Math.sqrt((Math.pow(IO.navX.getAHRS().getWorldLinearAccelX(),2) + Math.pow(IO.navX.getAHRS().getWorldLinearAccelY(), 2)));
		SmartDashboard.putNumber("Acceleration", acceleration);
		//driveMode = (int) SmartDashboard.getNumber("Auto Drive Mode", 1);
		switch(currentState) {
		case 0:
			IO.drive.setState(DriveType.KNOB_FIELD);
			initialTime = Time.getTime();
			currentState = 1;
			switch(driveMode) {
			case 0:
				driveToHopper.init(drivePaths.get(path));
				break;
			case 1:
				driveToHopperAccel.init();
				break;
			}
			
		case 1:
			boolean driveToHopperDone;
			switch(driveMode) {
			case 0:
				driveToHopperDone = driveToHopper.update();
				break;
			case 1:
				driveToHopperDone = driveToHopperAccel.update();
				break;
			default:
				driveToHopperDone = false;	
				break;
			}
			
			SmartDashboard.putBoolean("Drive to Hopper", done);
			if (driveToHopperDone || Time.getTime() - initialTime >= MAX_RUN_TIME_TO_HOPPER) {
				IO.drive.setState(DriveType.STICK_FIELD);
				IO.drive.drive(0, 0, 0, 0);
				currentState = 2;
				//currentState = -1;
				initialTime = Time.getTime();
			}
			Robot.shooter.update(true, false);
			break;
		case 2:
			IO.drive.setState(DriveType.STICK_FIELD);
			if (Time.getTime() - initialTime < MAX_HOPPER_RAM_TIME) {
				IO.drive.drive(SmartDashboard.getNumber("Alliance", 0), 0, 0, 0);
			} else {
				currentState = 3;
				initialTime = Time.getTime();
			}
			Robot.shooter.update(true, false);
			break;
		case 3:
			// Run Todd's Vibrator?
			IO.drive.setState(DriveType.STICK_FIELD);
			if (Time.getTime() - initialTime < MAX_DRIVE_TO_BALLS) {
				IO.drive.drive(0, 0.6, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 4;
				initialTime = Time.getTime();
			}
			IO.vibratorMotor.set(IO.VIBRATOR_SPEED);
			Robot.shooter.update(true, false);
			break;
		case 4:
			// Run Todd's Vibrator?
			// To move Todd's balls into the robot's hopper
			IO.drive.setState(DriveType.STICK_FIELD);
			if (Time.getTime() - initialTime < MAX_SIT_FOR_BALLS) {
				IO.drive.drive(0, 0, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 5;
				initialTime = Time.getTime();
			}
			IO.vibratorMotor.set(IO.VIBRATOR_SPEED);
			Robot.shooter.update(true, false);
			break;
		case 5:
			IO.drive.setState(DriveType.STICK_FIELD);
			//Start Tracking!
			//SmartDashboard.putBoolean("Follower", follower.update());
			if(follower.update()) {
				currentState = 6;
			}
			Robot.shooter.update(true, false);
			break;
		case 6:
			follower.update();
			Robot.shooter.update(true, true);
			break;
		default:
			done = true;
			Robot.shooter.update(false, false);
			IO.drive.setState(DriveType.STICK_FIELD);
			IO.drive.drive(0, 0, 0, 0);
		}
		return done;
	}

	@Override
	public String toString() {
		return "Auto Timed Shoot";
	}
}

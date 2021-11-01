package org.teamresistance.frc.util;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.vision.ShooterPipeline;

import edu.wpi.cscore.AxisCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class AutoTargetFollow {
	// PID constants
	private double kP; // Proportional constant
	private double kI = 0.0; // Integral constant
	private double kD = 0.0; // Derivative constant
	
	private double kPDistance = 0.0071; // Proportional constant
	private double kIDistance = 0.0; // Integral constant
	private double kDDistance = 0.00325; // Derivative constant
	
	// PID variables
	private double prevError = 0.0; // The error from the previous loop
	private double integral = 0.0; // Error integrated over time
	
	private double prevErrorDistance = 0.0; // The error from the previous loop
	private double integralDistance = 0.0; // Error integrated over time
	
	private double distanceSetpoint = 100;
	
	private long prevTime;
	
	private double tolerance = 0.1; // The percent tolerance for the error to be considered on target
	
	private double maxOutput = 0.6;
	private double minOutput = -0.6;

	private double maxOutputDistance = 0.5;
	private double minOutputDistance = -0.5;

	private ShooterPipeline p;
	
	private double imageWidth = 320;
	private double imageHeight = 240;
	
	private double centerX;
	private double centerY;
	
	private VisionThread visionThread;
	
	private Object imgLock;
	
	private double toleranceDistance = 0.1;
	
	private AxisCamera camera;
	
	public AutoTargetFollow() {
		imgLock = new Object();
		camera = CameraServer.getInstance().addAxisCamera("axis-camera.local");
	}
	
	public void init(double p, double i, double d) {
		this.kP = p;
		this.kI = i;
		this.kD = d;
		this.prevError = 0.0;
		this.integral = 0.0;
		this.prevTime = System.currentTimeMillis();
	}

	public void initDistance(double distance, double p, double i, double d) {
		this.kPDistance = p;
		this.kIDistance = i;
		this.kDDistance = d;
		this.prevErrorDistance = 0.0;
		this.integralDistance = 0.0;
	}	
	
	public void start() {
		visionThread = new VisionThread(camera, new ShooterPipeline(), pipeline -> {
	        if (!pipeline.filterContoursOutput().isEmpty()) {
	            Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
	            synchronized (imgLock) {
	                centerX = r.x + (r.width / 2);
	                centerY = r.y + (r.height / 2);
	            }
	        } else {
	        	centerX = Double.POSITIVE_INFINITY;
                centerY = Double.POSITIVE_INFINITY;
	        }
	    });
		visionThread.start();
	}
	
	public void stop() {
		visionThread.interrupt();
		visionThread = null;
	}
	
	public boolean update() {
		boolean done = true;
		double error;
		double errorDistance;
		synchronized (imgLock) {
			if(Double.isFinite(centerY)) {
				error = imageHeight/2.0 - centerY;
			} else {
				error = imageHeight/2.0;
			}
			
			if(Double.isFinite(centerX)) {
				errorDistance = distanceSetpoint - centerX;
			} else {
				errorDistance = distanceSetpoint;
			}
			
			SmartDashboard.putNumber("Center Y AutoTargetFollow", centerY);
			SmartDashboard.putNumber("Center X AutoTargetFollow", centerX);
			SmartDashboard.putNumber("Error AutoTargetFollow", error);
			SmartDashboard.putNumber("errorDistance", errorDistance);
		}
		
		long curTime = System.currentTimeMillis(); 
		double deltaTime = (curTime - prevTime) / 1000.0;
		boolean errorOnTarget = onTargetRotation(error);
		SmartDashboard.putBoolean("Error On Target", errorOnTarget);
		if(errorOnTarget) {
			error = 0.0;
		} else {
			done = false;
		}
		integral += error;
		double result = (error * kP) + (integral * kI * deltaTime) + ((error - prevError) * kD / deltaTime);
		prevError = error;
		if(result > maxOutput) result = maxOutput;
		else if(result < minOutput) result = minOutput;
		
		boolean errorDistanceOnTarget = onTargetDistance(errorDistance);
		SmartDashboard.putBoolean("Error On Target Distance", errorDistanceOnTarget);
		if(errorDistanceOnTarget) {
			errorDistance = 0.0;
		} else {
			done = false;
		}
		integral += errorDistance;		
		double resultDistance = (errorDistance * kPDistance) + (integral * kIDistance * deltaTime) + ((errorDistance - prevErrorDistance) * kDDistance / deltaTime);
		prevErrorDistance = errorDistance;
		if(resultDistance > maxOutputDistance) resultDistance = maxOutputDistance;
		else if(resultDistance < minOutputDistance) resultDistance = minOutputDistance;
		
		IO.drive.getDrive().mecanumDrive_Cartesian(0, resultDistance, result, 0);
		
		return done;
	}
	
	// If the error is less than or equal to the tolerance it is on target
	private boolean onTargetRotation(double error) {
		return Math.abs(error) <= tolerance * imageHeight/2;
	}
	
	private boolean onTargetDistance(double error) {
		return Math.abs(error) <= tolerance * distanceSetpoint/2;
	}
}

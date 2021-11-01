package org.teamresistance.frc;

import org.teamresistance.frc.io.IO;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Shreya on 2/20/2017.
 */
public class Shooter {

	public void init() {
		SmartDashboard.putNumber("Shooter Setpoint", 3100);
		IO.shooterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		IO.shooterMotor.reverseSensor(false);
		IO.shooterMotor.reverseOutput(false);
		IO.shooterMotor.configEncoderCodesPerRev(20);
		
		// Not sure what this nominal output voltage is for...
		IO.shooterMotor.configNominalOutputVoltage(+0.0, -0.0);
		// Only allow the motor to spin in the forward direction
		IO.shooterMotor.configPeakOutputVoltage(12.0, 0.0);
		
		//Will never change speed faster than 24V/Sec
		//IO.shooterMotor.setVoltageRampRate(24.0);
	}

	public void update(boolean shooter, boolean agitator) {
		double motorOutput = IO.shooterMotor.getOutputVoltage() / IO.shooterMotor.getBusVoltage();
		SmartDashboard.putNumber("Talon Motor Output", motorOutput);
		
		if(shooter) {
			IO.feederMotor.set(1.0);
			IO.shooterMotor.changeControlMode(TalonControlMode.Speed);
			IO.shooterMotor.set(SmartDashboard.getNumber("Shooter Setpoint", 3100));
			
			if (agitator) {
				IO.agitatorMotor.set(0.30);
				IO.vibratorMotor.set(IO.VIBRATOR_SPEED);
			} else {
				IO.agitatorMotor.set(0.0);
				IO.vibratorMotor.set(0.0);
			}
			
			//SmartDashboard.putNumber("Talon Error", IO.shooterMotor.getClosedLoopError());
		} else {
			IO.shooterMotor.changeControlMode(TalonControlMode.PercentVbus);
			IO.shooterMotor.set(0.0);
			IO.feederMotor.set(0.0);
			IO.agitatorMotor.set(0.0);
			IO.vibratorMotor.set(0.0);
		}
		
		SmartDashboard.putNumber("Talon Speed", IO.shooterMotor.getSpeed());
	}
}

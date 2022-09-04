package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.*;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

/**This is a framework for a TR86 subsystem state machine.
 * <p>Some hardware references have been left as examples.
 */
public class Shooter {
    // Reference or Initialize hardware
    private static TalonSRX shooter = IO.shooterMotor;
    private static VictorSP feeder = IO.feederMotor;

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnShooter = JS_IO.btnShooter;

    // Create objects for this SM
    private static int state = 0;

    private static double spdWSP = 0.7;
    private static Timer timer = new Timer(0);

    private static double rpmToTpc = .07833333; // TBD rpm to ticks per cycle (100ms) // 47 ticks per 1 rotation

    private static Integer rpmWSP = 3100; // Working RPM setpoint
    private static double kF = 2.5; // TalonSRX feedforward
    private static double kP = 100; // TalonSRX Proportional band
    private static double kI = 0; // TalonSRX Intgral term
    private static double kD = 0; // TalonSRX Differential term


    /**
     * Initialize objects for this SM. Usually called from autonomousInit or/and
     * teleopInit
     */
    public static void init() {
        //----- From old code ---------------
        // SmartDashboard.putNumber("Shooter Setpoint", 3100);
		// shooter.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        // shooter.reverseSensor(false);
		// shooter.reverseOutput(false);
		// shooter.configEncoderCodesPerRev(20);
		
		// // Not sure what this nominal output voltage is for...
		// shooter.configNominalOutputVoltage(+0.0, -0.0);
		// // Only allow the motor to spin in the forward direction
		// shooter.configPeakOutputVoltage(12.0, 0.0);
		
		// //Will never change speed faster than 24V/Sec
        // //IO.shooterMotor.setVoltageRampRate(24.0);
        
        shooter.configFactoryDefault();
        shooter.setInverted(false);
        shooter.setNeutralMode(NeutralMode.Coast);
        shooter.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

        shooter.enableVoltageCompensation(true);
        shooter.configVoltageCompSaturation(12, 0);
        shooter.configVoltageMeasurementFilter(32, 0);

        shooter.config_kF(0, kF); // Send configuration parms to TalonSRX
        shooter.config_kP(0, kP);
        shooter.config_kI(0, kI);
        shooter.config_kD(0, kD);


        sbdInit();
    }

    /**
     * Determine the state to select if some outside condition is detected such as a
     * joystick button press or change of state of a DI.
     */
    private static void determ() {
        if(btnShooter.onButtonPressed()) {
            state = state == 1 ? 0 : 1;
        }
    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     */
    public static void update() {
        determ();   // Check on external conditions
        sbdUpdate();// Update Smartdashboard stuff

        switch (state) {
            case 0: // Off & off. State 0 is normally the default, off.
                cmdUpdate(false);
                break;
            case 1: // Normally the kickoff. Ex. drop arm but wait 0.5 seconds to start motor
                cmdUpdate(true);
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false);
                System.out.println("Bad state for Shooter - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean shtrCmd) {
        if(shtrCmd){
            //-------------------- Old code ------------------
			// shooter.set(ControlMode.Velocity, SmartDashboard.getNumber("Shooter Setpoint", 3100));
			// shooter.set(ControlMode.Velocity, Math.abs(rpmWSP) * rpmToTpc); // control as velocity (RPM)
            shooter.set(ControlMode.PercentOutput, spdWSP);    //Coast down, DO NOT use 0 rpm sp
        }else{

            shooter.set(ControlMode.PercentOutput, 0.0);    //Coast down, DO NOT use 0 rpm sp
        }

        feeder.set(status() ? 1.0 : 0.0);   //Shooter should be at speed before feeding balls.
    }

    /** Initalize Smartdashbord items */
    private static void sbdInit() {
        SmartDashboard.putNumber("Shooter/rpm Wrkg SP", rpmWSP);
        SmartDashboard.putNumber("Shooter/Speed SP", spdWSP);
    }

    /** Update Smartdashbord items */
    private static void sbdUpdate() {
        rpmWSP = (int)SmartDashboard.getNumber("Shooter/rpm Wrkg SP", rpmWSP);
        spdWSP = SmartDashboard.getNumber("Shooter/Speed SP", spdWSP);

        // Put general Shooter info on sdb
        SmartDashboard.putNumber("Shooter/State", state);
        SmartDashboard.putBoolean("Shooter/On", ((state == 1) ? true : false));
        SmartDashboard.putBoolean("Shooter/Status", status());

        // Put Flywheel info on sdb
        SmartDashboard.putNumber("Shooter/Flywhl/Velocity", shooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Shooter/Flywhl/RPM", getRPM());
        SmartDashboard.putNumber("Shooter/Flywhl/SRX curr", shooter.getStatorCurrent());
        SmartDashboard.putNumber("Shooter/Flywhl/pdp curr", IO.pdp.getCurrent(2));
    }

    /**
     * @return the active state.
     */
    public static int getState(){
        return state;
    }

    /**
     * @return Encoded status of Leds: snorf, lift2, lift1.
     */
    public static boolean status(){
        //return (getRPM() > 1000) && (state > 0);
        return (state > 0);
    }

    /**
     * @return the shooter RPM as an interger.
     */
    public static int getRPM(){
        return (int)shooter.getSelectedSensorVelocity() * 600 / 47;
    }

}
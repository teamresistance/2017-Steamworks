package frc.robot.subsystem;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.*;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

/**This is a framework for a TR86 subsystem state machine.
 * <p>Some hardware references have been left as examples.
 */
public class Climber {
    // Reference or Initialize hardware
    private static VictorSP climber = IO.climberMotor;   // = IO.snorfMtr;

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnClimb = JS_IO.btnClimber;   // = JS_IO.btnToggleSnorf;

    // Create objects for this SM
    private static int state = 0;

    private static double ampClimber = IO.pdp.getCurrent(8);
    private static double ampHALmt = 70.0;
    private static Timer ampHATmr = new Timer(0.1);
    private static boolean ampHA = false;

    /**
     * Initialize objects for this SM. Usually called from autonomousInit or/and
     * teleopInit
     */
    public static void init() {
        sbdInit();
    }

    /**
     * Determine the state to select if some outside condition is detected such as a
     * joystick button press or change of state of a DI.
     */
    private static void determ() {
        if(btnClimb.onButtonPressed()) ampHA = false;
        if(ampHATmr.hasExpired(ampClimber > ampHALmt) && !ampHA ) ampHA = true;

        if(btnClimb.isDown() && !ampHA){
            state = 1;
        }else{
            state = 0;
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
            case 0: // State 0 is normally the default, off.
                cmdUpdate(false);
                break;
            case 1: // On
                cmdUpdate(true);
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false);
                System.out.println("Bad state for Climber - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean bCmd) {
        if(ampHA){
            climber.set(0.0);   //Safety!
        }else{
            climber.set(bCmd ? 1.0 : 0.0);  //Else send cmd
        }
    }

    /** Initalize Smartdashbord items */
    private static void sbdInit() {
        SmartDashboard.putNumber("Climb/Amps Hi Alm Limit", ampHALmt);
    }

    /** Update Smartdashbord items */
    private static void sbdUpdate() {
        ampHALmt = SmartDashboard.getNumber("Climb/Amps Hi Alm Limit", ampHALmt);
        SmartDashboard.putNumber("Climb/Amps", ampClimber);
        SmartDashboard.putBoolean("Climb/Amps Hi Alm", ampHA);
    }

    /**
     * @return the active state.
     */
    public static int getState(){
        return state;
    }

    /**
     * @return status true is motor running.
     */
    public static boolean status(){
        return climber.get() != 0.0;
    }

}
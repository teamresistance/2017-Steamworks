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
public class Agitator {
    // Reference or Initialize hardware
    private static VictorSP agitator = IO.agitatorMotor;

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnAgitator = JS_IO.btnAgitator;

    // Create objects for this SM
    private static double AGITATOR_SPD = 0.3;
    private static double VIBRATOR_SPD = 0.6;

    private static int state = 0;

    private static Timer timer = new Timer(0);

    /**
     * Initialize objects for this SM. Usually called from autonomousInit or/and
     * teleopInit
     */
    public static void init() {
        sdbInit();
    }

    /**
     * Determine the state to select if some outside condition is detected such as a
     * joystick button press or change of state of a DI.
     */
    private static void determ() {
        state = btnAgitator.isDown() && Shooter.getState() == 1 ? 1 : 0;
    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     */
    public static void update() {
        determ();   // Check on external conditions
        sdbUpdate();// Update Smartdashboard stuff

        switch (state) {
            case 0: // Default, off.
                cmdUpdate(false);
                timer.hasExpired(0.0, state);   //Set timer for next state
                break;
            case 1: // Normally the kickoff. Ex. drop arm but wait 0.5 seconds to start motor
                cmdUpdate(true);
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false);
                System.out.println("Bad state for Agitator - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean agitate) {
        if (agitate) {
            IO.agitatorMotor.set(AGITATOR_SPD);
            IO.vibratorMotor.set(VIBRATOR_SPD);
        } else {
            IO.agitatorMotor.set(0.0);
            IO.vibratorMotor.set(0.0);
        }
    }

    /** Initalize Smartdashbord items */
    private static void sdbInit() {
    }

    /** Update Smartdashbord items */
    private static void sdbUpdate() {
        SmartDashboard.putNumber("Agitator/State", state);
    }

    /**
     * @return the active state.
     */
    public static int getState(){
        return state;
    }

    /**
     * @return if agitator motor is running.
     */
    public static boolean status(){
        return (agitator.get() > 0.0);
    }

}
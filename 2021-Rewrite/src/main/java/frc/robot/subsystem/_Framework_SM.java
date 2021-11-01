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
public class _Framework_SM {
    // Reference or Initialize hardware
    private static VictorSP snorfMtr = IO.snorfMtr;

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnToggleSnorf = JS_IO.btnToggleSnorf;

    // Create objects for this SM
    private static int state = 0;

    private static Timer timer = new Timer(0);

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
                cmdUpdate(false, 0.0);
                timer.hasExpired(0.0, state);   //Set timer for next state
                break;
            case 1: // Normally the kickoff. Ex. drop arm but wait 0.5 seconds to start motor
                cmdUpdate(true, 0.0);
                if (timer.hasExpired(0.5, state)) // wait 0.5 seconds to start motor
                    state++;
                break;
            case 2: // Followup state as needed.
                cmdUpdate(true, 1.0);
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false, 0.0);
                System.out.println("Bad state for this SMName - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean bCmd, double aCmd) {

    }

    /** Initalize Smartdashbord items */
    private static void sbdInit() {
    }

    /** Update Smartdashbord items */
    private static void sbdUpdate() {
        SmartDashboard.putNumber("Snorfler/Motor", snorfMtr.get());
        SmartDashboard.putBoolean("Snorfler/Btn Toggle", btnToggleSnorf.isDown());
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
    public static int status(){
        return 0;
    }

}
package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.*;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

/**This is a framework for a TR86 subsystem state machine.
 * <p>Some hardware references have been left as examples.
 */
public class Snorfler_Jim {
    // Reference or Initialize hardware
    private static VictorSP snorfInOut = IO.snorfMtr;       //Retrieve ball, Suck, Spit, Off
    private static InvertibleSolenoid snorfArmDnSV = IO.snorfExtSV;  //Extend/Lower down arm
    private static InvertibleDigitalInput snorfHasBall = IO.snorfHasBall;
    private static double snorfMtrAmp(){ return IO.pdp.getCurrent(14); }

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnTglArmUpDn = JS_IO.btnToggleSnorf; //Toggle arm Up/Dn GP 3
    private static Button btnSpit = JS_IO.btnReverseSnorfler;   //Override mtr to release ball
    private static Button btnSuck = JS_IO.btnForwardSnorfler;   //Override mtr to retrieve ball

    // Create objects for this SM
    private static int state = 0;
    private static boolean armDn = false;
    private static Relay.Value snorfMtr = Value.kOff;
    private static boolean hasBall = false;

    private static Timer timer = new Timer(0);
    private static Timer ballTimer = new Timer(0);

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
        if(btnTglArmUpDn.onButtonPressed()){
            state = armDn ? 0 : 1;  //Up & Off else Dn & On
            armDn = !armDn;
        }

        if(btnSpit.isDown() || btnSuck.isDown()){
            snorfMtr = btnSpit.isDown() ? Value.kReverse : Value.kForward;
        }

        hasBall = hasBallUpd();
    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     */
    public static void update() {
        determ();   // Check on external conditions
        sdbUpdate();// Update Smartdashboard stuff

        switch (state) {
            case 0: // Default, Up & Off
                cmdUpdate(false, Value.kOff);
                timer.hasExpired(0.0, state);   //Set timer for next state
                break;
            case 1: // Arm Down & snorfling
                cmdUpdate(true, Value.kForward);
                if (hasBall) state++; // wait until we have a ball
                break;
            case 2: // Arm Down & Off.  Leave dn for 0.5 sec., just incase.
                cmdUpdate(true, Value.kOff);
                timer.startTimer(2.5);
                state++;
            case 3: // Arm Down & Off for 0.5 sec
                cmdUpdate(true, Value.kOff);
                if(timer.hasExpired()){
                    state++;
                    armDn = false;
                }
                break;
            case 4: // Wait for Raise arm
                cmdUpdate(false, Value.kOff);
                timer.startTimer(1.5);
                state++;
            case 5: // Arm Down & Off for 0.5 sec
                cmdUpdate(false, Value.kOff);
                if(timer.hasExpired()){
                    state++;
                    armDn = false;
                }
                break;
            case 6: // Start spitting
                cmdUpdate(false, Value.kReverse);
                timer.startTimer(1.5);
                state++;
            case 7: // Arm Down & Off for 0.5 sec
                cmdUpdate(false, Value.kReverse);
                if(timer.hasExpired()){
                    state = 0;
                }
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false, Value.kOff);
                System.out.println("Bad state for Snorfler - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean armCmd, Relay.Value mtrCmd) {
        snorfArmDnSV.set(armCmd);

        switch(mtrCmd){
            case kOff:
                snorfInOut.set(0.0);
            break;
            case kForward:
                snorfInOut.set(1.0);
            break;
            case kReverse:
                snorfInOut.set(-1.0);
            break;
            default:
                snorfInOut.set(0.0);
        }
    }

    /** Initalize Smartdashbord items */
    private static void sdbInit() {
    }

    /** Update Smartdashbord items */
    private static void sdbUpdate() {
        SmartDashboard.putNumber("Snorfler/state", state);
        SmartDashboard.putString("Snorfler/Motor", snorfMtr.getPrettyValue());
        // SmartDashboard.putBoolean("Snorfler/Btn Toggle", btnTglArmUpDn.isDown());
        SmartDashboard.putBoolean("Snorfler/hasBall", hasBall);
        SmartDashboard.putNumber("Snorfler/Snorf Mtr Amps", snorfMtrAmp());
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

    public static boolean hasBallUpd(){
        boolean tmpB = snorfMtrAmp() > 3.0;
        return tmpB && ballTimer.hasExpired(1.5, tmpB);
    }

}
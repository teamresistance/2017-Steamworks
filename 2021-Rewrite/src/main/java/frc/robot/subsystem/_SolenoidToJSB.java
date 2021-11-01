package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.joysticks.Button;

/**This is a class for a TR86 subsystem state machine.
 * <p>It interlocks a solenoid valve to a JS button.
 */
public class _SolenoidToJSB {
    // Reference or Initialize hardware
    private Solenoid svDevice;   //Solenoid valve device

    // Reference or Initialize Joystick axis, buttons or pov
    private Button btnForSV;     //JS Button to trigger SV

    // Create objects for this SM
    private int state;
    private String sdbTag;

    /**
     * Constructor to interlocks a solenoid valve to a JS button.
     * @param name used to tag the smartdashboard.
     * @param svDevice the solenoid device
     * @param trigger the JS button
     */
    public _SolenoidToJSB(String name, Solenoid svDevice, Button trigger){
        sdbTag = name;
        this.svDevice = svDevice;
        btnForSV = trigger;
    }

    /**
     * Initialize objects for this SM. Usually called from autonomousInit or/and
     * teleopInit
     */
    public void init() {
        state = 0;
        sdbInit();
    }

    /**
     * Determine the state to select if some outside condition is detected such as a
     * joystick button press or change of state of a DI.
     */
    private void determ() {
        state = btnForSV.isDown() ? 1 : 0;
    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     */
    public void update() {
        determ();   // Check on external conditions
        sdbUpdate();// Update Smartdashboard stuff

        switch (state) {
            case 0: // Off & off. State 0 is normally the default, off.
                cmdUpdate(false);
                break;
            case 1: // Normally the kickoff. Ex. drop arm but wait 0.5 seconds to start motor
                cmdUpdate(true);
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false);
                System.out.println("Bad state for this Catapult - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private void cmdUpdate(boolean bCmd) {
        svDevice.set(bCmd);
    }

    /** Initalize Smartdashbord items */
    private void sdbInit() {
    }

    /** Update Smartdashbord items */
    private void sdbUpdate() {
        SmartDashboard.putNumber( sdbTag + " SV/state", state);
        SmartDashboard.putBoolean(sdbTag + " SV/action", svDevice.get());
    }

    /**
     * @return the active state.
     */
    public int getState(){
        return state;
    }

    /**
     * @return Encoded status of Leds: snorf, lift2, lift1.
     */
    public boolean status(){
        return svDevice.get();
    }
}
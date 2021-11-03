package frc.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.*;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

/* Sequence of Operation
Note: A collinator is a split cylinder that can be expanded to put pressure
on the inside of a tube usually to lock it in place.
The collinator has a spring that is compressed when the gear is captured.  When
the collinator is unexpanded it launches the gear onto the peg with the spring.

There are 2 actions handles by the gear subsystem.
1.  Pickup the gear from the floor, horizontal.
2.  Place the gear on a vertical peg.

At startup (0) the collinator, finger assembly, starts expanded, vertical & tucked away
in the frame.  If btnPickupGear is pressed then start pickup sequence (1) else if btnPlaceGear
is pressed the start place gear sequence (10).

To start pickup (1) the robot traps the gear on the floor at specific spot 
in the frame which is sensed by gearFindBanner sensor. The collinator finger is unexpanded 
rotated down and not extended outside the frame.  If btnPickupGear is NOT pressed
down at this point (visually confirmed) OR if the gear is in position and after a 
short pickup delay the SM moves to state 2.

State 2 extends the collinator outside the frame and sets a time to allow time to
stab the gear then moves on 3.  State 3 waits then moves to state 4.

State 4 expands the collinator and sets a timer then move to state 5.
State 5 waits for timer then moves to state 6.

State 6 retracts the collinator.  The retractor has a end switch,so the the SM 
waits until the ES is true then moves to 7.

State 7 rotates the colloinator back to the horizonal position, sets a timer then moves to 8.
State 8 waits for the timer then moves to 12 (Note: should be renumbered).

State 12 twists the gear until a spoke is aligned, sensed by gearAlignBanner.  Sets a timer
and moves to stte 9.

State 9 If gearAlignBanner is true OR the timer expires then goes back to state 0.  
Else If btnPickupGear is pressed go to state 1 and try again. But continues twisting the gear.

State 10 starts place gear.  The SM was in state 0 where the collinator is expanded with a
trapped and spring loaded gear, up and retracted.  The collinator is unexpanded which should 
release the spring loaded gear and launch it onto the peg.  Set a timer and move to state 11.
State 11 wait for timer and gear to settle then go to 0.  Else if btnPickupGear is pressed 
go to state 1 to re grap the gear.  Fell off the peg?
*/

/**This is a framework for a TR86 subsystem state machine.
 * <p>Some hardware references have been left as examples.
 */
public class Gear {
    // Reference or Initialize hardware
    private static Solenoid gripGearSV = IO.gripSolenoid;
    private static Solenoid rotateDnSV = IO.rotateDnSolenoid;
    private static Solenoid extendOutSV = IO.extendSolenoid;
    private static VictorSP gearRotatorMotor = IO.gearRotatorMotor;
    private static InvertibleDigitalInput gearFindBanner = IO.gearFindBanner;
    private static InvertibleDigitalInput gearAlignBanner = IO.gearAlignBanner;
    private static InvertibleDigitalInput gearRetractedES = IO.gearRetractedES;

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnPickupGear = JS_IO.btnPickupGear;
    private static Button btnPlaceGear = JS_IO.btnPlaceGear;

    // Create objects for this SM
    private static int state = 0;

    private static Timer timer = new Timer(0);

    private static final double INSERT_DELAY = 0.5;
	private static final double GRAB_DELAY = 0.1;
	private static final double START_ROTATE_DELAY = 1.0;
	private static final double RELEASE_DELAY = 3.0;
	private static final double ROTATE_TIMEOUT = 5.0;
	private static final double PICKUP_DELAY = 0.9;
	private static final double ALIGNMTRSPD = 0.25;
	
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
        if(btnPickupGear.onButtonPressed()) {
            state = 1;
        } else if(btnPlaceGear.onButtonPressed()) {
            state = 11; //= 10;
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
            case 0: // Default; gripBig, rotateDn, extendOut, rotate mtr on.  All retracted & motor off.
                cmdUpdate(true, false, false, false);
                // if(btnPickupGear.onButtonPressed()) {
                //     state = 1;
                //     initialTime = Time.getTime();
                // } else if(btnPlaceGear.onButtonPressed()) {
                //     state = 10;
                // }

                timer.hasExpired(0.0, state);   //Set timer for next state
                break;
            case 1: // grip unexpanded, dn, in & off.  Wait for timer, up to dn rotation
                cmdUpdate(false, true, false, false);
                if(!btnPickupGear.isDown()) {
                    state = 3;  //= 2;
                } else if(gearFindBanner.get() && timer.hasExpired(PICKUP_DELAY, state)) {
                    state = 3;  //= 2;
                }
                break;
            // case 2: // Extend and start delay.
            //     cmdUpdate(false, true, true);
            //     initialTime = Time.getTime();
            //     state = 3;
            case 3: // unexpanded, dn, out, off.  Wait to extend to stab gear
                cmdUpdate(false, true, true, false);
                if(timer.hasExpired(INSERT_DELAY, state)) {
                    state = 5;  //= 4;
                }
                break;
            // case 4: // Expand Collintor
            //     cmdUpdate(true, true, true);
            //     initialTime = Time.getTime();
            //     state = 5;
            case 5: // expand, dn, exteded. off.  Wait for grip to lock gear
                cmdUpdate(true, true, true, false);
                if(timer.hasExpired(GRAB_DELAY, state)) {
                    state = 6;
                }
                break;
            case 6: // expanded, dn, retract, off.  Wait until retract end sw. is true.
                cmdUpdate(true, true, false, false);
                if(gearRetractedES.get()) {
                    state = 8;  //= 7;
                }
                break;
            // case 7: // Followup state as needed.
            //     cmdUpdate(true, false, false);
            //     initialTime = Time.getTime();
            //     state = 8;
            //     break;
            case 8: // expanded, up, retracted, off.  Wait for dn to up transistion.
                cmdUpdate(true, false, false, false);
                if(timer.hasExpired(START_ROTATE_DELAY, state)) {
                    state = 9;  //= 12;
                }
                break;
            // case 12: // 
            //     cmdUpdate(false, true, true);
            //     initialTime = Time.getTime();
            //     state = 9;
            //     gearRotatorMotor.set(0.25);
            //     break;
            case 9: // expanded, up, retracted, ON.  twist gear until spoke aligned
                cmdUpdate(true, false, false, true);
                // gearRotatorMotor.set(0.25);
                if(gearAlignBanner.get() || (timer.hasExpired(ROTATE_TIMEOUT, state))) {
                    // gearRotatorMotor.set(0.0);
                    state = 0;
                } else if(btnPickupGear.onButtonPressed()) {
                    // gearRotatorMotor.set(0.0);
                    state = 1;
                }
                break;
            // case 10: // 
            //     cmdUpdate(false, false, false);
            //     initialTime = Time.getTime();
            //     state = 11;
            //     break;
            case 11: // unexpand, up, retracted, off.  Launch gear onto peg and wait to settle.
                cmdUpdate(false, false, false, false);
                if(timer.hasExpired(RELEASE_DELAY, state)) {
                    state = 0;
                } else if(btnPickupGear.onButtonPressed()) {
                    state = 1;
                }
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false, false,false, false);
                System.out.println("Bad state for Gear - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean grip, boolean rotate, boolean extend, boolean align) {
        gripGearSV.set(grip);
        rotateDnSV.set(rotate);
        extendOutSV.set(extend);
        gearRotatorMotor.set(align ? ALIGNMTRSPD : 0.0);
    }

    /** Initalize Smartdashbord items */
    private static void sbdInit() {
    }

    /** Update Smartdashbord items */
    private static void sbdUpdate() {
    //     SmartDashboard.putNumber("Snorfler/Motor", snorfMtr.get());
    //     SmartDashboard.putBoolean("Snorfler/Btn Toggle", btnToggleSnorf.isDown());
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
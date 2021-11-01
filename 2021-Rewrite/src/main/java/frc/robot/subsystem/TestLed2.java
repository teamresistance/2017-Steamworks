package frc.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.IO;
import frc.robot.io.hdw_io.InvertibleDigitalInput;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

/**This is a test to practice using a TR86 state machine.
 * <p>There are 3 end switch inputs and 3 led outputs.  They are interlocked 1 to 1.
 * <p>Unless GP btn 5 is pressed then all leds are turned off.
 * <p>The sdb key "TestLed/0. Estop" immediately turns off all led if set true.
 */
public class TestLed2 {

    //Original code
    // public static void update(){
    //     IO.led1.set(IO.ledBtn1.get());
    //     IO.led2.set(IO.ledBtn2.get());
    //     IO.led3.set(IO.ledBtn3.get());
    // }
    
    // Reference or Initialize hardware
    private static DigitalInput liftTopStop = IO.liftTopStop;
    private static DigitalInput liftMidSnsr = IO.liftMidSnsr;
    private static DigitalInput liftBotStop = IO.liftBotStop;

    private static  Solenoid frntLedLift1 = IO.frntLedLift1;
    private static  Solenoid frntLedLift2 = IO.frntLedLift2;
    private static  Solenoid frntLedSnorf = IO.frntLedSnorf;

    private static InvertibleDigitalInput snorfHasBall = IO.snorfHasBall;

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnAllOn = JS_IO.btnToggleSnorf;  //Left Bump Btn, GP5

    // Create objects for this SM
    private static int state = 0;
    private static int tmpState = 0;
    private static boolean eStop = false;   //From Smartdashboard

    private static Timer timer = new Timer(0.5);    //Used to delay on ALL Leds

    /**
     * initialize objects for this SM. Ususally called from autonomoousInit or/and
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
        tmpState = allOnReq() ? 1 : 0;  //If all on state 1 else 0
        //If timing required and existing state != 1,2, or3 (not timing) then start timing else reg.
        state = (tmpState == 1 && (state > 1 && state < 5)) ? state : tmpState;
        state = eStop ? 10 : state;     //STOP!
    }

    /**
     * @return If ALL leds are being reqested.
     */
    private static boolean allOnReq(){
        return btnAllOn.isDown() ||         //All on pressed 
            (liftTopStop.get() && liftMidSnsr.get() && liftBotStop.get());   //or all switches on
    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     */
    public static void update() {
        determ();   // Check on external conditions
        sbdUpdate();// Update Smartdashboard stuff

        switch (state) {
            case 0: // Default- send switches to  Leds lift1, lift2, snorf
                cmdUpdate(liftBotStop.get(), liftTopStop.get(), liftMidSnsr.get());
                break;
            case 1: // All on.  Set timer for 1 sec before turning all on
                cmdUpdate(false, false, false); //Off for 1 sec then all on
                timer.startTimer(1.0);
                state++;
            case 2: // Wait 1 sec
                if(timer.hasExpired()) state++;
                break;
            case 3: // All on
                cmdUpdate(true, true, true);
                break;
            case 10: // eStop pressed, All OFF!
                cmdUpdate(false, false, false);
                break;
            default: // Always have a default, just incase.
                cmdUpdate(false, false, false);
                System.out.println("TestLed, Bad state - " + state);
        }
    }

    /**
     * Update commands for this subsystem. Commands to an object "should" only be
     * issued from one location.
     * <p>
     * Any safeties, things that if not handled will cause damage, should be here.
     */
    private static void cmdUpdate(boolean lift1Cmd, boolean lift2Cmd, boolean snorf3Cmd) {
        if(eStop){  //eStop is a safety, all off regardless of what cmds passed.
            frntLedLift1.set(false);
            frntLedLift2.set(false);
            frntLedSnorf.set(false);
        }else{
            frntLedLift1.set(lift1Cmd);
            frntLedLift2.set(lift2Cmd);
            frntLedSnorf.set(snorf3Cmd);
        }
    }

    /** Initalize Smartdashbord items */
    private static void sbdInit() {
        SmartDashboard.putBoolean("TestLed/0. Estop", false);   //Needed to PUT it on the sdb
    }

    /** Update Smartdashbord items */
    private static void sbdUpdate() {
        eStop = SmartDashboard.getBoolean("TestLed/0. Estop", eStop);   //MUST match sdbInit key
        SmartDashboard.putNumber( "TestLed/1. state", state);
        SmartDashboard.putBoolean("TestLed/2. Top ES", liftTopStop.get());
        SmartDashboard.putBoolean("TestLed/3. Mid Snsr", liftMidSnsr.get());
        SmartDashboard.putBoolean("TestLed/4. Bot ES", liftBotStop.get());
        SmartDashboard.putBoolean("TestLed/5. Snorf Led", frntLedSnorf.get());
        SmartDashboard.putBoolean("TestLed/6. Lift Led2", frntLedLift2.get());
        SmartDashboard.putBoolean("TestLed/7. Lift Led1", frntLedLift1.get());
        SmartDashboard.putNumber( "TestLed/8. Led Status", statusLed());
        SmartDashboard.putBoolean("TestLed/9. JS 5 All on", btnAllOn.isDown());
        SmartDashboard.putBoolean("TestLed/10. tmp has ball", snorfHasBall.get());
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
    public static int statusLed(){
        return ((frntLedLift1.get() ? 1 : 0) +
                (frntLedLift2.get() ? 2 : 0) +
                (frntLedSnorf.get() ? 4 : 0));
    }
}

package frc.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.IO;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

/**This is a test to practice using a TR86 state machine.
 * <p>There are 3 end switch inputs and 3 led outputs.  They are interlocked 1 to 1.
 * <p>Unless GP btn 5 is pressed then all leds are turned off.
 * <p>The sdb key "TestLed/0. Estop" immediately turns off all led if set true.
 */
public class TestLed {

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

    // Reference or Initialize Joystick axis, buttons or pov
    private static Button btnAllOn = JS_IO.btnToggleSnorf;  //Left Bump Btn, 5

    // Create objects for this SM
    private static int state = 0;
    private static int tmpState = 0;
    private static boolean eStop = false;   //From Smartdashboard

    private static Timer timer = new Timer(0.5);

    /**
     * initialize objects for this SM. Ususally called from autonomoousInit or/and
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
        //Encode switches into a single number
        tmpState = liftTopStop.get() ? 1 : 0;
        tmpState += liftBotStop.get() ? 2 : 0;
        tmpState += liftMidSnsr.get() ? 4 : 0;

        if(btnAllOn.isDown()) tmpState = 7; //GP Btn 5, all on

        state = (tmpState == 7 && state > 7) ? state : tmpState;   //All on but with timer

        state = eStop ? 0 : state;      //Emergency stop, all off
    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     */
    public static void update() {
        determ();   // Check on external conditions
        sdbUpdate();// Update Smartdashboard stuff

        switch (state) {
            case 0: // All Off - lift1, lift2, snorf
                cmdUpdate(false, false, false);
                break;
            case 1: // 
                cmdUpdate(false, false, true);
                break;
            case 2: // Followup state as needed.
                cmdUpdate(false, true, false);
                break;    
            case 3: // Followup state as needed.
                cmdUpdate(false, true, true);
                break;
            case 4: // Followup state as needed.
                cmdUpdate(true, false, false);
                break;
            case 5: // Followup state as needed.
                cmdUpdate(true, false, true);
                break;
            case 6: // Followup state as needed.
                cmdUpdate(true, true, false);
                break;
            case 7: // All on.  Wait 1 sec before turning all on
                timer.startTimer(1.0);
                state++;
            case 8:
                if(timer.hasExpired()) state++;
                break;
            case 9: // Followup state as needed.
                cmdUpdate(true, true, true);
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
    private static void sdbInit() {
        SmartDashboard.putBoolean("TestLed/0. Estop", false);   //Needed to PUT it on the sdb
    }

    /** Update Smartdashbord items */
    private static void sdbUpdate() {
        eStop = SmartDashboard.getBoolean("TestLed/0. Estop", eStop);   //MUST match sdbInit key
        SmartDashboard.putNumber( "TestLed/1. state", state);
        SmartDashboard.putBoolean("TestLed/2. Top ES", liftTopStop.get());
        SmartDashboard.putBoolean("TestLed/3. Mid Snsr", liftMidSnsr.get());
        SmartDashboard.putBoolean("TestLed/4. Bot ES", liftBotStop.get());
        SmartDashboard.putBoolean("TestLed/5. Snorf Led", frntLedSnorf.get());
        SmartDashboard.putBoolean("TestLed/6. Lift Led2", frntLedLift2.get());
        SmartDashboard.putBoolean("TestLed/7. Lift Led1", frntLedLift1.get());
        SmartDashboard.putNumber( "TestLed/8. Led Status", statusLed());
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

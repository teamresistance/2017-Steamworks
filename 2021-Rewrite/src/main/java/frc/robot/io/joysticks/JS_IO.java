package frc.robot.io.joysticks;
/*
Original Author: Joey & Anthony
Rewite Author: Jim Hofmann
History:
J&A - 11/6/2019 - Original Release
JCH - 11/6/2019 - Original rework
JCH - 10/17/2021 - Add Chooser & cleanup
TODO: Exception for bad or unattached devices.
      Auto config based on attached devices and position?
      Add enum for jsID & BtnID?  Button(eLJS, eBtn6) or Button(eGP, eBtnA)
Desc: Reads joystick (gamePad) values.  Can be used for different stick configurations
    based on feedback from Smartdashboard.  Various feedbacks from a joystick are
    implemented in classes, Button, Axis & Pov.
    This version is using named joysticks to istantiate axis, buttons & axis
*/

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Declares all joysticks, buttons, axis & pov's.
 * <p>Handles various JS configuration with various button assignments.
 */
public class JS_IO {
    //Configure possible JS combinations.  Must match configJS().
    public static int jsConfig = 2; // 0=Joysticks, 1=2 Joysticks, 2=gamePad, 4=Nintendo Pad
    private static SendableChooser<Integer> chsr = new SendableChooser<Integer>();
    private static String[] chsrDesc = {"3-Joysticks", "2-Joysticks", "Gamepad"};
    private static int[] chsrNum = {0, 1, 2};
                                
    // Declare all possible Joysticks
    public static Joystick leftJoystick = new Joystick(0); // Left JS
    public static Joystick rightJoystick = new Joystick(1); // Right JS
    public static Joystick coJoystick = new Joystick(2); // Co-Dvr JS
    public static Joystick gamePad = new Joystick(3); // Normal mode only (not Dual Trigger mode)
    // public static Joystick neoPad = new Joystick(4); // Nintendo style gamepad

    // Declare all stick control

    // Drive
    public static Axis axLeftY = new Axis();        // Left JS Y, Mecanum (Arcade) Left/Right turn
    public static Axis axLeftX = new Axis();        // Left JS X, Mecanum (Arcade) Fwd/Bkwd
    public static Axis axRightY = new Axis();       // Right JS Y, Mecanum Rotate orientation
    public static Axis axRightX = new Axis();       // Right JS X, unassigned

    //Gyro
    public static Button btnGyroReset = new Button();       // reset gyro to 0

    // No ideer
    public static Button btnHoldLeft = new Button();        //??
    public static Button btnHoldCenter = new Button();      //??
    public static Button btnHoldRight = new Button();       //??

    // Gear
    public static Button btnPickupGear = new Button();     // Pickup gear off floor
    public static Button btnPlaceGear = new Button();      // Place gear on ship peg

    // Shooter
    public static Button btnShooter = new Button();     // Start shooter then feeder(s)
    public static Button btnAgitator = new Button();    // Agitate the ball bin

    // Climb
    public static Button btnClimber = new Button();     // Climber rotating

    // Constructor.  Don't need it.  Just bc.
    public JS_IO() {
        init();
    }

    /**Initialize joysticks, axises, buttons & pov's.
     * <p>Normally called from robotInit in Robot.
     */
    public static void init() {
        chsrInit();
        configJS();
    }

    /**Initialize the Joystick Chooser.  Placed on Dashboard to chose
     * JS configuration, 3 JS's, 2 JS's or Gamepad.
     */
    public static void chsrInit(){
        for(int i = 0; i < chsrDesc.length; i++){
            chsr.addOption(chsrDesc[i], chsrNum[i]);
        }
        chsr.setDefaultOption(chsrDesc[2] + " (Default)", chsrNum[2]);   //Default MUST have a different name
        SmartDashboard.putData("JS/Choice", chsr);
        SmartDashboard.putString("JS/Choosen", chsrDesc[chsr.getSelected()]);   //Put selected on sdb
    }

    /**Check and update Joystick configuration. */
    public static void update() { // Chk for Joystick configuration
        if (jsConfig != chsr.getSelected()) {
            jsConfig = chsr.getSelected();
            caseDefault();
            configJS();
            SmartDashboard.putNumber("JS/JS_Config", jsConfig);
            SmartDashboard.putString("JS/Choosen", chsrDesc[chsr.getSelected()]);   //Put selected on sdb
        }
    }

    /**Call the configurator for the Joystick selected. */
    public static void configJS() { // Default Joystick else as gamepad
        // jsConfig = chsr.getSelected() == null ? 0 : chsr.getSelected();
        // SmartDashboard.putNumber("JS/JS_Config", jsConfig);

        switch (jsConfig) {
            case 0: // Normal 3 joystick config
                norm3JS();
                break;

            case 1: // Normal 2 joystick config No CoDrvr
                norm2JS();
                break;

            case 2: // Gamepad only
                a_GP();
                break;

            default: // Bad assignment
                System.out.println("Bad JS choice - " + jsConfig);
                caseDefault();
                break;
        }
    }

    // ================ Controller actions ================

    /**
     * ----------- Normal 3 Joysticks Configurator -------------
     */
    private static void norm3JS() {

        // Drive
        axLeftX.setAxis(leftJoystick, 0);       //Mecanum (Arcade) Left/Right turn
        axLeftY.setAxis(leftJoystick, 1);       //Mecanum (Arcade) Fwd/Bkwd
        axRightX.setAxis(rightJoystick, 0);     //Mecanum Rotate orientation
        axRightY.setAxis(rightJoystick, 1);     //unassigned

        btnGyroReset.setButton(rightJoystick, 6);    // reset gyro to 0

        btnHoldLeft.setAxis(rightJoystick, 4);       //??
        btnHoldCenter.setAxis(rightJoystick,3);      //??
        btnHoldRight.setAxis(rightJoystick, 5);      //??
    
        // Gear
        btnPickupGear.setAxis(coJoystick, 2);     // Pickup gear off floor
        btnPlaceGear.setAxis(coJoystick, 5);      // Place gear on ship peg
    
        // Shooter
        btnShooter.setAxis(coJoystick, 1);     // Start shooter then feeder(s)
        btnAgitator.setAxis(coJoystick, 3);    // Agitate the ball bin
    
        // Climb
        btnClimber.setAxis(coJoystick, 8);     // Climber rotating
    
    }

    /**
     * ----- GamePad only Configurator --------
     */
    private static void a_GP() {

        // Drive
        axLeftX.setAxis(gamePad, 0);    //Mecanum (Arcade) Left/Right turn
        axLeftY.setAxis(gamePad, 1);    //Mecanum (Arcade) Fwd/Bkwd
        axRightX.setAxis(gamePad, 4);   //Mecanum Rotate orientation
        axRightY.setAxis(gamePad, 5);   //unassigned

        btnGyroReset.setButton(gamePad, 7); //Reset / reset gyro to 0

        btnHoldLeft.setAxis(gamePad, 4);    //Y / ??
        btnHoldCenter.setAxis(gamePad,9);   //JSL / ??
        btnHoldRight.setAxis(gamePad, 10);  //JSR / ??
    
        // Gear
        btnPickupGear.setAxis(gamePad, 1);  //A / Pickup gear off floor
        btnPlaceGear.setAxis(gamePad, 2);   //B / Place gear on ship peg
    
        // Shooter
        btnShooter.setAxis(gamePad, 6);     //RT / Start shooter then feeder(s)
        btnAgitator.setAxis(gamePad, 3);    //X /  Agitate the ball bin
    
        // Climb
        btnClimber.setAxis(gamePad, 8);     //Start / Climber rotating

    }

    /**
     * ----------- Normal 2 Joysticks Configurator -------------
     */
    private static void norm2JS() {

        // Drive
        axLeftX.setAxis(leftJoystick, 0);       //Mecanum (Arcade) Left/Right turn
        axLeftY.setAxis(leftJoystick, 1);       //Mecanum (Arcade) Fwd/Bkwd
        axRightX.setAxis(rightJoystick, 0);     //Mecanum Rotate orientation
        axRightY.setAxis(rightJoystick, 1);     //unassigned

        btnGyroReset.setButton(rightJoystick, 6);    // reset gyro to 0

        btnHoldLeft.setAxis(rightJoystick, 4);       //??
        btnHoldCenter.setAxis(rightJoystick,3);      //??
        btnHoldRight.setAxis(rightJoystick, 5);      //??
    
        // Gear
        btnPickupGear.setAxis(leftJoystick, 2);     // Pickup gear off floor
        btnPlaceGear.setAxis( leftJoystick, 5);      // Place gear on ship peg
    
        // Shooter
        btnShooter.setAxis( leftJoystick, 1);     // Start shooter then feeder(s)
        btnAgitator.setAxis(leftJoystick, 3);    // Agitate the ball bin
    
        // Climb
        btnClimber.setAxis(leftJoystick, 8);     // Climber rotating

    }

    /**
     * ----------- Case Default Configurator -----------------
     * <p>(Clear all configurations)
     */
    private static void caseDefault() {

        // Drive
        axLeftX.setAxis(null, 0);       //Mecanum (Arcade) Left/Right turn
        axLeftY.setAxis(null, 0);       //Mecanum (Arcade) Fwd/Bkwd
        axRightX.setAxis(null, 0);     //Mecanum Rotate orientation
        axRightY.setAxis(null, 0);     //unassigned

        btnGyroReset.setButton(null, 0);    // reset gyro to 0

        btnHoldLeft.setAxis(null, 0);       //??
        btnHoldCenter.setAxis(null, 0);      //??
        btnHoldRight.setAxis(null, 0);      //??
    
        // Gear
        btnPickupGear.setAxis(null, 0);     // Pickup gear off floor
        btnPlaceGear.setAxis(null, 0);      // Place gear on ship peg
    
        // Shooter
        btnShooter.setAxis(null, 0);     // Start shooter then feeder(s)
        btnAgitator.setAxis(null, 0);    // Agitate the ball bin
    
        // Climb
        btnClimber.setAxis(null, 0);     // Climber rotating

    }
}
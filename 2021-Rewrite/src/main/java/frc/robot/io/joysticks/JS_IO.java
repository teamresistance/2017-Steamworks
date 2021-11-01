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

//
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
    public static Axis axLeftY = new Axis();        // Left JS Y - Added for testing in Drive3
    public static Axis axLeftX = new Axis();        // Left JS X
    public static Axis axRightY = new Axis();       // Right JS Y
    public static Axis axRightX = new Axis();       // Right JS X

    public static Button btnScaledDrive = new Button();     // scale the drive
    public static Button btnInvOrientation = new Button();  // invert the orientation of the robot (joystick: forwards
                                                            // becomes backwards for robot and same for backwards)

    public static Button btnSelDrv = new Button();      //In teleop manually select between tank, arcade & curvature
    public static Button btnHoldZero = new Button();    //Hold 0 heading.  Left stick moves fwd/bkwd
    public static Button btnHold180 = new Button();     //Hold 180 heading.  Left stick moves fwd/bkwd

    // Shooter
    public static Button btnFireShooter = new Button(); //Catapult boulder

    // Snorfler
    public static Button btnToggleSnorf = new Button();     //Toggle Snorfler up/dn
    public static Button btnForwardSnorfler = new Button(); //Suckin the boulder
    public static Button btnReverseSnorfler = new Button(); //Spitout the boulder
    public static Pov povSnorfInOut = new Pov();    //Alt. for Fwd/Rev

    // Climb
    public static Axis axClimb = new Axis();            //To move climber up or dn
    public static Button btnClimbTop = new Button();    //Move climber to top
    public static Button btnClimbBot = new Button();    //Move climber to bottom
    public static Button btnClimbExt = new Button();    //Extend climber.  For testing only

    //Flipper
    public static Button btnFlipper = new Button(); //Extend Flipper down

    //Antler
    public static Button btnAntler = new Button();  //Extend Antlers down


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
        axLeftX.setAxis(leftJoystick, 0);
        axLeftY.setAxis(leftJoystick, 1);
        axRightX.setAxis(rightJoystick, 0);
        axRightY.setAxis(rightJoystick, 1);

        btnScaledDrive.setButton(rightJoystick, 3);
        btnInvOrientation.setButton(rightJoystick, 1);

        btnSelDrv.setButton(rightJoystick, 4);
        btnHoldZero.setButton(rightJoystick, 5);
        btnHold180.setButton(rightJoystick, 6);

        // Shooter
        btnFireShooter.setButton(coJoystick, 1);

        // Snorfler
        btnToggleSnorf.setButton(coJoystick, 3);
        btnForwardSnorfler.setButton(coJoystick, 5);
        btnReverseSnorfler.setButton(coJoystick, 7);
        povSnorfInOut.setPov(coJoystick, 0);

        // Climb
        axClimb.setAxis(coJoystick, 1);
        btnClimbTop.setButton(coJoystick, 8);
        btnClimbBot.setButton(coJoystick, 9);
        btnClimbExt.setButton(leftJoystick, 10);  //For testing hdw only

        //Flipper
        btnFlipper.setButton(coJoystick, 10); //Extend Flipper down

        //Antler
        btnAntler.setButton(coJoystick, 11);  //Extend Antlers down

    }

    /**
     * ----- GamePad only Configurator --------
     */
    private static void a_GP() {
        // Drive
        axLeftX.setAxis(gamePad, 0);
        axLeftY.setAxis(gamePad, 1);
        axRightX.setAxis(gamePad, 4);
        axRightY.setAxis(gamePad, 5);

        // btnScaledDrive.setButton(gamePad, 5);
        // btnInvOrientation.setButton(gamePad, 10);

        // btnSelDrv.setButton(gamePad, 4);
        // btnHoldZero.setButton(gamePad, 5);
        // btnHold180.setButton(gamePad, 6);

        // Shooter
        btnFireShooter.setButton(gamePad, 6);

        // Snorfler
        btnToggleSnorf.setButton(gamePad, 3); // switched with reverse button
        btnForwardSnorfler.setButton(gamePad, 4);
        btnReverseSnorfler.setButton(gamePad, 5); // switched with toggleSnorf
        povSnorfInOut.setPov(gamePad, 0);

        // Climb
        axClimb.setAxis(gamePad, 1);
        btnClimbTop.setButton(gamePad, 7);
        btnClimbBot.setButton(gamePad, 8);
        btnClimbExt.setButton(gamePad, 9);  //For testing hdw only

        //Flipper
        btnFlipper.setButton(gamePad, 1); //Extend Flipper down

        //Antler
        btnAntler.setButton(gamePad, 2);  //Extend Antlers down

    }

    // 
    /**
     * ----------- Normal 2 Joysticks Configurator -------------
     */
    private static void norm2JS() {

        // Drive
        axLeftX.setAxis(leftJoystick, 0);
        axLeftY.setAxis(leftJoystick, 1);
        axRightX.setAxis(rightJoystick, 0);
        axRightY.setAxis(rightJoystick, 1);

        btnScaledDrive.setButton(rightJoystick, 3);
        btnInvOrientation.setButton(rightJoystick, 1);

        btnSelDrv.setButton(rightJoystick, 4);     //Added to test drive3
        btnHoldZero.setButton(rightJoystick, 5);
        btnHold180.setButton(rightJoystick, 6);

        // Shooter
        btnFireShooter.setButton(leftJoystick, 1);

        // Snorfler
        btnToggleSnorf.setButton(leftJoystick, 3);
        btnForwardSnorfler.setButton(leftJoystick, 5);
        btnReverseSnorfler.setButton(leftJoystick, 7);
        povSnorfInOut.setPov(leftJoystick, 0);

        // Climb
        axClimb.setAxis(leftJoystick, 0);
        btnClimbTop.setButton(leftJoystick, 8);
        btnClimbBot.setButton(leftJoystick, 9);
        btnClimbExt.setButton(leftJoystick, 10);  //For testing hdw only

        //Flipper
        btnFlipper.setButton(rightJoystick, 10); //Extend Flipper down

        //Antler
        btnAntler.setButton(rightJoystick, 11);  //Extend Antlers down
    }

    /**
     * ----------- Case Default Configurator -----------------
     * <p>(Clear all configurations)
     */
    private static void caseDefault() {

        // Drive
        axLeftX.setAxis(null, 0);
        axLeftY.setAxis(null, 0);
        axRightX.setAxis(null, 0);
        axRightY.setAxis(null, 0);

        btnScaledDrive.setButton(null, 0);
        btnInvOrientation.setButton(null, 0);

        btnSelDrv.setButton(null, 0);     //Added to test drive3
        btnHoldZero.setButton(null, 0);
        btnHold180.setButton(null, 0);

        // Shooter
        btnFireShooter.setButton(null, 0);

        // Snorfler
        btnToggleSnorf.setButton(null, 0);
        btnForwardSnorfler.setButton(null, 0);
        btnReverseSnorfler.setButton(null, 0);
        povSnorfInOut.setPov(null, -1);

        // Climb
        axClimb.setAxis(null, 0);
        btnClimbTop.setButton(null, 0);
        btnClimbBot.setButton(null, 0);

        //Flipper
        btnFlipper.setButton(null, 0); //Extend Flipper down

        //Antler
        btnAntler.setButton(null, 0);  //Extend Antlers down
    }
}
package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.IO;
import frc.robot.io.joysticks.JS_IO;

/**This class is used to test control & feedback
 * from the hardware on the 2016-Stronghold robot.
 */
public class Test_Hdw {
    /**
     * Initialize stuff to test.
     */
    public static void init(){

    }

    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     * <p>Other subsystems should be commented out when running this.
     */
    public static void update(){
        //Solenoids
        //----------- May require interlocking.  Need to talk w/ Joel -----------
        IO.gripSolenoid.set(JS_IO.povHoldLCR.is0());       //GP pov up
        IO.extendSolenoid.set(JS_IO.povHoldLCR.is90());    //GP pov right
        IO.rotateDnSolenoid.set(JS_IO.povHoldLCR.is180());   //GP pov dn

        //Motors
        IO.leftRearMotor.set(JS_IO.axLeftX.get());          //0
        IO.leftFrontMotor.set(-JS_IO.axLeftY.get());        //1
        IO.rightRearMotor.set(JS_IO.axRightX.get());        //4
        IO.rightFrontMotor.set(-JS_IO.axRightY.get());      //5

        IO.shooterMotor.set(ControlMode.PercentOutput, 
                            (JS_IO.btnShooter.isDown() ? 1.0 : 0.0));       //GP 6 (RT)
        IO.vibratorMotor.set(JS_IO.btnTstVibrator.isDown() ? 0.6 : 0.0);    //GP 5 (LT)
        IO.feederMotor.set(JS_IO.btnTstFdr.isDown() ? 1.0 : 0.0);           //GP 3 (X)
        IO.agitatorMotor.set(JS_IO.btnAgitator.isDown() ? 0.3 : 0.0);       //GP 4 (Y)
        IO.gearRotatorMotor.set(JS_IO.btnPickupGear.isDown() ? 1.0 : 0.0);  //GP 1 (A)
        IO.climberMotor.set(JS_IO.btnClimber.isDown() ? 1.0 : 0.0);         //GP 8 (Start)
  
        //Sensors
        SmartDashboard.putBoolean("Test/0. Gear Retracted Limit", IO.gearRetractedES.get()); //DIO 0
        SmartDashboard.putBoolean("Test/1. Gear Find Banner", IO.gearFindBanner.get());         //DIO 1
        SmartDashboard.putBoolean("Test/2. Gear Align Banner", IO.gearAlignBanner.get());       //DIO 2
        SmartDashboard.putBoolean("Test/3. Finger Exp Btn", JS_IO.povHoldLCR.is0());        //js POV = 0
        SmartDashboard.putBoolean("Test/4. Finger Dn Btn", JS_IO.povHoldLCR.is90());        //js POV = 90
        SmartDashboard.putBoolean("Test/5. Finger Ext Btn", JS_IO.povHoldLCR.is180());      //js POV = 180

        // SmartDashboard.putNumber("Test/pdp/Chnl 0", IO.pdp.getCurrent(0));  //1 PDP 1
        /*
        0 - Ball Feeder     4 - Gear Rotate Mtr  8 - Climber Motr       12 - Whl Right Rear
        1 - Whl Left Rear   5 -                  9 -                    13 - Ball Agitator
        2 - Shooter         6 -                 10 -                    14 -
        3 - Whl Left Front  7 -                 11 - Whl Right Front    15 - Bin Vibrator
        */
        for(int x = 0; x < 16; x++){
            SmartDashboard.putNumber("Test/pdp/Chnl " + x, IO.pdp.getCurrent(x));
        }
    }
}

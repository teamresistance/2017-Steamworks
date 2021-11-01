package frc.robot.subsystem;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.IO;
import frc.robot.io.joysticks.JS_IO;

/**This class is used to test control & feedback
 * from the hardware on the 2016-Stronghold robot.
 */
public class Test_Hdw {
    /**
     * Periodically Update commands to this subsystem. Normally called from
     * autonomousPeriodic or teleopPeroic in Robot.
     * <p>Other subsystems should be commented out when running this.
     */
    public static void update(){
        //Solendoids
        IO.catapult.set(JS_IO.btnFireShooter.isDown());     //5, GP 6 (RB)
        IO.flipperDn.set(JS_IO.btnFlipper.isDown());        //4, GP 1 (A)
        IO.antlerDn.set(JS_IO.btnAntler.isDown());          //1, GP 2 (B)

        IO.liftExt.set(JS_IO.btnClimbExt.isDown());         //2, GP 9 (JLB)
        IO.snorfExtSV.set(JS_IO.btnToggleSnorf.isDown());   //3, GP 5 (LB)

        IO.frntLedLift1.set(JS_IO.povSnorfInOut.is0());     //6, GP pov up
        IO.frntLedLift2.set(JS_IO.povSnorfInOut.is90());    //0, GP pov right
        IO.frntLedSnorf.set(JS_IO.povSnorfInOut.is180());   //7, GP pov dn

        //Motors
        IO.leftDrv.set(-JS_IO.axLeftY.get());     
                   //0. 
        IO.rightDrv.set(JS_IO.axRightY.get());              //1. 
        IO.snorfMtr.set(JS_IO.btnForwardSnorfler.isDown() ? 1.0 :       //3, GP 4 (Y)
                        JS_IO.btnReverseSnorfler.isDown() ? -1.0 : 0.0);//3, GP 3 (X)
        // !!!----The following lifter MUST NOT exceed end stops ----!!!
        IO.liftUP.set(JS_IO.btnClimbTop.isDown() ? 1.0 :            //2, GP 7 (Reset)
                      JS_IO.btnClimbBot.isDown() ? -1.0 : 0.0);     //2, GP 8 (Back)
  
        //Sensors
        SmartDashboard.putBoolean("Test/0. Snorf has ball", IO.snorfHasBall.get()); //DIO 0
        SmartDashboard.putBoolean("Test/1. Flip ES UP", IO.fliperIsUp.get()); //DIO 1
        SmartDashboard.putBoolean("Test/2. Flip ES Dn", IO.fliperIsDn.get()); //DIO 2
        SmartDashboard.putBoolean("Test/3. Lift ES Top", IO.liftTopStop.get()); //DIO 3
        SmartDashboard.putBoolean("Test/4. Lift ES Mid", IO.liftMidSnsr.get()); //DIO 4
        SmartDashboard.putBoolean("Test/5. Lift ES UP", IO.liftBotStop.get()); //DIO 5
        SmartDashboard.putNumber( "Test/7. Compressor Motor", IO.pdp.getCurrent(7));//auto PDP 7

        SmartDashboard.putNumber("Test/10. Right Motor A", IO.pdp.getCurrent(0));   //1 PDP 1
        SmartDashboard.putNumber("Test/11. Left Motor A", IO.pdp.getCurrent(1));    //0 PDP 0
        SmartDashboard.putNumber("Test/14. Snorfler Motor", IO.pdp.getCurrent(14)); //3 PDP 14
        SmartDashboard.putNumber("Test/15. Lifter Motor", IO.pdp.getCurrent(15));    //2 PDP 15
    }
}

package frc.robot.subsystem;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.io.hdw_io.*;
import frc.robot.io.joysticks.Button;
import frc.robot.io.joysticks.JS_IO;
import frc.util.Timer;

public class Snorfler {
    // Initialize motors
    private static VictorSP snorfFeederV = IO.snorfMtr; // Collector motor
    private static VictorSP snorfMtr = IO.snorfMtr;

    //initialize Solenoids
    private static InvertibleSolenoid snorfExtSV = IO.snorfExtSV; // Lowers snorfler

    //initialize buttons
    // private static Button btnForwardSnorfler = JS_IO.btnForwardSnorfler;
    // private static Button btnReverseSnorfler = JS_IO.btnReverseSnorfler;
    private static Button btnToggleSnorf = JS_IO.btnToggleSnorf;

    private static int state = 0;
    private static boolean wentDown = false;

    private static Timer timer = new Timer(0);
    // private static InvertibleDigitalInput snorfHasBall = IO.hasBallSensor; //
    // Banner snsr, ball at top of snorfler


    public static void update() {
        // // in and out function
        // if (btnForwardSnorfler.isDown())
        //     snorfMtr.setSpeed(1);
        // else if (btnReverseSnorfler.isDown())
        //     snorfMtr.setSpeed(-1);
        // else
        //     snorfMtr.setSpeed(0);

        // // Arm Toggle
        // if (JS_IO.btnTglSnorArmDn.onButtonPressed()) {
        //     if (IO.snorflerExtSV.get()) {
        //         IO.snorflerExtSV.set(false);
        //     } else {
        //         IO.snorflerExtSV.set(true);
        //     }
        // }
        // if(JS_IO.btnTglSnorArmDn.isDown() && !IO.snorflerExtSV.get())
        // IO.snorflerExtSV.set(true);
        switch (state) {
            case 0: // snorfler is up and resting
            snorfMtr.setSpeed(0);
            if (btnToggleSnorf.onButtonPressed())
                state++;
                break;
            case 1: // ball sucking state
            snorfExtSV.set(true); //lower snorfler
            snorfMtr.setSpeed(1); //set snorfler to ball sucking
            if (btnToggleSnorf.onButtonPressed())
            {
                state++;
                timer.startTimer(2.0);
            }
            break;
            case 2: // snorfler coming up state
            snorfMtr.setSpeed(0); // turn off sucking
            snorfExtSV.set(false); // pick up snorfler
            if (timer.hasExpired()) {  // wait two seconds for snorfler to come up
                state++;
                timer.startTimer(2);
            }
            break;
            case 3: // ball spitting state
            snorfMtr.setSpeed(-1);
            if (timer.hasExpired()) // wait two seconds to spit balls then return to waiting state
            state = 0;
            break;



        }

        sbdUpdate();
    }

    public static void determ()
    {

    }

    public static void sbdUpdate() {
        // SmartDashboard.putBoolean("btnForwardSnorfler", btnForwardSnorfler.isDown());
        // SmartDashboard.putBoolean("btnReverseSnorfler", btnReverseSnorfler.isDown());
        SmartDashboard.putBoolean("Snorfler Toggle", btnToggleSnorf.isDown());
        SmartDashboard.putNumber("Snorfler State", state);

    }
}
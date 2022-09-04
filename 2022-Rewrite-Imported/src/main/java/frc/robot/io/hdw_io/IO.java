package frc.robot.io.hdw_io;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class IO {
	// NavX-MXP navigation sensor
	public static NavX navX = new NavX();

    // PDP
    public static PowerDistributionPanel pdp = new PowerDistributionPanel(0);

    // Air
    public static Compressor compressor = new Compressor(1);
    public static Relay compressorRelay = new Relay(0);

    // Drive Motors
	public static Victor leftFrontMotor = new Victor(7);
	public static Victor leftRearMotor = new Victor(8);
	public static Victor rightFrontMotor = new Victor(3);
	public static Victor rightRearMotor = new Victor(1);
	// public static MecanumDrive drive = new MecanumDrive(new RobotDrive(IO.leftFrontMotor, IO.leftRearMotor, IO.rightFrontMotor, IO.rightRearMotor), navX);

	// Motors -- shooting
	// public static CANTalon shooterMotor = new CANTalon(3);
	public static TalonSRX shooterMotor = new TalonSRX(3);		//Ball shooter

	public static VictorSP feederMotor = new VictorSP(2);		//Ball Feeder to shooter
	public static VictorSP agitatorMotor = new VictorSP(6);		//Ball agitiator (spinner)
	public static VictorSP vibratorMotor = new VictorSP(0);		//Ball bin vibrator

	// Motors -- other
	public static VictorSP gearRotatorMotor = new VictorSP(5);	//Gear rotator
	public static VictorSP climberMotor = new VictorSP(9);		//Climber motor

	// Banner Sensors for Gear
	public static InvertibleDigitalInput gearFindBanner = new InvertibleDigitalInput(1, true);	//Gear in floor slot
	public static InvertibleDigitalInput gearAlignBanner = new InvertibleDigitalInput(2, true);	//Gear spoke aligned
	public static InvertibleDigitalInput gearRetractedES = new InvertibleDigitalInput(0, true);	//Gear finger is retracted

	// Pneumatic Cylinders (controlled via Solenoids)
	// public static SingleSolenoid gripSolenoid = new InvertibleSolenoid(1, 2, true);
	// public static SingleSolenoid extendSolenoid = new InvertibleSolenoidWithPosition(1, 0, false, gearRetractedLimit);
	// public static SingleSolenoid rotateSolenoid = new InvertibleSolenoid(1, 1, false);
	public static InvertibleSolenoid gripSolenoid = new InvertibleSolenoid(1, 2, true);
	public static InvertibleSolenoid extendSolenoid = new InvertibleSolenoid(1, 0, false);
	public static InvertibleSolenoid rotateDnSolenoid = new InvertibleSolenoid(1, 1, false);

	// Relay for green LEDs
	public static Relay cameraLights = new Relay(1);


    /**Initialize any hardware.  Usually called from robotInit in Robot. */
    public static void init() {
		// IO.rightFrontMotor.setInverted(true);
		// IO.rightRearMotor.setInverted(true);

		IO.climberMotor.setInverted(true);

		IO.feederMotor.setInverted(true);

		// gearCamera.setFPS(20);
		// IO.gearCamera.setResolution(320, 240);
		// IO.gearCamera.setBrightness(0);
    }

    /**Periodicly update any hardware here.  Usually called from robotPeriodic in Robot. */
    public static void update() {
    }

}

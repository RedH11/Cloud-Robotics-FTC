package org.firstinspires.ftc.teamcode.RobotClasses;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Movement.Driving;
import org.firstinspires.ftc.teamcode.Movement.Turning;

public class MainRobot {

    /* Public OpMode members. */
    private DcMotor lb              = null;
    private DcMotor lf              = null;
    private DcMotor rb              = null;
    private DcMotor rf              = null;
    private BNO055IMU imu           = null;
    private Servo lFoundation       = null;
    private Servo rFoundation       = null;
    private TouchSensor touchSensor = null;

    /* IMU Variables */
    Orientation angles;

    /* Robot functionality */
    private Driving driving;
    private Turning turning;
    //private NNVision vision;

    /* Error Variables*/
    private double angleError = 0; // accounts for the IMU's error
    private double turnAccuracyThreshold = 1; // Amount of degrees which the turn should be within
    private double longestTurnTime = 5; // 5 seconds is the longest time a turn can take

    /* Encoder Values */
    private double drivingTicksPerInch = 89; // The calculated value the wheels should respect (although 80.94 was used previously)
    private double sideStrafingTicksPerInch = 60; // Estimated value

    /* local OpMode members. */
    private HardwareMap hardwareMap = null;
    private Telemetry telemetry     = null;
    private ElapsedTime timer       = new ElapsedTime();

    /**
     * The main constructor
     */
    public MainRobot(HardwareMap hwMap, Telemetry telemetry) {
        hardwareMap = hwMap;
        this.telemetry = telemetry;
        initMotors();
        initIMU();
        initSensors();
        driving = new Driving(lb, lf, rb, rf, drivingTicksPerInch, sideStrafingTicksPerInch, imu, angleError);
        turning = new Turning(imu, angleError, driving, telemetry);
        //vision = new NNVision(hardwareMap);
    }


    // --------------------------------------------------------------------------------------------
    // AUTO METHODS
    // --------------------------------------------------------------------------------------------

    public void infinityLoop() {
        try {
            turning.complexTurn(90);
            turning.complexTurn(180);
            turning.complexTurn(-90);
            turning.complexTurn(0);
        } catch (Exception ex) {
            telemetry.addData("ERROR", ex.getStackTrace());
            telemetry.update();
        }
    }

    public void redFoundationBridge() {
        driving.fullPowerDriveBackwards(40);

        // Find foundation
        while (!touchSensor.isPressed()) {
            driving.fullPowerDriveBackwards(1);
        }

        // Grab foundation
        foundationGrab(true);
        driving.driveBackwards(5, 0.5);
        try {
            wait(500);
        } catch (Exception ex) {
            handleException("Red Foundation Error");
        }
        // Driving foundation
        driving.fullPowerDriveForward(35); // was 25 but hit when trying to park
        turning.lazyTurn(75);

        // Park foundation
        foundationGrab(false);
        driving.fullPowerDriveBackwards(15);

        // Park robot
        driving.fullPowerDriveForward(35);
    }

    public void blueFoundationBridge() {
        driving.fullPowerDriveBackwards(40);

        // Find foundation
        while (!touchSensor.isPressed()) {
            driving.fullPowerDriveBackwards(1);
        }

        // Grab foundation
        foundationGrab(true);
        driving.driveBackwards(5, 0.5);
        try {
            wait(500);
        } catch (Exception ex) {
            handleException("Red Foundation Error");
        }
        // Driving foundation
        driving.fullPowerDriveForward(30);
        turning.lazyTurn(-75);

        // Park foundation
        foundationGrab(false);
        driving.fullPowerDriveBackwards(15);

        // Park robot
        driving.fullPowerDriveForward(35);
    }

    public void park(int inches) {
        telemetry.addData("Action", "Driving!");
        telemetry.update();
        driving.driveForward(inches, 0.7);
        telemetry.addData("Action", "Done Driving!");
    }

    // --------------------------------------------------------------------------------------------
    // SERVO METHODS
    // --------------------------------------------------------------------------------------------

    public void foundationGrab(Boolean grab) {
        if (grab) {
            lFoundation.setPosition(0);
            rFoundation.setPosition(1);
        } else {
            lFoundation.setPosition(1);
            rFoundation.setPosition(0);
        }
    }

    // --------------------------------------------------------------------------------------------
    // INITIALIZATION METHODS
    // --------------------------------------------------------------------------------------------

    /**
     * Initialize standard Hardware interfaces
     *
     * <p>
     *      The left wheels are initialized in reverse while the right are initialized as forward so the power/encoders are flipped for the left side
     * </p>
     */
    private void initMotors() {

        // Servos
        lFoundation = hardwareMap.servo.get("lFoundation");
        rFoundation = hardwareMap.servo.get("rFoundation");
        foundationGrab(false);

        // DcMotors
        lb = hardwareMap.dcMotor.get("ldb");
        lf = hardwareMap.dcMotor.get("ldf");
        rb = hardwareMap.dcMotor.get("rdb");
        rf = hardwareMap.dcMotor.get("rdf");

        //lf.setDirection(DcMotor.Direction.REVERSE);
        //lb.setDirection(DcMotor.Direction.REVERSE);

        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        resetEncoders();
    }

    private void resetEncoders() {
        // Reset encoder values
        lb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void initSensors() {
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
    }

    /**
     * Initializes the gyroscope
     */
    private void initIMU() {
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);

        while (angleError == 0) {
            try {
                wait(100);
            } catch (Exception ex) {
                //telemetry.addData("SYSTEM ERROR", "ERROR IN SYSTEM WAIT IN INITIMU");
            }

            Orientation tempAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            angleError = AngleUnit.DEGREES.fromUnit(tempAngles.angleUnit, tempAngles.firstAngle);
        }
    }

    // --------------------------------------------------------------------------------------------
    // OTHER
    // --------------------------------------------------------------------------------------------

    private void handleException(String message) {
        telemetry.addData("System Error", message);
        telemetry.update();
    }

    public void stop() {
        driving.stopWheels();
    }

    /**
     * Outputs the relevant information of the robot
     */
    public void printInfo() {
        telemetry.addData("Encoder Vals", driving.averageEncoderVal());
        telemetry.addData("Angle", driving.getGyroAngle());
        telemetry.addData("Error", driving.getAngleError());
        //telemetry.addData("X Acceleration", imu.getLinearAcceleration().xAccel);
        //telemetry.addData("Y Acceleration", imu.getLinearAcceleration().yAccel);
        //telemetry.addData("Z Acceleration", imu.getLinearAcceleration().zAccel);
        telemetry.update();
    }
}

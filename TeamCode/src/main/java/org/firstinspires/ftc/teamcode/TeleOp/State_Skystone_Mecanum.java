package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Timer;

/**
 * TODO: Use bumpers for turns and the arrows for tape measure
 *
 */
@TeleOp(name="State_Skystone_Mecanum")
public class State_Skystone_Mecanum extends LinearOpMode {
    DcMotor lf;
    DcMotor lb;
    DcMotor rf;
    DcMotor rb;

    DcMotor rIntake;
    DcMotor lIntake;

    DcMotor tapeLift;

    private BNO055IMU imu;
    //private BNO055IMU imu2;

    //Servo spinner;
    //Servo lFoundation;
    //Servo rFoundation;

    //Servo tapeRotate;

    private double angleError = 0; // NOT CURRENTLY SET

    // Error Variables
    private double turnAccuracyThreshold = 1; // Amount of degrees which the turn should be within
    private double longestTurnTime = 5; // 5 seconds is the longest time a turn can take

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime toggleTimer = new ElapsedTime();


    //doubles for motor power
    double fl = 0;
    double fr = 0;
    double bl = 0;
    double br = 0;

    int g = 0;
    int liftP = 0;
    int tape = 0;

    Boolean pressed = false;

    ElapsedTime t = new ElapsedTime();

    private double turningRegularFactor = 1;
    private double turningSlowingFactor = 0.6;

    boolean turning = false;
    double desiredAngle = 0;

    boolean fast = false;

    /*
     * Code that sets the power of the wheels on a mecanum drive train
     */
    public void mecanumDrive() {
        double lefty = 0;
        double leftx = 0;
        double rightx = 0;

        if (!fast) {
            leftx = gamepad1.left_stick_x * turningSlowingFactor;
            lefty = gamepad1.left_stick_y * turningSlowingFactor;
            rightx = gamepad1.right_stick_x * turningSlowingFactor;
            rightx = - rightx * turningSlowingFactor;
        } else {
            leftx = gamepad1.left_stick_x * turningRegularFactor;
            lefty = gamepad1.left_stick_y * turningRegularFactor;
            rightx = gamepad1.right_stick_x * turningRegularFactor;
            rightx = -rightx * turningRegularFactor;
        }

        fl = -(lefty-leftx+rightx);
        fr = -(lefty+leftx-rightx);
        bl = -(lefty+leftx+rightx);
        br = -(lefty-leftx-rightx);

        if (fl>1) fl=1;
        if (fr>1) fr=1;
        if (bl>1) bl=1;
        if (br>1) br=1;

        if (fast) {
            lf.setPower(fl);
            rf.setPower(fr);
            lb.setPower(bl);
            rb.setPower(br);
        } else {
            lf.setPower(fl);
            rf.setPower(fr);
            lb.setPower(bl);
            rb.setPower(br);
        }
    }

    private final double INTAKE_POWER = 0.7;
    /*
     * Code that sets the power of the intake
     */
    public void intake() {
        double lint = 0;
        double rint = 0;
        //sets for bringing it in
        if(gamepad1.right_trigger > 0) {
            rint = INTAKE_POWER;
            lint = -INTAKE_POWER;
        }
        //sets for pushing out
        else if(gamepad1.left_trigger > 0) {
            rint = -INTAKE_POWER;
            lint = INTAKE_POWER;
        }
        //If nothing motors stop
        else {
            rint = 0;
            lint = 0;
        }
        rIntake.setPower(rint);
        lIntake.setPower(lint);

        telemetry.addData("lIntake",lint);
        telemetry.addData("rIntake",rint);
    }

    /*public void foundationServo(){
        if(gamepad1.left_stick_button || gamepad2.left_stick_button){
            if(lFoundation.getPosition() == 1){
                lFoundation.setPosition(0);
                rFoundation.setPosition(1);
            }else{
                lFoundation.setPosition(1);
                rFoundation.setPosition(0);
            }
        }
        while (gamepad1.left_stick_button || gamepad2.left_stick_button);

    }*/

    private void tapeMeasure(){
        if (gamepad1.dpad_up) tapeLift.setPower(-1);
        else if (gamepad1.dpad_down) tapeLift.setPower(1);
        else tapeLift.setPower(0);
    }
/*
    private void RotateTheTape(){
        if(gamepad2.left_bumper)tapeRotate.setPosition(0.5);
        if(gamepad2.right_bumper)tapeRotate.setPosition(1.0);

    }

 */


    /*
     * Code to run ONCE when the driver hits INIT
     */
    public void initHardware() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        lb = hardwareMap.dcMotor.get("ldb");
        lf = hardwareMap.dcMotor.get("ldf");
        rb = hardwareMap.dcMotor.get("rdb");
        rf = hardwareMap.dcMotor.get("rdf");

        lf.setDirection(DcMotor.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.REVERSE);

        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        tapeLift = hardwareMap.dcMotor.get("tapeLift");
        tapeLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        tapeLift.setDirection(DcMotor.Direction.REVERSE);

        lIntake = hardwareMap.dcMotor.get("lIntake");
        rIntake = hardwareMap.dcMotor.get("rIntake");

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Ready");
    }

    /*
     Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        telemetry.addData("Init", "Ready!");
        waitForStart();

        while (opModeIsActive()) {
            intake();

            //autoTurn();

            if (gamepad1.a && toggleTimer.milliseconds() > 500) {
                fast = !fast;
                toggleTimer.reset();
                toggleTimer.startTime();
            }

            mecanumDrive();

            //foundationServo();

            tapeMeasure();

            if (fast) telemetry.addData("MODE: ", "Fast");
            else telemetry.addData("MODE: ", "Slow");

            telemetry.update();

            //telemetry.addData("rb", br);
            //telemetry.addData("rf", fr);
            //telemetry.addData("lb", bl);
            //telemetry.addData("lf", fl);
            //telemetry.addData("Heading Main", getGyroAngle());
            //telemetry.addData("tapeRotate", tapeRotate.getPosition());
            //telemetry.update();
        }

        /*Thread main = new Thread(new Runnable() {
            @Override
            public void run() {

                while (opModeIsActive()) {
                    intake();

                    autoTurn();

                    if (gamepad1.x || gamepad1.right_stick_button) mecanumDrive(false);
                    else mecanumDrive(true);

                    //foundationServo();

                    tapeMeasure();

                    //telemetry.addData("rb", br);
                    //telemetry.addData("rf", fr);
                    //telemetry.addData("lb", bl);
                    //telemetry.addData("lf", fl);
                    //telemetry.addData("Heading Main", getGyroAngle());
                    //telemetry.addData("tapeRotate", tapeRotate.getPosition());
                    //telemetry.update();
                }
            }
        });


        Thread angle = new Thread(new Runnable() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    if (turning) {
                        if (!(getGyroAngle() < desiredAngle + turnAccuracyThreshold && getGyroAngle() > desiredAngle - turnAccuracyThreshold)) {
                            turning = false;
                        }
                    }
                }
            }
        });

        main.start();
        angle.start();

        main.join();
        angle.join();*/
    }

    /**
     * Sets the power of the wheels to spin the same direction
     *
     * <p>
     *     The left wheels have a negative power input because their motors are flipped in the
     *     physical robot and thus turn opposite of the right wheels
     * </p>
     *
     * @param power The maximum power given to the wheels
     */
    public void setWheelPower(double power) {
        lb.setPower(-power);
        lf.setPower(-power);
        rf.setPower(power);
        rb.setPower(power);
    }

    /**
     * Sets the power of the wheels compensating for the left side having flipped motors
     * @param lbPower The power for the left back wheel
     * @param lfPower The power for the left front wheel
     * @param rfPower The power for the right front wheel
     * @param rbPower The power for the right back wheel
     */
    public void setWheelPower(double lbPower, double lfPower, double rfPower, double rbPower) {
        lb.setPower(-lbPower);
        lf.setPower(-lfPower);
        rf.setPower(rfPower);
        rb.setPower(rbPower);
    }


    /**
     * Stops the wheels by cutting off power given to them
     */
    public void stopWheels() {
        setWheelPower(0);
    }
}
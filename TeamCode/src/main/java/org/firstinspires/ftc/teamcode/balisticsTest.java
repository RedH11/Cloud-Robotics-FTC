package org.firstinspires.ftc.teamcode;


import static java.lang.Math.abs;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotMotion.BalisticProfile;
import org.firstinspires.ftc.teamcode.RobotMotion.BalisticProfile.*;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;


@Autonomous(name="balisticsTest", group="Linear Opmode")

public class balisticsTest extends LinearOpMode {

    // Declare OpMode members.
    double fl = 0;
    double fr = 0;
    double bl = 0;
    double br = 0;

    int ldfP = 0;
    int ldbP = 0;
    int rdfP = 0;
    int rdbP = 0;

    DcMotor ldf;
    DcMotor ldb;
    DcMotor rdf;
    DcMotor rdb;

    DcMotor rIntake;
    DcMotor lIntake;

    DcMotor lift;

    Servo lGrabber;
    Servo rGrabber;
    Servo lFoundation;
    Servo rFoundation;

    ColorSensor cs;

    //angles it accelerates
    double acceleration_distance= 15;
    double deceleration_distance=60;
    double start_power=.1;
    double full_power=.6;
    double end_power=0;
    CURVE_TYPE acceleration_curve = CURVE_TYPE.SINUSOIDAL_NORMAL;
    CURVE_TYPE deceleration_curve = CURVE_TYPE.SINUSOIDAL_NORMAL;

    private BalisticProfile rotationBalisticProfile = new BalisticProfile(acceleration_distance, deceleration_distance, start_power, full_power,end_power, acceleration_curve, deceleration_curve);

    BNO055IMU imu;

    Orientation lastAngles = new Orientation();
    double globalAngle;

    private ElapsedTime t = new ElapsedTime();

    //ColorSensor csr;

    //creating a global variable that will get the color values
    int green = 0;
    int red = 0;
    int blue = 0;

    public void mecanumDrive(double lefty, double leftx, double rightx) {

        fl = lefty-leftx+rightx;
        fr = lefty+leftx-rightx;
        bl = lefty+leftx+rightx;
        br = lefty-leftx-rightx;

        if(fl>1)fl=1;
        if(fr>1)fr=1;
        if(bl>1)bl=1;
        if(br>1)br=1;
    }

    public void driveright(double inches, double power){
        ldf.setTargetPosition((int)(inches * 1120) + ldf.getCurrentPosition());
        ldf.setPower(power);
        ldb.setTargetPosition((int)(-inches * 1120) + ldb.getCurrentPosition());
        ldb.setPower(power);
        rdb.setTargetPosition((int)(inches * 1120) + rdb.getCurrentPosition());
        rdb.setPower(power);
        rdf.setTargetPosition((int)(-inches * 1120) + rdf.getCurrentPosition());
        rdf.setPower(power);
        while ((opModeIsActive() && ((ldf.isBusy()) || (ldb.isBusy()) || (rdf.isBusy()) || ( rdb.isBusy())))) {
            telemetry.addData("ldf encoder-fwd", ldf.getCurrentPosition() + "  busy=" + ldf.isBusy());
            telemetry.addData("ldb encoder-fwd", ldb.getCurrentPosition() + "  busy=" + ldb.isBusy());
            telemetry.addData("rdf encoder-fwd", rdf.getCurrentPosition() + "  busy=" + rdf.isBusy());
            telemetry.addData("rdb encoder-fwd", rdb.getCurrentPosition() + "  busy=" + rdb.isBusy());
            telemetry.update();
        }
    }

    public void driveleft(double inches, double power){
        ldf.setTargetPosition((int)(-inches * 1120) + ldf.getCurrentPosition());
        ldf.setPower(power);
        ldb.setTargetPosition((int)(inches * 1120) + ldb.getCurrentPosition());
        ldb.setPower(power);
        rdb.setTargetPosition((int)(-inches * 1120) + rdb.getCurrentPosition());
        rdb.setPower(power);
        rdf.setTargetPosition((int)(inches * 1120) + rdf.getCurrentPosition());
        rdf.setPower(power);
        while ((opModeIsActive() && ((ldf.isBusy()) || (ldb.isBusy()) || (rdf.isBusy()) || ( rdb.isBusy())))) {
            telemetry.addData("ldf encoder-fwd", ldf.getCurrentPosition() + "  busy=" + ldf.isBusy());
            telemetry.addData("ldb encoder-fwd", ldb.getCurrentPosition() + "  busy=" + ldb.isBusy());
            telemetry.addData("rdf encoder-fwd", rdf.getCurrentPosition() + "  busy=" + rdf.isBusy());
            telemetry.addData("rdb encoder-fwd", rdb.getCurrentPosition() + "  busy=" + rdb.isBusy());
            telemetry.update();
        }
    }

    public void driveForward(double inches, double power){
        ldf.setTargetPosition((int)(inches * 94) + ldf.getCurrentPosition());
        ldf.setPower(power);
        ldb.setTargetPosition((int)(inches * 94) + ldb.getCurrentPosition());
        ldb.setPower(power);
        rdb.setTargetPosition((int)(inches * 94) + rdb.getCurrentPosition());
        rdb.setPower(power);
        rdf.setTargetPosition((int)(inches * 94) + rdf.getCurrentPosition());
        rdf.setPower(power);
        while ((opModeIsActive() && ((ldf.isBusy()) || (ldb.isBusy()) || (rdf.isBusy()) || ( rdb.isBusy())))) {
            telemetry.addData("ldf encoder-fwd", ldf.getCurrentPosition() + "  busy=" + ldf.isBusy());
            telemetry.addData("ldb encoder-fwd", ldb.getCurrentPosition() + "  busy=" + ldb.isBusy());
            telemetry.addData("rdf encoder-fwd", rdf.getCurrentPosition() + "  busy=" + rdf.isBusy());
            telemetry.addData("rdb encoder-fwd", rdb.getCurrentPosition() + "  busy=" + rdb.isBusy());
            colorTest();
            telemetry.update();
        }
    }

    public void driveBack(double inches, double power){
        ldf.setTargetPosition((int)(-inches * 94) + ldf.getCurrentPosition());
        ldf.setPower(power);
        ldb.setTargetPosition((int)(-inches * 94) + ldb.getCurrentPosition());
        ldb.setPower(power);
        rdb.setTargetPosition((int)(-inches * 94) + rdb.getCurrentPosition());
        rdb.setPower(power);
        rdf.setTargetPosition((int)(-inches * 94) + rdf.getCurrentPosition());
        rdf.setPower(power);
        while ((opModeIsActive() && ((ldf.isBusy()) || (ldb.isBusy()) || (rdf.isBusy()) || ( rdb.isBusy())))) {
            telemetry.addData("ldf encoder-fwd", ldf.getCurrentPosition() + "  busy=" + ldf.isBusy());
            telemetry.addData("ldb encoder-fwd", ldb.getCurrentPosition() + "  busy=" + ldb.isBusy());
            telemetry.addData("rdf encoder-fwd", rdf.getCurrentPosition() + "  busy=" + rdf.isBusy());
            telemetry.addData("rdb encoder-fwd", rdb.getCurrentPosition() + "  busy=" + rdb.isBusy());
            telemetry.update();
        }
    }

    public void turn90r( double power, double times){
        int distance = 2100;
        distance = (int)(distance * times);
        ldf.setTargetPosition((distance) + ldf.getCurrentPosition());
        ldf.setPower(power);
        ldb.setTargetPosition((distance) + ldb.getCurrentPosition());
        ldb.setPower(power);
        rdb.setTargetPosition((-distance) + rdb.getCurrentPosition());
        rdb.setPower(power);
        rdf.setTargetPosition((-distance) + rdf.getCurrentPosition());
        rdf.setPower(power);
        while ((opModeIsActive() && ((ldf.isBusy()) || (ldb.isBusy()) || (rdf.isBusy()) || ( rdb.isBusy())))) {
            telemetry.addData(" Gyro X",getAngle());
            telemetry.addData("ldf encoder-fwd", ldf.getCurrentPosition() + "  busy=" + ldf.isBusy());
            telemetry.addData("ldb encoder-fwd", ldb.getCurrentPosition() + "  busy=" + ldb.isBusy());
            telemetry.addData("rdf encoder-fwd", rdf.getCurrentPosition() + "  busy=" + rdf.isBusy());
            telemetry.addData("rdb encoder-fwd", rdb.getCurrentPosition() + "  busy=" + rdb.isBusy());
            telemetry.update();
        }
    }
    public void turn90l( double power, double times){
        int distance = 2100;
        distance = (int)(distance * times);
        ldf.setTargetPosition((-distance) + ldf.getCurrentPosition());
        ldf.setPower(power);
        ldb.setTargetPosition((-distance) + ldb.getCurrentPosition());
        ldb.setPower(power);
        rdb.setTargetPosition((distance) + rdb.getCurrentPosition());
        rdb.setPower(power);
        rdf.setTargetPosition((distance) + rdf.getCurrentPosition());
        rdf.setPower(power);
        while ((opModeIsActive() && ((ldf.isBusy()) || (ldb.isBusy()) || (rdf.isBusy()) || ( rdb.isBusy())))) {
            telemetry.addData("ldf encoder-fwd", ldf.getCurrentPosition() + "  busy=" + ldf.isBusy());
            telemetry.addData("ldb encoder-fwd", ldb.getCurrentPosition() + "  busy=" + ldb.isBusy());
            telemetry.addData("rdf encoder-fwd", rdf.getCurrentPosition() + "  busy=" + rdf.isBusy());
            telemetry.addData("rdb encoder-fwd", rdb.getCurrentPosition() + "  busy=" + rdb.isBusy());
            telemetry.update();
        }
    }

    public void foundationGrab(Boolean grb) {
        if (grb) {
            lFoundation.setPosition(0);
            rFoundation.setPosition(1);

        } else {
            lFoundation.setPosition(1);
            rFoundation.setPosition(0);
        }
    }

    public void colorTest(){
        red = cs.red();
        green = cs.green();
        blue = cs.blue();
        telemetry.addData("Blue", blue);
        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.update();
    }

    public void intake(){

    }

    public void initIMU(){
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        imu.initialize(parameters);
    }

    private double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }


    public void turnToDegreeFast(double desiredEndRotation) {

        double startRotation = getAngle();
        double currentRotation;
        double motorPower;

        rotationBalisticProfile.setCurve(startRotation, desiredEndRotation);

        while ((! rotationBalisticProfile.isDone() || imu.getAngularVelocity().xRotationRate >
                rotationBalisticProfile.getEnd_power()
                        * 90)
                && opModeIsActive()) {

            currentRotation = getAngle();

            rotationBalisticProfile.setCurrentPosition(currentRotation);
            motorPower = rotationBalisticProfile.getCurrentPowerAccelDecel();
            rotMecanumPowerDrive(motorPower);

            telemetry.addLine("Motor Power:" + motorPower);
            telemetry.addLine("Robot Angle:" + getAngle());
            telemetry.addLine("Targer Angle:" + desiredEndRotation);
            telemetry.update();
        }

        rotMecanumPowerDrive(0);
    }

    private void mecanumPowerDrive(double forward, double rotation){
        ldb.setPower(forward - rotation);
        ldf.setPower(forward - rotation);
        rdb.setPower(forward + rotation);
        rdf.setPower(forward + rotation);
    }

    private void rotMecanumPowerDrive(double power){
        mecanumPowerDrive(0, power);
    }

    private void strMecanumPowerDrive(double power){
        mecanumPowerDrive(power, 0);
    }

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    private void encodersOff() {
        ldb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ldf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rdb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rdf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    private void encodersOn() {
        ldb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ldf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rdb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rdf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    public void initialization(){
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        ldb = hardwareMap.dcMotor.get("ldb");
        ldb.setDirection(DcMotor.Direction.REVERSE);
        ldb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ldb.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        ldf = hardwareMap.dcMotor.get("ldf");
        ldf.setDirection(DcMotor.Direction.REVERSE);
        ldf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ldf.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        rdb = hardwareMap.dcMotor.get("rdb");
        rdb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rdb.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        rdf = hardwareMap.dcMotor.get("rdf");
        rdf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rdf.setMode(DcMotor.RunMode.RUN_TO_POSITION);



        lIntake = hardwareMap.dcMotor.get("lIntake");


        rIntake = hardwareMap.dcMotor.get("rIntake");


        lift = hardwareMap.dcMotor.get("lift");
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        lGrabber = hardwareMap.servo.get("lGrabber");
        rGrabber = hardwareMap.servo.get("rGrabber");

        lFoundation = hardwareMap.servo.get("lFoundation");
        rFoundation = hardwareMap.servo.get("rFoundation");



        cs = hardwareMap.colorSensor.get("cs");
        cs.enableLed(true);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        initIMU();
        resetAngle();
        telemetry.addData("Say", "Are you ready kids?");
        telemetry.update();
    }

    public void runOpMode(){
        initialization();

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            ldb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            ldf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rdb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rdf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            encodersOff();
            turnToDegreeFast(270);
            encodersOn();

            stop();
        }
    }
}
package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="VelocityDrive")
public class VelocityDrive extends LinearOpMode {
    DcMotorEx ldf;
    DcMotorEx ldb;
    DcMotorEx rdf;
    DcMotorEx rdb;

    DcMotorEx rIntake;
    DcMotorEx lIntake;


    DcMotor tapeLift;

    //Servo spinner;
    Servo lFoundation;
    Servo rFoundation;

    //CRServo tapeRotate;


    //doubles for motor power
    double fl = 0;
    double fr = 0;
    double bl = 0;
    double br = 0;

    double ticks = 2010;
    double intakeTicks = 1650;

    double DriveSpeed = 1;

    int g = 0;
    int liftP = 0;
    int tape = 0;

    Boolean pressed = false;

    ElapsedTime t = new ElapsedTime();

    /*
     * Code that sets the power of the wheels on a mecanum drive train
     * public void mecanumDrive() {
        double lefty = 0;
        double leftx = 0;
        double rightx = 0;

        leftx = gamepad1.left_stick_x;
        lefty = gamepad1.left_stick_y;
        rightx = gamepad1.right_stick_x;
        rightx = - rightx;

        fl = -(lefty-leftx+rightx);
        fr = -(lefty+leftx-rightx);
        bl = -(lefty+leftx+rightx);
        br = -(lefty-leftx-rightx);

        if(fl>1)fl=1;
        if(fr>1)fr=1;
        if(bl>1)bl=1;
        if(br>1)br=1;

        DSToggle();
        if(gamepad1.right_bumper){
            fl = -1;
            fr = 1;
            bl = 1;
            br = -1;
        }else if(gamepad1.left_bumper){
            fl = 1;
            fr = -1;
            bl = -1;
            br = 1;
        }
        ldf.setPower(fl*DriveSpeed);
        rdf.setPower(fr*DriveSpeed);
        ldb.setPower(bl*DriveSpeed);
        rdb.setPower(br*DriveSpeed);
    }
     */
    public void newMecanumDrive() {
        double lefty = 0;
        double leftx = 0;
        double rightx = 0;

        leftx = gamepad1.left_stick_x;
        lefty = gamepad1.left_stick_y;
        rightx = gamepad1.right_stick_x;
        rightx = - rightx;

        fl = -(lefty-leftx+rightx);
        fr = -(lefty+leftx-rightx);
        bl = -(lefty+leftx+rightx);
        br = -(lefty-leftx-rightx);

        if(fl>1)fl=1;
        if(fr>1)fr=1;
        if(bl>1)bl=1;
        if(br>1)br=1;

        DSToggle();
        if(gamepad1.right_bumper){
            fl = -1;
            fr = 1;
            bl = 1;
            br = -1;
        }else if(gamepad1.left_bumper){
            fl = 1;
            fr = -1;
            bl = -1;
            br = 1;
        }

        //ldf.setPower(-fl*DriveSpeed);
        //rdf.setPower(fr*DriveSpeed);
        //ldb.setPower(-bl*DriveSpeed);
        //rdb.setPower(br*DriveSpeed);

        ldf.setVelocity(-fl*ticks*DriveSpeed);
        ldb.setVelocity(-bl*ticks*DriveSpeed);
        rdf.setVelocity(fr*ticks*DriveSpeed);
        rdb.setVelocity(br*ticks*DriveSpeed);
    }

    /*
     * Code that sets the power of the intake
     */
    public void intake() {
        int lint = 0;
        int rint = 0;
        //sets for bringing it in
        if(gamepad1.right_trigger > 0){
            rint = 1;
            lint = -1;
        }
        //sets for pushing out
        else if(gamepad1.left_trigger > 0){
            rint = -1;
            lint = 1;
        }
        //If nothing motors stop
        else{
            rint = 0;
            lint = 0;
        }
        rIntake.setVelocity(intakeTicks*rint);
        lIntake.setVelocity(intakeTicks*lint);

    }

    private void tapeMeasure(){
        if(gamepad1.dpad_up) tapeLift.setPower(1);
        else if(gamepad1.dpad_down) tapeLift.setPower(-1);
        else tapeLift.setPower(0);
    }

/*
    private void RotateTheTape(){
        if(gamepad2.left_bumper){
            tapeRotate.setPower(0);
        }
        else if(gamepad2.right_bumper){
            tapeRotate.setPower(-1);

        }
        else tapeRotate.setPower(-0.5);
    }

 */

    public void DSToggle(){
        if(gamepad1.y){
            if(DriveSpeed == 1)DriveSpeed=0.25;
            else DriveSpeed = 1;
            while(gamepad1.y);
        }

    }


    /*
     * Code to run ONCE when the driver hits INIT
     */
    public void initHardware() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        ldb = (DcMotorEx)hardwareMap.dcMotor.get("ldb");
        ldf = (DcMotorEx)hardwareMap.dcMotor.get("ldf");
        rdb = (DcMotorEx)hardwareMap.dcMotor.get("rdb");
        rdf = (DcMotorEx)hardwareMap.dcMotor.get("rdf");

        ldb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        ldf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rdb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rdf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        tapeLift = hardwareMap.dcMotor.get("tapeLift");

        lIntake = (DcMotorEx) hardwareMap.dcMotor.get("lIntake");
        rIntake = (DcMotorEx) hardwareMap.dcMotor.get("rIntake");

        //tapeRotate = hardwareMap.crservo.get("tapeRotate");


        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Are you ready kids?");
    }

    /*
     Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void runOpMode() {
        initHardware();

        waitForStart();
        while(opModeIsActive()) {
            intake();

            newMecanumDrive();

            tapeMeasure();

            //RotateTheTape();
            if(gamepad1.x)intakeTicks++;
            if(gamepad1.b)intakeTicks--;


            telemetry.addData("Drive Speed = ", DriveSpeed);
            telemetry.addData("Ticks", ticks);
            telemetry.addData("Intake Ticks", intakeTicks);
            telemetry.addData("rdb", br*DriveSpeed*ticks);
            telemetry.addData("rdf", fr*DriveSpeed*ticks);
            telemetry.addData("ldb", bl*DriveSpeed*ticks);
            telemetry.addData("ldf", fl*DriveSpeed*ticks);
            telemetry.update();
        }

    }


}



package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Only Driving/Intake")
public class OnlyDriving extends LinearOpMode {

    DcMotor ldf;
    DcMotor ldb;
    DcMotor rdf;
    DcMotor rdb;

    DcMotor rIntake;
    DcMotor lIntake;

    //doubles for motor power
    double fl = 0;
    double fr = 0;
    double bl = 0;
    double br = 0;

    /*
     * Code that sets the power of the wheels on a mecanum drive train
     */
    public void mecanumDrive() {
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

        ldf.setPower(fl);
        rdf.setPower(fr);
        ldb.setPower(bl);
        rdb.setPower(br);
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
        rIntake.setPower(rint);
        lIntake.setPower(lint);

        telemetry.addData("lIntake",lint);
        telemetry.addData("rIntake",rint);
    }

    /*
     * Code to run ONCE when the driver hits INIT
     */
    public void initHardware() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        ldb = hardwareMap.dcMotor.get("ldb");
        ldf = hardwareMap.dcMotor.get("ldf");
        rdb = hardwareMap.dcMotor.get("rdb");
        rdf = hardwareMap.dcMotor.get("rdf");

        ldf.setDirection(DcMotor.Direction.REVERSE);
        ldb.setDirection(DcMotor.Direction.REVERSE);

        ldb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        ldf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rdb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rdf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        lIntake = hardwareMap.dcMotor.get("lIntake");
        rIntake = hardwareMap.dcMotor.get("rIntake");

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


        while (opModeIsActive()) {
            intake();

            mecanumDrive();

            telemetry.addData("rdb", br);
            telemetry.addData("rdf", fr);
            telemetry.addData("ldb", bl);
            telemetry.addData("ldf", fl);
            //telemetry.addData("tapeRotate", tapeRotate.getPosition());
            telemetry.update();
        }
    }
}

package org.firstinspires.ftc.teamcode.Competition.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Part Testing")
public class PartTestOp extends LinearOpMode {

    DcMotor motor;
    Servo servo;

    private boolean motorOn = false;
    private boolean servoOn = false;

    @Override
    public void runOpMode() throws InterruptedException {
        printText("Say", "Initializing...");
        initParts();
        printText("Say", "Ready!");
        waitForStart();

        while (opModeIsActive()) {
            readControls();
            if (isStopRequested()) break;
        }
    }

    private void initParts() {
        // DcMotor
        motor = hardwareMap.dcMotor.get("motor");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //lf.setDirection(DcMotor.Direction.REVERSE);

        // Servo
        servo = hardwareMap.servo.get("servo");
    }

    private void readControls() {
        if (gamepad1.right_bumper) runMotor();

        if (gamepad1.left_bumper) runMotorBackwards();

        if (!gamepad1.right_bumper && !gamepad1.left_bumper) stopMotor();

        if (gamepad1.a) runServo();
        else stopServo();
    }

    private void runMotor() {
        if (!motorOn) motor.setPower(.7);
        motorOn = true;
    }

    private void runMotorBackwards() {
        if (!motorOn) motor.setPower(-0.7);
        motorOn = false;
    }

    private void stopMotor() {
        if (motorOn) motor.setPower(0);
        motorOn = false;
    }

    private void runServo() {
        if (!servoOn) servo.setPosition(1);
        servoOn = true;
    }

    private void stopServo() {
        if (servoOn) servo.setPosition(0);
        servoOn = false;
    }


    private void printText(String caption, String message) {
        telemetry.addData(caption, message);
        telemetry.update();
    }
}

package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotUtils.BlueCoords;
import org.firstinspires.ftc.teamcode.Tools.RobotTools;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREV;

@Autonomous(group = "test", name = "Two Block Delivery")
public class TwoBlockDeliver extends LinearOpMode {

    SampleMecanumDriveBase drive;
    RobotTools tools;

    BlueCoords coords = new BlueCoords();

    private int blockNum = 1;

    private double offset = 0.0;

    ElapsedTime timer;

    @Override
    public void runOpMode() throws InterruptedException {
        printTel("Init", "Starting Initialization...");
        initialize();
        printTel("Init", "Ready!");

        /*while (!gamepad1.a && !isStarted()) {

            if (timer.milliseconds() > 500) {

                if (gamepad1.dpad_up) {
                    if (blockNum < 6) blockNum++;
                } else if (gamepad1.dpad_down) {
                    if (blockNum > 0) blockNum--;
                } else if (gamepad1.dpad_right) {
                    offset += .5;
                } else if (gamepad1.dpad_left) {
                    offset -= .5;
                }

                timer.reset();
                timer.startTime();
            }

            telemetry.addData("Block Num", blockNum);
            telemetry.addData("Offset", offset);
            telemetry.update();
        }*/

        waitForStart();

        act();

        /*while (opModeIsActive()) {

            if (isStopRequested()) break;
        }*/
    }

    private void initialize() {
        drive = new SampleMecanumDriveREV(hardwareMap);
        tools = new RobotTools(hardwareMap);
        timer = new ElapsedTime();
        timer.startTime();
    }

    private void act() {

        drive.setPoseEstimate(new Pose2d(0, 0, 0));

        tools.intake(true, 0.7);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.blockCoords.get(5))
                        .build()
        );

        tools.intake(false, 0);


        // Deliver the block
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(coords.underBridgeBackwardBridge)
                        .splineTo(coords.bridgeDeliveryBackward)
                        .build()
        );

        tools.intake(true, 0.7);

        sleep(2000);

        // Get another block (reset for now)
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(new Pose2d(0, 0, 0))
                        .build()
        );



        tools.intake(false, 0);

    }

    private void printTel(String caption, String message) {
        telemetry.addData(caption, message);
        telemetry.update();
    }
}


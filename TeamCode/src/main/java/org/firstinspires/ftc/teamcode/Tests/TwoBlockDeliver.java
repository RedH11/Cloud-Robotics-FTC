package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotUtils.RedCoords;
import org.firstinspires.ftc.teamcode.Tools.RobotTools;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREV;

@Autonomous(group = "test", name = "Two Block Delivery")
public class TwoBlockDeliver extends LinearOpMode {

    SampleMecanumDriveBase drive;
    RobotTools tools;

    RedCoords coords = new RedCoords();

    private int blockNum = 5;
    private int deliveredStoneCount = 0;

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

        Thread angle = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!opModeIsActive()) {
                    telemetry.addData("Angle", drive.getExternalHeading());
                    telemetry.update();

                    if (isStopRequested()) break;
                }
            }
        });

        Thread acting = new Thread(new Runnable() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    act();
                    if (!isStopRequested()) break;
                }
            }
        });

        angle.start();
        acting.start();

        while (opModeIsActive()) {}
    }

    private void initialize() {
        drive = new SampleMecanumDriveREV(hardwareMap);
        tools = new RobotTools(hardwareMap);
        timer = new ElapsedTime();
        timer.startTime();
    }

    private void act() {

        drive.setPoseEstimate(new Pose2d(0, 0, 0));

        getSkyStones(5);

        getBlock(1);
        getBlock(2);
        getBlock(3);
    }

    private void getSkyStones(int blockNum) {
        tools.intake(true, 0.7);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.initialBlockCoords.get(blockNum))
                        .build()
        );

        tools.intake(false, 0);

        deliverBlock();

        tools.intake(true, 0.7);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.initialBlockCoords.get(blockNum - 3))
                        .build()
        );

        tools.intake(false, 0);

        deliverBlock();
    }

    private void getBlock(int blockNum) {
        tools.intake(true, 0.7);

        if (blockNum == 4) {
            drive.followTrajectorySync(
                    drive.trajectoryBuilder()
                            .splineTo(coords.post4coords.get(deliveredStoneCount + 1))
                            .build()
            );
        } else if (blockNum == 5) {
            drive.followTrajectorySync(
                    drive.trajectoryBuilder()
                            .splineTo(coords.post5coords.get(deliveredStoneCount + 1))
                            .build()
            );
        } else if (blockNum == 6) {
            drive.followTrajectorySync(
                    drive.trajectoryBuilder()
                            .splineTo(coords.post6coords.get(deliveredStoneCount + 1))
                            .build()
            );
        }

        tools.intake(false, 0);

        deliveredStoneCount++;

        deliverBlock();
    }

    private void deliverBlock() {
        // Deliver block
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        //.splineTo(coords.underBridgeBackwardBridge)
                        .splineTo(coords.bridgeDeliveryBackward)
                        .build()
        );

        tools.intake(true, 1);

        sleep(300);
    }

    private void printTel(String caption, String message) {
        telemetry.addData(caption, message);
        telemetry.update();
    }
}


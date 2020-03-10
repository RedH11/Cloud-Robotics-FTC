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

    private int blockNum = 4;
    private int deliveredStoneCount = 0;

    ElapsedTime timer;

    // DRIVING VARIABLES
    private final int strafeDist = 13;

    @Override
    public void runOpMode() throws InterruptedException {
        printTel("Init", "Starting Initialization...");
        initialize();
        printTel("Init", "Ready!");

        while (!gamepad1.a && !isStarted()) {

            if (timer.milliseconds() > 500) {

                if (gamepad1.dpad_up) {
                    if (blockNum < 6) blockNum++;
                } else if (gamepad1.dpad_down) {
                    if (blockNum > 4) blockNum--;
                }

                timer.reset();
                timer.startTime();
            }

            telemetry.addData("Block Num", blockNum);
            telemetry.update();
        }

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

        //angle.start();

        while (opModeIsActive()) {
            act();
            stop();
            if(isStopRequested())stop();
        }
    }

    private void initialize() {
        drive = new SampleMecanumDriveREV(hardwareMap);
        tools = new RobotTools(hardwareMap);
        timer = new ElapsedTime();
        timer.startTime();
    }

    private void act() {

        drive.setPoseEstimate(new Pose2d(0, 0, 0));

        getSkyStones(blockNum);

        parkBot();

        //getBlock(1);
        //getBlock(2);
        //getBlock(3);
    }

    private void getSkyStones(int blockNum) {
        switch (blockNum) {
            case (4):
                pos4Actions();
                break;
            case (5):
                pos5Actions();
                break;
            case (6):
                pos6Actions();
                break;
        }
    }

    private void pos4Actions() {
        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.initialBlockCoords.get(blockNum))
                        .build()
        );

        tools.intake(false);

        deliverBlock();

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.underBridgeForwardBridge)
                        .splineTo(coords.skyStone4StrafeReady)
                        .build()
        );

        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .strafeRight(strafeDist) // 15 lower volt 8 for higher volt
                        .forward(10)
                        .build()
        );

        tools.intake(false);

        postStrafeDelivery();

        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.lastStone4)
                        .build()
        );

        tools.intake(false);

        lastDelivery();

        parkBot();
    }

    private void pos5Actions() {
        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.initialBlockCoords.get(blockNum))
                        .build()
        );

        tools.intake(false);

        deliverBlock();

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.underBridgeForwardBridge)
                        .splineTo(coords.skyStone5StrafeReady)
                        .build()
        );

        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .strafeRight(strafeDist)
                        .forward(10)
                        .build()
        );

        tools.intake(false);

        postStrafeDelivery();

        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.lastStone5)
                        .build()
        );

        tools.intake(false);

        lastDelivery();

        parkBot();
    }

    private void pos6Actions() {
        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.initialBlockCoords.get(blockNum))
                        .build()
        );

        tools.intake(false);

        deliverBlock();

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.underBridgeForwardBridge)
                        .splineTo(coords.skyStone6StrafeReady)
                        .build()
        );

        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .strafeRight(strafeDist)
                        .forward(10)
                        .build()
        );

        tools.intake(false);

        postStrafeDelivery();

        tools.intake(true);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.lastStone6)
                        .build()
        );

        tools.intake(false);

        lastDelivery();

        parkBot();
    }

    private void deliverBlock() {
        // Deliver block
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(coords.underBridgeBackwardBridge)
                        .splineTo(coords.bridgeDeliveryBackward)
                        .build()
        );

        tools.intake(true);

        sleep(200); // 200 on higher voltage 400 on lower

        tools.intake(false);

    }

    private void postStrafeDelivery() {
        // Deliver block
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(coords.underBridgeBackwardBridgePostStrafe)
                        .splineTo(coords.bridgeDeliveryBackwardPostStrafe)
                        .build()
        );

        tools.intake(true);

        sleep(600); // 600 on higher voltage 800 on lower

        tools.intake(false);
    }

    private void lastDelivery() {
        // Deliver block
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(coords.lastUnderBridgeBackwardBridge)
                        .splineTo(coords.lastDelivery)
                        .build()
        );

        tools.intake(true);

        sleep(400); // 400 on higher voltage 600 on lower

        tools.intake(false);
    }

    private void parkBot() {
        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.parkBridge)
                        .build()
        );
    }

    private void printTel(String caption, String message) {
        telemetry.addData(caption, message);
        telemetry.update();
    }

    private void printLocation() {
        printTel("Ubicación", drive.getPoseEstimate().getX() + ", " + drive.getPoseEstimate().getY());
        sleep(5000);
    }
}


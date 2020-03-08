package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotUtils.BlueCoords;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREV;

@Autonomous(group = "test", name = "Two Block Delivery")
public class TwoBlockDeliver extends LinearOpMode {

    SampleMecanumDriveBase drive;

    BlueCoords coords = new BlueCoords();

    @Override
    public void runOpMode() throws InterruptedException {
        printTel("Init", "Starting Initialization...");
        initialize();
        printTel("Init", "Ready!");
        waitForStart();

        while (opModeIsActive()) {
            act();
            if (isStopRequested()) break;
        }
    }

    private void initialize() {
        drive = new SampleMecanumDriveREV(hardwareMap);
    }

    private void act() {

        drive.setPoseEstimate(new Pose2d(0, 0, 0));

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .splineTo(coords.blockCoords.get(1))
                        .build()
        );

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(new Pose2d(0, 0, 0))
                        .build()
        );
    }

    private void printTel(String caption, String message) {
        telemetry.addData(caption, message);
        telemetry.update();
    }
}

/*
        Notes:

        Y Axis is between the sign for no shoes and the pillar
        Negative y axis when facing away from the phone chargers puts it closer to the no shoes sign
        Positive x axis goes forward
        90 degrees is a full half rotation

        (In terms of the original heading)
        -Y: Robot towards right
        Y: Robot towards left
        X: Forwards
        -X: Backwards
         */


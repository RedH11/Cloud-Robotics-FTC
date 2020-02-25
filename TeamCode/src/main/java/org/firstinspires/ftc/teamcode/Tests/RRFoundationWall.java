package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Pose2dKt;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotClasses.MainRobot;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveMR;

import static org.firstinspires.ftc.teamcode.drive.DriveConstants.kA;
import static org.firstinspires.ftc.teamcode.drive.DriveConstants.kStatic;
import static org.firstinspires.ftc.teamcode.drive.DriveConstants.kV;

@Autonomous(name="RoadRunner Foundation Wall", group="Linear Opmode")
public class RRFoundationWall extends LinearOpMode {

    private MainRobot robot;
    private Runnable info;
    private Runnable main;
    private Thread infoThread;
    private Thread mainThread;

    // PIDF Variables
    private double kP = 0;
    private double kI = 0;
    private double kD = 0;
    private PIDCoefficients coeffs;
    private PIDFController controller;
    private double correction;
    private double kG; // a constant factor for the voltage that is sent to the motors
    // https://acme-robotics.gitbook.io/road-runner/tour/feedforward-control

    // Roadrunner variables
    private DriveConstraints constraints = new DriveConstraints(20, 40, 80, 1, 2, 4);
    //private Trajectory traj = TrajectoryGenerator.generateTrajectory(path, constraints);


    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("Say", "Initializing...");
        telemetry.update();
        SampleMecanumDriveMR driveBase = new SampleMecanumDriveMR(hardwareMap, telemetry);
        telemetry.addData("Say", "Ready");
        telemetry.update();
        waitForStart();


        driveBase.setPoseEstimate(new Pose2d(0, 0, 0));

        // Simulate grabbing a block
        driveBase.followTrajectorySync(
                driveBase.trajectoryBuilder()
                        .splineTo(new Pose2d(40, 5, Math.PI/16))
                        ///.lineTo(new Pose2d())
                        .build()
        );

        // Get to the blue foundation (by driving close to the wall
        driveBase.followTrajectorySync(
                driveBase.trajectoryBuilder()
                        .reverse()
                        .splineTo(new Pose2d(0, 20, -Math.PI/2))
                        .splineTo(new Pose2d(0, 70, -Math.PI/2)) // Was 90
                        .splineTo(new Pose2d(37, 95, Math.PI + (Math.PI/16))) // A little correction for the last turn
                        .build()
        );

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

        while (!isStopRequested()) {
            // ..
        }
    }

    private void initializePIDF() {
        // Create the controller
        controller = new PIDFController(coeffs, kV, kA, kStatic);
        // Species the startPoint
        controller.setTargetPosition(0);

        //correction = controller.update(position, velocity, acceleration + g);
    }

    private void initThreads() {
        info = () -> {
            while (opModeIsActive()) {
                robot.printInfo();
            }

            infoThread.interrupt();// Stop the thread

            stop();
        };

        main = () -> {
            try {
                while (opModeIsActive()) {

                    // robot.action()

                    stop();
                }

                robot.stop();
                mainThread.interrupt(); // Stop the thread

            } catch (Exception ex) {
                robot.stop();
            }
        };
    }
}
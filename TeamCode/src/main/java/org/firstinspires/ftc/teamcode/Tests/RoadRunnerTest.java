package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotClasses.MainRobot;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveMR;

import java.util.Vector;

import static org.firstinspires.ftc.teamcode.drive.DriveConstants.kA;
import static org.firstinspires.ftc.teamcode.drive.DriveConstants.kStatic;
import static org.firstinspires.ftc.teamcode.drive.DriveConstants.kV;

@Autonomous(name="RoadRunner Test", group="Linear Opmode")
public class RoadRunnerTest extends LinearOpMode {

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

     Pose2d redBottomBottom = new Pose2d(0,0,0);
    Pose2d redMiddleBottom = new Pose2d(-60,-21,-90);
    // https://acme-robotics.gitbook.io/road-runner/tour/feedforward-control

    // Roadrunner variables
    private DriveConstraints constraints = new DriveConstraints(20, 40, 80, 1, 2, 4);
    //private Trajectory traj = TrajectoryGenerator.generateTrajectory(path, constraints);

    @Override
    public void runOpMode() throws InterruptedException {

        SampleMecanumDriveMR driveBase = new SampleMecanumDriveMR(hardwareMap);

        waitForStart();

        // Red Block to Foundation
        /*
        driveBase.followTrajectorySync(
                driveBase.trajectoryBuilder()
                        .splineTo(new Pose2d(-30, -25, 0))
                        .splineTo(new Pose2d(0, -45, 0))
                        .splineTo(new Pose2d(45, -25, 90))
                        .build()
        );*/

        driveBase.setPoseEstimate(redMiddleBottom);
        // Blue Block to Foundation
        driveBase.followTrajectorySync(
                driveBase.trajectoryBuilder()
                        .lineTo(new Vector2d(-30, -51))
                        //.splineTo(new Pose2d(48, -25, 90))
                        .build()
        );

        // Starting: -35 25 0
        // 0, 45, 0
        // 45 25 90

        // TEST PATH
        /*driveBase.followTrajectorySync(
                driveBase.trajectoryBuilder()
                        .splineTo(new Pose2d(-45, 25, 0))
                        .splineTo(new Pose2d(-45, 25, 90))
                        .build()
        );*/

        /*robot = new MainRobot(hardwareMap, telemetry);
        infoThread = new Thread(info);
        mainThread = new Thread(main);

        initThreads();
        this.telemetry.addData("Say", "Robot is Ready");

        waitForStart();

        main.run();*/
        stop();
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

enum Mode {
    IDLE,
    TURN,
    FOLLOW_TRAJECTORY
}
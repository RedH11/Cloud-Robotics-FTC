package org.firstinspires.ftc.teamcode.Competition.Auto.Park;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotClasses.MainRobot;

@Autonomous(name="Drive Forward 6 Inches", group="Linear Opmode")
public class ParkRobot extends LinearOpMode {

    MainRobot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        while (opModeIsActive()) {
            robot.park(6);
            stop();
            if (!opModeIsActive()) robot.stop();
        }
    }

    /**
     * Initializing the robot for competing
     */
    private void initialize() {
        telemetry.addData("Say", "Starting initialization... (0/4)");
        telemetry.update();

        robot = new MainRobot(hardwareMap, telemetry);

        telemetry.addData("Say", "Starting initialization... (1/4)");
        telemetry.update();

        // ...

        telemetry.addData("Say", "Starting initialization... (2/4)");
        telemetry.update();

        // ...

        telemetry.addData("Say", "Starting initialization... (3/4)");
        telemetry.update();

        // ...

        telemetry.addData("Say", "Robot is Ready (4/4)");
        telemetry.update();
    }
}

package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotClasses.MainRobot;

// PHONE IP:
//10.186.70.240

/**
 * TO WORK ON
 *
 * The turning method sometimes overshoots and keeps going (super rare but a big problem)
 * The blue foundation code isn't update to the code of the red foundation, so implement the red fixes
 *
 * Fix and test the wheel skew method
 */
@Autonomous(name="Main Test", group="Linear Opmode")
public class Testing extends LinearOpMode {

    MainRobot robot;
    Runnable main;

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("Say", "Starting initialization... (0/4)");
        telemetry.update();
        robot = new MainRobot(hardwareMap, telemetry);

        telemetry.addData("Say", "Starting initialization... (1/4)");
        telemetry.update();
        initThreads();

        telemetry.addData("Say", "Starting initialization... (2/4)");
        telemetry.update();

        telemetry.addData("Say", "Starting initialization... (3/4)");
        telemetry.update();
        telemetry.addData("Say", "Robot is Ready (4/4)");
        telemetry.update();

        waitForStart();

        main.run();
    }

    private void initThreads() {
        main = () -> {
            try {
                while (opModeIsActive()) {

                    //robot.infinityLoop();
                    //stop();
                    // robot.action()

                    if (!opModeIsActive()) break;
                }

                robot.stop();
                stop();

                //mainThread.interrupt(); // Stop the thread

            } catch (Exception ex) {
                robot.stop();
            }
        };
    }
}

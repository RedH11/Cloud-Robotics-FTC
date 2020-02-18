package org.firstinspires.ftc.teamcode;

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
@Autonomous(name="Testing", group="Linear Opmode")
public class Testing extends LinearOpMode {

    MainRobot robot;
    Runnable info;
    Runnable main;
    Thread infoThread;
    Thread mainThread;

    @Override
    public void runOpMode() throws InterruptedException {

        robot = new MainRobot(hardwareMap, telemetry);
        infoThread = new Thread(info);
        mainThread = new Thread(main);

        initThreads();
        this.telemetry.addData("Say", "Robot is Ready");

        waitForStart();

        info.run();
        main.run();
    }

    private void initThreads() {
        info = () -> {
            while (opModeIsActive()) {
                robot.printInfo();
                if (!opModeIsActive()) break;
            }

            infoThread.interrupt();// Stop the thread

            stop();
        };

        main = () -> {
            try {
                while (opModeIsActive()) {

                    robot.park();
                    stop();
                    // robot.action()

                    if (!opModeIsActive()) break;
                }

                robot.stop();
                mainThread.interrupt(); // Stop the thread

            } catch (Exception ex) {
                robot.stop();
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.NNMode;
import org.firstinspires.ftc.teamcode.Vision.NNVision;

@Autonomous(name="Only Vision Test", group="Linear Opmode")
public class NNVisionOnlyTest extends LinearOpMode {

    NNVision vision;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Say", "Initializing...");
        telemetry.update();
        // NNModes:
        // initial - find starting locations and print them to telemetry as 0's and 1's
        // display - show the identifications of the neural network ont he screen
        // running - tbd ability to run the neural network to scout for stones on the go
        vision = new NNVision(hardwareMap, telemetry, NNMode.display);
        telemetry.addData("Say", "Done Initializing!");

        waitForStart();
    }
}

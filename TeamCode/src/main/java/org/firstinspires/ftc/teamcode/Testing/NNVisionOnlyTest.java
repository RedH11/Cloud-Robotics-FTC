package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Vision.NNVision;

@Autonomous(name="Only Vision Test", group="Linear Opmode")
public class NNVisionOnlyTest extends LinearOpMode {

    NNVision vision;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Say", "Initializing...");
        telemetry.update();
        vision = new NNVision(hardwareMap, telemetry);
        telemetry.addData("Say", "Done Initializing!");

        waitForStart();
    }
}

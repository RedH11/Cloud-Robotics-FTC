package org.firstinspires.ftc.teamcode.drive.localizer;

import android.support.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 * Note: this could be optimized significantly with REV bulk reads
 */
@Config
public class StandardTrackingWheelLocalizer extends ThreeTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 1450;
    public static double WHEEL_RADIUS = 0.75; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    /*Don't need this We should set the positon manually*/
    //public static double LATERAL_DISTANCE = 14.9; // in; distance between the left and right wheels
    //public static double FORWARD_OFFSET = 12.25; // in; offset of the lateral wheel

    private DcMotor leftEncoder, rightEncoder, frontEncoder;


    /*
    *Setup for the encoder wheels positions are the location of where each odomotry wheel is compared to the center
     */
    public StandardTrackingWheelLocalizer(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Pose2d(0, 0, 0), // left
                new Pose2d(0, 0, 0), // right
                new Pose2d(0, 0, Math.toRadians(90)) // front
        ));

        leftEncoder = hardwareMap.dcMotor.get("lIntake");
        rightEncoder = hardwareMap.dcMotor.get("rIntake");
        frontEncoder = hardwareMap.dcMotor.get("tapeLift");
    }

    public static double encoderTicksToInches(int ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCurrentPosition()),
                encoderTicksToInches(rightEncoder.getCurrentPosition()),
                encoderTicksToInches(frontEncoder.getCurrentPosition())
        );
    }
}

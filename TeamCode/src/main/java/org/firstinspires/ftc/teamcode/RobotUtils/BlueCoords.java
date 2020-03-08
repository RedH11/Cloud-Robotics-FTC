package org.firstinspires.ftc.teamcode.RobotUtils;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.util.ArrayList;
import java.util.HashMap;

public class BlueCoords {


    private final int startBlocKDist = 35; // 35

    public final HashMap<Integer, Pose2d> blockCoords = new HashMap<Integer, Pose2d>() {
        {

            // FILL IN THE COORDS
            put(1, new Pose2d(startBlocKDist, 17, -Math.PI/4));
            put(2, new Pose2d(startBlocKDist, 12, 0));
            put(3, new Pose2d(startBlocKDist, 7, 0));
            put(4, new Pose2d(startBlocKDist, -3, 0));
            put(5, new Pose2d(startBlocKDist, -10, 0));
            put(6, new Pose2d(startBlocKDist, -15, -Math.PI/4));

            // 456 further
        }
    };


}

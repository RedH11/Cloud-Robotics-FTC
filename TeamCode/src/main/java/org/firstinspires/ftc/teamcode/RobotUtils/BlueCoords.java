package org.firstinspires.ftc.teamcode.RobotUtils;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.util.ArrayList;
import java.util.HashMap;

public class BlueCoords {

    public final HashMap<Integer, Pose2d> blockCoords = new HashMap<Integer, Pose2d>() {
        {
            // FILL IN THE COORDS
            put(1, new Pose2d(20, 5, Math.PI/16));
            put(2, new Pose2d(20, 5, Math.PI/16));
            put(3, new Pose2d(20, 5, Math.PI/16));
            put(4, new Pose2d(20, 5, Math.PI/16));
            put(5, new Pose2d(20, 5, Math.PI/16));
            put(6, new Pose2d(20, 5, Math.PI/16));
        }
    };


}

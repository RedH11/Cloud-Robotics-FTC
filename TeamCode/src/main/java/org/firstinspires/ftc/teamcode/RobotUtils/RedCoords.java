package org.firstinspires.ftc.teamcode.RobotUtils;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.util.ArrayList;
import java.util.HashMap;

// I think this is actually red

public class RedCoords {

    private final double underAngle = -Math.PI/2;
    private final int startBlockAndSome = 36; // 35
    private final int startBlockDist = 32;

    public final HashMap<Integer, Pose2d> initialBlockCoords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockDist, 26, -underAngle / 4));
            put(2, new Pose2d(startBlockDist, 29, -underAngle));
            put(3, new Pose2d(startBlockDist, 7, -underAngle / 4));
            put(4, new Pose2d(startBlockAndSome, 3, Math.PI/32)); // works
            put(5, new Pose2d(startBlockAndSome, -2, Math.PI/16)); // works
            put(6, new Pose2d(startBlockAndSome, -10, -Math.PI/16)); // works
        }
    };

    public final HashMap<Integer, Pose2d> post4coords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockAndSome, 26, Math.PI/4));
            put(2, new Pose2d(startBlockAndSome, 22, Math.PI/8));
            put(3, new Pose2d(startBlockAndSome, 7, Math.PI/8));
        }
    };

    public final HashMap<Integer, Pose2d> post5coords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockAndSome, 26, Math.PI/4));
            put(2, new Pose2d(startBlockAndSome, 22, Math.PI/8));
            put(3, new Pose2d(startBlockAndSome, 7, Math.PI/8));
        }
    };

    public final HashMap<Integer, Pose2d> post6coords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockAndSome, 26, Math.PI/4));
            put(2, new Pose2d(startBlockAndSome, 22, Math.PI/8));
            put(3, new Pose2d(startBlockAndSome, 7, Math.PI/8));
        }
    };

    private final double bridgeX = 10;
    private final double wallX = 0;

    public final Pose2d underBridgeForwardBridge = new Pose2d(bridgeX, -20, underAngle);
    public final Pose2d underBridgeForwardWall = new Pose2d(wallX, -20, underAngle);
    public final Pose2d bridgeDeliveryForward = new Pose2d(bridgeX, -30, underAngle);

    public final Pose2d underBridgeBackwardBridge = new Pose2d(bridgeX, -20, -underAngle);
    public final Pose2d underBridgeBackwardWall = new Pose2d(wallX, -20, -underAngle);
    public final Pose2d bridgeDeliveryBackward = new Pose2d(10, -40, -underAngle);

/*
        Notes:

        (In terms of the original heading)
        -Y: Robot towards right
        Y: Robot towards left
        X: Forwards
        -X: Backwards

        Negative angle turns right
 */


}

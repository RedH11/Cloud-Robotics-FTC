package org.firstinspires.ftc.teamcode.RobotUtils;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.util.ArrayList;
import java.util.HashMap;

// I think this is actually red
// are you sure about that
public class RedCoords {

    private final double underAngle = -Math.PI/2;
    private final int startBlockAndSome = 37; // 35
    private final int startBlockDist = 30;

    public final HashMap<Integer, Pose2d> initialBlockCoords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockDist + 10, 18, underAngle / 4));
            put(2, new Pose2d(startBlockDist, 17, underAngle / 4)); // almost works (around 20)
            put(3, new Pose2d(startBlockDist, 7, underAngle / 4));
            put(4, new Pose2d(startBlockAndSome, 3, Math.PI/32)); // kinda works
            put(5, new Pose2d(startBlockAndSome, -2, Math.PI/16)); // almost works
            put(6, new Pose2d(startBlockAndSome, -10, Math.PI/16)); // kinda works
        }
    };

    public final HashMap<Integer, Pose2d> post4coords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockAndSome, 26, Math.PI/8));
            put(2, new Pose2d(startBlockAndSome, 22, Math.PI/8));
            put(3, new Pose2d(startBlockAndSome, 7, Math.PI/8));
            put(4, new Pose2d(startBlockAndSome, 7, Math.PI/8));
        }
    };

    public final HashMap<Integer, Pose2d> post5coords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockAndSome, 26, underAngle / 4));
            put(2, new Pose2d(startBlockAndSome, 22, underAngle / 4));
            put(3, new Pose2d(startBlockAndSome, 7, underAngle / 4));
            put(4, new Pose2d(startBlockAndSome, 7, underAngle / 4));
        }
    };

    public final HashMap<Integer, Pose2d> post6coords = new HashMap<Integer, Pose2d>() {
        {
            put(1, new Pose2d(startBlockAndSome, 26, Math.PI/4));
            put(2, new Pose2d(startBlockAndSome, 22, Math.PI/8));
            put(3, new Pose2d(startBlockAndSome, 7, Math.PI/8));
            put(4, new Pose2d(startBlockAndSome, 7, Math.PI/8));
        }
    };

    private final double bridgeX = 11.5;
    private final double wallX = 0;

    // 10 for lower volts when you strafe 15 and 8 for other volts
    private final double strafeDiff = 8; // The x difference between the internal and real position (add to x positions after strafe to fix pathing)

    private final double angleNudge = Math.PI/16;

    public final Pose2d underBridgeForwardBridge = new Pose2d(bridgeX + 8, -20 - 5, -underAngle + angleNudge);
    public final Pose2d underBridgeForwardWall = new Pose2d(wallX, -20, underAngle + angleNudge);
    public final Pose2d bridgeDeliveryForward = new Pose2d(bridgeX, -30, underAngle + angleNudge);

    public final Pose2d underBridgeBackwardBridge = new Pose2d(bridgeX, -20, -underAngle - angleNudge);
    public final Pose2d underBridgeBackwardWall = new Pose2d(wallX, -20, -underAngle - angleNudge);
    public final Pose2d bridgeDeliveryBackward = new Pose2d(9, -45, -underAngle - angleNudge); // x was 10 and y was -40

    public final Pose2d bridgeDeliveryBackwardPostStrafe = new Pose2d(9 + strafeDiff, -45, -underAngle - angleNudge); // x was 10 and y was -40
    public final Pose2d underBridgeBackwardBridgePostStrafe = new Pose2d(bridgeX + strafeDiff, -20, -underAngle - angleNudge);
    //public final Pose2d bridgeDeliveryBackwardPostStrafeLastBlock = new Pose2d(9, -45, -underAngle - angleNudge); // x was 10 and y was -40

    public final Pose2d pose4ExtraStone = new Pose2d();

    // SKYSTONE IN FOURTH POSITION
    public final Pose2d skyStone4StrafeReady = new Pose2d(18, 10, Math.PI/2);
    public final Pose2d lastStone4 = new Pose2d(40 + strafeDiff, 15, Math.PI/8);

    // SKYSTONE IN FIFTH POSITION
    public final Pose2d skyStone5StrafeReady = new Pose2d(18, 8, Math.PI/2);
    public final Pose2d lastStone5 = new Pose2d(40 + strafeDiff, 18 + strafeDiff, Math.PI/8);
    // SKYSTONE IN SIXTH POSITION
    public final Pose2d skyStone6StrafeReady = new Pose2d(18, 4, Math.PI/2);
    public final Pose2d lastStone6 = new Pose2d(40 + strafeDiff, 18 + strafeDiff, Math.PI/8);

    // LAST BLOCK
    public final Pose2d lastUnderBridgeBackwardBridge = new Pose2d(bridgeX - 3, -20, -underAngle - angleNudge);
    public final Pose2d lastDelivery = new Pose2d(bridgeX - 3, -45, -underAngle - angleNudge);

    public final Pose2d parkBridge = new Pose2d(bridgeX + strafeDiff, -20 - 5, -underAngle - angleNudge);
    //19, -39
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

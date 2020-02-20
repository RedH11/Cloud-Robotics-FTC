package org.firstinspires.ftc.teamcode.Movement;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TurnCalculation {

    private final double acceleration_distance;
    private final double deceleration_distance;
    private final double start_power;
    private final double full_power;
    private final double endPower;
    private final CURVE_TYPE acceleration_curve;
    private final CURVE_TYPE deceleration_curve;

    private double startPosition;
    private double endPosition;
    private double currentPosition;

    private boolean movementIsBackward;
    private boolean isAccelerating;
    private boolean isDecelerating;

    /**
     * Initialize the class
     * @param accelDist The distance to be accelerated
     * @param decelDist The distance to be decelerated
     * @param startPower The starting power of the robot
     * @param fullPower The maximum power of the robot
     * @param endPower The end power of the robot
     * @param accelCurve The rate that the acceleration increases
     * @param decelCurve The rate that the acceleration decreases
     */
    public TurnCalculation(final double accelDist, final double decelDist, final double startPower, final double fullPower, final double endPower, final CURVE_TYPE accelCurve, final CURVE_TYPE decelCurve) {
        this.acceleration_distance = accelDist;
        this.deceleration_distance = decelDist;
        this.start_power           = startPower;
        this.full_power            = fullPower;
        this.endPower              = endPower;
        this.acceleration_curve    = accelCurve;
        this.deceleration_curve    = decelCurve;
    }

    public double getEndPower() {
        return endPower;
    }

    private double rawCurve(double x, double d, CURVE_TYPE curve_type) {
        switch (curve_type) {
            case LINEAR:
                return x / d;
            case SINUSOIDAL_NORMAL:
                return sin((PI / 2) * (x / d));
            case SINUSOIDAL_INVERTED:
                return -cos((PI / 2) * (x / d)) + 1;
            case SINUSOIDAL_SCURVE:
                return -.5 * cos(PI * (x / d)) + .5;
        }

        return 0;
    }

    private double processedCurve(double power_adjuster, double x, double distance_zone, CURVE_TYPE curve_type, double min_power){
        return power_adjuster * rawCurve(x, distance_zone, curve_type) + min_power;
    }

    private int invert(){
        if (movementIsBackward)
            return -1;
        else
            return 1;
    }

    private double accelerationCurve(){
        return processedCurve((full_power - start_power) * invert(),
                (currentPosition - startPosition) * invert(),
                acceleration_distance,
                acceleration_curve,
                start_power * invert());
    }

    private double decelerationCurve(){
        return processedCurve((full_power - endPower) * invert(),
                (endPosition - currentPosition) * invert(),
                deceleration_distance,
                deceleration_curve,
                endPower * invert());
    }

    public void setCurve(double start_position, double end_position){
        this.startPosition      = start_position;
        this.endPosition        = end_position;
        this.movementIsBackward = start_position > end_position;
        this.currentPosition    = start_position;
    }

    public void setCurrentPosition(double currentPosition){
        this.currentPosition = currentPosition;
        isAccelerating       = abs(startPosition - currentPosition) < acceleration_distance;
        isDecelerating       = abs(endPosition - currentPosition) < deceleration_distance;
    }

    public double getCurrentPowerAccelDecel(){
        if (isAccelerating && isDecelerating) {
            return accelerationCurve() * decelerationCurve() * invert();
        } else if (isAccelerating) {
            return accelerationCurve();
        } else if (isDecelerating) {
            return decelerationCurve();
        } else {
            return full_power * invert();
        }
    }

    public double getCurrentPowerDecel(){
        if (isDecelerating) {
            return decelerationCurve();
        } else {
            return full_power * invert();
        }
    }

    public double getCurrentPowerAccel(){
        if (isAccelerating) {
            return accelerationCurve();
        } else {
            return full_power * invert();
        }
    }

    public double getCurrentPower() {
        if (isAccelerating) return accelerationCurve();
        else if (isDecelerating) return decelerationCurve();
        else return full_power * invert();
    }

    public double getCurrentPowerFull(){
        return full_power * invert();
    }

    public boolean isDone(){
        return abs(currentPosition - endPosition) < deceleration_distance / 100;
    }

    public enum CURVE_TYPE {
        LINEAR,
        SINUSOIDAL_NORMAL,
        SINUSOIDAL_INVERTED,
        SINUSOIDAL_SCURVE
    }
}

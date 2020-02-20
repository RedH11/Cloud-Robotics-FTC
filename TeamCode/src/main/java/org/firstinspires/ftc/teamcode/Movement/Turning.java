package org.firstinspires.ftc.teamcode.Movement;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Turning {

    private TurnCalculation turnCalc;
    private BNO055IMU imu;
    private Driving driving;
    // Error Variables
    private double angleError = 0; // accounts for the IMU's error
    private double turnAccuracyThreshold = 1; // Amount of degrees which the turn should be within
    private double longestTurnTime = 5; // 5 seconds is the longest time a turn can take

    private ElapsedTime timer = new ElapsedTime();
    private Telemetry telemetry;

    //angles it accelerates
    double acceleration_distance    = 15;
    double deceleration_distance    = 30;
    double start_power              = .3;
    double full_power               = 1;
    double end_power                = 0;

    TurnCalculation.CURVE_TYPE acceleration_curve = TurnCalculation.CURVE_TYPE.LINEAR;
    TurnCalculation.CURVE_TYPE deceleration_curve = TurnCalculation.CURVE_TYPE.LINEAR;

    public Turning(BNO055IMU imu, double angleError, Driving driving, Telemetry telemetry) {
        this.imu = imu;
        this.angleError = angleError;
        this.driving = driving;
        this.telemetry = telemetry;
    }

    public void lazyTurn(double desiredAngle) {

        boolean powerSet = false;
        desiredAngle %= 360;

        timer.reset();
        timer.startTime();

        boolean leftTurn =  desiredAngle <= 0; // If the desired angle is less than one, turn the robot left

        // While the gyros angle is less or greater than the desired angle keep it turning until it reaches the angle or time runs out
        while (!(getGyroAngle() < desiredAngle + turnAccuracyThreshold && getGyroAngle() > desiredAngle - turnAccuracyThreshold) && timer.seconds() < longestTurnTime) {

            telemetry.addData("Current Angle", getGyroAngle());
            telemetry.update();

            double angleDif = Math.abs(getGyroAngle() - desiredAngle);

            if (!powerSet && leftTurn) {
                turnLeft(1);
                powerSet = true;
            } else if (!powerSet) {
                turnRight(1);
                powerSet = true;
            }

            if (angleDif <= 10) {
                if (leftTurn) turnLeft(0.2);
                else turnRight(0.2);
            } else if (angleDif <= 20) {
                if (leftTurn) turnLeft(0.3);
                else turnRight(0.3);
            }
        }

        driving.stopWheels();
    }

    public void complexTurn(double desiredAngle) {
        turnCalc = new TurnCalculation(acceleration_distance, deceleration_distance, start_power, full_power, end_power, acceleration_curve, deceleration_curve);
        turnCalc.setCurve(getGyroAngle(), desiredAngle);
        boolean leftTurn = desiredAngle <= 0; // If the desired angle is less than one, turn the robot left

        while (!turnCalc.isDone()) {
            turnCalc.setCurrentPosition(getGyroAngle());
            telemetry.addData("Current Angle", getGyroAngle());
            telemetry.update();
            if (leftTurn) turnLeft(turnCalc.getCurrentPower());
            else turnRight(turnCalc.getCurrentPower());
        }
    }

    private void updateAngle() {

    }

    public void encoderTurning(double power) {
        // Set up formulas
    }

    private void turnLeft(double power) {
        driving.setWheelPower(-power, -power, power, power);
    }

    private void turnRight(double power) {
        driving.setWheelPower(power, power, -power, -power);
    }

    /**
     * Retrieves the angle from the gyroscope and corrects for the error of the gyroscope
     *
     * NOTE: A coefficient of -1 is added to account for the gyro returning negative for the robot turning left which is counter-intuitive
     * @return Returns the double value of the robot's angle
     */
    public double getGyroAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return -1 * (AngleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle) - angleError);
    }
}

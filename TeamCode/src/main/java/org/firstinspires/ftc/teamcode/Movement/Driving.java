package org.firstinspires.ftc.teamcode.Movement;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Driving {

    // Wheel Motors
    private DcMotor lb;
    private DcMotor lf;
    private DcMotor rb;
    private DcMotor rf;

    private double prevEncodersValue = 0; // At the start of each drive, this allows the tracking of the current distance travelled rather than total
    private double drivingTicksPerInch;
    private double sideStrafingTicksPerInch;
    private double diagonalStrafingTicksPerInch;

    // Gyroscope Variables
    private BNO055IMU imu;
    private double angleError = 0;

    public Driving(DcMotor lb, DcMotor lf, DcMotor rb, DcMotor rf, double drivingTicksPerInch, double sideStrafingTicksPerInch,  BNO055IMU imu, double angleError) {
        this.lb = lb;
        this.lf = lf;
        this.rb = rb;
        this.rf = rf;
        this.drivingTicksPerInch = drivingTicksPerInch;
        this.sideStrafingTicksPerInch = sideStrafingTicksPerInch;
        this.imu = imu;
        this.angleError = angleError;
    }

    /**
     * Drives the robot forward without error correction
     * @param inches The desired distance for the robot to drive
     */
    public void lazyDriveForward(int inches, double ticksPerInch) {

        double curInches = 0;

        prevEncodersValue = averageEncoderVal();

        while (curInches < inches) {
            curInches = (averageEncoderVal() - prevEncodersValue) / ticksPerInch;
            setWheelPower(1);
        }

        stopWheels();
    }

    /**
     * Drives forwards with full power
     * @param inches The desired amount of inches for the robot to travel
     */
    public void fullPowerDriveForward(int inches) {
        double curInches = 0;

        prevEncodersValue = averageEncoderVal();

        double heading = getGyroAngle(); // The initial angle the robot is driving

        while (curInches < inches) {
            curInches = (averageEncoderVal() - prevEncodersValue) / drivingTicksPerInch;
            setFixedWheelPower(1, 0.3, heading);
        }

        stopWheels();
    }

    /**
     * Drives forward
     * @param inches The desired amount of inches for the robot to travel
     * @param power The maximum power given to the wheels
     */
    public void driveForward(int inches, double power) {
        double curInches = 0;

        recordPrevEncoderVal();

        double heading = getGyroAngle(); // The initial angle the robot is driving

        while (curInches < inches) {
            curInches = (averageEncoderVal() - prevEncodersValue) / drivingTicksPerInch;
            setFixedWheelPower(power, 0.3, heading);
        }

        stopWheels();
    }

    /**
     * Drives backwards with full power
     * @param inches The desired amount of inches for the robot to drive
     */
    public void fullPowerDriveBackwards(int inches) {
        double curInches = 0;

        recordPrevEncoderVal();

        while (curInches > -inches) {
            curInches = (averageEncoderVal() - prevEncodersValue) / drivingTicksPerInch;
            setWheelPower(-1);
        }

        stopWheels();
    }

    /**
     * Drives backwards
     * @param inches The desired amount of inches for the robot to drive
     * @param power The maximum power given to the wheels
     */
    public void driveBackwards(int inches, double power) {
        double curInches = 0;

        recordPrevEncoderVal();

        while (curInches > -inches) {
            curInches = (averageEncoderVal() - prevEncodersValue) / drivingTicksPerInch;
            setWheelPower(-power);
        }

        stopWheels();
    }

    // BASED ON HOW THE WHEELS ENCODERS REGISTER WHEN STRAFING, THIS MAY BE MESSED UP
    public void sideStrafe(int inches, double power, boolean left) {
        double curInches = 0;

        recordPrevEncoderVal();

        while (curInches < inches) {
            curInches = (averageEncoderVal() - prevEncodersValue) / sideStrafingTicksPerInch;

            if (left) {
                setWheelPower(power, -power, power, -power);
            } else {
                setWheelPower(-power, power, -power, power);
            }
        }
    }

    public void diagonalBackStrafe(boolean left) {
        // Useful link: https://www.vexforum.com/uploads/default/original/2X/c/c7963dec531b7e893ddcf91c4fc9159928c4b56c.jpeg
    }

    public void diagonalForwardStrafe(boolean left) {
        // Useful link: https://www.vexforum.com/uploads/default/original/2X/c/c7963dec531b7e893ddcf91c4fc9159928c4b56c.jpeg
    }

    /**
     * Stops the wheels by cutting off power given to them
     */
    public void stopWheels() {
        setWheelPower(0);
    }

    /**
     * Sets the power of the wheels to spin the same direction
     *
     * <p>
     *     The left wheels have a negative power input because their motors are flipped in the
     *     physical robot and thus turn opposite of the right wheels
     * </p>
     *
     * @param power The maximum power given to the wheels
     */
    public void setWheelPower(double power) {
        lb.setPower(-power);
        lf.setPower(-power);
        rf.setPower(power);
        rb.setPower(power);
    }

    /**
     * Sets the power of the wheels compensating for the left side having flipped motors
     * @param lbPower The power for the left back wheel
     * @param lfPower The power for the left front wheel
     * @param rfPower The power for the right front wheel
     * @param rbPower The power for the right back wheel
     */
    public void setWheelPower(double lbPower, double lfPower, double rfPower, double rbPower) {
        lb.setPower(-lbPower);
        lf.setPower(-lfPower);
        rf.setPower(rfPower);
        rb.setPower(rbPower);
    }

    /**
     * Changes the power of each wheel to compensate for slight turns while the robot drives
     * @param power The power given to the wheels
     * @param skew The amount of power taken away from some wheels for heading correction
     */
    public void setFixedWheelPower(double power, double skew, double heading) {

        double gyroAngle = getGyroAngle() - heading;

        if (gyroAngle < -1) {
            lb.setPower(-power);
            lf.setPower(-power);
            rf.setPower(power - skew);
            rb.setPower(power - skew);
        } else if (gyroAngle > 1) {
            lb.setPower(-power + skew);
            lf.setPower(-power + skew);
            rf.setPower(power);
            rb.setPower(power);
        } else {
            setWheelPower(power);
        }
    }

    /**
     * The average value of the wheel's encoders
     * @return The double value of the average position of the wheels encoders
     */
    public double averageEncoderVal() {
        return (-lf.getCurrentPosition() - lb.getCurrentPosition() + rb.getCurrentPosition() + rf.getCurrentPosition()) / 4;
    }

    /**
     * Returns the amount the encoders have changed from the last action
     * @return
     */
    public double getCurrentEncoderVal() {
        return averageEncoderVal() - prevEncodersValue;
    }

    /**
     * Saves the current state of the encoders in anticipation of another action
     */
    public void recordPrevEncoderVal() {
        prevEncodersValue = averageEncoderVal();
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

    public double getAngleError() {
        return angleError;
    }
}

package org.firstinspires.ftc.teamcode.Tools;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Access to the robots tools excluding the drive base and imu
 */
public class RobotTools {

    //private Servo lFoundation       = null;
    //private Servo rFoundation       = null;
    //private TouchSensor touchSensor = null;
    private DcMotorEx lIntake = null;
    private DcMotorEx rIntake = null;

    private HardwareMap hwMap;

    public RobotTools(HardwareMap hwMap) {
        this.hwMap = hwMap;
        //lFoundation = hwMap.servo.get("lFoundation");
        //rFoundation = hwMap.servo.get("rFoundation");
        //touchSensor = hwMap.touchSensor.get("touchSensor");
        lIntake = hwMap.get(DcMotorEx.class, "lIntake");
        rIntake = hwMap.get(DcMotorEx.class, "rIntake");

        rIntake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void intake(boolean on) {

        if (on) {
            lIntake.setVelocity(-1700);
            rIntake.setVelocity(-1700);
        } else {
            lIntake.setPower(0);
            rIntake.setPower(0);
        }
    }
}

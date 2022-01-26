package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.api.DcMotorX;
import org.firstinspires.ftc.teamcode.api.Drivetrain;
import org.firstinspires.ftc.teamcode.api.ControlledDrivetrain;
import org.firstinspires.ftc.teamcode.api.LimitedMotorX;
import org.firstinspires.ftc.teamcode.api.ServoX;
import org.firstinspires.ftc.teamcode.api.State;
import org.firstinspires.ftc.teamcode.api.Odometry;

/*
 * Robohawks ftc team 5741
 * Drive code for driver controlled period
 * contributers: Wolfie Davis, Crawford Phillips, Will Sprigg
 * ruined by: Cailean Sorce, and Wolfie Davis too
 */

@TeleOp
public class odometryTest extends OpMode {


    private Drivetrain drivetrain;
    private DcMotorX
            spinner,
            intake;
    private LimitedMotorX linear;
    private ServoX
            outtake,
            odoL,
            odoR,
            odoB;

    // Using a custom state instead of saving entire gamepad1 (doing otherwise causes lag)
    private State.Buttons lastButtons1 = new State.Buttons();
    private State.Dpad lastDpads1 = new State.Dpad();
    private State.Bumpers lastBumpers1 = new State.Bumpers();
    // Using a custom state instead of saving entire gamepad2
    private State.Buttons lastButtons2 = new State.Buttons();
    private State.Dpad lastDpads2 = new State.Dpad();
    private State.Bumpers lastBumpers2 = new State.Bumpers();


    // Odometry parameters
    private int ticksPerRev = 8192;
    private double circumference = 15.71;


    public void init() {
        DcMotorX mRF = new DcMotorX(hardwareMap.dcMotor.get("mRF")),
                mLF = new DcMotorX(hardwareMap.dcMotor.get("mLF")),
                mRB = new DcMotorX(hardwareMap.dcMotor.get("mRB")),
                mLB = new DcMotorX(hardwareMap.dcMotor.get("mLB"));

        drivetrain = new Drivetrain(mRF, mLF, mRB, mLB);

        //servos to raise and lower the odometry pods
        odoL = new ServoX(hardwareMap.servo.get("odoL"));
        odoR = new ServoX(hardwareMap.servo.get("odoR"));
        odoB = new ServoX(hardwareMap.servo.get("odoB"));

        //raise the pods up at the start of driver controlled to make sure they're up
        odoL.setAngle(180);
        odoR.setAngle(180);
        odoB.setAngle(180);

        //odometry initialization code
        Odometry odo = new Odometry(
                new DcMotorX(hardwareMap.dcMotor.get("wheelR"), ticksPerRev, circumference),
                new DcMotorX(hardwareMap.dcMotor.get("wheelL"), ticksPerRev, circumference),
                new DcMotorX(hardwareMap.dcMotor.get("wheelB"), ticksPerRev, circumference),
                50,
                -41.577 / (2 * Math.PI),
                40.8, //cm between side odometry wheels
                0, //set to 0 as in auto from last year - in documentation they were set to 5
                0,
                0
        );
        Thread positionTracking = new Thread(odo);
        positionTracking.start();

    }// end of init

    public void loop() {

        /* ------------- define variables to keep track of the controls ------------- */

        //joystick values for driving.
        double leftX = gamepad1.left_stick_x;
        double rightX = -gamepad1.right_stick_x;
        double rightY = -gamepad1.right_stick_y; // Reads negative from the controller

        //button definitions for gamepad1
        boolean a1 = gamepad1.a;
        boolean b1 = gamepad1.b;
        boolean x1 = gamepad1.x;
        boolean y1 = gamepad1.y;
        boolean dpadUp1 = gamepad1.dpad_up;
        boolean dpadDown1 = gamepad1.dpad_down;
        boolean dpadRight1 = gamepad1.dpad_right;
        boolean dpadLeft1 = gamepad1.dpad_left;
        boolean xHit1 = x1 && !lastButtons1.x;
        boolean yHit1 = y1 && !lastButtons1.y;
        boolean aHit1 = a1 && !lastButtons1.a;
        boolean bHit1 = b1 && !lastButtons1.b;
        boolean dpadUpHit1 = dpadUp1 && !lastDpads1.dpad_up;
        boolean dpadDownHit1 = dpadDown1 && !lastDpads1.dpad_down;
        boolean dpadRightHit1 = dpadRight1 && !lastDpads1.dpad_right;
        boolean dpadLeftHit1 = dpadLeft1 && !lastDpads1.dpad_left;


        /* ------------------------- odometry pods up and down test ------------------------ */

        if (dpadUpHit1) { //up
            odoL.setAngle(180);
            odoR.setAngle(180);
            odoB.setAngle(180);
        } else if (dpadDownHit1) { //down
            odoL.setAngle(0);
            odoR.setAngle(0);
            odoB.setAngle(0);
        }

        /* ------------------------- control the drivetrain ------------------------- */
        // Drive the robot with joysticks if they are moved (with rates)
        if (Math.abs(leftX) > .1 || Math.abs(rightX) > .1 || Math.abs(rightY) > .1) {
            double multiplier = 1;
            drivetrain.driveWithGamepad(1, rateCurve(rightY, 1.7), rateCurve(leftX, 1.7) * multiplier/* 0.5*leftX */, rateCurve(rightX, 1.7)); //curved stick rates
        } else {
            // If the joysticks are not pressed, do not move the bot
            drivetrain.stop();
        }


        /* ------------- record button states, to be used in determining ------------ */
        /* ------------------- "pressed" vs "held" and "released" ------------------- */
        // Save button states
        lastButtons1.update(a1, b1, x1, y1);
        lastDpads1.update(dpadUp1, dpadDown1, dpadRight1, dpadLeft1);


        /* ------- print to telemetry (used for calibration/ trouble shooting) ------ */
        telemetry.addData("x", odo.x); // Get the robot's current x coordinate
        telemetry.addData("y", odo.y); // Get the robot's current y coordinate
        telemetry.addData("phi", odo.phi); // Get the robot's current heading

    }//end of loop

    /* ------------------ used to "curve" the joystick input ------------------ */
    private double rateCurve(double input, double rate) {
        return Math.pow(Math.abs(input), rate) * ((input > 0) ? 1 : -1);
    }

    odo.stop();
}



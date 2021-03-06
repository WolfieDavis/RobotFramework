package org.firstinspires.ftc.teamcode.oldCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.api.ControlledDrivetrain;
import org.firstinspires.ftc.teamcode.api.DcMotorX;
import org.firstinspires.ftc.teamcode.api.Drivetrain;
import org.firstinspires.ftc.teamcode.api.LimitedMotorX;
import org.firstinspires.ftc.teamcode.api.Odometry;
import org.firstinspires.ftc.teamcode.api.ServoX;

import java.util.Arrays;

//@Autonomous
public class FreightAutonTest extends LinearOpMode {

    // Odometry parameters
    private int ticksPerRev = 8225; //left same as last year
    private double circumference = 15.725; //left same as last year
    private double width = 26.7385; //distance between centers of odometry wheels
    private double backDistancePerRadian = 22.222; //TODO: test to see what this is - rotate bot 360 - take the x value and put it over 2pi - it compensates fo the wheel being in the back of the bot

//    private final double TILE_SIZE = 60.96; //NO we're not measuring in fractional tiles this year, SAE is enough as it is

    private Drivetrain drivetrain;

    private DcMotorX
            mRF,
            mLF,
            mRB,
            mLB,
            spinner,
            intake,
            wheelR,
            wheelL,
            wheelB;
    private LimitedMotorX linear;
    private ServoX
            outtake,
            odoL,
            odoR,
            odoB;

    @Override
    public void runOpMode() throws InterruptedException {
        // Get all of the drivetrain motors
        mRF = new DcMotorX(hardwareMap.dcMotor.get("mRF"));
        mLF = new DcMotorX(hardwareMap.dcMotor.get("mLF"));
        mRB = new DcMotorX(hardwareMap.dcMotor.get("mRB"));
        mLB = new DcMotorX(hardwareMap.dcMotor.get("mLB"));

        intake = new DcMotorX(hardwareMap.dcMotor.get("intake"));
        spinner = new DcMotorX(hardwareMap.dcMotor.get("spinner"));
        linear = new LimitedMotorX(hardwareMap.dcMotor.get("linear"));
        outtake = new ServoX(hardwareMap.servo.get("outtake"));

        // Get the odometry wheels
        wheelR = new DcMotorX(hardwareMap.dcMotor.get("odoR"), ticksPerRev, (circumference));
        wheelL = new DcMotorX(hardwareMap.dcMotor.get("mLF"), ticksPerRev, (-circumference));
        wheelB = new DcMotorX(hardwareMap.dcMotor.get("mLB"), ticksPerRev, -(circumference));

        // Create an odometry instance for the drivetrain
        Odometry positionTracker = new Odometry(wheelR, wheelL, wheelB, 50, backDistancePerRadian, width, 0, 0, 0);

        // sets up drivetrain
        drivetrain = new Drivetrain(mRF, mLF, mRB, mLB);
        //drivetrain.reverse();
        // drivetrain.telemetry = telemetry;

        double[] initialPos = {0, 0, 0}; //x, y, phi
        positionTracker.x = initialPos[0];
        positionTracker.y = -initialPos[1];
        positionTracker.phi = initialPos[2];

        Thread positionTracking = new Thread(positionTracker);
        positionTracking.start();

        //TODO: add linear slide code in here if we are using it

        telemetry.addData("Done initializing", "");
        telemetry.update();


        /* ----------- waiting for start ----------- */
        waitForStart();

        //drop odometry pods
//        odoL.setAngle(0);
//        odoR.setAngle(0);
//        odoB.setAngle(0); //odoB.goToAngle(0, 500); //gives them time to drop //TODO: move odometry and drivetrain init code down here to make sure it initializes when servos are down and reading

        //reads out where we are in the code
        telemetry.addData("started??", "");
        telemetry.update();


        /* --------------- move robot --------------- */
        //movement parameters
        double exponent = 4; //4 //exponent that the rate curve is raised to
        double[] speed = {0.4, 0.35, 0.35}; //x, y, phi     //first argument(number) is for straight line movement, second is for turning
        double[] stopTolerance = {4, (Math.PI/45)}; //acceptable tolerance (cm for linear, radians for turning) for the robot to be in a position
        /* --- rate curve explained ---
        (bx)/(bx + a)
        coeff = b, needs to be > 1
        adjuster = a, needs to be > 1
        adjuster/coeff is the x location at which the speed = 1/2 of initial speed */
        double[] coeff = {2, 2};
        double[] adjuster = {15, 15};

        //just needs to be here
        double[] drivePower;

        //positions
        // double[] position1 = {0, 50, 0}; //x, y, phi. (in cm for x and y and radians for phi) this can be declared at the top of the program
        double[] carousel = {0, 50, 0}; //forward
        double[] position2 = {50, 50, 0}; //strafe after forward
        double[] position3 = {0, 0, 0}; //back to 0
        double[] position4 = {0, 0, Math.PI}; //rotate

        /* --------------- move robot --------------- */
        //forward

//        drive(initialPos, carousel, positionTracker, speed, exponent, stopTolerance);

        do {
            drivePower = fakePid_DrivingEdition(initialPos, carousel, positionTracker, speed, exponent, stopTolerance);
            drivetrain.driveWithGamepad(1, drivePower[1], drivePower[2], drivePower[0]);

        } while (!isStopRequested() && !Arrays.equals(drivePower, new double[]{0, 0, 0}));

        sleep(500);

        do {
            drivePower = fakePid_DrivingEdition(carousel, position2, positionTracker, speed, exponent, stopTolerance);
            drivetrain.driveWithGamepad(1, drivePower[1], drivePower[2], drivePower[0]);

        } while (!isStopRequested() && !Arrays.equals(drivePower, new double[]{0, 0, 0}));

        sleep(500);

        do {
            drivePower = fakePid_DrivingEdition(position2, position3, positionTracker, speed, exponent, stopTolerance);
            drivetrain.driveWithGamepad(1, drivePower[1], drivePower[2], drivePower[0]);

        } while (!isStopRequested() && !Arrays.equals(drivePower, new double[]{0, 0, 0}));

        sleep(500);

        do {
            drivePower = fakePid_DrivingEdition(position3, position4, positionTracker, speed, exponent, stopTolerance);
            drivetrain.driveWithGamepad(1, drivePower[1], drivePower[2], drivePower[0]);

        } while (!isStopRequested() && !Arrays.equals(drivePower, new double[]{0, 0, 0}));

        sleep(500);

        /* ------------------ other ------------------ */
        spinDucks(0.5, 500); //turns on carousel spinner at power 0.5 for 500ms (or whatever you set them to)
//        linear.setPower(.8);
//        sleep(200);
//        linear.setPower(0);
//        outtake.setAngle(80);
//        sleep(500);


        /* ---------------- shut down ---------------- */
        drivetrain.setBrake(true);
        drivetrain.stop();
    }//end of runOpMode


    /* ----------- backend of drive code: fake pid ----------- */
//    private double[] fakePid_DrivingEdition(double[] targetPos, Odometry odo, double[] speed, double[] coeff, double[] adjuster, double[] stopTolerance) {
//        double[] distanceToMove = {targetPos[0] - odo.x, targetPos[1] - (-odo.y), targetPos[2] - odo.phi};
//        double totalDistance = Math.sqrt(Math.pow(distanceToMove[0], 2) + Math.pow(distanceToMove[1], 2));
//
//        double[] returnPowers = {0, 0, 0};
//        if (totalDistance > stopTolerance[0]) {
//            double[] powerFractions = {distanceToMove[0] / totalDistance, distanceToMove[1] / totalDistance};
//            double scaleToOne;
//            if (powerFractions[0] > powerFractions[1]) {
//                scaleToOne = Math.abs(powerFractions[0]);
//            } else {
//                scaleToOne = Math.abs(powerFractions[1]);
//            }
//            double fakePidAdjustment = (coeff[0] * totalDistance)/(coeff[0] * totalDistance + adjuster[0]) * speed[0]; //curned as it approaches - works
//            returnPowers[0] = powerFractions[0] / scaleToOne * fakePidAdjustment;
//            returnPowers[1] = powerFractions[1] / scaleToOne * fakePidAdjustment;
//        }
//        double totalTurnDistance = Math.abs(distanceToMove[2]);
//        if (totalTurnDistance > stopTolerance[1]) {
//            returnPowers[2] = (coeff[1] * totalTurnDistance)/(coeff[1] * totalTurnDistance + adjuster[1]) * speed[1] * (distanceToMove[2] >= 0 ? 1 : -1);
//        }

    /* ----------- backend of drive code: fake pid, but curved on both ends ----------- */
    private double[] fakePid_DrivingEdition(double[] startPos, double[] targetPos, Odometry odo, double[] speed, double exponent, double[] stopTolerance) {
        double[] distBetween = {targetPos[0] - startPos[0], targetPos[1] - startPos[1], targetPos[2] - startPos[2]};
        double totDistBetween = Math.sqrt(Math.pow(distBetween[0], 2) + Math.pow(distBetween[1], 2));
        double[] distanceToMoveRemaining = {targetPos[0] - odo.x, targetPos[1] - (-odo.y), targetPos[2] - odo.phi};
        double totalDistanceRemaining = Math.sqrt(Math.pow(distanceToMoveRemaining[0], 2) + Math.pow(distanceToMoveRemaining[1], 2));

        double[] returnPowers = {0, 0, 0};
        if (totalDistanceRemaining > stopTolerance[0]) {
            double[] powerFractions = {distanceToMoveRemaining[0] / totalDistanceRemaining, distanceToMoveRemaining[1] / totalDistanceRemaining};
            double scaleToOne;
            if (powerFractions[0] > powerFractions[1]) {
                scaleToOne = Math.abs(powerFractions[0]);
            } else {
                scaleToOne = Math.abs(powerFractions[1]);
            }
            double fakePidAdjustment = (-(Math.pow((((totalDistanceRemaining) - (totDistBetween)) / (totDistBetween)), (exponent))) + 1); //curved as it starts and ends - experimental
            returnPowers[0] = powerFractions[0] / scaleToOne * fakePidAdjustment * speed[0];
            returnPowers[1] = powerFractions[1] / scaleToOne * fakePidAdjustment * speed[1];
        }
        double totalTurnDistance = Math.abs(distanceToMoveRemaining[2]);
        if (totalTurnDistance > stopTolerance[1]) {
//            returnPowers[2] = (coeff[1] * totalTurnDistance)/(coeff[1] * totalTurnDistance + adjuster[1]) * speed[1] * (distanceToMoveRemaining[2] >= 0 ? 1 : -1);
//            returnPowers[2] = speed[1] * (distanceToMoveRemaining[2] >= 0 ? 1 : -1);
//            returnPowers[2] = ((-(Math.pow((((totalTurnDistance) - (distBetween[2])) / (distBetween[2])), 4)) + 1)) * (distanceToMoveRemaining[2] >= 0 ? 1 : -1); //TODO: get curved rates for turning working
            returnPowers[2] = speed[2] * (distanceToMoveRemaining[2] >= 0 ? 1 : -1);
        }

        //read out positions
        telemetry.addData("x current", odo.x);
        telemetry.addData("y current", -odo.y);
        telemetry.addData("phi current (deg)", odo.phi * 180 / Math.PI);
        telemetry.addData("x target", targetPos[0]);
        telemetry.addData("y target", targetPos[1]);
        telemetry.addData("phi target (deg)", targetPos[2]);
        telemetry.update();

        return returnPowers;
    }

//    private void drive(double[] initPos, double[] finalPos, Odometry.positionTracker.odo, double[] speed, double exponent, double[] stopTolerance) {
//        double[] drivePower;
//
//        do {
//            drivePower = fakePid_DrivingEdition(initPos, finalPos, positionTracker, speed, exponent, stopTolerance);
//            drivetrain.driveWithGamepad(1, drivePower[1], drivePower[2], drivePower[0]);
//
//        } while (!isStopRequested() && !Arrays.equals(drivePower, new double[]{0, 0, 0}));
//    }

    /* ----------------- spins the carousel ----------------- */
    private void spinDucks(double power, int waitTime) {
        //will spin duck wheel- intake for testing for now
        intake.setPower(power);
        sleep(waitTime);
        intake.setPower(0);
    }


}//end of linear op mode
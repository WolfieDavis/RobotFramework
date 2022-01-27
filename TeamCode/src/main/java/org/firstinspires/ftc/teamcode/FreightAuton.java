package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.api.ControlledDrivetrain;
import org.firstinspires.ftc.teamcode.api.DcMotorX;
import org.firstinspires.ftc.teamcode.api.Drivetrain;
import org.firstinspires.ftc.teamcode.api.LimitedMotorX;
import org.firstinspires.ftc.teamcode.api.Odometry;
import org.firstinspires.ftc.teamcode.api.ServoX;

@Autonomous
public class FreightAuton extends LinearOpMode {

    // Odometry parameters
    private int ticksPerRev = 8192; //left same as last year
    private double circumference = 15.71; //left same as last year
    private double width = 26.9; //distance between centers of odometry wheels
    private double backDistancePerRadian = 0 / (2 * Math.PI); //TODO: test to see what this is - rotate bot 360 - take the x value and put it over 2pi - it compensates fo the wheel being in the back of the bot

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

        // Get the odometry wheels
        wheelR = new DcMotorX(hardwareMap.dcMotor.get("odoR"), ticksPerRev, (-circumference));
        wheelL = new DcMotorX(hardwareMap.dcMotor.get("mLF"), ticksPerRev, (-circumference));
        wheelB = new DcMotorX(hardwareMap.dcMotor.get("mLB"), ticksPerRev, circumference);

        // Create an odometry instance for the drivetrain
        Odometry positionTracker = new Odometry(wheelR, wheelL, wheelB, 50, backDistancePerRadian, width, 0, 0, 0);

        // sets up drivetrain
        drivetrain = new Drivetrain(mRF, mLF, mRB, mLB);
        drivetrain.reverse(); //TODO: I don't think u really want this, it's because we started the bot backwards last year
        // drivetrain.telemetry = telemetry;

        //TODO: add linear slide code in here if we are using it

        telemetry.addData("Done initializing", "");
        telemetry.update();

      waitForStart();

        Thread drivetrainThread = new Thread(drivetrain); // Run it in a separate thread
        drivetrainThread.start(); // Start the thread

        //drop odometry pods
        odoL.setAngle(0);
        odoR.setAngle(0);
        odoB.setAngle(0);


        //TODO: call code that makes it go forward and stuff up here
        double[] position1 = [1, 1, 0] //x, y, phi. this can be declared at the top of the program
        //for each movement copy this while loop, change position1
        while(!isStopRequested()){
            double[] drivePower = fakePid_DrivingEdition(position1, positionTracker, [1, 1], [30, 30], [1, 1]);
            drivetrain.driveWithGamepad(1, drivePower[0],drivePower[1], drivePower[2]);
        }


        //code at the end of auto that shuts everything down
        drivetrain.setBrake(true);
        drivetrain.stop();
        // drivetrain.setActive(false);
        // drivetrain.stopController();
    }//end of runOpMode


    //TODO: write code that makes it go forward and stuff down here... right?
    private double[] fakePid_DrivingEdition(double[] targetPos, Odometry odo, double[] speed, double[] adjuster, double[] stopTolerance){
        double[] distanceToMove = {targetPos[0] - odo.x, targetPos[1] - odo.y, targetPos[2] - odo.phi};
        double totalDistance = Math.sqrt(Math.pow(distanceToMove[0], 2) + Math.pow(distanceToMove[1], 2));

        double returnPowers = {0, 0, 0};
        if (totalDistance > stopTolerance[0]){
            double[] powerFractions = {distanceToMove[0]/totalDistance, distanceToMove[1]/totalDistance};
            if (powerFractions[0] > powerFractions[1]) {
                powerFractions[1] = powerFractions[1]/powerFractions[0];
                powerFractions[0] = 1;
            } else {
                powerFractions[0] = powerFractions[0]/powerFractions[1];
                powerFractions[1] = 1;
            }
            double fakePidAdjustment = Math.pow(totalDistance,speed[0]/adjuster[0]);
            returnPowers[0] = powerFractions[0] * fakePidAdjustment;
            returnPowers[1] = powerFractions[1] * fakePidAdjustment;
        } 
        double totalTurnDistance = Math.abs(distanceToMove[2]);
        if (totalTurnDistance > stopTolerance[1]) {
            returnPowers[2] = Math.pow(totalTurnDistance,speed[1]/adjuster[1])*(distanceToMove[2] >= 0? 1:-1);
        }

        return returnPowers;
    }


}//end of linear op mode






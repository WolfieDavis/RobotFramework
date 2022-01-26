package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.api.DcMotorX;
import org.firstinspires.ftc.teamcode.api.Drivetrain;
import org.firstinspires.ftc.teamcode.api.LimitedMotorX;
import org.firstinspires.ftc.teamcode.api.Odometry;
import org.firstinspires.ftc.teamcode.api.ServoX;



/*



//@Autonomous
public class FreightAuton extends LinearOpMode {

    private Drivetrain drivetrain;

    // Odometry parameters
    private int ticksPerRev = 8192; //left same as last year
    private double circumference = 15.71; //left same as last year
    private double width = 26.9; //distance between centers of odometry wheels
    private double backDistancePerRadian = -41.577 / (2 * Math.PI); //TODO: figure out what this math means

//    private final double TILE_SIZE = 60.96; //NO we're not measuring in fractional tiles this year, SAE is enough as it is


    drivetrain =new

    Drivetrain(mRF, mLF, mRB, mLB);

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
        wheelR = new DcMotorX(hardwareMap.dcMotor.get("mRB"), ticksPerRev, circumference);
        wheelL = new DcMotorX(hardwareMap.dcMotor.get("mLF"), ticksPerRev, circumference);
        wheelB = new DcMotorX(hardwareMap.dcMotor.get("mRF"), ticksPerRev, circumference);

        // Create an odometry instance for the drivetrain
        Odometry positionTracker = new Odometry(wheelR, wheelL, wheelB, 50, backDistancePerRadian, width, 0, 0, 0);

        //TODO: add linear slide limits and code in here if we are using it

        telemetry.addData("Done initializing", "");
        telemetry.update();

        waitForStart();


//        private double fakePid (DcMotorX motor,double targetPos, double speed, double adjuster,
//        double stopTolerance){
//            double currentPos = motor.getPosition();
//            double distanceToMove = Math.abs(targetPos - currentPos);
//            if (distanceToMove > stopTolerance) {
//                return Math.pow(distanceToMove, speed / adjuster) * (currentPos < targetPos ? 1 : -1);
//            } else {
//                return 0.0;
//            }
//        }

    }//end of runOpMode
}//end of linear op mode





*/

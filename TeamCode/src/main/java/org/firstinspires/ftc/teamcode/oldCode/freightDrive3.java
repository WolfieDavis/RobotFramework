package org.firstinspires.ftc.teamcode.oldCode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.api.DcMotorX;
import org.firstinspires.ftc.teamcode.api.Drivetrain;
import org.firstinspires.ftc.teamcode.api.LimitedMotorX;
import org.firstinspires.ftc.teamcode.api.ServoX;
import org.firstinspires.ftc.teamcode.api.State;

/*
 * Robohawks ftc team 5741
 * Drive code for driver controlled period
 * contributers: Wolfie Davis, Crawford Phillips, Will Sprigg
 * ruined by: Cailean Sorce
 */

//@TeleOp
public class freightDrive3 extends OpMode {


    private Drivetrain drivetrain;
    private DcMotorX
            spinner,
            intake,
            tapeExtend;
    private LimitedMotorX linear;
    private ServoX
            outtake,
            tip,
            odoL,
            odoR,
            odoB,
            tapePan,
            tapeTilt;
    private TouchSensor
            bottom,
            low,
            middle,
            top;

    private double
            //various positions the outake arm can be in, in inches from the bottom
            linearMaxSpeed = 0.2,
            linOffset = 0,
            minLinearPos = 0.9525 + linOffset, //0.635 = .25" //0.375 //the btm position of the outake (how far down it will go)
            maxLinearPos = 34.625 + linOffset,//13.625" //13.6875 total    //15.875 - the top position of the outake (how far up it will go)
            linearDownPos = 0 + linOffset, //the pick stuff up position
            linearUpPos = maxLinearPos - minLinearPos + linOffset, //the dump high position
            linearStagedPos = 5 + linOffset, //2" //the ready to pick up position (but still have clearance)
            linearLowPos = 7.62 + linOffset, //3" //the bottom dump pos
            linearMidPos = 20.32 + linOffset, //8" //the dump low position
            linearGoToPos = -1, // used to keep track of which position to go to


    //bucket positions and trip point
    outtakeLinearTrip = 1 + linOffset, //the bucket tips up to hold stuff in when linear is moved above this point
            outtakeTravelPos = 137.5, //137.5      //125 //120 - the bucket is in this angle when traveling
            outtakeCollectPos = 180, //175         //175 //180 - 178 is too low, 175 is too high - the bucket is in this position when collecting
            outtakeDumpPos = 85, //85              //80 //100, 45 - the bucket is in this position when dumping
//        private TouchSensor spinLimit,
//                linearBtmLimit;
    //limit switch is named spinLimit

    tapePanValue = 90,
            tapeTiltValue = 90,
            panMin = 60, //left
            panMax = 120, //right
            tiltMin = 40, //down
            tiltMax = 115, //up
            tapeExtendPower = 0.85,
            tapePanMultiplier = 0.10, //.15
            tapeTiltMultiplier = 0.10; //.15


    // Using a custom state instead of saving entire gamepad1 (doing otherwise causes lag)
    private State.Buttons lastButtons1 = new State.Buttons();
    private State.Dpad lastDpads1 = new State.Dpad();
    private State.Bumpers lastBumpers1 = new State.Bumpers();
    // Using a custom state instead of saving entire gamepad2
    private State.Buttons lastButtons2 = new State.Buttons();
    private State.Dpad lastDpads2 = new State.Dpad();
    private State.Bumpers lastBumpers2 = new State.Bumpers();

    //misc toggle and value change variables
    int spinDirection = 1;
    int intakeToggle = 1;
    int intakeSpinDir = 1;
    boolean isReversed = false;
    //int outtakeToggle = 1;

    public void init() {
        DcMotorX mRF = new DcMotorX(hardwareMap.dcMotor.get("mRF")),
                mLF = new DcMotorX(hardwareMap.dcMotor.get("mLF")),
                mRB = new DcMotorX(hardwareMap.dcMotor.get("mRB")),
                mLB = new DcMotorX(hardwareMap.dcMotor.get("mLB"));

        drivetrain = new Drivetrain(mRF, mLF, mRB, mLB);
        drivetrain.reverse();

        linear = new LimitedMotorX(hardwareMap.dcMotor.get("linear"), 1607, 34.76625);//13.6875); //1968, 16.25 bigger pulley   // 2900, 16.25 motor for linear rail
        intake = new DcMotorX(hardwareMap.dcMotor.get("intake"));//motor for intake spinner
        spinner = new DcMotorX(hardwareMap.dcMotor.get("spinner"));//motor for carousel spinner
        tapeExtend = new DcMotorX(hardwareMap.dcMotor.get("odoRear"));
        outtake = new ServoX(hardwareMap.servo.get("outtake"));//servo for outtake dropper
        tip = new ServoX(hardwareMap.servo.get("tip"));
        odoL = new ServoX(hardwareMap.servo.get("odoL"));
        odoR = new ServoX(hardwareMap.servo.get("odoR"));
        odoB = new ServoX(hardwareMap.servo.get("odoB"));
        tapePan = new ServoX(hardwareMap.servo.get("tapePan"), 180, panMax, panMin);
        tapeTilt = new ServoX(hardwareMap.servo.get("tapeTilt"), 180, tiltMax, tiltMin);
//            linear.setLimits(hardwareMap.touchSensor.get("linearBtmLimit"), 12.0); //UNCOMMENT WITH LIMIT SWITCH
        //tip.setAngle(90);

        bottom = hardwareMap.touchSensor.get("bottom");
//        low = hardwareMap.touchSensor.get("low");
//        middle = hardwareMap.touchSensor.get("middle");
        top = hardwareMap.touchSensor.get("top");

        drivetrain.setBrake(true);
        drivetrain.stop();

    }// end of init

    public void start() {
        //            linear.reset(); //TODO: UNCOMMENT WITH LIMIT SWITCH
        linear.setBrake(true); //so that the outake motor arm will hold pos and won't "bounce"
        linear.controlVelocity();
        drivetrain.stop();

        /* ------------------------ lift the odometry pods up ----------------------- */
        odoL.setAngle(180);
        odoR.setAngle(178);
        odoB.setAngle(155);

    }


    public void loop() {

        /* ------------- loop to fix the init position if auton messed up ------------- */
//        if (fixLoopContinue != 1) {
////            fixInitLoop();
//            double maxSpeed = 0.5;
//            if (Math.abs(gamepad2.right_stick_y) > .1) {
//                linear.setVelocity(gamepad2.right_stick_y * maxSpeed);
//            } else {
//                linear.setVelocity(0.0);
//            }
//
//            if (gamepad2.right_stick_button) {
//                outtake.setAngle(outtakeTravelPos);
//            }
//
//            if (gamepad2.left_stick_button) {
////                fixLoopContinue = 1;
//                linear.reset(); //assuming the outake arm is at the btm, set the encoder to 0
////                drivetrain.stop();
//            }

//        } else {
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
        boolean bumperLeft1 = gamepad1.left_bumper;
        boolean bumperRight1 = gamepad1.right_bumper;
        boolean xHit1 = x1 && !lastButtons1.x;
        boolean yHit1 = y1 && !lastButtons1.y;
        boolean aHit1 = a1 && !lastButtons1.a;
        boolean bHit1 = b1 && !lastButtons1.b;
        boolean dpadUpHit1 = dpadUp1 && !lastDpads1.dpad_up;
        boolean dpadDownHit1 = dpadDown1 && !lastDpads1.dpad_down;
        boolean dpadRightHit1 = dpadRight1 && !lastDpads1.dpad_right;
        boolean dpadLeftHit1 = dpadLeft1 && !lastDpads1.dpad_left;
        boolean bumperLeftHit1 = bumperLeft1 && !lastBumpers1.left_bumper;
        boolean bumperRightHit1 = bumperRight1 && !lastBumpers1.right_bumper;

        //button definitions for gamepad2
        boolean a2 = gamepad2.a;
        boolean b2 = gamepad2.b;
        boolean x2 = gamepad2.x;
        boolean y2 = gamepad2.y;
        boolean dpadUp2 = gamepad2.dpad_up;
        boolean dpadDown2 = gamepad2.dpad_down;
        boolean dpadRight2 = gamepad2.dpad_right;
        boolean dpadLeft2 = gamepad2.dpad_left;
        boolean bumperLeft2 = gamepad2.left_bumper;
        boolean bumperRight2 = gamepad2.right_bumper;
        boolean xHit2 = x2 && !lastButtons2.x;
        boolean yHit2 = y2 && !lastButtons2.y;
        boolean aHit2 = a2 && !lastButtons2.a;
        boolean bHit2 = b2 && !lastButtons2.b;
        boolean dpadUpHit2 = dpadUp2 && !lastDpads1.dpad_up;
        boolean dpadDownHit2 = dpadDown2 && !lastDpads1.dpad_down;
        boolean dpadRightHit2 = dpadRight2 && !lastDpads1.dpad_right;
        boolean dpadLeftHit2 = dpadLeft2 && !lastDpads1.dpad_left;
        boolean bumperLeftHit2 = bumperLeft2 && !lastBumpers2.left_bumper;
        boolean bumperRightHit2 = bumperRight2 && !lastBumpers2.right_bumper;



        /* --------- reverse the bot if d pad right on controller 1 is pressed --------- */
//        if(dpadRightHit1){
//
//            drivetrain.reverse();
//            isReversed = !isReversed;
//        }//reverses the bot


        if (bottom.isPressed()) linOffset = linear.getPosition();

        /* ------------- move the outake linear slide ( called "linear") ------------ */
        // first check if the triggers have been pressed (for manual movement). if they have been and the arm is not at the end of its travel, move the arm at the speed indicated by the trigger.
        if (gamepad2.right_trigger > 0.01 && linear.getPosition() < maxLinearPos && !top.isPressed()) {
            // linear.controlVelocity();                       //change to the appropriate control mode
            linear.setVelocity(gamepad2.right_trigger * linearMaxSpeed);    // set the speed of the arm
            linearGoToPos = -1;                      // cancel any automatic movement
        } else if (gamepad2.left_trigger > 0.01 && linear.getPosition() > minLinearPos && !bottom.isPressed()) {
            // linear.controlVelocity();
            linear.setVelocity(-gamepad2.left_trigger * linearMaxSpeed);
            linearGoToPos = -1;

            //if there is no manual movement...
        } else {
            //check the dpad for automatic movement requests, and record the position requested in the linearGoToPos variable. recording the requested position like this means the driver doesn't have to keep the dpad depressed until the movement is finished, they can just press and release it.

//            if (dpadUpHit2) {
//                linearGoToPos = linearUpPos;
//            } else if (dpadDownHit2) {
//                linearGoToPos = linearDownPos;
//            } else if (dpadLeftHit2) {
//                linearGoToPos = linearMidPos;
//            } else if (dpadRightHit2) {
//                linearGoToPos = linearStagedPos;
//            }

            //if there is automatic movement requested (can be from current iteration OR from past iteration) go to the position
            if (linearGoToPos != -1) {
                //linear.controlPosition();
                //TODO: THIS MIGHT NEED TO HAVE A 2nd ARG of 0.7 or 1 (the speed)
                // linear.setPosition(linearGoToPos, 0.7); //does this work now if I add a comment
//                linear.setVelocity(fakePid(linear, linearGoToPos, 0.8, 4, 1.5875)); //0.625 //change the 3rd arg to adjust slow down speed, should be >1
                linear.setVelocity(fakePid(linear, linear.getPosition(), linearGoToPos, 0.2, 1.7));
                //0.7
                //finally if no manual control was requested AND there is no automatic control, set the velocity to 0
            } else {
                // linear.controlVelocity();
                linear.setVelocity(0.0);
            }
        }


        /* ------------------------- set the bucket position ------------------------ */
//        if (y1||y2){ // if a "dump"  has been requested
//            outtake.setAngle(outtakeDumpPos);
//
//        //if the bucket is in the upper section of the arm (traveling or dumping position) tip it back a little to keep stuff from falling out
//        } else if ((linear.getPosition() > outtakeLinearTrip) &&! (x2||a2)){
//            outtake.setAngle(outtakeTravelPos);
//
//        //if the bucket is in the lower section of the arm tip it down to the collecting position
//        } else {
//            outtake.setAngle(outtakeCollectPos);
//        }


        /* ----------------- set the bucket position w/ compound ---------------- */
        if (x2 && (linear.getPosition() > 13.25)) {
            outtake.setAngle(160);
            tip.setAngle(2);
        } else if (a2 && (linear.getPosition() > 13.25)) {
            outtake.setAngle(160);
            tip.setAngle(180);
        } else {
            tip.setAngle(87); //90 but moved to avoid hitting the wire
            if ((y1 || y2) && (linear.getPosition() > 9)) { // if a "dump"  has been requested
                outtake.setAngle(outtakeDumpPos);

                //if the bucket is in the upper section of the arm
            } else if (linear.getPosition() > outtakeLinearTrip) {
                outtake.setAngle(outtakeTravelPos);

                //if the bucket is in the lower section of the arm tip it down
            } else {
                outtake.setAngle(outtakeCollectPos);
            }
        }




        /* ------------------------ tape measure pan/tile/extend ----------------------- */
        if (Math.abs(gamepad2.left_stick_y) > .1)
            tapeExtend.setPower(gamepad2.left_stick_y * tapeExtendPower);
        else tapeExtend.setPower(0);

        if (Math.abs(gamepad2.right_stick_x) > .1 || Math.abs(gamepad2.right_stick_y) > .1) {
            tapePanValue += gamepad2.right_stick_x * tapePanMultiplier;
            tapeTiltValue += gamepad2.right_stick_y * -tapeTiltMultiplier;
        }

//        if (Math.abs(gamepad2.right_stick_x) > .1 && tapePanValue < panMax && tapePanValue > panMin) {
//                tapePanValue += gamepad2.right_stick_x * tapePanMultiplier;
//        }
//
//        if (Math.abs(gamepad2.right_stick_y) > .1 && tapeTiltValue < tiltMax && tapeTiltValue > tiltMin) {
//                tapeTiltValue += gamepad2.right_stick_y * -tapeTiltMultiplier;
//        }

        tapePan.setAngle(tapePanValue);
        tapeTilt.setAngle(tapeTiltValue);

//        telemetry.addData("pan", tapePanValue);
//        telemetry.addData("tilt", tapeTiltValue);
//        telemetry.update();


        /* ------------------------ odometry pods up and down ----------------------- */

        if (dpadUpHit1) { //up
            odoL.setAngle(180);
            odoR.setAngle(178);
            odoB.setAngle(155);
        } else if (dpadDownHit1) { //down
            odoL.setAngle(0);
            odoR.setAngle(0);
            odoB.setAngle(0);
        }

        /* -------------- set the intake spinner direction / on / off -------------- */
        intakeSpinDir = (bumperRightHit1 || bumperRightHit2) ? intakeSpinDir *= -1 : intakeSpinDir;//toggles intake direction
        //intake spinner is toggled if b is pressed
        if (bHit1 || bHit2) {
            intakeToggle *= -1;
            switch (intakeToggle) {
                case -1:
                    intake.setPower(1 * intakeSpinDir);
                    break;
                case 1:
                    intake.setPower(0.0);
                    break;
            }//end of switch case
        }


        /* -------------- set the carousel spinner direction / on / off ------------- */
        spinDirection = (bumperLeftHit1 || bumperLeftHit2) ? spinDirection *= -1 : spinDirection; //reverse the direction if left bumper  is pressed
        //carousel spinner triggered w/ a press
        if (a1) {
            spinner.setPower(-0.8 * spinDirection);
        } else {
            spinner.setPower(0);
        }


        /* ------------------------- control the drivetrain ------------------------- */
        // Drive the robot with joysticks if they are moved (with rates)
        if (Math.abs(leftX) > .1 || Math.abs(rightX) > .1 || Math.abs(rightY) > .1) {
            double multiplier = (isReversed) ? -1 : 1;
//            drivetrain.driveWithGamepad(0.8, rateCurve(-rightY, 1.7), rateCurve(-leftX, 1.7) * multiplier * 0.625, rateCurve(rightX, 1.7)); //curved stick rates
            drivetrain.driveWithGamepad(0.95, rateCurve(-rightY, 1.9), rateCurve(-leftX, 1.9) * multiplier * 0.7, rateCurve(rightX, 1.9)); //curved stick rates
        } else {
            // If the joysticks are not pressed, do not move the bot
            drivetrain.stop();
        }


        /* ------------- record button states, to be used in determining ------------ */
        /* ------------------- "pressed" vs "held" and "released" ------------------- */
        // Save button states
        lastButtons1.update(a1, b1, x1, y1);
        lastDpads1.update(dpadUp1, dpadDown1, dpadRight1, dpadLeft1);
        lastBumpers1.update(bumperRight1, bumperLeft1);

        lastButtons2.update(a2, b2, x2, y2);
        lastDpads2.update(dpadUp2, dpadDown2, dpadRight2, dpadLeft2);
        lastBumpers2.update(bumperRight2, bumperLeft2);

        /* ------- print to telemetry (used for calibration/ trouble shooting) ------ */

        telemetry.addData("Data:", linear.getPosition());

//        telemetry.addData()
//         telemetry.addData("Dpad up: ", dpadUp2);


//        } //end of fix itself code if used


    }//end of loop

    /* ------------------ used to "curve" the joystick input ------------------ */
    private double rateCurve(double input, double rate) {
        return Math.pow(Math.abs(input), rate) * ((input > 0) ? 1 : -1);
    }

    /* ---------- used to slow a motor down when approching target pos ---------- */
    /* ------------- returns (distance left to travel)^(1/adjuster) ------------- */
    private double fakePid(DcMotorX motor, double startPos, double targetPos, double speed, /*double exponent, /*double adjuster,*/ double stopTolerance) {
        double currentPos = motor.getPosition();
        double totalMoveDist = (targetPos - startPos);
        double distanceToMove = Math.abs(targetPos - currentPos);

        double exponent;
        if (totalMoveDist < 5) exponent = 2;
        else if (totalMoveDist < 15) exponent = 4;
        else if (totalMoveDist < 25) exponent = 6;
        else exponent = 6;

        double negAdjust;
        if ((totalMoveDist > 0) && ((currentPos - startPos) < 2)) negAdjust = (totalMoveDist - 1);
        else negAdjust = 0;

        double distanceRemaining = Math.abs(targetPos - currentPos);
        if (distanceRemaining > stopTolerance) {
//            return (-(Math.pow((((currentPos) - (totalMoveDist / 2) + negAdjust) / (totalMoveDist / 2)), (exponent))) + 1) * speed;
//            return Math.pow(distanceToMove, speed / adjuster) * (currentPos < targetPos ? 1 : -1);
            return Math.pow(distanceToMove, speed / 50) * (currentPos < targetPos ? 1 : -1);
        } else {
            return 0.0;
        }
    }


}




   /*
    RRRRR           b            hh                                k     k          5555555 77777777 44    44  1111
   R::::::R        b.b           h.h                              k.k   kk          5.5          7.7 4.4  4.4 1 1.1
   R::RR:::R       b.b           h.h                              k.k  k.k  sssss   5.5         7.7  4.4  4.4   1.1
   R::::::R   ooo  b..bbb   ooo  h..hhhh   aaa.a www    ww    www k.k.k.k  s.s      55555      7.7   4..44..4   1.1
   R:R R::R  o.o.o b..b..b o.o.o h......h a..a..a w.w w.ww.w w.w  k.k.k     sssss       5.5   7.7         4.4   1.1
   R:R  R::R o.o.o b..b..b o.o.o h.h  h.h a..a..a  w.w.w  w.w.w   k.k k.k      s.s      5.5  7.7          4.4   1.1
   RRR   RRR  ooo   bbbb    ooo  hhh  hhh  aaa  aa  www    www    kk   k.k ssssss   555555  7.7           444 11111111
  */



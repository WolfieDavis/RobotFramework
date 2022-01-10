package org.firstinspires.ftc.teamcode;

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

@TeleOp
public class freightDriveExperimental extends OpMode {


    private Drivetrain drivetrain;
    private DcMotorX
            spinner,
            intake;
    private LimitedMotorX linear;
    private ServoX
            outtake;

    private double
            power = 1,
            minLinearPos = 0.375,
            maxLinearPos = 15.875,
            linearDownPos = 0, //the pos to run to when dpad is used
            linearUpPos = 16,
            linearStagedPos = 2,
            linearMinPos = 10,
            linearGoToPosition = null;
            outtakeLinearTrip = 1, //the bucket tips up to hold stuff in when linear is moved above this point
            outtakeTravelPos = 120,
            outtakeCollectPos = 180,
            outtakeDumpPos = 45;
//        private TouchSensor spinLimit,
//                linearBtmLimit;
    //limit switch is named spinLimit

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

    public void init(){
        DcMotorX mRF= new DcMotorX(hardwareMap.dcMotor.get("mRF")),
                mLF = new DcMotorX(hardwareMap.dcMotor.get("mLF")),
                mRB = new DcMotorX(hardwareMap.dcMotor.get("mRB")),
                mLB = new DcMotorX(hardwareMap.dcMotor.get("mLB"));

        drivetrain = new Drivetrain(mRF, mLF, mRB, mLB);


        linear = new LimitedMotorX(hardwareMap.dcMotor.get("linear"), 2900, 16.25);//motor for linear rail
        intake = new DcMotorX(hardwareMap.dcMotor.get("intake"));//motor for intake spinner
        spinner = new DcMotorX(hardwareMap.dcMotor.get("spinner"));//motor for carousel spinner
        outtake = new ServoX(hardwareMap.servo.get("outtake"));//servo for outtake dropper
//            linear.setLimits(hardwareMap.touchSensor.get("linearBtmLimit"), 12.0); //UNCOMMENT WITH LIMIT SWITCH

    }// end of init

    public void start(){
        //            linear.reset(); //UNCOMMENT WITH LIMIT SWITCH
        linear.resetEncoder();
        linear.setBrake(true);
        linear.controlVelocity();
    }

    public void loop(){

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

        //do we want this? does it work?
        if(dpadUpHit1){
            drivetrain.reverse();
            isReversed = !isReversed;
        }//reverses the bot


        //code for the linear rail uses the values read by the trigger.
        if (gamepad2.right_trigger > 0.01 && linear.getPosition() > minLinearPos){ //raises the linear slide
            linear.controlVelocity();
            linear.setVelocity(-gamepad2.right_trigger);
            linearGoToPosition = null;
        } else if (gamepad2.left_trigger > 0.01 && linear.getPosition() < maxLinearPos){ //lowers linear slide
            linear.controlVelocity();
            linear.setVelocity(gamepad2.left_trigger);
            linearGoToPosition = null;
        } else if (dpadUpHit2) {
            linearGoToPosition = linearUpPos;
        } else if (dpadDownHit2) {
            linearGoToPosition = linearDownPos;
        } else if (dpadLeftHit2) {
            linearGoToPosition = linearMinPos;
        } else if (dpadRightHit2) {
            linearGoToPosition = linearStagedPos;
        } else {
            linear.controlVelocity();
            linear.setVelocity(0.0);
        }


        if (linearGoToPosition != null) {
            linear.controlPosition();
            linear.setPosition(linearGoToPosition); //THIS MIGHT NEED TO HAVE A 2nd ARG of 0.7 or 1 (the speed)
        }



        //set bucket pos
        if (y1||y2){//flip the outake
            outtake.setAngle(outtakeDumpPos);
        } else if (linear.getPosition() > outtakeLinearTrip){
            outtake.setAngle(outtakeTravelPos);
        } else {
            outtake.setAngle(outtakeCollectPos);
        }



        intakeSpinDir = (bumperRightHit1 || bumperRightHit2)? intakeSpinDir *= -1: intakeSpinDir;//toggles intake direction
        //intake spinner is toggled if b is pressed
        if (bHit1 || bHit2){
            intakeToggle *= -1;
            switch (intakeToggle){
                case -1 :
                    intake.setPower(0.8 * intakeSpinDir);
                    break;
                case 1 :
                    intake.setPower(0.0);
                    break;
            }//end of switch case
        }

        spinDirection = (bumperLeftHit1 || bumperLeftHit2)? spinDirection *= -1: spinDirection; //reverse the direction if left bumper  is pressed
        //carousel spinner triggered w/ a press
        if(a1 || a2){
            spinner.setPower(0.8 * spinDirection);
        } else {
            spinner.setPower(0);
        }


        // Drive the robot with joysticks if they are moved (with rates)
        if(Math.abs(leftX) > .1 || Math.abs(rightX) > .1 || Math.abs(rightY) > .1) {
            double multiplier = (isReversed)? -1: 1;
            drivetrain.driveWithGamepad(1, rateCurve(rightY, 1.7),rateCurve(leftX, 1.7) * multiplier/* 0.5*leftX */, rateCurve(rightX,1.7)); //curved stick rates
        }else{
            // If the joysticks are not pressed, do not move the bot
            drivetrain.stop();
        }

        // Save button states
        lastButtons1.update(a1, b1, x1, y1);
        lastDpads1.update(dpadUp1, dpadDown1, dpadRight1, dpadLeft1);
        lastBumpers1.update(bumperRight1, bumperLeft1);

        lastButtons2.update(a2, b2, x2, y2);
        lastDpads2.update(dpadUp2, dpadDown2, dpadRight2, dpadLeft2);
        lastBumpers2.update(bumperRight2, bumperLeft2);

        telemetry.addData("Data:", linear.getPosition());
        telemetry.addData("Dpad up: ", dpadUp2);

    }//end of loop

    private double rateCurve(double input, double rate){
        return Math.pow(Math.abs(input),rate)*((input>0)?1:-1);
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


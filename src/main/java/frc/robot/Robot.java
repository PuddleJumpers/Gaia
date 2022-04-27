// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX; 
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.cameraserver.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final Spark topleft = new Spark(0);
  private final Spark bottomleft = new Spark(1);
  private final Spark topright = new Spark(3);
  private final Spark bottomright = new Spark(2);
  private final Spark shooterBack = new Spark(4);
  private final PWMVictorSPX shooterFront = new PWMVictorSPX(5);
  private final Spark conveyorbelt = new Spark(6);
  private final Spark intakeMotor = new Spark(7);
  private final Spark armMotor = new Spark(8); 
  private final MotorControllerGroup leftside = new MotorControllerGroup(topleft, bottomleft);
  private final MotorControllerGroup rightside = new MotorControllerGroup(topright, bottomright);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(leftside, rightside);
  private final Joystick m_stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  private double[][] instructions1 = {
    // {leftside, rightside, conveyor, shooterFront, shooterBack, seconds}
    // leftside & rightside: +1.0 forward --> -1.0 backward
    // conveyor +1.0 --> -1.0
    // shooterFront & shooterBack +1.0 --> -1.0
    {0.0, 0.0, -0.7, -0.4, -0.55, 5.0}, // Run conveyor and shooter for 5 seconds for maximum power and control 
    {-0.55, -0.45, 0.0, 0.0, 0.0, 2.5} // drive backwards for 3 seconds to be the best team there is 
  };
  
  private int currentInstruction;
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture();
    rightside.setInverted(true);
    shooterBack.setInverted(true);
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
    // start at first instruction then destroy everyone 
    currentInstruction = 0;
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    double[][] instructionSet = instructions1;

     double instructionLeftside = 0.0;
     double instructionRightside = 0.0;
     double instructionConveyor = 0.0;
     double instructionShooterFront = 0.0;
     double instructionShooterBack = 0.0;
     double instructionSeconds = 1.0;
     if (currentInstruction < instructionSet.length) {
      instructionLeftside = instructionSet[currentInstruction][0]; // leftside
      instructionRightside = instructionSet[currentInstruction][1]; // rightside
      instructionConveyor = instructionSet[currentInstruction][2]; // conveyor
      instructionShooterFront = instructionSet[currentInstruction][3]; // shooter front
      instructionShooterBack = instructionSet[currentInstruction][4]; // shooter back
      instructionSeconds = instructionSet[currentInstruction][5]; // set time for instruction
    }

    if (m_timer.get() < instructionSeconds) {
      leftside.set(instructionLeftside);
      rightside.set(instructionRightside);
      conveyorbelt.set(instructionConveyor);
      shooterFront.set(instructionShooterFront);
      shooterBack.set(instructionShooterBack);
    } else {
      m_timer.reset();
      currentInstruction++;
    }
  }
    
  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    m_timer.reset();
    m_timer.stop();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {

    boolean shootingButton = m_stick.getRawButton(1);
    boolean conveyorBeltForward = m_stick.getRawButton(8);
    boolean conveyorBeltBackward = m_stick.getRawButton(10);
    boolean intakeButtonForward = m_stick.getRawButton(7);
    boolean intakeButtonBackward = m_stick.getRawButton(9);
    boolean armButtonUp = m_stick.getRawButton(12);
    boolean armButtonDown = m_stick.getRawButton(11);

    m_robotDrive.arcadeDrive(-m_stick.getY(), m_stick.getX());
    
    if (armButtonUp) { // hold to raise arm up
      armMotor.set(-2.0);
    } else if (armButtonDown) { // hold to lower arm down 
      armMotor.set(2.0);
    } else {
      armMotor.set(0.0);
    }
    
    if (intakeButtonForward) { // hold button to run intake forward
      intakeMotor.set(-0.3);
    } else if (intakeButtonBackward) { // hold button to run intake backward and then dominate the world
      intakeMotor.set(0.3);
    } else {
      intakeMotor.set(0.0);
    }
    // arm motor up 5 seconds
    if (conveyorBeltForward) { // hold button to run conveyor belt forwards to end world hunger
      conveyorbelt.set(-0.7);
    } else if (conveyorBeltBackward) { // hold button to run conveyor belt backwards to hack into the mainframe
      conveyorbelt.set(0.7);
    } else {
      conveyorbelt.set(0.0);
    }
    
    if (shootingButton) { // hold to run shooter make Mankaran cry. Because he is not worthy of happiness. Ally!
      shooterFront.set(-0.2);
      shooterBack.set(-0.4);
    } else {
      shooterFront.set(0.0);
      shooterBack.set(0.0);
    }
  }
   
  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {

  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    
  }
}
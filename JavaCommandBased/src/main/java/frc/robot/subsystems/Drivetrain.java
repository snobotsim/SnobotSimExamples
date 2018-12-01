package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.RobotMap;
import frc.robot.commands.TeleDrive;

public class Drivetrain extends Subsystem
{
    private final DifferentialDrive mRobotDrive;

    public Drivetrain()
    {
        mRobotDrive = new DifferentialDrive(new Talon(RobotMap.sDRIVETRAIN_MOTOR_LEFT), new Talon(RobotMap.sDRIVETRAIN_MOTOR_RIGHT));
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new TeleDrive());
    }

    public void setLeftRightSpeed(double aLeft, double aRight)
    {
        mRobotDrive.tankDrive(aLeft, aRight);
    }

}

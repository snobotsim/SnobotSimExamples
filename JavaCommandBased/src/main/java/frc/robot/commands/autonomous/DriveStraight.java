package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.robot.Robot;

public class DriveStraight extends TimedCommand
{
    private final double mSpeed;

    public DriveStraight(double aTimeout, double aSpeed)
    {
        super(aTimeout);
        requires(Robot.mDrivetrain);
        
        mSpeed = aSpeed;
    }

    @Override
    protected void execute()
    {
        Robot.mDrivetrain.setLeftRightSpeed(mSpeed, mSpeed);
    }

}

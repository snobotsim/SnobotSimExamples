package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class TeleDrive extends Command
{
    public TeleDrive() 
    {
        requires(Robot.mDrivetrain);
    }
    
    @Override
    protected void execute() 
    {
        Robot.mDrivetrain.setLeftRightSpeed(Robot.mOperatorInterface.getDriveLeftSpeed(), Robot.mOperatorInterface.getDriveRightSpeed());
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }

}

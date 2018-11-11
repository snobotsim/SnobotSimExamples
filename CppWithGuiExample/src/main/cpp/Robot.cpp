
#include "Robot.h"

#include "Joystick/SnobotDriverJoystick.h"
#include "Joystick/SnobotOperatorJoystick.h"
#include "Climber/SnobotClimber.h"
#include "Gearboss/SnobotGearBoss.h"
#include "Drivetrain/SnobotDrivetrain.h"
#include "Positioner/SnobotPositioner.h"
#include "SnobotLib/Logger/SnobotLogger.h"

// WpiLib
#include "frc/AnalogGyro.h"
#include "frc/Talon.h"


void Robot::RobotInit()
{
    std::shared_ptr<ILogger> logger(new SnobotLogger);

    ////////////////////////////////////////////////////////////
    // Initialize joysticks
    ////////////////////////////////////////////////////////////
    std::shared_ptr<frc::Joystick> driverXbox(new frc::Joystick(0));
    std::shared_ptr<frc::Joystick> operatorXbox(new frc::Joystick(1));

    std::shared_ptr<IOperatorJoystick> operatorJoystick(new SnobotOperatorJoystick(operatorXbox));
    std::shared_ptr<IDriverJoystick> driverJoystick(new SnobotDriverJoystick(driverXbox, logger));

    ////////////////////////////////////////////////////////////
    // Initialize subsystems
    ////////////////////////////////////////////////////////////
    std::shared_ptr<frc::SpeedController> driveLeftMotor(new frc::Talon(0));
    std::shared_ptr<frc::SpeedController> driveRightMotor(new frc::Talon(1));
    std::shared_ptr<frc::Encoder> driveLeftEncoder(new frc::Encoder(0, 1));
    std::shared_ptr<frc::Encoder> driveRightEncoder(new frc::Encoder(2, 3));
    mDrivetrain = std::shared_ptr<IDrivetrain>(new SnobotDrivetrain(driveLeftMotor, driveRightMotor, driverJoystick, driveLeftEncoder, driveRightEncoder));
    addModule(mDrivetrain);

    // Gearboss
    std::shared_ptr<frc::DoubleSolenoid> gearSolenoid(new frc::DoubleSolenoid(0, 1));
    mGearBoss = std::shared_ptr<SnobotGearBoss>(new SnobotGearBoss(gearSolenoid, operatorJoystick, logger));
    addModule(mGearBoss);

    // Climber
    std::shared_ptr<frc::SpeedController> climberMotor(new frc::Talon(2));
    mClimber = std::shared_ptr<SnobotClimber>(new SnobotClimber(climberMotor, operatorJoystick, logger));
    addModule(mClimber);

    // Positioner
    std::shared_ptr<frc::Gyro> gyro(new frc::AnalogGyro(0));
    mPositioner = std::shared_ptr<IPositioner>(new SnobotPositioner(mDrivetrain, gyro, logger));
    addModule(mPositioner);
}



#ifdef STANDALONE_SNOBOT_SIM
    #include "SnobotSim/StartStandaloneSimulatorMacro.h"
    START_STANDALONE_SIMULATOR(Robot, SnobotSim::AStandaloneSimulator)
#else
    #ifndef RUNNING_FRC_TESTS
    START_ROBOT_CLASS(Robot)
    #endif
#endif
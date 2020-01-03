/*
 * ASnobotDrivetrain.cpp
 *
 *  Created on: May 19, 2017
 *      Author: preiniger
 */

#include "Drivetrain/ASnobotDrivetrain.h"
#include "SmartDashboardNames.h"

// WpiLib
#include "frc/smartdashboard/SmartDashboard.h"

ASnobotDrivetrain::ASnobotDrivetrain(
        const std::shared_ptr<frc::SpeedController>& aLeftMotor,
        const std::shared_ptr<frc::SpeedController>& aRightMotor,
        const std::shared_ptr<IDriverJoystick>& aJoystick) :

        mLeftMotor(aLeftMotor),
        mRightMotor(aRightMotor),
        mDriverJoystick(aJoystick),
        mRobotDrive(*aLeftMotor, *aRightMotor),

        mLeftMotorSpeed(0), mRightMotorSpeed(0), mRightMotorDistance(0), mLeftMotorDistance(0), mRightEncoderRaw(0), mLeftEncoderRaw(0)
{

}

ASnobotDrivetrain::~ASnobotDrivetrain()
{

}


void ASnobotDrivetrain::initializeLogHeaders()
{
    mLogger->addHeader("LeftEncoderDistance");
    mLogger->addHeader("RightEncoderDistance");
    mLogger->addHeader("LeftMotorSpeed");
    mLogger->addHeader("RightMotorSpeed");
}

void ASnobotDrivetrain::control()
{
    setLeftRightSpeed(mDriverJoystick->getLeftSpeed(), mDriverJoystick->getRightSpeed());
}

void ASnobotDrivetrain::updateSmartDashboard()
{
    frc::SmartDashboard::PutNumber(SmartDashboardNames::sLEFT_DRIVE_ENCODER_RAW, mLeftEncoderRaw);
    frc::SmartDashboard::PutNumber(SmartDashboardNames::sRIGHT_DRIVE_ENCODER_RAW, mRightEncoderRaw);
    frc::SmartDashboard::PutNumber(SmartDashboardNames::sRIGHT_DRIVE_ENCODER_DISTANCE, mRightMotorDistance);
    frc::SmartDashboard::PutNumber(SmartDashboardNames::sLEFT_DRIVE_ENCODER_DISTANCE, mLeftMotorDistance);
    frc::SmartDashboard::PutNumber(SmartDashboardNames::sLEFT_DRIVE_MOTOR_SPEED, mLeftMotorSpeed);
    frc::SmartDashboard::PutNumber(SmartDashboardNames::sRIGHT_DRIVE_MOTOR_SPEED, mRightMotorSpeed);
}

void ASnobotDrivetrain::updateLog()
{
    mLogger->updateLogger(mLeftMotorDistance);
    mLogger->updateLogger(mRightMotorDistance);
    mLogger->updateLogger(mLeftMotorSpeed);
    mLogger->updateLogger(mRightMotorSpeed);
}

double ASnobotDrivetrain::getRightDistance()
{
    return mRightMotorDistance;
}

double ASnobotDrivetrain::getLeftDistance()
{
    return mLeftMotorDistance;
}

void ASnobotDrivetrain::setLeftRightSpeed(double aLeftSpeed, double aRightSpeed)
{
    mLeftMotorSpeed = aLeftSpeed;
    mRightMotorSpeed = aRightSpeed;
    mRobotDrive.TankDrive(mLeftMotorSpeed, mRightMotorSpeed);
}

double ASnobotDrivetrain::getLeftMotorSpeed()
{
    return mLeftMotorSpeed;
}

double ASnobotDrivetrain::getRightMotorSpeed()
{
    return mRightMotorSpeed;
}

void ASnobotDrivetrain::stop()
{
    setLeftRightSpeed(0, 0);
}


/*
 * SnobotGearBoss.cpp
 *
 *  Created on: May 19, 2017
 *      Author: preiniger
 */

#include "Gearboss/SnobotGearBoss.h"
#include "SmartDashboardNames.h"

// WpiLib
#include "frc/SmartDashboard/SmartDashboard.h"


const frc::DoubleSolenoid::Value SnobotGearBoss::sGEAR_DOWN_VALUE = frc::DoubleSolenoid::kForward;
const frc::DoubleSolenoid::Value SnobotGearBoss::sGEAR_UP_VALUE = frc::DoubleSolenoid::kReverse;

SnobotGearBoss::SnobotGearBoss(std::shared_ptr<frc::DoubleSolenoid> aGearSolenoid,
        std::shared_ptr<IOperatorJoystick> aOperatorJoystick,
        std::shared_ptr<ILogger> aLogger) :
        mGearIsUp(false)
{
    mGearSolenoid = aGearSolenoid;
    mOperatorJoystick = aOperatorJoystick;
    mLogger = aLogger;

    moveGearHigh();
}

SnobotGearBoss::~SnobotGearBoss()
{

}

void SnobotGearBoss::initializeLogHeaders()
{
    mLogger->addHeader("SolenoidPosition");
}

void SnobotGearBoss::update()
{
    mGearIsUp = mGearSolenoid->Get() == sGEAR_UP_VALUE;
    mOperatorJoystick->setShouldRumble(!mGearIsUp);
}

void SnobotGearBoss::control()
{
    switch (mOperatorJoystick->moveGearBossToPosition())
    {
    case IOperatorJoystick::GearBossPositions::UP:
    {
        moveGearHigh();
        break;
    }
    case IOperatorJoystick::GearBossPositions::DOWN:
    {
        moveGearLow();
        break;
    }
    case IOperatorJoystick::GearBossPositions::NONE:
    {
        break;
    }
    }
}

void SnobotGearBoss::updateSmartDashboard()
{
    frc::SmartDashboard::PutBoolean(SmartDashboardNames::sGEAR_BOSS_SOLENOID,
            mGearIsUp);
}

void SnobotGearBoss::updateLog()
{
    mLogger->updateLogger(mGearIsUp);
}

void SnobotGearBoss::stop()
{
    moveGearHigh();
}

void SnobotGearBoss::moveGearHigh()
{
    mGearSolenoid->Set(sGEAR_UP_VALUE);
}

void SnobotGearBoss::moveGearLow()
{
    mGearSolenoid->Set(sGEAR_DOWN_VALUE);
}

bool SnobotGearBoss::isGearUp()
{
    return mGearIsUp;
}

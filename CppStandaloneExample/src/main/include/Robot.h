/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

#pragma once

#include "Drivetrain/IDrivetrain.h"
#include "Gearboss/IGearBoss.h"
#include "Climber/IClimber.h"
#include "Positioner/IPositioner.h"

#include "SnobotLib/ASnobot.h"

class Robot: public ASnobot
{
public:
    void RobotInit() override;

protected:

    std::shared_ptr<IDrivetrain> mDrivetrain;
    std::shared_ptr<IGearBoss> mGearBoss;
    std::shared_ptr<IClimber> mClimber;
    std::shared_ptr<IPositioner> mPositioner;

};

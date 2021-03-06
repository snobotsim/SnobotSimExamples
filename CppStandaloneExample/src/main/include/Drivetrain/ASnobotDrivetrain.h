/*
 * ASnobotDrivetrain.h
 *
 *  Created on: May 19, 2017
 *      Author: preiniger
 */

#ifndef SRC_DRIVETRAIN_ASNOBOTDRIVETRAIN_H_
#define SRC_DRIVETRAIN_ASNOBOTDRIVETRAIN_H_

#include "Drivetrain/IDrivetrain.h"
#include "Joystick/IDriverJoystick.h"
#include "SnobotLib/Logger/ILogger.h"

// WpiLIb
#include "frc/SpeedController.h"
#include "frc/drive/DifferentialDrive.h"

class ASnobotDrivetrain: public IDrivetrain
{
public:
    ASnobotDrivetrain(
            const std::shared_ptr<frc::SpeedController>& aLeftMotor,
            const std::shared_ptr<frc::SpeedController>& aRightMotor, const std::shared_ptr<IDriverJoystick>& aJoystick);
    virtual ~ASnobotDrivetrain();

    /**
     * Request right encoder distance
     *
     * @return distance in inches
     */
    virtual double getRightDistance() override;

    /**
     * Request left encoder distance
     *
     * @return distance in inches
     */
    virtual double getLeftDistance() override;

    /**
     * Set left and right drive mode speed
     *
     * @param aLeftSpeed
     *            Set motor speed between -1 and 1
     * @param aRightSpeed
     *            Set motor speed between -1 and 1
     */
    virtual void setLeftRightSpeed(double aLeftSpeed, double aRightSpeed) override;

    virtual double getLeftMotorSpeed() override;
    virtual double getRightMotorSpeed() override;

    ////////////////////////////////////////////
    // Subsystem Overrides
    ////////////////////////////////////////////

    /**
     * Setting sensor and device states.
     */
    virtual void control() override;

    /**
     * Stops all sensors and motors
     */
    virtual void stop() override;

    /**
     * Perform initialization.
     */
    virtual void initializeLogHeaders() override;

    /**
     * Updates the logger.
     */
    virtual void updateLog() override;

    /**
     * Updates information that is sent to SmartDashboard Takes Enum argument
     */
    virtual void updateSmartDashboard() override;

protected:

    std::shared_ptr<frc::SpeedController> mLeftMotor;
    std::shared_ptr<frc::SpeedController> mRightMotor;
    std::shared_ptr<IDriverJoystick> mDriverJoystick;

    std::shared_ptr<ILogger> mLogger;

    frc::DifferentialDrive mRobotDrive;

    double mLeftMotorSpeed;
    double mRightMotorSpeed;

    double mRightMotorDistance;
    double mLeftMotorDistance;

    int mRightEncoderRaw;
    int mLeftEncoderRaw;

};

#endif /* SRC_DRIVETRAIN_ASNOBOTDRIVETRAIN_H_ */

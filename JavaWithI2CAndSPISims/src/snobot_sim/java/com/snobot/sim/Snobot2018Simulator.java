package com.snobot.sim;


import com.snobot.simulator.ASimulator;
import com.snobot.simulator.SensorActuatorRegistry;
import com.snobot.simulator.robot_container.IRobotClassContainer;
import com.snobot.simulator.robot_container.JavaRobotContainer;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import com.snobot.simulator.wrapper_accessors.java.JavaI2CWrapperAccessor;
import com.snobot.simulator.wrapper_accessors.java.JavaSpiWrapperAccessor;

import frc.robot.Robot;

/**
 * When you have custom factories, you need to tell SnobotSim about them. This class,
 * upon construction, tells the library to use your overrides instead of the default.
 * <p>
 * Note: This class must be called out in the configuration file, {@code simulator_config/simulator_config.properties}
 */
public class Snobot2018Simulator extends ASimulator
{
    private Robot mRobot;

    /**
     * Constructor.
     * 
     * @param aUseCan
     *            If CTRE modules are being used. Used to specify which
     *            simulator config to read
     */
    public Snobot2018Simulator()
    {
        JavaSpiWrapperAccessor spiAccessor = (JavaSpiWrapperAccessor) DataAccessorFactory.getInstance().getSpiAccessor();
        spiAccessor.setSpiFactory(new SnobotSpiFactory());
        
        JavaI2CWrapperAccessor i2cAccessor = (JavaI2CWrapperAccessor) DataAccessorFactory.getInstance().getI2CAccessor();
        i2cAccessor.setI2CFactory(new SnobotI2CFactory());
    }
    
    @Override
    public void setRobot(IRobotClassContainer aRobot)
    {
        setRobot((Robot) ((JavaRobotContainer) aRobot).getJavaRobot());
    }

    public void setRobot(Robot aRobot)
    {
        mRobot = aRobot;
    }


    @Override
    public void update()
    {
        PixyCamSim wrapper = (PixyCamSim) SensorActuatorRegistry.get().getI2CWrappers().get(Robot.sPIXY_PORT.value);
        wrapper.update(mRobot);
    }
}

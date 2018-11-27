package frc.robot;

import com.snobot.sim.Snobot2018Simulator;
import com.snobot.simulator.DefaultDataAccessorFactory;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import com.snobot.simulator.wrapper_accessors.SimulatorDataAccessor.SnobotLogLevel;

public class BaseTestFixture
{
    private static boolean INITIALIZED;

    protected Robot mRobot;
    protected Snobot2018Simulator mSimulator;

    protected BaseTestFixture()
    {
        setupSimulator();

        mSimulator = new Snobot2018Simulator();
        mSimulator.loadConfig("simulator_config/simulator_config.yml");

        mRobot = new Robot();
        mRobot.robotInit();

        mSimulator.setRobot(mRobot);
    }

    private void setupSimulator()
    {
        if (!INITIALIZED)
        {
            DefaultDataAccessorFactory.initalize();
            DataAccessorFactory.getInstance().getSimulatorDataAccessor().setLogLevel(SnobotLogLevel.DEBUG);

            INITIALIZED = true;
        }
        
        DataAccessorFactory.getInstance().getSimulatorDataAccessor().reset();
    }

    protected void simulateForTime(double aSeconds)
    {
        simulateForTime(aSeconds, () -> 
        {
        });
    }

    protected void simulateForTime(double aSeconds, Runnable aTask)
    {
        simulateForTime(aSeconds, .02, aTask);
    }

    protected void simulateForTime(double aSeconds, double aUpdatePeriod, Runnable aTask)
    {
        double updateFrequency = 1 / aUpdatePeriod;

        for (int i = 0; i < updateFrequency * aSeconds; ++i)
        {
            aTask.run();
            mSimulator.update();
            mRobot.robotPeriodic();
            mRobot.teleopPeriodic();
            DataAccessorFactory.getInstance().getSimulatorDataAccessor().updateSimulatorComponents(aUpdatePeriod);
        }
    }
}

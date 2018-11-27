package frc.robot;

import com.snobot.sim.DotstarSim;
import com.snobot.simulator.SensorActuatorRegistry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LedTest extends BaseTestFixture
{

    
    @Test
    public void testWithinRange()
    {
        mRobot.getPositioner().setPosition(25, 0, 0);
        simulateForTime(.02);
        
        DotstarSim wrapper = (DotstarSim) SensorActuatorRegistry.get().getSpiWrappers().get(Robot.sDOTSTAR_PORT.value);
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0, -1, 0, -103, 0, -1, 0, -103, 0, -1, 0, -103, 0, -1, -1, -1, -1}, wrapper.getLastWrite());
    }
    
    @Test
    public void testTargetLeft()
    {
        mRobot.getPositioner().setPosition(50, 0, 0);
        simulateForTime(.02);
        
        DotstarSim wrapper = (DotstarSim) SensorActuatorRegistry.get().getSpiWrappers().get(Robot.sDOTSTAR_PORT.value);
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, -103, -1, -1, -1, -1}, wrapper.getLastWrite());
    }
    
    @Test
    public void testTargetRight()
    {
        mRobot.getPositioner().setPosition(0, 0, 0);
        simulateForTime(.02);
        
        DotstarSim wrapper = (DotstarSim) SensorActuatorRegistry.get().getSpiWrappers().get(Robot.sDOTSTAR_PORT.value);
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0, -1, 0, 0, -103, -1, 0, 0, 0, -1, 0, 0, 0, -1, -1, -1, -1}, wrapper.getLastWrite());
    }
}

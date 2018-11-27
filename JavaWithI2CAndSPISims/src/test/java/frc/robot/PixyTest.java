package frc.robot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PixyTest extends BaseTestFixture
{

    
    @Test
    public void testAtTarget()
    {
        mRobot.getPositioner().setPosition(25, 25, 0);
        simulateForTime(.02);

        Assertions.assertTrue(mRobot.getPixyCam().isInRange());
        Assertions.assertEquals(127, mRobot.getPixyCam().getLastOffset());
    }
    
    @Test
    public void testLeftAndDown()
    {
        mRobot.getPositioner().setPosition(0, 0, 0);
        simulateForTime(.02);

        Assertions.assertTrue(mRobot.getPixyCam().isInRange());
        Assertions.assertEquals(190, mRobot.getPixyCam().getLastOffset());
    }
    
    @Test
    public void testRightAndDown()
    {
        mRobot.getPositioner().setPosition(50, 0, 0);
        simulateForTime(.02);

        Assertions.assertTrue(mRobot.getPixyCam().isInRange());
        Assertions.assertEquals(63, mRobot.getPixyCam().getLastOffset());
    }
    
    @Test
    public void testOutOfFOV()
    {
        mRobot.getPositioner().setPosition(50, 50, 0);
        simulateForTime(.02);

        Assertions.assertFalse(mRobot.getPixyCam().isInRange());
        Assertions.assertEquals(127, mRobot.getPixyCam().getLastOffset());
    }

}

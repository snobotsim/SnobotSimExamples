
package com.snobot.sim;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.snobot.simulator.module_wrapper.ASensorWrapper;
import com.snobot.simulator.module_wrapper.interfaces.II2CWrapper;

import edu.wpi.first.hal.sim.BufferCallback;
import edu.wpi.first.hal.sim.CallbackStore;
import edu.wpi.first.hal.sim.I2CSim;
import frc.robot.Robot;
import frc.robot.PixyCam.PixyBlock;

/**
 * This class simulates the PixyCam interface. Based on robot angle, it will change the relative position of the blocks read by the robot
 */
public class PixyCamSim extends ASensorWrapper implements II2CWrapper, BufferCallback
{
    private static final double TARGET_X = 25;
    private static final double TARGET_Y = 25;
    private static final double FIELD_OF_VIEW = 90;

    private final CallbackStore mCallbackStore;
    private final List<PixyBlock> mBlocks;

    public PixyCamSim(int aPort)
    {
        super("Pixy Cam");

        I2CSim wpiWrapper = new I2CSim(aPort);
        mCallbackStore =  wpiWrapper.registerReadCallback(this);


        mBlocks = new ArrayList<>();
    }
    
    @Override
    public void callback(String aName, byte[] aBuffer, int aCount) 
    {
        ByteBuffer buffer = ByteBuffer.wrap(aBuffer);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x55);
        buffer.put((byte) 0xaa);

        for (PixyBlock block : mBlocks)
        {
            buffer.putShort((short) 0xaa55); // sync
            buffer.putShort((short) 0xBEEF); // checksum
            buffer.putShort((short) 0x0100); // signature
            buffer.putShort((short) block.mCenterX);
            buffer.putShort((short) block.mCenterY);
            buffer.putShort((short) block.mWidth);
            buffer.putShort((short) block.mHeight);
        }
    }

    public void update(Robot aRobot) 
    {
        mBlocks.clear();

        double robotX = aRobot.getPositioner().getX();
        double robotY = aRobot.getPositioner().getY();
        double dx = TARGET_X - robotX;
        double dy = TARGET_Y - robotY;
        double angleToTarget = Math.toDegrees(Math.atan2(dx, dy));

        if (Math.abs(angleToTarget) < FIELD_OF_VIEW)
        {
            PixyBlock block;

            double offset = angleToTarget / FIELD_OF_VIEW / 2;
            int center = (int) (127 + offset * 255);

            block = new PixyBlock();
            block.mCenterX = center + 10;
            block.mCenterY = 5;
            block.mHeight = 5;
            block.mWidth = 5;
            mBlocks.add(block);

            block = new PixyBlock();
            block.mCenterX = center - 10;
            block.mCenterY = 5;
            block.mHeight = 5;
            block.mWidth = 5;
            mBlocks.add(block);
        }
    }
    
    @Override
    public void close() throws Exception
    {
        super.close();
        mCallbackStore.close();
    }
}

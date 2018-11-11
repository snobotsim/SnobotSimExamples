
package com.snobot.sim;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.snobot.simulator.module_wrapper.ASensorWrapper;
import com.snobot.simulator.module_wrapper.interfaces.II2CWrapper;

import edu.wpi.first.hal.sim.BufferCallback;
import edu.wpi.first.hal.sim.CallbackStore;
import edu.wpi.first.hal.sim.I2CSim;
import frc.robot.PixyCam.PixyBlock;

/**
 * This class simulates the PixyCam interface. Based on robot angle, it will change the relative position of the blocks read by the robot
 */
public class PixyCamSim extends ASensorWrapper implements II2CWrapper, BufferCallback
{
    protected final I2CSim mWpiWrapper;
    private final CallbackStore mCallbackStore;
    private List<PixyBlock> mBlocks;

    public PixyCamSim(int aPort)
    {
        super("Pixy Cam");

        mWpiWrapper = new I2CSim(aPort);
        mCallbackStore =  mWpiWrapper.registerReadCallback(this);


        mBlocks = new ArrayList<>();

        PixyBlock block;

        block = new PixyBlock();
        block.centerX = 140;
        block.centerY = 5;
        block.height = 5;
        block.width = 5;
        mBlocks.add(block);

        block = new PixyBlock();
        block.centerX = 160;
        block.centerY = 5;
        block.height = 5;
        block.width = 5;
        mBlocks.add(block);
    }
    
    @Override
    public void callback(String name, byte[] buffer, int count) {
        ByteBuffer aBuffer = ByteBuffer.wrap(buffer);

        aBuffer.order(ByteOrder.LITTLE_ENDIAN);
        aBuffer.put((byte) 0x55);
        aBuffer.put((byte) 0xaa);

        for (PixyBlock block : mBlocks)
        {
            aBuffer.putShort((short) 0xaa55); // sync
            aBuffer.putShort((short) 0xBEEF); // checksum
            aBuffer.putShort((short) 0x0100); // signature
            aBuffer.putShort((short) block.centerX);
            aBuffer.putShort((short) block.centerY);
            aBuffer.putShort((short) block.width);
            aBuffer.putShort((short) block.height);
        }
    }
    
    public void close() throws Exception
    {
        super.close();
        mCallbackStore.close();
    }
}
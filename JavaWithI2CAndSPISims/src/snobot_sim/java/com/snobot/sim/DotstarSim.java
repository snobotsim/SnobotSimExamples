
package com.snobot.sim;

import java.util.Arrays;

import com.snobot.simulator.module_wrapper.ASensorWrapper;
import com.snobot.simulator.module_wrapper.interfaces.ISpiWrapper;

import edu.wpi.first.hal.sim.CallbackStore;
import edu.wpi.first.hal.sim.ConstBufferCallback;
import edu.wpi.first.hal.sim.SPISim;

public class DotstarSim extends ASensorWrapper implements ISpiWrapper, ConstBufferCallback
{
    protected final SPISim mWpiWrapper;
    protected final CallbackStore mWriteStore;
    protected byte[] mLastWrite;

    public DotstarSim(int aPort)
    {
        super("Dotstar");

        mWpiWrapper = new SPISim(aPort);
        mWriteStore = mWpiWrapper.registerWriteCallback(this);

    }

    @Override
    public void callback(String aName, byte[] aBuffer, int aCount) 
    {
        mLastWrite = Arrays.copyOf(aBuffer, aCount);
    }
    
    @Override
    public void close() throws Exception
    {
        super.close();
        mWriteStore.close();
    }

    public byte[] getLastWrite()
    {
        return mLastWrite;
    }
}

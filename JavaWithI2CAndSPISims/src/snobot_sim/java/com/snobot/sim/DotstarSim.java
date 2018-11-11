
package com.snobot.sim;

import com.snobot.simulator.module_wrapper.ASensorWrapper;
import com.snobot.simulator.module_wrapper.interfaces.ISpiWrapper;

import edu.wpi.first.hal.sim.CallbackStore;
import edu.wpi.first.hal.sim.ConstBufferCallback;
import edu.wpi.first.hal.sim.SPISim;

public class DotstarSim extends ASensorWrapper implements ISpiWrapper, ConstBufferCallback
{
    protected final SPISim mWpiWrapper;
    protected final CallbackStore mWriteStore;

    public DotstarSim(int aPort)
    {
        super("Dotstar");

        mWpiWrapper = new SPISim(aPort);
        mWriteStore = mWpiWrapper.registerWriteCallback(this);

    }

    @Override
    public void callback(String name, byte[] buffer, int count) {
        // System.out.println("Getting callback..." + name + ", " + Arrays.toString(buffer));
    }
    
    public void close() throws Exception
    {
        super.close();
        mWriteStore.close();
    }
}
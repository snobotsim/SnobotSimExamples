package com.snobot.sim;

import java.util.ArrayList;
import java.util.Collection;

import com.snobot.simulator.SensorActuatorRegistry;
import com.snobot.simulator.module_wrapper.factories.DefaultI2CSimulatorFactory;
import com.snobot.simulator.module_wrapper.interfaces.II2CWrapper;

/**
 * When adding your own I2C wrapper, you must create a new factory which knows about it. The logic
 * is pretty simple, create a name for your object, and intercept calls going to the basic factory
 * in SnobotSim to create your thing if needed.  
 */
public class SnobotI2CFactory extends DefaultI2CSimulatorFactory
{
    private static final String sPIXYCAM_TYPE = "Pixy Cam";

    @Override
    public boolean create(int aPort, String aType)
    {
        if (sPIXYCAM_TYPE.equals(aType))
        {
            PixyCamSim simulator = new PixyCamSim(aPort);
            SensorActuatorRegistry.get().register(simulator, aPort);
            return true;
        }

        return super.create(aPort, aType);
    }

    @Override
    protected String getNameForType(II2CWrapper aType)
    {
        if (aType instanceof PixyCamSim)
        {
            return sPIXYCAM_TYPE;
        }

        return super.getNameForType(aType);
    }

    @Override
    public Collection<String> getAvailableTypes()
    {
        ArrayList<String> output = new ArrayList<>(super.getAvailableTypes());
        output.add(sPIXYCAM_TYPE);
        return output;
    }
}

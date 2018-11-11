package com.snobot.sim;

import java.util.ArrayList;
import java.util.Collection;

import com.snobot.simulator.SensorActuatorRegistry;
import com.snobot.simulator.module_wrapper.factories.DefaultSpiSimulatorFactory;
import com.snobot.simulator.module_wrapper.interfaces.ISpiWrapper;

/**
 * When adding your own SPI wrapper, you must create a new factory which knows about it. The logic
 * is pretty simple, create a name for your object, and intercept calls going to the basic factory
 * in SnobotSim to create your thing if needed.  
 */
public class SnobotSpiFactory extends DefaultSpiSimulatorFactory
{
    private static final String sDOTSTAR_TYPE = "Dotstar";

    @Override
    public boolean create(int aPort, String aType)
    {
        if (sDOTSTAR_TYPE.equals(aType))
        {
            DotstarSim simulator = new DotstarSim(aPort);
            SensorActuatorRegistry.get().register(simulator, aPort);
            return true;
        }

        return super.create(aPort, aType);
    }

    @Override
    protected String getNameForType(ISpiWrapper aType)
    {
        if (aType instanceof DotstarSim)
        {
            return sDOTSTAR_TYPE;
        }

        return super.getNameForType(aType);
    }

    @Override
    public Collection<String> getAvailableTypes()
    {
        ArrayList<String> output = new ArrayList<>(super.getAvailableTypes());
        output.add(sDOTSTAR_TYPE);
        return output;
    }
}

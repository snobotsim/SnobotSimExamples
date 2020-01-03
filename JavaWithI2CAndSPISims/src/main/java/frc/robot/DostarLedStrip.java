package frc.robot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import edu.wpi.first.wpilibj.SPI;

/**
 * This class was developed as a way to interface with a <a href="https://www.adafruit.com/product/2241">Adafruit Dotstar</a> 
 *
 * Originally developed by <a href="https://github.com/RobotCasserole1736/CasseroleLib/blob/master/java/src/org/usfirst/frc/team1736/lib/LEDs/DotStarsLEDStrip.java">FRC1736</a> and gratefully borrowed by <a href="https://github.com/ArcticWarriors/snobot-2018/blob/master/RobotCode/snobot2018/src/org/snobot/leds/DostarLedStrip.java">FRC174</a>.
 * It sends LED RGBA values through a SPI port on the robot.
 */
public class DostarLedStrip
{
    private static final int sSPI_CLK_RATE = 13000000;
    private static final int sMAX_BYTES_PER_MESSAGE = 124; // Max size the hardware supports is 127.  Rounding down to an 4 byte boundary

    private static final double sMAX_BRIGHTNESS = .6; // Brightness scaling factor, 0-1

    private static final int sBYTES_PER_LED = 4;
    private static final int sLED_ZERO_OFFSET = 4;

    private final int mBufferSize;

    private final int mColorUnusedOffset;
    private final int mColorOffsetRed;
    private final int mColorOffsetGreen; // Note: Green and blue are backwards
    private final int mColorOffsetBlue;

    private final SPI mSpi;
    private final ByteBuffer mDataBuffer;

    /**
     * Constructor.
     * 
     * @param aNumLeds
     *            The number of LEDs to talk to. Used to specify the magic word
     *            for "end of transmission"
     * @param aPort
     *            The SPI port this is connected to
     */
    public DostarLedStrip(int aNumLeds, SPI.Port aPort)
    {
        mBufferSize = aNumLeds * sBYTES_PER_LED + 4 + 4;
        mDataBuffer = ByteBuffer.allocate(mBufferSize);
        mDataBuffer.order(ByteOrder.LITTLE_ENDIAN);

        mColorUnusedOffset = 0;
        mColorOffsetRed = 3;
        mColorOffsetGreen = 2; // Note: Green and blue are backwards
        mColorOffsetBlue = 1;

        mSpi = new SPI(aPort);
        mSpi.setMSBFirst();
        mSpi.setClockActiveLow();
        mSpi.setClockRate(sSPI_CLK_RATE);
        mSpi.setSampleDataOnTrailingEdge();

        // Populate the start of the message
        for (int i = 0; i < sBYTES_PER_LED; ++i)
        {
            mDataBuffer.put(i, (byte) 0);
        }

        // Populate the start of the message
        int endStart = mBufferSize - sBYTES_PER_LED;
        for (int i = endStart; i < endStart + sBYTES_PER_LED; ++i)
        {
            mDataBuffer.put(i, (byte) 255);
        }
    }
    
    public void updateStrip()
    {
        // Make local copy of ledBuffer to help make color changing clean
        byte[] bufferCopy = new byte[mBufferSize];
        mDataBuffer.position(0);
        mDataBuffer.get(bufferCopy);

        for (int offset = 0; offset < bufferCopy.length; offset = offset + sMAX_BYTES_PER_MESSAGE)
        {
            int startIndex = offset;
            int endIndex = Math.min(offset + sMAX_BYTES_PER_MESSAGE, bufferCopy.length);
            int size = endIndex - startIndex;
            byte[] txArray = Arrays.copyOfRange(bufferCopy, startIndex, endIndex);

            mSpi.write(txArray, size);
        }

    }

    public void setColor(int aLedIndex, int aRed, int aGreen, int aBlue)
    {
        mDataBuffer.put(aLedIndex * sBYTES_PER_LED + sLED_ZERO_OFFSET + mColorUnusedOffset, (byte) 255);
        mDataBuffer.put(aLedIndex * sBYTES_PER_LED + sLED_ZERO_OFFSET + mColorOffsetRed, (byte) (aRed * sMAX_BRIGHTNESS));
        mDataBuffer.put(aLedIndex * sBYTES_PER_LED + sLED_ZERO_OFFSET + mColorOffsetGreen, (byte) (aGreen * sMAX_BRIGHTNESS));
        mDataBuffer.put(aLedIndex * sBYTES_PER_LED + sLED_ZERO_OFFSET + mColorOffsetBlue, (byte) (aBlue * sMAX_BRIGHTNESS));
    }
}

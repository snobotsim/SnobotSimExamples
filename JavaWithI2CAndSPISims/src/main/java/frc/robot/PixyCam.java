package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * This class talks to a PixyCam.
 * 
 * I'm not sure how specific this example is, I have not worked with a PixyCam
 * before. This logic is borrwed from <a href=
 * "https://github.com/Vandle879/RoboSquad2017PracticeBot/blob/master/RoboSquad2017PracticeBot/src/org/usfirst/frc/team558/robot/subsystems/PixyCam.java">
 * FRC558 </a>, the original SnobotSim beta testers.
 * <p>
 * It read values coming from an onboard I2C port
 */
public class PixyCam extends Subsystem
{

    private static final int[] distances = { 0, 113, 81, 62, 49, 41, 35, 30, 28, 25, 22, 21 };
    public static final double PIXY_FOV = 75; // Changes with lens change
    public static final double IMAGE_WIDTH = 320.0;
    public static final double GEAR_WIDTH_FT = 1.166;
    public static final int BLOCK_SIZE = 14;
    public static int PIXY_ADDRESS = 0x54;

    private final List<PixyBlock> mPixyBlocks;
    private final I2C mPort;
    private boolean mInRange;
    private double mOffset;


    public PixyCam(I2C.Port aPort)
    {
        mPixyBlocks = new ArrayList<>();
        mPort = new I2C(aPort, PIXY_ADDRESS);
    }

    public void setLastOffset(double aOffset)
    {
        if (aOffset > 1)
        {
            this.mOffset = aOffset;
            setInRange(true);
        }
    }

    public double getLastOffset()
    {
        return mOffset;
    }

    public void setInRange(boolean aInRange)
    {
        this.mInRange = aInRange;
    }

    public double getDistance(double aWidth, double aTargetCenter)
    {
        int index = 0;
        int smallest = 1000;
        for (int i = 0; i < distances.length; i++)
        {
            if (Math.abs(aWidth - distances[i]) < smallest)
            {
                index = i;
                smallest = Math.abs((int) (aWidth - distances[i]));
            }
        }
        double distance = index;
        return index;
    }

    public List<PixyBlock> read()
    {
        mPixyBlocks.clear();
        byte[] bytes = new byte[64];
        mPort.read(0x54, 64, bytes);
        int index = 0;
        for (; index < bytes.length - 1; ++index)
        {
            int b1 = bytes[index];
            if (b1 < 0)
            {
                b1 += 256;
            }

            int b2 = bytes[index + 1];
            if (b2 < 0)
            {
                b2 += 256;
            }

            if (b1 == 0x55 && b2 == 0xaa)
            {
                break;
            }
        }

        if (index == 63)
        {    
            return null;
        }
        else if (index == 0)
        {
            index += 2;
        }
        for (int byteOffset = index; byteOffset < bytes.length - BLOCK_SIZE - 1;)
        {
            // checking for sync block
            int b1 = bytes[byteOffset];
            if (b1 < 0)
            {
                b1 += 256;
            }

            int b2 = bytes[byteOffset + 1];
            if (b2 < 0)
            {
                b2 += 256;
            }

            if (b1 == 0x55 && b2 == 0xaa)
            {
                // copy block into temp buffer
                byte[] temp = new byte[BLOCK_SIZE];
                StringBuilder sb = new StringBuilder("Data : ");
                for (int tempOffset = 0; tempOffset < BLOCK_SIZE; ++tempOffset)
                {
                    temp[tempOffset] = bytes[byteOffset + tempOffset];
                    sb.append(temp[tempOffset]).append(", ");
                }

                PixyBlock block = bytesToBlock(temp);

                // Added so blocks are only added if their signature is 1 to remove noise from
                // signal
                if (block.mSignature == 1)
                {
                    mPixyBlocks.add(block);
                    byteOffset += BLOCK_SIZE - 1;
                }
                else
                {
                    ++byteOffset;
                }
            }
            else
            {
                ++byteOffset;
            }
        }

        if (!mPixyBlocks.isEmpty())
        {
            if (mPixyBlocks.size() >= 2)
            {
                PixyBlock leftBlock;
                PixyBlock rightBlock;
                if (mPixyBlocks.get(0).mCenterX > mPixyBlocks.get(1).mCenterX)
                {
                    leftBlock = mPixyBlocks.get(1);
                    rightBlock = mPixyBlocks.get(0);
                }
                else
                {
                    leftBlock = mPixyBlocks.get(0);
                    rightBlock = mPixyBlocks.get(1);
                }
                double difference = (rightBlock.mCenterX + leftBlock.mCenterX) / 2;
                setLastOffset(difference);
                double total = (rightBlock.mCenterX) - (leftBlock.mCenterX);
                getDistance(total, difference);
            }

            else
            {

                setLastOffset(127); // Keeps robot going straight if only one signal is picked up

            }
        }
        else
        {
            setLastOffset(127); // Keeps robot going straight if nothing is picked up
            setInRange(false);
        }
        return mPixyBlocks;
    }

    public PixyBlock bytesToBlock(byte[] aBytes)
    {
        PixyBlock pixyBlock = new PixyBlock();
        pixyBlock.mSync = bytesToInt(aBytes[1], aBytes[0]);
        pixyBlock.mChecksum = bytesToInt(aBytes[3], aBytes[2]);

        pixyBlock.mSignature = orBytes(aBytes[5], aBytes[4]);
        pixyBlock.mCenterX = (((int) aBytes[7] & 0xff) << 8) | ((int) aBytes[6] & 0xff);
        pixyBlock.mCenterY = (((int) aBytes[9] & 0xff) << 8) | ((int) aBytes[8] & 0xff);
        pixyBlock.mWidth = (((int) aBytes[11] & 0xff) << 8) | ((int) aBytes[10] & 0xff);
        pixyBlock.mHeight = (((int) aBytes[13] & 0xff) << 8) | ((int) aBytes[12] & 0xff);
        return pixyBlock;
    }

    public int orBytes(byte aB1, byte aB2)
    {
        return (aB1 & 0xff) | (aB2 & 0xff);
    }

    public int bytesToInt(int aB1, int aB2)
    {
        if (aB1 < 0)
        {
            aB1 += 256;
        }

        if (aB2 < 0)
        {
            aB2 += 256;
        }

        int intValue = aB1 * 256;
        intValue += aB2;
        return intValue;
    }

    public boolean isInRange()
    {
        return mInRange;
    }

    public static class PixyBlock
    {
        // 0, 1 0 sync (0xaa55)
        // 2, 3 1 checksum (sum of all 16-bit words 2-6)
        // 4, 5 2 signature number
        // 6, 7 3 x center of object
        // 8, 9 4 y center of object
        // 10, 11 5 width of object
        // 12, 13 6 height of object

        // read byte : 85 read byte : -86
        // read byte : 85 read byte : -86
        // read byte : 22 read byte : 1
        // read by
        // read byte : -128 read byte : 0
        // read byte : 118 read byte : 0
        // read byte : 22 read byte : 0

        public int mSync;
        public int mChecksum;
        public int mSignature;
        public int mCenterX;
        public int mCenterY;
        public int mWidth;
        public int mHeight;
    }

    @Override
    protected void initDefaultCommand()
    {

    }
}

package frc.robot;

public class Positioner
{
    private double mX;
    private double mY;
    private double mAngle;

    public Positioner()
    {
        
    }

    public void setPosition(double aX, double aY, double aAngle)
    {
        mX = aX;
        mY = aY;
        mAngle = aAngle;
    }

    public double getX()
    {
        return mX;
    }

    public double getY()
    {
        return mY;
    }

    public double getAngle()
    {
        return mAngle;
    }
}

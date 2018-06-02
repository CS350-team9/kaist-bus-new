package kr.ac.kaist.kyotong.utils;

public class LocationCoordinates {
    public double latitude;
    public double longitude;

    public LocationCoordinates(double latitude, double longitude)
    {
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public LocationCoordinates()
    {
        this(0.0, 0.0);
    }
}

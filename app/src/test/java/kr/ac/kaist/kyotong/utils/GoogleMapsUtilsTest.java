package kr.ac.kaist.kyotong.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import static org.junit.Assert.assertThat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class GoogleMapsUtilsTest {
    @Test
    public void sphericalUtil_interpolateTest() {
        LatLng a, b;

        a = new LatLng(0, 0);
        b = new LatLng(3, 8);
        assertThat(SphericalUtil.interpolate(a, b, 0), is(a, 0.001));
        assertThat(SphericalUtil.interpolate(a, b, 1), is(b, 0.001));

        a = new LatLng(44, 12);
        b = new LatLng(87, 20);
        assertThat(SphericalUtil.interpolate(a, b, 0), is(a, 0.001));
        assertThat(SphericalUtil.interpolate(a, b, 1), is(b, 0.001));

        ArrayList<LatLng> points = new ArrayList<>(Arrays.asList(
                new LatLng(0, 0),
                new LatLng(2, 3),
                new LatLng(5, 7.5),
                new LatLng(8, 12),
                new LatLng(9, 13.5)
        ));

        assertThat(GoogleMapsUtils.interpolate(points, 0.0001), is(points.get(0), 0.01));
        assertThat(GoogleMapsUtils.interpolate(points, 0.9999), is(points.get(4), 0.01));
    }

    public static TypeSafeMatcher<LatLng> is(final LatLng point, final double precision) {
        return new TypeSafeMatcher<LatLng>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("must match %s with precision of %f", point, precision));
            }

            @Override
            protected boolean matchesSafely(LatLng item) {
                return Math.abs(item.latitude - point.latitude) < precision
                        && Math.abs(item.longitude - point.longitude) < precision;
            }
        };
    }
}

package jcsobrino.tddm.uoc.sharetool.common;

import android.location.Location;

import java.util.Random;

import jcsobrino.tddm.uoc.sharetool.domain.Tool;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;

/**
 * Created by JoséCarlos on 18/11/2015.
 */
public final class UtilFunctions {

    private static final Random RANDOM = new Random();

    private UtilFunctions() {

    }

    public static final Float randomBetween(final Float minFloat, final Float maxFloat) {

        return RANDOM.nextFloat() * (maxFloat - minFloat) + minFloat;
    }

    public static final Integer randomBetween(final Integer minInteger, final Integer maxInteger) {

        return RANDOM.nextInt(maxInteger - minInteger) + minInteger;
    }

    public static final Float calculateDistance(final ITool tool, final float lat, final float lng) {

        float[] distance = new float[3];
        Location.distanceBetween(tool.getPositionLat(), tool.getPositionLng(), lat, lng, distance);
        return distance[0] / 1000;
    }

    public static final Float calculateDistance(final ITool tool) {

        Location currentLocation = LocationService.getCurrentLocation();
        if (currentLocation != null) {
            float[] distance = new float[3];
            Location.distanceBetween(tool.getPositionLat(), tool.getPositionLng(), currentLocation.getLatitude(), currentLocation.getLongitude(), distance);
            return distance[0] / 1000;
        }
        return null;
    }
}

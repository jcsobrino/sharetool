package jcsobrino.tddm.uoc.sharetool.common;

import android.location.Location;
import android.text.Editable;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Random;

import jcsobrino.tddm.uoc.sharetool.domain.ITool;

/**
 * Clase de utilidades con funciones comunes
 * Created by JoséCarlos on 18/11/2015.
 */
public final class UtilFunctions {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final Random RANDOM = new Random();
    private static final String IMAGE_GENERATOR_URL = "http://lorempixel.com/600/300/?id=%s";

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

    public static boolean isEmpty(final Editable field){

        return TextUtils.isEmpty(field) || TextUtils.isEmpty(field.toString().trim());
    }

    public static String getImagePlaceholder(final Long id){

        return String.format(IMAGE_GENERATOR_URL, id);
    }
}

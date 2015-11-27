package jcsobrino.tddm.uoc.sharetool.common;

import java.util.Random;

/**
 * Created by JoséCarlos on 18/11/2015.
 */
public final class UtilFunctions {

    private static final Double FACTOR = 57.29577951308232087679815481410517033235;
    private static final Random RANDOM = new Random();

    private UtilFunctions(){

    }

    public static final Float randomBetween(final Float minFloat, final Float maxFloat){

        return RANDOM.nextFloat() * (maxFloat - minFloat) + minFloat;
    }

    public static final Integer randomBetween(final Integer minInteger, final Integer maxInteger){

        return RANDOM.nextInt(maxInteger - minInteger) + minInteger;
    }

    public static final Float calculateDistance(final Float latFrom, final Float lngFrom, final Float latTo, final Float lngTo){

        Double lat_rad = latTo / FACTOR;
        Double lng_rad = lngTo / FACTOR;
        Double f_lat_rad = latFrom / FACTOR;
        Double f_lng_rad = lngFrom / FACTOR;

        return new Float(6371 * Math.acos(Math.cos(f_lat_rad) * Math.cos(lat_rad) * Math.cos(lng_rad - f_lng_rad) + Math.sin(f_lat_rad) * Math.sin(lat_rad)));
    }
}

package jcsobrino.tddm.uoc.sharetool.dto;


import java.io.Serializable;

import jcsobrino.tddm.uoc.sharetool.domain.User;

/**
 * Created by JoséCarlos on 13/11/2015.
 */
public interface ITool extends Serializable{


    Long getId();

   // void setId(Integer id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Float getPricePerDay();

    void setPricePerDay(Float pricePerDay);

    User getUser();

    void setUser(User user);

    Float getPositionLat();

    void setPositionLat(Float positionLat);

    Float getPositionLng();

    void setPositionLng(Float positionLng);

    String getMainImageCode();

    void setMainImageCode(String mainImageCode);

    String[] getSecondaryImageCodes();

    void setSecondaryImageCodes(String[] secondaryImageCodes);

    Float getDistanceInKilometers();
}

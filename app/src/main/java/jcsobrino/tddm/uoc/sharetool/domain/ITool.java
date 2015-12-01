package jcsobrino.tddm.uoc.sharetool.domain;


import java.io.Serializable;

import jcsobrino.tddm.uoc.sharetool.domain.impl.User;

/**
 * Interfaz que representa una Herramienta en el dominio de la aplicación
 * La fuente proveedora deberá implementar esta interfaz para representar esta entidad
 * Created by JoséCarlos on 13/11/2015.
 */
public interface ITool extends Serializable{

    Long getId();

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

    Float getDistanceInKilometers();
}

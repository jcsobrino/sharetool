package jcsobrino.tddm.uoc.sharetool.service;


import java.util.List;

import jcsobrino.tddm.uoc.sharetool.common.ToolOrderEnum;
import jcsobrino.tddm.uoc.sharetool.domain.ITool;
import jcsobrino.tddm.uoc.sharetool.domain.IUser;

/**
 * Interfaz con las funciones necesarias para gestionar las herramientas y usuarios de la aplicación
 * La fuente proveedora de la informacion de dominio debe implementar esta interfaz y añadir dicha
 * implementación a la clase ApiFactory
 * Created by JoséCarlos on 13/11/2015.
 */
public interface ApiService {

    IUser login(final String email, final String password);

    boolean userExistsByEmail(final String email);

    IUser createUser(final String name, final String email, final String password);

    List<? extends ITool> findTools(final String name, final Float maxPrice, final Float maxKilometers, final Float lat, final Float Lng, final ToolOrderEnum toolOrder);

    ITool getToolById(final Integer id);

    IUser getUserById(final Integer id);
}

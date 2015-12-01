package jcsobrino.tddm.uoc.sharetool.common;


import jcsobrino.tddm.uoc.sharetool.service.ApiService;
import jcsobrino.tddm.uoc.sharetool.service.impl.ApiServiceImpl;

/**
 * Factoría de creación de servicios que implementan la API con las funciones de gestión de entidades de dominio
 * Contiene una única implementación basada en SQLite
 * Created by JoséCarlos on 13/11/2015.
 */
public enum ApiFactory {
    INSTANCE;

    private ApiService[] listServices = {new ApiServiceImpl()};

    public ApiService getApi() {
        return listServices[0];
    }
}

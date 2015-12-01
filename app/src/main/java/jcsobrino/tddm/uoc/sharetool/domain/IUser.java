package jcsobrino.tddm.uoc.sharetool.domain;

import java.io.Serializable;

/**
 * Interfaz que representa un Usuario en el dominio de la aplicación
 * La fuente proveedora deberá implementar esta interfaz para representar esta entidad
 * Created by JoséCarlos on 13/11/2015.
 */
public interface IUser extends Serializable{

    Long getId();

    String getName();

    void setName(String name);

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);
}

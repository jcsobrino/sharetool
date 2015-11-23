package jcsobrino.tddm.uoc.sharetool.dto;

import java.io.Serializable;

/**
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

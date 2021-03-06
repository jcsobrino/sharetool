package jcsobrino.tddm.uoc.sharetool.domain.impl;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import jcsobrino.tddm.uoc.sharetool.domain.IUser;

/**
 * Implementación de la interfaz IUser para SQLite
 * Created by JoséCarlos on 13/11/2015.
 */
@Table(name = "Users")
public class User extends Model implements IUser {

    @Column(notNull = true)
    private String name;
    @Column(notNull = true, unique = true)
    private String email;
    @Column(notNull = true)
    private String password;

    public User() {
        super();
    }

    public User(String name, String email, String password) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

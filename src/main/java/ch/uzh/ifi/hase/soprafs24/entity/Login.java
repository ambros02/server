package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

public class Login{

    private Boolean usernameExists;
    private Boolean passwordCorrect;
    private String token;

    public Login(Boolean une, Boolean pc, String t){
        this.usernameExists = une;
        this. passwordCorrect = pc;
        this.token = t;
    }

    public Boolean getUsernameExists() {
        return usernameExists;
    }

    public void setUsernameExists(Boolean usernameExists){
        this.usernameExists = usernameExists;
    }

    public Boolean getPasswordCorrect() {
        return passwordCorrect;
    }

    public void setPasswordCorrect(Boolean passwordCorrect){
        this.passwordCorrect = passwordCorrect;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
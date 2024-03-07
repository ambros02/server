package ch.uzh.ifi.hase.soprafs24.entity;


public class Login{

    private Boolean usernameExists;
    private Boolean passwordCorrect;
    private String token;
    private Long id;

    public Login(Boolean une, Boolean pc, String t, Long i){
        this.usernameExists = une;
        this. passwordCorrect = pc;
        this.token = t;
        this.id = i;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
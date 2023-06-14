package eu.europa.esig.dss.web.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String number;

    protected User() {}

    public User(String username, String password, String number) {
        this.username = username;
        this.password = password;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return String.format(
                "User[id=%d, username='%s', password='%s', number='%s']",
                id, username, password, number);
    }
}

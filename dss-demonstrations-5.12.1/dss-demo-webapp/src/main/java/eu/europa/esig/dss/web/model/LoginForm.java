package eu.europa.esig.dss.web.model;

public class LoginForm {
    
    private String username;
    private String password;

    public LoginForm() {
    }

    public LoginForm(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LoginForm[");
        sb.append("username=").append(username).append(", ");
        sb.append("password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}

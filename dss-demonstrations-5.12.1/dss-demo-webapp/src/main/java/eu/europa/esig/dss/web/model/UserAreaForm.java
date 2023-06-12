package eu.europa.esig.dss.web.model;

public class UserAreaForm {
    
    private String number;

    public UserAreaForm() {
    }

    public UserAreaForm(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserAreaForm[");
        sb.append("number=").append(number);
        sb.append("]");
        return sb.toString();
    }
}
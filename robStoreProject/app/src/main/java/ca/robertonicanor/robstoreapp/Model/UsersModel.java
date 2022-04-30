package ca.robertonicanor.robstoreapp.Model;

public class UsersModel {

    private String name,
            password,
            username,
            address;

    public UsersModel(){}

    public UsersModel(String name, String password, String username, String address) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

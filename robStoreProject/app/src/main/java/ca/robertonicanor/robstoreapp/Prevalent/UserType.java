package ca.robertonicanor.robstoreapp.Prevalent;

public class UserType {
    private boolean isAdmin;

    public UserType() {
    }

    public UserType(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}

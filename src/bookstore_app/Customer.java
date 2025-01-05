package bookstore_app;

public class Customer {
    private final String username;
    private final String password;
    private int points;

    //Constructor
    public Customer(String username, String password, int points) {
        this.username = username;
        this.password = password;
        this.points = points;
    }

    //Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoints() {
        return points;
    }
    
    public String getStatus() {
        return points >= 1000 ? "Gold" : "Silver";
    }

    //Setters
    public void setPoints(int points) {
        this.points = points;
    }
}



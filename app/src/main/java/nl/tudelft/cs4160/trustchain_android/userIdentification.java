package nl.tudelft.cs4160.trustchain_android;

/**
 * Created by Boning on 12/3/2017.
 */

public class userIdentification {

    private int imageIndex;
    private String username;

    public userIdentification(String username, int imageIndex) {
        this.imageIndex = imageIndex;
        this.username = username;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
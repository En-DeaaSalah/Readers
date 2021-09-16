package wifi.codewl.readers.model.channel;

import android.graphics.Bitmap;

import wifi.codewl.readers.model.Model;

public class Channel_Profile extends Model {

    private int suc_count;

    private Bitmap profileBackground;

    private Bitmap profileImage;

    private String channelName;

    public static final int TYPE_ID = 1;

    public Channel_Profile() {
    }

    public int getSuc_count() {
        return suc_count;
    }

    public void setSuc_count(int suc_count) {
        this.suc_count = suc_count;
    }


    public Channel_Profile(Bitmap profileBackground, Bitmap profileImage, String channelName) {
        super(TYPE_ID);
        modelType = TYPE_ID;
        this.profileBackground = profileBackground;
        this.profileImage = profileImage;
        this.channelName = channelName;
    }

    public Bitmap getProfileBackground() {
        return profileBackground;
    }

    public void setProfileBackground(Bitmap profileBackground) {
        this.profileBackground = profileBackground;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getChannelName() {
        return channelName;
    }


}

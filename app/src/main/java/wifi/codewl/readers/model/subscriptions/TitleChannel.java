package wifi.codewl.readers.model.subscriptions;

import android.graphics.Bitmap;

import wifi.codewl.readers.model.channel.Channel_Profile;

public class TitleChannel extends Subscriptions {




    private String ChannelId;

    private Bitmap profileImage;

    private String channel_name;

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public TitleChannel(String channelId, Bitmap channel_profile, String channel_name) {
        ChannelId = channelId;
        this.profileImage = profileImage;
        this.channel_name = channel_name;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }











}

package wifi.codewl.readers.model.channel;

import java.util.ArrayList;

import wifi.codewl.readers.model.Model;

public class Channel extends Model {


    private String ChannelId;



    private Channel_Profile channel_profile;

    private ArrayList<Channel_File> channel_files;

    private String description_text="";

    private String Channel_create_date;

    public String getChannel_create_date() {
        return Channel_create_date;
    }

    public void setChannel_create_date(String channel_create_date) {
        Channel_create_date = channel_create_date;
    }



    public String getDescription_text() {
        return description_text;
    }

    public void setDescription_text(String description_text) {
        this.description_text = description_text;
    }

    public void add_file(Channel_File file) {


        channel_files.add(file);


    }

    public ArrayList<Channel_File> getChannel_files() {
        return channel_files;
    }

    public void setChannel_files(ArrayList<Channel_File> channel_files) {
        this.channel_files = channel_files;
    }

    public Channel() {
    }

    public Channel(String channelId, Channel_Profile channel_profile) {
        ChannelId = channelId;
        this.channel_profile = channel_profile;
        channel_files = new ArrayList<>();
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public Channel_Profile getChannel_profile() {
        return channel_profile;
    }



    public void setChannel_profile(Channel_Profile channel_profile) {
        this.channel_profile = channel_profile;
    }
}

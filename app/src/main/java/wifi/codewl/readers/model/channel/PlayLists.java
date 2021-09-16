package wifi.codewl.readers.model.channel;

import java.io.File;
import java.util.ArrayList;

import wifi.codewl.readers.model.Model;

public class PlayLists extends Model {

    private int playListId;
    private String playListName;
    private int file_count;
    private String channel_name;
    private String background_image;
    private ArrayList<Integer>playListFilesIds;

    public PlayLists(int playListId, String playListName, int file_count, String channel_name, String background_image, ArrayList<Integer>playListFilesIds) {
        this.playListId = playListId;
        this.playListName = playListName;
        this.file_count = file_count;
        this.channel_name = channel_name;
        this.background_image = background_image;
        this.playListFilesIds = playListFilesIds;
    }


    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public int getPlayListId() {
        return playListId;
    }

    public void setPlayListId(int playListId) {
        this.playListId = playListId;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public int getFile_count() {
        return file_count;
    }

    public void setFile_count(int file_count) {
        this.file_count = file_count;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public ArrayList<Integer> getPlayListFilesIds() {
        return playListFilesIds;
    }

    public void setPlayListFilesIds(ArrayList<Integer>playListFilesIds) {
        this.playListFilesIds = playListFilesIds;
    }
}

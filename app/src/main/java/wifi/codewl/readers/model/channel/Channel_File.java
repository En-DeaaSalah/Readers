package wifi.codewl.readers.model.channel;

import android.graphics.Bitmap;

import java.io.Serializable;

import wifi.codewl.readers.model.Model;

public class Channel_File extends Model implements Serializable {


    private String title;
    private String url_on_server;
    private String id;
    private String description;
    private String file_background;

    private String upload_date;
    private int view_Counter;


    public static final int TYPE_ID = 1;


    public Channel_File(String title, String description) {
        super(TYPE_ID);
        modelType = TYPE_ID;
        this.title = title;
        this.description = description;

    }

    public Channel_File(String id, String title, String description, String file_background, String url_on_server, String upload_date,int view_Counter) {
        super(TYPE_ID);
        modelType = TYPE_ID;
        this.title = title;
        this.description = description;
        this.id = id;
        this.file_background = file_background;
        this.url_on_server = url_on_server;
        this.upload_date = upload_date;
        this.view_Counter=view_Counter;
    }

    public String getFile_background() {
        return file_background;
    }

    public void setFile_background(String file_background) {
        this.file_background = file_background;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl_on_server() {
        return url_on_server;
    }

    public void setUrl_on_server(String url_on_server) {
        this.url_on_server = url_on_server;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getView_Counter() {
        return view_Counter;
    }

    public void setView_Counter(int view_Counter) {
        this.view_Counter = view_Counter;
    }
}

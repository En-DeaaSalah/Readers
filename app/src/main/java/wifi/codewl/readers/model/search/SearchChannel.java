package wifi.codewl.readers.model.search;


import android.graphics.Bitmap;

public class SearchChannel extends Search {

    private Bitmap imageChannel;
    private String nameChannel;
    private int numberSub,numberFiles,idChannel;

    public Bitmap getImageChannel() {
        return imageChannel;
    }

    public int getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(int idChannel) {
        this.idChannel = idChannel;
    }

    public void setImageChannel(Bitmap imageChannel) {
        this.imageChannel = imageChannel;
    }

    public String getNameChannel() {
        return nameChannel;
    }

    public void setNameChannel(String nameChannel) {
        this.nameChannel = nameChannel;
    }

    public int getNumberSub() {
        return numberSub;
    }

    public void setNumberSub(int numberSub) {
        this.numberSub = numberSub;
    }

    public int getNumberFiles() {
        return numberFiles;
    }

    public void setNumberFiles(int numberFiles) {
        this.numberFiles = numberFiles;
    }

    private boolean subscribed;

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}

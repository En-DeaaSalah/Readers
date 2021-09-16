package wifi.codewl.readers.model.library;

import android.graphics.Bitmap;

public class History extends Library {
    private Bitmap imageFile = null;
    private String nameFile,nameChannel,dateFile,urlFile;
    private int idFile,idHistory;
    private int numberViews;

    public int getIdFile() {
        return idFile;
    }

    public int getIdHistory() {
        return idHistory;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public void setIdHistory(int idHistory) {
        this.idHistory = idHistory;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public Bitmap getImageFile() {
        return imageFile;
    }

    public void setImageFile(Bitmap imageFile) {
        this.imageFile = imageFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getNameChannel() {
        return nameChannel;
    }

    public void setNameChannel(String nameChannel) {
        this.nameChannel = nameChannel;
    }

    public String getDateFile() {
        return dateFile;
    }

    public void setDateFile(String dateFile) {
        this.dateFile = dateFile;
    }

    public int getNumberViews() {
        return numberViews;
    }

    public void setNumberViews(int numberViews) {
        this.numberViews = numberViews;
    }
}

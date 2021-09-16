package wifi.codewl.readers.model.library;

import android.graphics.Bitmap;

public class RecentHistory extends Library {
    private Bitmap ImageFile;
    private String nameFile,nameChannel,urlFile;
    private int idFile,idHistory;
    public Bitmap getImageFile() {
        return ImageFile;
    }

    public int getIdFile() {
        return idFile;
    }

    public int getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(int idHistory) {
        this.idHistory = idHistory;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public void setImageFile(Bitmap imageFile) {
        ImageFile = imageFile;
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
}

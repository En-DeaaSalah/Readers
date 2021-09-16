package wifi.codewl.readers.model.home;

import android.graphics.Bitmap;


public class Proposal extends Home {
    private int idFile,idChannel;
    private Bitmap imageFile = null;
    private String nameFile;
    private Bitmap imageChannel = null;
    private String nameChannel;
    private int numberViews;
    private String date,urlFile;
    private boolean progress;

    public int getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(int idChannel) {
        this.idChannel = idChannel;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public void setImageFile(Bitmap imageFile) {
        this.imageFile = imageFile;
    }

    public int getIdFile() {
        return idFile;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public void setImageChannel(Bitmap imageChannel) {
        this.imageChannel = imageChannel;
    }

    public void setNameChannel(String nameChannel) {
        this.nameChannel = nameChannel;
    }

    public void setNumberViews(int numberViews) {
        this.numberViews = numberViews;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setProgress(boolean progress) {
        this.progress = progress;
    }

    public Proposal(boolean progress){
        this.progress = progress;
    }

    public Proposal(Bitmap imageFile, String nameFile, Bitmap imageChannel, String nameChannel, int numberViews, String date, boolean progress) {
        this.imageFile = imageFile;
        this.nameFile = nameFile;
        this.imageChannel = imageChannel;
        this.nameChannel = nameChannel;
        this.numberViews = numberViews;
        this.date = date;
        this.progress = progress;
    }


    public Bitmap getImageFile() {
        return imageFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public Bitmap getImageChannel() {
        return imageChannel;
    }

    public String getNameChannel() {
        return nameChannel;
    }

    public int getNumberViews() {
        return numberViews;
    }

    public String getDate() {
        return date;
    }

    public boolean isProgress() {
        return progress;
    }
}

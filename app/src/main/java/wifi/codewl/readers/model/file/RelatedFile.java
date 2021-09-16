package wifi.codewl.readers.model.file;


import android.graphics.Bitmap;

public class RelatedFile extends InformationFile {
    private Bitmap imageFile = null;
    private String nameFile,nameChannel,dateFile,urlFile;
    private int idFile;
    private int numberViews;

    public int getIdFile() {
        return idFile;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
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

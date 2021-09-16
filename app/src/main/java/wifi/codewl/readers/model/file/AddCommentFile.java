package wifi.codewl.readers.model.file;


import android.graphics.Bitmap;

public class AddCommentFile extends InformationFile {
    private Bitmap personalImage;
    private int numberComment,idFile,idChannel;

    public int getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(int idChannel) {
        this.idChannel = idChannel;
    }

    public int getNumberComment() {
        return numberComment;
    }

    public int getIdFile() {
        return idFile;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public void setNumberComment(int numberComment) {
        this.numberComment = numberComment;
    }

    public Bitmap getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(Bitmap personalImage) {
        this.personalImage = personalImage;
    }
}

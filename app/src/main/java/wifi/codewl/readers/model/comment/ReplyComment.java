package wifi.codewl.readers.model.comment;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import wifi.codewl.readers.model.file.ViewCommentFile;

public class ReplyComment extends Comment {
    private Bitmap personalImage;
    private String nameUser,text,dateComment;


    public ReplyComment(){

    }

    public String getDateComment() {
        return dateComment;
    }

    public void setDateComment(String dateComment) {
        this.dateComment = dateComment;
    }

    public Bitmap getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(Bitmap personalImage) {
        this.personalImage = personalImage;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

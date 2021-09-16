package wifi.codewl.readers.model.file;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ViewCommentFile extends InformationFile implements Parcelable {
    private Bitmap personalImage;
    private String nameUser,text,dateComment;
    private int idComment,numberReply,idChannel;

    public int getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(int idChannel) {
        this.idChannel = idChannel;
    }

    public ViewCommentFile(){

    }
    protected ViewCommentFile(Parcel in) {
        personalImage = in.readParcelable(Bitmap.class.getClassLoader());
        nameUser = in.readString();
        text = in.readString();
        dateComment = in.readString();
        idComment = in.readInt();
        numberReply = in.readInt();
    }

    public static final Creator<ViewCommentFile> CREATOR = new Creator<ViewCommentFile>() {
        @Override
        public ViewCommentFile createFromParcel(Parcel in) {
            return new ViewCommentFile(in);
        }

        @Override
        public ViewCommentFile[] newArray(int size) {
            return new ViewCommentFile[size];
        }
    };

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

    public int getIdComment() {
        return idComment;
    }

    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }

    public int getNumberReply() {
        return numberReply;
    }

    public void setNumberReply(int numberReply) {
        this.numberReply = numberReply;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(personalImage, flags);
        dest.writeString(nameUser);
        dest.writeString(text);
        dest.writeString(dateComment);
        dest.writeInt(idComment);
        dest.writeInt(numberReply);
    }
}

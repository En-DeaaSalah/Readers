package wifi.codewl.readers.model.comment;

import android.graphics.Bitmap;

import wifi.codewl.readers.model.file.ViewCommentFile;

public class AddReplyComment extends Comment {
    private ViewCommentFile viewCommentFile;
    private Bitmap UserPersonalImage;

    public ViewCommentFile getViewCommentFile() {
        return viewCommentFile;
    }

    public Bitmap getUserPersonalImage() {
        return UserPersonalImage;
    }

    public void setUserPersonalImage(Bitmap userPersonalImage) {
        UserPersonalImage = userPersonalImage;
    }

    public void setViewCommentFile(ViewCommentFile viewCommentFile) {
        this.viewCommentFile = viewCommentFile;
    }
}

package wifi.codewl.readers.model.channel;

import wifi.codewl.readers.model.Model;

public class Content extends Model {

    private int image;
    public static final int TYPE_ID = 2;


    public Content(int image) {
        super(TYPE_ID);
        this.image = image;
        modelType = TYPE_ID;
    }

    public int getImage() {
        return image;
    }

}

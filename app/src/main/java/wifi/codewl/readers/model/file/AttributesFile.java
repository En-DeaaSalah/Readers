package wifi.codewl.readers.model.file;


import android.graphics.Bitmap;

public class AttributesFile extends InformationFile {

    private boolean expand = false;
    private int idFile,idChannel;
    private boolean subscribed;
    private String titleFile,dateFile,nameChanel,descriptionFile,urlFile;
    private int numberView,numberLike,numberDislike,numberSub;
    private int status;
    private Bitmap imageChannel;

    public String getUrlFile() {
        return urlFile;
    }

    public int getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(int idChannel) {
        this.idChannel = idChannel;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public String getTitleFile() {
        return titleFile;
    }

    public int getIdFile() {
        return idFile;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public void setTitleFile(String titleFile) {
        this.titleFile = titleFile;
    }

    public String getDateFile() {
        return dateFile;
    }

    public void setDateFile(String dateFile) {
        this.dateFile = dateFile;
    }

    public String getNameChanel() {
        return nameChanel;
    }

    public void setNameChanel(String nameChanel) {
        this.nameChanel = nameChanel;
    }

    public String getDescriptionFile() {
        return descriptionFile;
    }

    public void setDescriptionFile(String descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public int getNumberView() {
        return numberView;
    }

    public void setNumberView(int numberView) {
        this.numberView = numberView;
    }

    public int getNumberLike() {
        return numberLike;
    }

    public void setNumberLike(int numberLike) {
        this.numberLike = numberLike;
    }

    public int getNumberDislike() {
        return numberDislike;
    }

    public void setNumberDislike(int numberDislike) {
        this.numberDislike = numberDislike;
    }

    public int getNumberSub() {
        return numberSub;
    }

    public void setNumberSub(int numberSub) {
        this.numberSub = numberSub;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Bitmap getImageChannel() {
        return imageChannel;
    }

    public void setImageChannel(Bitmap imageChannel) {
        this.imageChannel = imageChannel;
    }



    public AttributesFile(boolean subscribed){
        this.subscribed = subscribed;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean isExpand(){
        return expand;
    }
    public void setExpand(boolean expand){
        this.expand = expand;
    }

}
package wifi.codewl.readers.model;

import android.os.Parcelable;

import wifi.codewl.readers.model.progress.ProgressFile;

public abstract class Model  {

    protected  int modelType;


    public  int getModelType() {
        return modelType;
    }
    public void setModelType(int modelType){
        this.modelType = modelType;
    }
    public Model() {
    }

    public Model(int model_Type) {

        modelType = model_Type;
    }

}

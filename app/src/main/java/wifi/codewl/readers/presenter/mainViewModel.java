package wifi.codewl.readers.presenter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class mainViewModel extends ViewModel {


   private  MutableLiveData<String>mutableLiveData=new MutableLiveData<>();


    public void setText(String s){


        mutableLiveData.setValue(s);



    }
    public MutableLiveData<String>getText(){


        return mutableLiveData;
    }


}

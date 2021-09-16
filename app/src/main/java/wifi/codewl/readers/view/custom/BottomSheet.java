package wifi.codewl.readers.view.custom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import wifi.codewl.readers.R;


public class BottomSheet extends BottomSheetDialogFragment {


    private int type;

    public BottomSheet(int type) {

        this.type = type;



    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        if (type == 1) {

            view = inflater.inflate(R.layout.background_bottom_sheet, container, false);

            return view;
        }
        view = inflater.inflate(R.layout.image_bottom_sheet, container, false);
        return view;
    }
}
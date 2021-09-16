package wifi.codewl.readers.view.fragment.subscriptions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import wifi.codewl.readers.R;


 public class MyFragment extends Fragment {




    public MyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view=inflater.inflate(R.layout.fragment_layout,parent,false);


        TextView textView=view.findViewById(R.id.fragmentTextView);
        textView.setText(getArguments().getString("title"));
        return  view;

    }

}
package wifi.codewl.readers.view.fragment.channel;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.presenter.HomeChannelPageAdapter;

public class HomePage extends Rocket {

    RecyclerView pageContent;
    private Context context;

    private HomePage() {

        super.fragmentName = "HomePage_fragment";
        fragmentName = "HomePage_fragment";
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d("diaa",(fragmentName + "   is Resume"));



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("FragmentName",fragmentName);
        View view = inflater.inflate(R.layout.activity_personal_activity, container, false);

        pageContent = view.findViewById(R.id.scrollable_continer);


        ArrayList<Model> items = new ArrayList<>();


        if(Channel_Fragment.MyChannel) {
            items.add(User.getUserReference().getChannel().getChannel_profile());
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
        else{

            items.add(Channel_Fragment.people_channel);

        }





        pageContent.setAdapter(new HomeChannelPageAdapter(items));

        return view;
    }


    public static HomePage newInstance() {
        HomePage HomePageFragment = new HomePage();
        Bundle bundle = new Bundle();
        HomePageFragment.setArguments(bundle);
        return HomePageFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }




}
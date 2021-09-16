package wifi.codewl.readers.view.fragment.channel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;


public class AboutPage extends Rocket {
    private Context context;

    private AboutPage() {
        super.fragmentName = "about_fragment";

        fragmentName = "about_fragment";
    }


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FragmentName",fragmentName);

        View view = inflater.inflate(R.layout.about_fragment, container, false);


        TextView description_text = view.findViewById(R.id.description_area);
        TextView joined_text = view.findViewById(R.id.joined_date_label);
        String text;

        if(Channel_Fragment.MyChannel) {
            description_text.setText(User.getUserReference().getChannel().getDescription_text());
            text = " joined : " + User.getUserReference().getChannel().getChannel_create_date();
        }else{

            description_text.setText(Channel_Fragment.people_channel.getDescription_text());
            text = " joined : " + Channel_Fragment.people_channel.getChannel_create_date();
        }



        joined_text.setText(text);


        return view;
    }


    public static AboutPage newInstance() {
        AboutPage AboutPageFragment = new AboutPage();
        Bundle bundle = new Bundle();
        AboutPageFragment.setArguments(bundle);
        return AboutPageFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


}

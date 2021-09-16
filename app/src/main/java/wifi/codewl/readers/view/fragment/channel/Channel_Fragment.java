package wifi.codewl.readers.view.fragment.channel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.channel.Channel;
import wifi.codewl.readers.presenter.ViewPagerAdapter;


public class Channel_Fragment extends Rocket {

    private Context context;

    public static TabLayout tabLayout;

    public static ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    public static boolean MyChannel;

    public static int target_channel_id;

    public static Channel people_channel;


    public static Channel  getObject(){

        if(people_channel==null)
        {
            people_channel=new Channel();

        }
        return people_channel;

    }




    {
        fragmentName = "channel_fragment";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_channel_, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.ViewPager);



        ArrayList<String> tabItems = new ArrayList<>();
        tabItems.add(getString(R.string.home_label));
        tabItems.add(getString(R.string.files_label));
        tabItems.add(getString(R.string.play_list_label));
        tabItems.add(getString(R.string.about_label));

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        prepareFragment(tabItems);


        tabLayout.setupWithViewPager(viewPager);


        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Rocket fragment = (Rocket) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(position);

                if (position == 1 && fragment != null) {
                    fragment.onResume();


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }

    private void prepareFragment(ArrayList<String> tabItems) {


        for (int i = 0; i < tabItems.size(); i++) {


            Bundle bundle = new Bundle();
            Rocket addedFragment;

            switch (tabItems.get(i)) {

                case "Home": {


                    addedFragment = HomePage.newInstance();


                    viewPagerAdapter.addFragment(addedFragment, tabItems.get(i));

                    break;
                }
                case "playList": {

                    addedFragment = PlayListPage.newInstance();


                    viewPagerAdapter.addFragment(addedFragment, tabItems.get(i));
                    break;
                }

                case "Files": {

                    addedFragment = FilePage.newInstance();


                    viewPagerAdapter.addFragment(addedFragment, tabItems.get(i));

                    break;
                }


                case "about": {

                    addedFragment = AboutPage.newInstance();


                    viewPagerAdapter.addFragment(addedFragment, tabItems.get(i));

                    break;
                }


            }


        }


    }


}
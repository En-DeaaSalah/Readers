package wifi.codewl.readers.presenter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.view.fragment.channel.Channel_Fragment;
import wifi.codewl.readers.view.fragment.subscriptions.MyFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<String> titles = new ArrayList<>();
    List<Rocket> fragments = new ArrayList<>();
    Map<Integer,String>FragmentTags;


    public void addFragment(Rocket fragment, String title) {


        titles.add(title);
        fragments.add(fragment);


    }


    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

        FragmentTags= new HashMap<>();


    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object object=super.instantiateItem(container, position);
        if(object instanceof Fragment){

            Fragment f=(Fragment)object;
            String tag=f.getTag();
            FragmentTags.put(position,tag);


        }

        return object;


    }






    @NonNull
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return titles.get(position);

    }

}

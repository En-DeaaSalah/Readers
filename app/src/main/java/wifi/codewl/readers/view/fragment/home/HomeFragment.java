package wifi.codewl.readers.view.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wifi.codewl.readers.R;
import wifi.codewl.readers.api.MySingleton;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.progress.ProgressHome;
import wifi.codewl.readers.model.home.Proposal;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.presenter.HomeAdapter;
import wifi.codewl.readers.view.activity.MainActivity;

public class HomeFragment extends Rocket {


    private SwipeRefreshLayout refreshLayoutHome;

    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter;

    private List<Model> list;

    private Context context;

    {
        fragmentName = "navigation_home";
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        this.setRetainInstance(true);

        refreshLayoutHome = root.findViewById(R.id.swipeRefresh_home);
        refreshLayoutHome.setColorSchemeColors(getResources().getColor(R.color.colorPrimary,context.getTheme()));
        refreshLayoutHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        homeAdapter.update();
                        homeAdapter.getData(true);
                        refreshLayoutHome.post(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayoutHome.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });


        homeRecyclerView = root.findViewById(R.id.recyclerview_home);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));

        list = new ArrayList<>();
        homeAdapter = new HomeAdapter(context,list);
        homeAdapter.getData(true);
        //list.add(new ProgressHome());

        homeRecyclerView.setAdapter(homeAdapter);






        return root;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }



    public static HomeFragment newInstance(){
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        homeFragment.setArguments(bundle);
        return homeFragment;
    }
}
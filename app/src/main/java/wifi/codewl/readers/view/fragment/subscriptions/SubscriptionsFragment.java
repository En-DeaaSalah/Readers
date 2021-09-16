package wifi.codewl.readers.view.fragment.subscriptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.api.MySingleton;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.subscriptions.TitleChannel;
import wifi.codewl.readers.presenter.TitleChannelAdapter;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.SubscriptionListActivity;
import wifi.codewl.readers.view.custom.CustomSwipeToRefresh;


public class SubscriptionsFragment extends Rocket {

    private Context context;
    private SubscriptionsViewModel notificationsViewModel;
    private CustomSwipeToRefresh swipeRefreshLayout;
    private RecyclerView titleChannelRecyclerview;
    private RecyclerView content_channels;
    private TitleChannelAdapter titleChannelAdapter;
    private Button showAllButton;
     ArrayList<Model> list;
    static View emptyListMessage;
    private View line;

    {
        fragmentName = "navigation_subscriptions";
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        showAllButton = root.findViewById(R.id.show_all_button);
        emptyListMessage=root.findViewById(R.id.empty_list_layout2);
        line=root.findViewById(R.id.line5);
        getSubscriptionChannels();
        titleChannelRecyclerview = root.findViewById(R.id.recyclerview_title_channel);
        titleChannelRecyclerview.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        titleChannelAdapter = new TitleChannelAdapter(context, list);
        titleChannelRecyclerview.setAdapter(titleChannelAdapter);




        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh_subscriptions);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });


















        showAllButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SubscriptionListActivity.class);

                SubscriptionListActivity.ChannelTitle=list;


                startActivity(intent);

            }
        });





/*
        content_channels=root.findViewById(R.id.recyclerView);
        content_channels.setLayoutManager(new LinearLayoutManager(context));


        Content_Channel_Adapter content_channel_adapter = new Content_Channel_Adapter(context,new ArrayList<>());
        content_channel_adapter.getData(true);
        content_channels.setAdapter(content_channel_adapter);

 */









        return root;
    }

    private void getSubscriptionChannels() {

        list=new ArrayList<>();













        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "getchannels", response -> {


            try {

                JSONArray jsonArray = new JSONArray(response);


                if(jsonArray.length()==0){

                    Log.d("check","check");

                    showAllButton.setVisibility(View.INVISIBLE);
                    emptyListMessage.setVisibility(View.VISIBLE);
                    line.setVisibility(View.INVISIBLE);
                    return;
                }

                showAllButton.setVisibility(View.VISIBLE);
                emptyListMessage.setVisibility(View.GONE);
                line.setVisibility(View.VISIBLE);



                if(jsonArray.length()==list.size()) {
                    return;
                }


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);


                    Log.d("channel_id", jsonObject.get("id").toString());




                    TitleChannel titleChannel = new TitleChannel(jsonObject.get("id").toString(),null,jsonObject.get("Name").toString());


                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.get("Personal_image").toString(), reesponse -> {

                        titleChannel.setProfileImage(reesponse);
                        list.add(titleChannel);
                        titleChannelAdapter.notifyDataSetChanged();

                    }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


                    });
                    MySingleton.getInstance(context).getRequestQueue().add(imageRequest);


                }




            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fetch", e.getMessage());
            }


        }, error -> {
            Log.d("fetch", "error");


        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", User.getUserReference().getUserId());

                System.err.println(User.getUserReference().getUserId());
                return map;
            }

        };
        MySingleton.getInstance(context).getRequestQueue().add(request);



    }






    public static SubscriptionsFragment newInstance() {
        SubscriptionsFragment subscriptionsFragment = new SubscriptionsFragment();
        Bundle bundle = new Bundle();
        subscriptionsFragment.setArguments(bundle);
        return subscriptionsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
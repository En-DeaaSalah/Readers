package wifi.codewl.readers.view.fragment.channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel_File;
import wifi.codewl.readers.model.channel.PlayLists;
import wifi.codewl.readers.presenter.ChannelFileAdapter;
import wifi.codewl.readers.presenter.playAdapter;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.SelectFileToFlpayList;

public class PlayListPage extends Rocket {

    private SwipeRefreshLayout swipeRefreshLayout;

    private Context context;

    private FloatingActionButton addButton;

    private RecyclerView platListRecycle;

    private playAdapter adapter;

    private ArrayList<PlayLists> playLists;

    TextView tv_empty;


    private PlayListPage() {
        super.fragmentName = "PlayListPage_fragment";
        playLists = new ArrayList<>();
        fragmentName = "PlayListPage_fragment";
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FragmentName", fragmentName);

        View view = inflater.inflate(R.layout.play_list_layout, container, false);

        playLists = new ArrayList<>();

        tv_empty = view.findViewById(R.id.tv_empty2);
        addButton = view.findViewById(R.id.add_pLaylist_button);
        if (!Channel_Fragment.MyChannel) {

            addButton.setVisibility(View.GONE);

        } else {

            addButton.setVisibility(View.VISIBLE);

        }


        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_play_list);
        platListRecycle = view.findViewById(R.id.recycler_play_list);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, SelectFileToFlpayList.class);
                startActivity(intent);


            }
        });

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, context.getTheme()));
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
                                playLists.clear();
                               fetchFilesFromServer();

                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);

                            }
                        });
                    }
                }).start();
            }
        });

        fetchFilesFromServer();



        return view;
    }


    public void fetchFilesFromServer() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "return_all_playlist", response -> {


            try {

                JSONArray jsonArray = new JSONArray(response);


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("check")) {

                        break;
                    }



                    String ids=jsonObject.getString("file_id");



                    String []array=ids.split(",");





                    ArrayList<Integer>fileIds=new ArrayList<>();

                    for (int j = 0; j <array.length ; j++) {

                      fileIds.add(Integer.parseInt(array[j]));

                    }

                    //Toast.makeText(context, fileIds.toString(), Toast.LENGTH_SHORT).show();





                    PlayLists file = new PlayLists(Integer.parseInt(jsonObject.getString("id")), jsonObject.getString("Title"), Integer.parseInt(jsonObject.getString("number"))
                            , User.getUserReference().getChannel().getChannel_profile().getChannelName(), jsonObject.getString("image"),fileIds);

                    file.setFile_count(Integer.parseInt(jsonObject.getString("number")));
                    file.setPlayListFilesIds(fileIds);
                    playLists.add(file);



                }

                System.err.println(playLists.size());

                adapter = new playAdapter(context, playLists, tv_empty);

                adapter.notifyDataSetChanged();
                platListRecycle.setAdapter(adapter);


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
                map.put("user_id", User.getUserReference().getUserId());
                return map;
            }

        };
        requestQueue.add(request);


    }




    public static PlayListPage newInstance() {
        PlayListPage PlayListPageFragment = new PlayListPage();
        Bundle bundle = new Bundle();
        PlayListPageFragment.setArguments(bundle);
        return PlayListPageFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


}

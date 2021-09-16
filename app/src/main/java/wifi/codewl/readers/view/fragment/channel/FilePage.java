package wifi.codewl.readers.view.fragment.channel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;

import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel_File;
import wifi.codewl.readers.presenter.ChannelFileAdapter;
import wifi.codewl.readers.view.activity.MainActivity;


public class FilePage extends Rocket {

    private Context context;


    private FilePage() {
        super.fragmentName = "file_page_fragment";
        fragmentName = "file_page_fragment";

    }


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ChannelFileAdapter channelFileAdapter;
    private static ArrayList<Model> files;
    private View emptyListMessage;
    private Button createFileBtn;


    public static ArrayList<Model> getFiles() {


        return files;


    }


    public void fetchFilesFromServer() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "all_files", response -> {


            try {

                JSONArray jsonArray = new JSONArray(response);


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("check")) {

                        break;
                    }


                    Channel_File file = new Channel_File(jsonObject.getString("id"), jsonObject.getString("Title"), jsonObject.getString("Description")
                            , jsonObject.getString("image"), jsonObject.getString("PDF"), jsonObject.getString("created_at"),jsonObject.getInt("Count_view"));



                    files.add(file);

                }


                channelFileAdapter = new ChannelFileAdapter(context, files, emptyListMessage);
                channelFileAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(channelFileAdapter);


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
                return map;
            }

        };
        requestQueue.add(request);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.files_channel_fragment, container, false);
        emptyListMessage = view.findViewById(R.id.empty_list_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_channel_files);
        recyclerView = view.findViewById(R.id.files_channel_list);
        createFileBtn = view.findViewById(R.id.createFileBtn);
        if (!Channel_Fragment.MyChannel) {
            createFileBtn.setVisibility(View.INVISIBLE);
        }


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
                                files.clear();
                                fetchFilesFromServer();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });


        files = new ArrayList<>();

        fetchFilesFromServer();

        return view;


    }

    public static FilePage newInstance() {
        FilePage FilePageFragment = new FilePage();
        Bundle bundle = new Bundle();
        FilePageFragment.setArguments(bundle);
        return FilePageFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (channelFileAdapter != null) {

            channelFileAdapter.notifyDataSetChanged();

        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }


}

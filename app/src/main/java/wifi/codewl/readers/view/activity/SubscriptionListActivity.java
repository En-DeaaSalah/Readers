package wifi.codewl.readers.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.subscriptions.TitleChannel;
import wifi.codewl.readers.presenter.SubscriptionAdapter;

public class SubscriptionListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SubscriptionAdapter adapter;
   static public  ArrayList<Model>ChannelTitle;


    private ImageButton searchButton,searchSpeech;
    private LinearLayout searchLayout;
    private SearchView searchView;
    private TextView toolbarName;
    private List<Model> list;
    private Animation animation;
    private ImageView imageButton7cmknjdlcwhl;

    private SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
       // getData();
        setContentView(R.layout.activity_sub_channel_list);


        animation = AnimationUtils.loadAnimation(this,R.anim.tranction_down);

        searchButton = findViewById(R.id.search_toolbar);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_view);
        searchSpeech = findViewById(R.id.search_speech);
        toolbarName = findViewById(R.id.app_toolbar_name);
        imageButton7cmknjdlcwhl = findViewById(R.id.imageButton7cmknjdlcwhl);
        imageButton7cmknjdlcwhl.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileImage());
        swipeRefreshLayout = findViewById(R.id.swipeRefresh_list_sub);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary,getTheme()));
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

        adapter=new SubscriptionAdapter(ChannelTitle,this);


        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        recyclerView.setAdapter(adapter);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.setVisibility(View.GONE);
                toolbarName.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchLayout.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                toolbarName.setVisibility(View.VISIBLE);
                toolbarName.startAnimation(animation);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new  SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SubscriptionListActivity.this, SearchActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchSpeech.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getSpeechToText();
            }
        });


    }

    private void getData(){


        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "getchannels", response -> {


            try {

                JSONArray jsonArray = new JSONArray(response);


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);






                    TitleChannel titleChannel = new TitleChannel(jsonObject.get("id").toString(),null,jsonObject.get("Name").toString());


                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.get("Personal_image").toString(), reesponse -> {

                        titleChannel.setProfileImage(reesponse);
                        ChannelTitle.add(titleChannel);


                    }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


                    });

                    requestQueue.add(imageRequest);

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
        requestQueue.add(request);





    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (toolbarName.getVisibility() == View.GONE) {
            searchLayout.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
            toolbarName.setVisibility(View.VISIBLE);
        }
    }

    public void getSpeechToText(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,162);
        }else {
            Toast.makeText(getApplicationContext(),"Your device does not support converting speech to text",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 162 && data !=null){
            ArrayList<String> list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert list != null;
            searchView.setQuery(list.get(0),true);
        }
    }
}

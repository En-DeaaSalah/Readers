package wifi.codewl.readers.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.presenter.WatchLaterAdapter;

public class WatchLaterActivity extends AppCompatActivity {

    private SwipeRefreshLayout watchLaterSwipeRefresh;
    private RecyclerView recyclerViewWatchLater;
    private WatchLaterAdapter watchLaterAdapter;
    private List<Model> list;

    private ImageButton searchButton,searchSpeech;
    private LinearLayout searchLayout;
    private SearchView searchView;
    private TextView toolbarName;
    private Animation animation;
    private ImageView imageButton7cmknjdlcwhl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_watch_later);

        animation = AnimationUtils.loadAnimation(this,R.anim.tranction_down);

        searchButton = findViewById(R.id.search_toolbar);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_view);
        searchSpeech = findViewById(R.id.search_speech);
        toolbarName = findViewById(R.id.app_toolbar_name);
        imageButton7cmknjdlcwhl = findViewById(R.id.imageButton7cmknjdlcwhl);
        imageButton7cmknjdlcwhl.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileImage());
        watchLaterSwipeRefresh = findViewById(R.id.swipeRefresh_history);
        watchLaterSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary,getTheme()));
        watchLaterSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                        watchLaterSwipeRefresh.post(new Runnable() {
                            @Override
                            public void run() {
                                list.clear();
                                watchLaterAdapter.getData();
                                watchLaterSwipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        recyclerViewWatchLater = findViewById(R.id.recyclerViewWatchLater);
        recyclerViewWatchLater.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        watchLaterAdapter = new WatchLaterAdapter(this,list);
        watchLaterAdapter.getData();

        recyclerViewWatchLater.setAdapter(watchLaterAdapter);


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
                Intent intent = new Intent(WatchLaterActivity.this, SearchActivity.class);
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
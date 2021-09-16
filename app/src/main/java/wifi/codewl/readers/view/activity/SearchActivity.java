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
import wifi.codewl.readers.model.progress.ProgressSearch;
import wifi.codewl.readers.model.search.SearchChannel;
import wifi.codewl.readers.model.search.SearchFile;
import wifi.codewl.readers.presenter.SearchAdapter;

public class SearchActivity extends AppCompatActivity {


    private SwipeRefreshLayout swipeRefreshSearch;
    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private ImageButton searchButton,searchSpeech;
    private LinearLayout searchLayout;
    private SearchView searchView;
    private TextView toolbarName;

    private ImageView imageButton7Peronsmal;

    private Animation animation;

    private List<Model> list;

    private Bundle extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search);

        extra = getIntent().getExtras();

        String query = extra.getString("query");


        searchButton = findViewById(R.id.search_toolbar);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_view);
        searchSpeech = findViewById(R.id.search_speech);
        toolbarName = findViewById(R.id.app_toolbar_name);
        imageButton7Peronsmal = findViewById(R.id.imageButton7Peronsmal);

        imageButton7Peronsmal.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileImage());


        animation = AnimationUtils.loadAnimation(this,R.anim.tranction_down);


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

                //YoYo.with(Techniques.Tada).duration(700).playOn(toolbarName);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new  SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
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


        swipeRefreshSearch = findViewById(R.id.swipeRefresh_search);
        swipeRefreshSearch.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, getTheme()));
        swipeRefreshSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                        swipeRefreshSearch.post(new Runnable() {
                            @Override
                            public void run() {
                                list.clear();
                                searchAdapter.getData(query);
                                swipeRefreshSearch.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        searchRecyclerView = findViewById(R.id.recyclerview_search);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        searchAdapter = new SearchAdapter(this,list);
        searchAdapter.getData(query);
        searchRecyclerView.setAdapter(searchAdapter);

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
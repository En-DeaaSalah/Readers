package wifi.codewl.readers.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
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
import wifi.codewl.readers.model.comment.AddReplyComment;
import wifi.codewl.readers.model.comment.ReplyComment;
import wifi.codewl.readers.model.file.ViewCommentFile;
import wifi.codewl.readers.model.progress.ProgressComment;
import wifi.codewl.readers.presenter.CommentAdapter;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;

    private ImageButton searchButton,searchSpeech;
    private LinearLayout searchLayout;
    private SearchView searchView;
    private TextView toolbarName;

    private Animation animation;
    private ImageView imageButton7prerok;

    private Bundle extra;

    private List<Model> list;

    private int idComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_comment);

        extra = getIntent().getExtras();

        idComment =  extra.getInt("idComment");


        animation = AnimationUtils.loadAnimation(this,R.anim.tranction_down);

        searchButton = findViewById(R.id.search_toolbar);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_view);
        searchSpeech = findViewById(R.id.search_speech);
        toolbarName = findViewById(R.id.app_toolbar_name);
        imageButton7prerok  = findViewById(R.id.imageButton7prerok);

        imageButton7prerok.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileImage());

        commentRecyclerView = findViewById(R.id.recyclerview_comment);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        list = new ArrayList<>();


  /*    list.add(new AddReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ReplyComment());
        list.add(new ProgressComment());
*/
        commentAdapter = new CommentAdapter(this,list);
        commentAdapter.getData(idComment);

        commentRecyclerView.setAdapter(commentAdapter);


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
                Intent intent = new Intent(CommentActivity.this, SearchActivity.class);
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
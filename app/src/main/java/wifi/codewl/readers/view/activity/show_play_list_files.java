package wifi.codewl.readers.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.presenter.ChannelFileAdapter;
import wifi.codewl.readers.presenter.show_files_playlist_adapter;

public class show_play_list_files extends AppCompatActivity {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private show_files_playlist_adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_play_list_files);
        swipeRefreshLayout =findViewById(R.id.swipe_refresh_channel_files);
        recyclerView =findViewById(R.id.files_channel_list);
        adapter=new show_files_playlist_adapter(this, (ArrayList<Integer>) getIntent().getExtras().get("files_show"));
        adapter.fetchFilesFromServer();
        adapter.notifyDataSetChanged();



    }
}
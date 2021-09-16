package wifi.codewl.readers.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.easing.linear.Linear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel_File;
import wifi.codewl.readers.presenter.selectedFileAdapter;
import wifi.codewl.readers.view.fragment.channel.FilePage;


public class SelectFileToFlpayList extends AppCompatActivity {


    public TextView tvEmpty;

    RecyclerView recyclerView;

    selectedFileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file_to_flpay_list);


        tvEmpty = findViewById(R.id.tv_empty);
        recyclerView = findViewById(R.id.selected_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new selectedFileAdapter(this, tvEmpty);
        recyclerView.setAdapter(adapter);

        if (FilePage.getFiles().size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);

        }


    }


}
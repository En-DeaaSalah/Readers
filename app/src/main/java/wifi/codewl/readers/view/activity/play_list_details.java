package wifi.codewl.readers.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel_File;

public class play_list_details extends AppCompatActivity {



    private ImageView PlayListBackground;

    private Bitmap bitmap;

    private EditText name_field;

    private TextView name_label;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_details);

        name_field = findViewById(R.id.name_filed);

        name_label = findViewById(R.id.name_label);

        PlayListBackground = findViewById(R.id.playListbackground);


        name_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus || name_field.getText().toString().length() != 0) {
                    name_label.setVisibility(View.VISIBLE);

                    name_field.setHint("");

                } else {

                    name_label.setVisibility(View.INVISIBLE);
                    name_field.setHint("title");


                }


            }
        });
    }



    public void onCreatePlayListListerner(View view) {


        ArrayList<Channel_File> files=(ArrayList<Channel_File>)getIntent().getExtras().get("play_list_file");

        StringBuilder ids=new StringBuilder();
        for (int i = 0; i < files.size(); i++) {

            ids.append(files.get(i).getId());
            if((i+1)!=files.size())
            ids.append(",");


        }


        Toast.makeText(this,ids.toString(), Toast.LENGTH_SHORT).show();





        try {



            StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "create_playlist", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    System.err.println(response);
                    finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    System.err.println(error);

                    //finish();


                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", User.getUserReference().getUserId());
                    map.put("name", name_field.getText().toString());
                    map.put("files_id",ids.toString());
                    map.put("image", MainActivity.getStringImage(bitmap));
                    return map;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        } catch (Exception e) {

            finish();


        }




    }

    public void choosePlayListBackground(View view) {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                PlayListBackground.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }


    }

}
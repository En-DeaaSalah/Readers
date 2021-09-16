package wifi.codewl.readers.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;



import wifi.codewl.readers.R;
import wifi.codewl.readers.model.User;

public class UploadFileActivity extends AppCompatActivity {

    private EditText title_field, description_field;

    private TextView title_label, description_label;

    private ImageView file_background;

    private Bitmap bitmap;

    public static Intent uploaded_file;

    private AlertDialog dialog;

    public void chooseBackgroundFile(View view) {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }


    public void uploadFile(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setView(R.layout.progress_dialog_layout);

        builder.setCancelable(false);

        dialog = builder.create();

        dialog.show();

        String extension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uploaded_file.getData()));

        try {
            InputStream inputStream = getContentResolver().openInputStream(uploaded_file.getData());
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "Upload_file", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    dialog.dismiss();

                    finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    finish();


                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", User.getUserReference().getUserId());
                    map.put("channel_id", User.getUserReference().getUserId());
                    map.put("pdf", encodedFile);
                    map.put("title", title_field.getText().toString());
                    map.put("description", description_field.getText().toString());
                    map.put("image", MainActivity.getStringImage(bitmap));
                    map.put("suff", extension);


                    return map;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        } catch (Exception e) {

            finish();


        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                file_background.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);


        title_field = findViewById(R.id.title_filed);
        description_field = findViewById(R.id.description_field);
        title_label = findViewById(R.id.title_label);
        description_label = findViewById(R.id.description_label);
        file_background = findViewById(R.id.file_background);


        title_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus || title_field.getText().toString().length() != 0) {
                    title_label.setVisibility(View.VISIBLE);

                    title_field.setHint("");

                } else {

                    title_label.setVisibility(View.INVISIBLE);
                    title_field.setHint("title");


                }


            }
        });

        description_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus || description_field.getText().toString().length() != 0) {
                    description_label.setVisibility(View.VISIBLE);
                    description_field.setHint("");


                } else {


                    description_label.setVisibility(View.INVISIBLE);

                    description_field.setHint("description");

                }


            }
        });


    }
}
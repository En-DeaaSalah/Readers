package wifi.codewl.readers.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.channel.Channel_File;

public class EditFile extends AppCompatActivity {

    private EditText edit_title_field, edit_description_field;
    private ImageView edit_file_background;
    private Button update_button;
    private Bitmap bitmap;
    private AlertDialog dialog;

    public void chooseBackgroundFile(View view) {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_file);

        Channel_File file = (Channel_File) getIntent().getSerializableExtra("file");

        edit_title_field = findViewById(R.id.edit_title_filed);
        edit_title_field.setText(file.getTitle());

        edit_description_field = findViewById(R.id.edit_description_field);
        edit_description_field.setText(file.getDescription());

        edit_file_background = findViewById(R.id.edit_file_background);
        getFileBackground(file.getFile_background());

        update_button = findViewById(R.id.update_edit_file);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditFile.this);
                builder.setView(R.layout.progress_dialog_layout);
                builder.setCancelable(false);
                dialog = builder.create();
                dialog.show();


                StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "edit_file", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            file.setDescription(edit_description_field.getText().toString());
                            file.setTitle(edit_title_field.getText().toString());
                            file.setFile_background(jsonObject.getString("image"));
                            dialog.dismiss();
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            finish();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        finish();
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", file.getId());
                        map.put("title", edit_title_field.getText().toString());
                        map.put("description", edit_description_field.getText().toString());
                        map.put("image", MainActivity.getStringImage(bitmap));


                        return map;
                    }
                };


                RequestQueue requestQueue = Volley.newRequestQueue(EditFile.this);
                requestQueue.add(stringRequest);


            }
        });


    }


    private void getFileBackground(String imageName) {

        ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + imageName, response -> {
            edit_file_background.setImageBitmap(response);
            bitmap = response;


        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


        });
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(imageRequest);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                edit_file_background.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }


    }
}
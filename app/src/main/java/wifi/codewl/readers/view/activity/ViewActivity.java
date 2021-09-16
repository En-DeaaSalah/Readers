package wifi.codewl.readers.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.api.MySingleton;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.file.AttributesFile;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.progress.ProgressFile;
import wifi.codewl.readers.presenter.ViewAdapter;

public class ViewActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private ImageButton viewFileBack, viewFileSave, viewFileDirection,viewFileFullScreen;
    private PDFView pdfView;

    private boolean showLayout = true, vertical = true,fullScreen;

    private Animation rotateDown, rotateUp;

    private final int REQUEST_PERMISSION = 643;


    private RecyclerView viewRecyclerView;
    private ViewAdapter viewAdapter;

    private List<Model> list;

    private Bundle extra;

    private File currentFile;

    int idFile = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view);



        extra = getIntent().getExtras();

        viewRecyclerView = findViewById(R.id.recyclerview_view);

        list = new ArrayList<>();


        idFile = extra.getInt("id");






        rotateDown = AnimationUtils.loadAnimation(this, R.anim.rotate_down);
        rotateUp = AnimationUtils.loadAnimation(this, R.anim.rotate_up);


        layout = findViewById(R.id.layoutView);


        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        pdfView = findViewById(R.id.pdfView);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pdfView.getLayoutParams();
        params.setMargins(0, 0, 0, (int) (metrics.heightPixels * 0.485));
        pdfView.setLayoutParams(params);

        viewFileBack = findViewById(R.id.viewFileBack);

        viewFileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewFileSave = findViewById(R.id.viewFileSave);

        viewFileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission1() || checkPermission2()) {
                    requestPermission();
                } else {
                    saveFile();
                }
            }
        });

        viewFileDirection = findViewById(R.id.viewFileDirection);

        viewFileDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vertical) {
                    vertical = false;
                    viewFileDirection.startAnimation(rotateDown);
                    final int page = pdfView.getCurrentPage();
                    pdfView.fromFile(currentFile).spacing(5).swipeHorizontal(false).defaultPage(page).enableDoubletap(true).enableSwipe(true).load();
                } else {
                    vertical = true;
                    viewFileDirection.startAnimation(rotateUp);
                    int page = pdfView.getCurrentPage();
                    pdfView.fromFile(currentFile).spacing(5).swipeHorizontal(true).defaultPage(page).enableDoubletap(true).enableSwipe(true).load();
                }

            }
        });


        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showLayout) {
                    YoYo.with(Techniques.FadeOut).duration(200).playOn(layout);
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(225);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            layout.setVisibility(View.GONE);
                        }
                    });
                    showLayout = false;
                } else {
                    YoYo.with(Techniques.FadeIn).duration(200).playOn(layout);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(225);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            layout.setVisibility(View.VISIBLE);
                        }
                    });
                    showLayout = true;
                }

            }
        });


        viewFileFullScreen = findViewById(R.id.viewFileScreen);


        viewFileFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fullScreen){
                    fullScreen = false;
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pdfView.getLayoutParams();
                    params.setMargins(0, 0, 0,  (int) (metrics.heightPixels * 0.485));
                    pdfView.setLayoutParams(params);
                    viewFileFullScreen.setImageResource(R.drawable.ic_baseline_fullscreen_24);
                    viewRecyclerView.setVisibility(View.VISIBLE);
                }else {
                    fullScreen = true;
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pdfView.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    pdfView.setLayoutParams(params);
                    viewFileFullScreen.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24);
                    viewRecyclerView.setVisibility(View.GONE);
                }

            }
        });




        viewRecyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));

        viewAdapter = new ViewAdapter(ViewActivity.this, list);
        viewRecyclerView.setAdapter(viewAdapter);



        viewAdapter.getDataFile(idFile, true, new ShowFile() {
            @Override
            public void showFile(File file) {
                currentFile = file;
                pdfView.fromFile(file).spacing(5).swipeHorizontal(true).enableDoubletap(true).enableSwipe(true).load();
            }
        });

    }

    public interface ShowFile{
        void showFile(File file);
    }


    private boolean checkPermission1() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPermission2() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

    }

    private void saveFile()  {
        String title = viewAdapter.getTitleFile();
        File backupFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), title + ".pdf");
        copy(currentFile, backupFile);
        Toast.makeText(this, "Save as path : " + backupFile.getAbsoluteFile(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }

    public void copy(File src, File dst)  {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(src);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(dst);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION)
            if (checkPermission1() || checkPermission2())
                System.out.println();
            else {
                saveFile();
            }
    }

}
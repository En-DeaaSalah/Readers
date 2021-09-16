package wifi.codewl.readers.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.util.Log;
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


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel_Profile;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.custom.BottomSheet;
import wifi.codewl.readers.view.fragment.channel.Channel_Fragment;
import wifi.codewl.readers.view.fragment.home.HomeFragment;
import wifi.codewl.readers.view.fragment.library.LibraryFragment;
import wifi.codewl.readers.view.fragment.notifications.NotificationsFragment;
import wifi.codewl.readers.view.fragment.subscriptions.SubscriptionsFragment;


public class MainActivity extends AppCompatActivity {


    public static final String SERVER_API_URL = "http://192.168.43.145/you_tube/public/api/";
    public static final String SERVER_IMAGES_URL = "http://192.168.43.145/you_tube/public/upload/";

    private BottomNavigationView navView;

    private HomeFragment homeFragment = HomeFragment.newInstance();
    private SubscriptionsFragment subscriptionsFragment = SubscriptionsFragment.newInstance();
    private NotificationsFragment notificationsFragment = NotificationsFragment.newInstance();
    private LibraryFragment libraryFragment = LibraryFragment.newInstance();
    private Stack<Rocket> stack;
    @SuppressLint("StaticFieldLeak")
    private ImageButton searchButton, searchSpeech;
    private LinearLayout searchLayout;
    private SearchView searchView;
    private TextView toolbarName;
    private BottomSheet bottomSheet;
    private static MainActivity context;

    private Animation animation;





    public static MainActivity getContext() {

        return context;

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        context = this;

        navView = findViewById(R.id.nav_view);
        searchButton = findViewById(R.id.search_toolbar);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_view);
        searchSpeech = findViewById(R.id.search_speech);
        toolbarName = findViewById(R.id.app_toolbar_name);
        CircularImageView myChannel_image = findViewById(R.id.myChannel);
        myChannel_image.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileImage());


        animation = AnimationUtils.loadAnimation(this, R.anim.tranction_down);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechToText();
            }
        });


        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        changeFragment(homeFragment);
        stack = new Stack<>();
        stack.push(homeFragment);

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

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home: {
                            Rocket fragment = stack.peek();
                            if (!(fragment instanceof HomeFragment)) {
                                stack.push(homeFragment);
                                changeFragment(homeFragment);

                            }

                            return true;
                        }
                        case R.id.navigation_subscriptions: {

                            Rocket fragment = stack.peek();
                            if (!(fragment instanceof SubscriptionsFragment)) {
                                stack.push(subscriptionsFragment);
                                changeFragment(subscriptionsFragment);
                            }

                            return true;
                        }
                        case R.id.navigation_notifications: {
                            Rocket fragment = stack.peek();
                            if (!(fragment instanceof NotificationsFragment)) {
                                stack.push(notificationsFragment);
                                changeFragment(notificationsFragment);
                            }


                            return true;
                        }
                        case R.id.navigation_library: {
                            Rocket fragment = stack.peek();
                            if (!(fragment instanceof LibraryFragment)) {

                                changeFragment(libraryFragment);
                                stack.push(libraryFragment);
                            }

                            return true;
                        }
                    }
                    return false;
                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void changeFragment(Rocket fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.getFragments().get(0);

        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment);
        }

        Fragment fragmentTemp = fragmentManager.findFragmentByTag(fragment.fragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.nav_host_fragment, fragmentTemp, fragment.fragmentName);
        } else {
            fragmentTransaction.attach(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();


    }


    @SuppressLint("ResourceType")
    @Override
    public void onBackPressed() {
        Rocket rocket = stack.pop();


        if (stack.isEmpty()) {
            super.onBackPressed();
            return;
        }

        if (toolbarName.getVisibility() == View.GONE) {
            searchLayout.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
            toolbarName.setVisibility(View.VISIBLE);
            toolbarName.startAnimation(animation);
        }


        if (!stack.isEmpty()) {
            rocket = stack.peek();
        }


        switch (rocket.fragmentName) {

            case "channel_fragment": {
                super.onBackPressed();
                return;
            }
            case "navigation_home": {
                HomeFragment homeFragment = (HomeFragment) rocket;
                navView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                changeFragment(homeFragment);
                break;
            }
            case "navigation_subscriptions": {
                SubscriptionsFragment subscriptionsFragment = (SubscriptionsFragment) rocket;
                navView.getMenu().findItem(R.id.navigation_subscriptions).setChecked(true);
                changeFragment(subscriptionsFragment);
                break;
            }
            case "navigation_notifications": {
                NotificationsFragment notificationsFragment = (NotificationsFragment) rocket;
                navView.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
                changeFragment(notificationsFragment);
                break;
            }
            case "navigation_library": {
                LibraryFragment libraryFragment = (LibraryFragment) rocket;
                navView.getMenu().findItem(R.id.navigation_library).setChecked(true);
                changeFragment(libraryFragment);
                break;
            }
        }

    }


    public void getSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 162);
        } else {
            Toast.makeText(getApplicationContext(), "Your device does not support converting speech to text", Toast.LENGTH_LONG).show();
        }
    }


























    public void goToUploadFile(View view) {

        String[] mimeTypes = {
                "application/pdf",
                "application/zip",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain"
        };

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        chooseFile.setType("application/pdf");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(chooseFile, CHOOSE_FILE_REQUEST);


    }




    public void goTo_My_Channel(View view) {



        Channel_Fragment.MyChannel=true;
        Channel_Fragment fragment = new Channel_Fragment();
        if (!stack.peek().fragmentName.equals(fragment.fragmentName))
            stack.push(fragment);
        changeFragment(fragment);

    }


    public void goTo_People_Channel(int idUser) {



        Channel_Fragment.MyChannel=false;
        Channel_Fragment.target_channel_id=idUser;
        getData();



    }

    public  void getData() {


        System.out.println("text 0");

        Channel_Fragment.getObject().setChannel_profile(new Channel_Profile());

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.getContext());


        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "user_info", response -> {


            try {


                JSONObject jsonObject = new JSONObject(response);



                Channel_Fragment.getObject().setChannelId(jsonObject.getString("id"));


                Channel_Fragment.getObject().getChannel_profile().setChannelName(jsonObject.getString("Channel_name"));


                Channel_Fragment.getObject().setChannel_create_date(jsonObject.getString("Create_date"));

                Channel_Fragment.getObject().getChannel_profile().setSuc_count(jsonObject.getInt("Count_sub"));


                if (jsonObject.getString("Description").equals("null"))
                    Channel_Fragment.getObject().setDescription_text("");
                else
                    Channel_Fragment.getObject().setDescription_text(jsonObject.getString("Description"));


                ImageRequest imageRequest = new ImageRequest(
                        MainActivity.SERVER_IMAGES_URL + jsonObject.get("Personal_image"), response1 -> {

                    Channel_Fragment.getObject().getChannel_profile().setProfileImage(response1);


                }, 0,
                        0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


                });


                ImageRequest imageRequest2 = new ImageRequest(
                        MainActivity.SERVER_IMAGES_URL + jsonObject.get("Background_image"), response1 -> {



                    System.err.println("Test 1");

                    Channel_Fragment.getObject().getChannel_profile().setProfileBackground(response1);

                    Channel_Fragment fragment = new Channel_Fragment();



                    stack.push(fragment);

                    changeFragment(fragment);



                }, 0,
                        0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


                });


                requestQueue.add(imageRequest);
                requestQueue.add(imageRequest2);


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("test", e.getMessage());

            }


        }, error -> {
            Log.d("test", error.getMessage());
            System.out.println(error.getMessage());


        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(Channel_Fragment.target_channel_id));
                return map;
            }

        };

        Toast.makeText(MainActivity.this, "Map "+Channel_Fragment.target_channel_id, Toast.LENGTH_SHORT).show();

        requestQueue.add(request);


    }



    public void show_sheet(View view) {


        if (view.getId() == R.id.change_background) {
            bottomSheet = new BottomSheet(1);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        } else {
            bottomSheet = new BottomSheet(2);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }


    }

    private  final int  CHOOSE_FILE_REQUEST=5;
    private  final int  SET_PROFILE_IMAGE_FROM_GALLERY_REQUEST=3;
    private  final int  SET_PROFILE_IMAGE_FROM_CAMERA_REQUEST=4;
    private  final int  SET_PROFILE_BACKGROUND_FROM_GALLERY_REQUEST=1;
    private  final int  SET_PROFILE_BACKGROUND_CAMERA_REQUEST=2;

    public void setProfileImageFromGallery(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, SET_PROFILE_IMAGE_FROM_GALLERY_REQUEST);
        bottomSheet.dismiss();


    }


    public void setProfileImageFromCamera(View view) {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, SET_PROFILE_IMAGE_FROM_CAMERA_REQUEST);
        bottomSheet.dismiss();


    }


    public void setProfileBackgroundFromGallery(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, SET_PROFILE_BACKGROUND_FROM_GALLERY_REQUEST);
        bottomSheet.dismiss();
    }


    public void setProfileBackgroundFromCamera(View view) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(i, SET_PROFILE_BACKGROUND_CAMERA_REQUEST);
        bottomSheet.dismiss();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SET_PROFILE_BACKGROUND_FROM_GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {


            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_API_URL + "Editbackground",

                    response -> {


                        ImageView imageView = findViewById(R.id.profile_background);
                        User.getUserReference().getChannel().getChannel_profile().setProfileBackground(bitmap);
                        imageView.setImageBitmap(bitmap);


                        final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), response, Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                        snackbar.setAction("Close", v1 -> snackbar.dismiss());
                        double yOffset = Screen.getScreenHeight() * Screen.display1;
                        View snackbarLayout = snackbar.getView();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, (int)yOffset, 0, 0);
                        snackbarLayout.setLayoutParams(lp);
                        snackbar.show();


                    }, error -> {

                final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), error.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                snackbar.setAction("Close", v1 -> snackbar.dismiss());
                double yOffset = Screen.getScreenHeight() * Screen.display1;
                View snackbarLayout = snackbar.getView();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, (int)yOffset, 0, 0);
                snackbarLayout.setLayoutParams(lp);
                snackbar.show();


            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", User.getUserReference().getUserId());
                    map.put("image", getStringImage(bitmap));
                    return map;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        }


        if (requestCode == SET_PROFILE_BACKGROUND_CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {


            final Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_API_URL + "Editbackground", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    User.getUserReference().getChannel().getChannel_profile().setProfileBackground(captureImage);


                    ImageView imageView = findViewById(R.id.profile_background);

                    imageView.setImageBitmap(captureImage);


                    final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), response, Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), error.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", User.getUserReference().getUserId());
                    map.put("image", getStringImage(captureImage));

                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        }


        if (requestCode == SET_PROFILE_IMAGE_FROM_GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {


            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_API_URL + "EditPersonal", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    CircularImageView imageView = findViewById(R.id.profile_people_image);
                    CircularImageView imageView1 = findViewById(R.id.myChannel);
                    imageView1.setImageBitmap(bitmap);
                    User.getUserReference().getChannel().getChannel_profile().setProfileImage(bitmap);
                    imageView.setImageBitmap(bitmap);


                    final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), response, Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), error.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();


                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", User.getUserReference().getUserId());
                    map.put("image", getStringImage(bitmap));
                    return map;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        }


        if (requestCode == SET_PROFILE_IMAGE_FROM_CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {


            Bitmap captureImage = (Bitmap) data.getExtras().get("data");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_API_URL + "EditPersonal", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    CircularImageView imageView = (CircularImageView) findViewById(R.id.profile_people_image);
                    imageView.setImageBitmap(captureImage);
                    User.getUserReference().getChannel().getChannel_profile().setProfileImage(captureImage);
                    CircularImageView imageView1 = findViewById(R.id.myChannel);
                    imageView1.setImageBitmap(captureImage);


                    final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), response, Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    final Snackbar snackbar = Snackbar.make(MainActivity.this, findViewById(R.id.nav_host_fragment).getRootView(), error.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", User.getUserReference().getUserId());
                    map.put("image", getStringImage(captureImage));

                    return map;
                }


            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        }


        if (requestCode == CHOOSE_FILE_REQUEST && resultCode == RESULT_OK && null != data) {


            Intent intent = new Intent(this, UploadFileActivity.class);

            UploadFileActivity.uploaded_file = data;
            startActivity(intent);


        }


        if (requestCode == 162 && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert list != null;
            searchView.setQuery(list.get(0), true);
        }
    }


    public static String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String s = Base64.encodeToString(bytes, Base64.DEFAULT);
        return s;
    }
}

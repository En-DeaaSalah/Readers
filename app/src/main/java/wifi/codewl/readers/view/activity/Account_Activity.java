package wifi.codewl.readers.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Account;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel;
import wifi.codewl.readers.model.channel.Channel_Profile;
import wifi.codewl.readers.presenter.ViewPagerAdapter;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.fragment.auth.LoginFragment;
import wifi.codewl.readers.view.fragment.auth.SinginFragment;

public class Account_Activity extends AppCompatActivity {


    TabLayout tabLayout;

    ViewPager viewPager;

    ArrayList<String> tabItems;

    ViewPagerAdapter viewPagerAdapter;

    public static SharedPreferences saved_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connect_to_server(true);



    }



    private  void connect_to_server(boolean flag){




        saved_data = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        if (saved_data.getString("Already_login", "no").equals("yes")) {


               if(flag)
                setContentView(R.layout.wite_layout);


            RequestQueue requestQueue = Volley.newRequestQueue(this);


            StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "user_info", response -> {


                try {


                    JSONObject jsonObject = new JSONObject(response);

                    User.getUserReference().setUserId(jsonObject.getString("id"));

                    User.getUserReference().setUserName(jsonObject.getString("Username"));



                    User.getUserReference().setChannel(new Channel(jsonObject.getString("id"), new Channel_Profile(null, null, jsonObject.getString("Channel_name"))));

                    User.getUserReference().setAccount(new Account(jsonObject.getString("User_email"), jsonObject.getString("User_password")));

                    User.getUserReference().getChannel().setChannel_create_date(jsonObject.getString("Create_date"));


                    if (jsonObject.getString("Description").equals("null"))
                        User.getUserReference().getChannel().setDescription_text("");
                    else
                        User.getUserReference().getChannel().setDescription_text(jsonObject.getString("Description"));


                    ImageRequest imageRequest = new ImageRequest(
                            MainActivity.SERVER_IMAGES_URL + jsonObject.get("Personal_image"), response1 -> {

                        User.getUserReference().getChannel().getChannel_profile().setProfileImage(response1);


                    }, 0,
                            0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


                    });


                    ImageRequest imageRequest2 = new ImageRequest(
                            MainActivity.SERVER_IMAGES_URL + jsonObject.get("Background_image"), response1 -> {

                        User.getUserReference().getChannel().getChannel_profile().setProfileBackground(response1);


                        Intent intent = new Intent(Account_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();


                    }, 0,
                            0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


                    });


                    requestQueue.add(imageRequest);
                    requestQueue.add(imageRequest2);


                } catch (Exception e) {
                    e.printStackTrace();

                    setContentView(R.layout.network_failure);



                }


            }, error -> {

                setContentView(R.layout.network_failure);



            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", saved_data.getString("id", "-1"));
                    return map;
                }

            };


            requestQueue.add(request);


        } else {


            setContentView(R.layout.activity_account);
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.view_pager);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            tabItems = new ArrayList<>();
            tabItems.add("login");
            tabItems.add("sign in");


            LoginFragment f1 = new LoginFragment(saved_data, this);


            SinginFragment f2 = new SinginFragment(saved_data, this);


            viewPagerAdapter.addFragment(f1, tabItems.get(0));
            viewPagerAdapter.addFragment(f2, tabItems.get(1));

            tabLayout.setupWithViewPager(viewPager);
            viewPager.setAdapter(viewPagerAdapter);


        }






    }


    public void RetryConnection(View view) {


        connect_to_server(false);

    }


}
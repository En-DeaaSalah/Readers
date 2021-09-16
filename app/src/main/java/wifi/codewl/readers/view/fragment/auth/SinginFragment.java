package wifi.codewl.readers.view.fragment.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Account;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel;
import wifi.codewl.readers.model.channel.Channel_Profile;
import wifi.codewl.readers.view.activity.Account_Activity;
import wifi.codewl.readers.view.activity.MainActivity;

public class SinginFragment extends Rocket {


    private EditText userNameFiled, passwordFiled, userEmail;
    private SharedPreferences account_setting;
    private Button signin_btn;
    private Account_Activity context;
    private ImageButton password_visable_btn;
    private static boolean password_visible_state;
    private RequestQueue requestQueue;


    public SinginFragment(SharedPreferences account_setting, Account_Activity account_activity) {

        this.context = account_activity;
        requestQueue = Volley.newRequestQueue(context);
        this.account_setting = account_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View view = inflater.inflate(R.layout.fragment_sing_in, parent, false);

        userNameFiled = view.findViewById(R.id.sigin_user_name_field);
        passwordFiled = view.findViewById(R.id.sigin_password_field);
        userEmail = view.findViewById(R.id.sigin_email_field);


        signin_btn = view.findViewById(R.id.singin_btn);


        password_visable_btn = (ImageButton) view.findViewById(R.id.singin_visable_state_password);
        password_visible_state = false;


        password_visable_btn.setOnClickListener(v -> {

            if (!password_visible_state) {


                passwordFiled.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordFiled.setSelection(passwordFiled.getText().toString().length());

                password_visable_btn.setImageDrawable(getResources().getDrawable(R.drawable.visiable_password, context.getTheme()));
                password_visible_state = true;


            } else {


                passwordFiled.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordFiled.setSelection(passwordFiled.getText().toString().length());
                password_visable_btn.setImageDrawable(getResources().getDrawable(R.drawable.visable_off_password, context.getTheme()));

                password_visible_state = false;


            }


        });


        signin_btn.setOnClickListener(v -> {

            if (check_validate_info(view)) {


                sing_in_Request(view);

            }


        });


        return view;
    }


    private void sing_in_Request(View view) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "register", response -> {

            JSONObject jsonObject = null;


            try {

                jsonObject = new JSONObject(response);
                if (jsonObject.getString("check").equals("1")) {

                    User.getUserReference().setUserId(jsonObject.getString("user_id"));

                    User.getUserReference().setUserName(userNameFiled.getText().toString());

                    User.getUserReference().setChannel(new Channel(jsonObject.getString("user_id"), new Channel_Profile(null, null, User.getUserReference().getUserName())));

                    User.getUserReference().setAccount(new Account(userEmail.getText().toString(), passwordFiled.getText().toString()));

                    User.getUserReference().getChannel().setChannel_create_date(jsonObject.getString("create_date"));


                    ImageRequest imageRequest = new ImageRequest(
                            MainActivity.SERVER_IMAGES_URL + jsonObject.get("personal_image"), response1 -> {
                        User.getUserReference().getChannel().getChannel_profile().setProfileImage(response1);


                    }, 0,
                            0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {
                        final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.singin_fragment_root), error.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.setAction("Close", v1 -> snackbar.dismiss());
                        snackbar.show();

                    });


                    ImageRequest imageRequest2 = new ImageRequest(
                            MainActivity.SERVER_IMAGES_URL + jsonObject.get("background_image"), response1 -> {

                        User.getUserReference().getChannel().getChannel_profile().setProfileBackground(response1);

                        account_setting.edit().putString("id", User.getUserReference().getUserId()).apply();
                        account_setting.edit().putString("Already_login", "yes").apply();




                        try {
                            Intent intent = new Intent(context, MainActivity.class);
                            context.finish();
                            startActivity(intent);
                        }catch (Exception e){

                            context.finish();

                        }




                    }, 0,
                            0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {
                        final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.singin_fragment_root), error.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.setAction("Close", v1 -> snackbar.dismiss());
                        snackbar.show();

                    });


                    requestQueue.add(imageRequest);
                    requestQueue.add(imageRequest2);


                } else {
                    final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.singin_fragment_root), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    snackbar.show();

                }


            } catch (Exception e) {
                e.printStackTrace();
                try {
                    final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.singin_fragment_root), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    snackbar.show();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }


        },


                error -> {
                    final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.singin_fragment_root), "Some thing has being wrong !!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    snackbar.show();

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("name", userNameFiled.getText().toString());
                map.put("email", userEmail.getText().toString());
                map.put("password", passwordFiled.getText().toString());
                return map;
            }
        };


        requestQueue.add(stringRequest);


    }


    private boolean check_validate_info(View view) {

        boolean processFlag = true;


        if (userNameFiled.getText() == null || userNameFiled.getText().toString().length() == 0) {
            TextView alert_email_field = view.findViewById(R.id.alert_user_name);
            alert_email_field.setVisibility(View.VISIBLE);
            processFlag = false;


        } else {
            TextView alert_email_field = view.findViewById(R.id.alert_user_name);
            alert_email_field.setVisibility(View.INVISIBLE);

        }
        if (passwordFiled.getText() == null || passwordFiled.getText().toString().length() == 0) {
            TextView alert_email_field = view.findViewById(R.id.alert_password);
            alert_email_field.setVisibility(View.VISIBLE);

            processFlag = false;
        } else {
            TextView alert_email_field = view.findViewById(R.id.alert_password);
            alert_email_field.setVisibility(View.INVISIBLE);

        }
        if (userEmail.getText() == null || userEmail.getText().toString().length() == 0) {
            TextView alert_email_field = view.findViewById(R.id.alert_email);
            alert_email_field.setVisibility(View.VISIBLE);
            processFlag = false;
        } else {

            TextView alert_email_field = view.findViewById(R.id.alert_email);
            alert_email_field.setVisibility(View.INVISIBLE);

        }


        return processFlag;


    }


}
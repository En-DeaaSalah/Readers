package wifi.codewl.readers.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import javax.net.ssl.SSLEngineResult;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Account;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }


    public void logOut(View view) {

        SharedPreferences saved_data = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saved_data.edit();

        editor.putString("Already_login", "no");
        editor.commit();

        Intent intent = new Intent(this, Account_Activity.class);
        MainActivity.getContext().finish();
        finish();
        startActivity(intent);



    }
}
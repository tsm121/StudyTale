package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Continue app from story library
        if (userExcist()){
            startActivity(new Intent(SplashActivity.this, StoryLibrary.class));
            finish();
        }

        //New user, start setup prosess
        else {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }
    }

    //Check if there exist a user
    public boolean userExcist() {

        //Check if user has set up a profile with a timetable and name
        SharedPreferences settings = getSharedPreferences("userdata", MODE_PRIVATE);
        if(settings.contains("username") && settings.contains("timetable")) return true;
        else return false;

    }
}
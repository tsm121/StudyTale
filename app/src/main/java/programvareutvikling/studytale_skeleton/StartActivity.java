package programvareutvikling.studytale_skeleton;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class StartActivity extends AppCompatActivity {

    private EditText user_name_input;
    static private StepView sw;

    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_CODE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Check if user has given permission for location services
        checkLocationPermissions();

        //Check if user has reset his user data, if true, reset stepView
        if(getIntent().hasExtra("hasReset")){
            resetStepView();
        }

        //Set refrence to StepView
        HorizontalStepView stepView = (HorizontalStepView) findViewById(R.id.step_view);
        //Set current step
        sw.setSteps(0);
        //Set StepView
        sw.setStepView(stepView,this);

    }

    //Reseting stepView
    private void resetStepView() {
        sw.resetSteps();
    }

    //Check if user has given permission for location services
    private void checkLocationPermissions() {

        //Check if user has given permission, if not ask user.
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
                // If any permission above not allowed by user, this condition will execute every time, else your else part will work
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not activate location service. Check your device settings", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // onClick event for input field "Name"
    public void userNameInput(View view) {

        //set references
        user_name_input = (EditText) findViewById(R.id.user_name_input);
        //Get user focus on input field
        user_name_input.requestFocus();
        user_name_input.setFocusableInTouchMode(true);

    }

    //onClick: Pop-up to confirm name and gender
    public void confirm(View view) {

        //set references
        user_name_input = (EditText) findViewById(R.id.user_name_input);
        final String input = user_name_input.getText().toString();
        final RadioButton male_btn = (RadioButton) findViewById(R.id.boy_radiobtn);
        final RadioButton female_btn = (RadioButton) findViewById(R.id.girl_radiobtn);

        //Check if user has filled in a name (not just spaces) and selected a gender
        if (!male_btn.isChecked() && !female_btn.isChecked() && input.replace(" ", "").length() < 1 ){

            //Smal nudge animations
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(male_btn);

            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(female_btn);

            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(user_name_input);

            Toast.makeText(StartActivity.this, "Please fill inn a name and select a gender",Toast.LENGTH_LONG).show();
            return;
        }

        //Check if user has selected a gender
        else if (!male_btn.isChecked() && !female_btn.isChecked()){

            //Smal nudge animations
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(male_btn);

            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(female_btn);
            Toast.makeText(StartActivity.this, "You need to select a gender. \nNo, there is nothing else to choose...",Toast.LENGTH_LONG).show();
            return;
        }

        //Check if user has filled in a username (not just spaces)
        else if (input.replace(" ","").length() < 1){

            //Smal nudge animation
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(user_name_input);
            Toast.makeText(StartActivity.this, "I need to know your name",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            final String gender;

            if(male_btn.isChecked()){
                gender= "male";
            }
            else {
                gender = "female";
            }

            //make an alert object with buttons
            AlertDialog.Builder confirm_name_alert = new AlertDialog.Builder(this);
            confirm_name_alert.setMessage("Are you a " + gender + " and is \" " + input + " \" your name?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //remove textinput
                            user_name_input.setText("");

                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Save username and gender
                            saveData(input, gender);
                            //Set step done
                            sw.stepDone(0);
                            //Start new activity and send with StepView obj
                            startNextActivity();
                        }
                    })
                    .create();
            confirm_name_alert.show();
        }
    }

    //Saving username and gender to file
    private void saveData(String username, String gender){

        //Save username to shared preferences
        SharedPreferences settings = getSharedPreferences("userdata",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("gender", gender);
        // Commit username and gender
        editor.apply();
    }

    //Start nest activity. Needed to send StepView obj to next activity
    private void startNextActivity(){

        Intent intent = new Intent(StartActivity.this, TimeTableActivity.class);
        //Sending StepView obj to next activiy
        intent.putExtra("stepview_obj",sw);
        startActivity(intent);
        //Smooooooth transission
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }
}
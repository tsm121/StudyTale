package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;

public class Settings extends AppCompatActivity {

    //Userdata
    private String gender;
    private String username;

    //Set references
    private EditText userNameInput;
    private RadioButton male_btn;
    private RadioButton female_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Set refrences
        this.userNameInput = (EditText) findViewById(R.id.edit_user_name_input);
        this.male_btn = (RadioButton) findViewById(R.id.boy_radiobtn);
        this.female_btn = (RadioButton) findViewById(R.id.girl_radiobtn);

        //Set up menubar
        setActionBarChild("Settings");

        //Get userinfo
        readUserInfo();
    }

    //Set up Toolbar/Menu for child
    private void setActionBarChild(String heading) {

        //Set references
        Toolbar myToolbar = (Toolbar) findViewById(R.id.menubar);

        //Set Toolbar as active Actionbar for current activity
        setSupportActionBar(myToolbar);
        //Get Actionbar
        ActionBar actionbar = getSupportActionBar();
        //Enable back/home button
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.cat1)));
        //Set Heading
        actionbar.setTitle(heading);

        //Show menu
        actionbar.show();
    }

    //When closing resume last activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //onClick listener on edit timetable button
    public void goToTimetable(View view) {
        Intent intent = new Intent(Settings.this,TimeTableActivity.class);
        //Tell TimeTableActivity that user comes from settings and not from start screen
        intent.putExtra("from_settings", true);
        startActivity(intent);
        finish();

    }

    //Read filename and gender into EditText and RadioButton
    private void readUserInfo(){

        // Get username and gender
        SharedPreferences settings = getSharedPreferences("userdata", MODE_PRIVATE);
        this.username = settings.getString("username", "username");
        this.gender = settings.getString("gender", "male");

        //Set name into EditText
        this.userNameInput.setText(this.username);

        //Get focus and set cursor at end of text
        this.userNameInput.requestFocus();
        this.userNameInput.setSelection(this.username.length());

        //Select correct gender
        if(this.gender.matches("male")) this.male_btn.setChecked(true);
        else this.female_btn.setChecked(true);

    }

    //Check if input is correct
    private boolean confirmInput(){

        //Save old data
        String old_name = this.username;
        String old_gender = this.gender;

        //Get new data
        String input = userNameInput.getText().toString();
        String new_gender = male_btn.isChecked() ? "male" : "female";

        //No need to check if gender is selected since it's not possible to deselect a radio button
        //Check if there actualy was a change
        if(old_name.matches(input) && old_gender.matches(new_gender)){

            Toast.makeText(Settings.this, "No changes were done",Toast.LENGTH_LONG).show();
            finish();
            return false;

        }

        //Check if user has filled in a username (not just spaces)
        else if (input.replace(" ","").length() < 1){

            //Smal nudge animation
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(userNameInput);
            Toast.makeText(Settings.this, "I need to know your name",Toast.LENGTH_LONG).show();
            return false;
        }

        else return true;
    }

    //onClick for SAVE button
    public void goBack(View view) {

        //Save and check changes
        //Try again if true
        if (!confirmInput()){
            return;
        }
        //Continue, show info dialog
        else {
            saveInfoDialog();

        }

    }

    //Info dialog for edit user info
    public void saveInfoDialog() {

        //Make alert dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        //Inflate with custom dialog
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.info_dialog, null);
        //Set custom layout
        dialogBuilder.setView(view);
        //Create Dialog
        final AlertDialog alertdialog = dialogBuilder.create();

        //Set refrences
        Button ok_btn = (Button) view.findViewById(R.id.dialog_btn);
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        TextView messageText = (TextView) view.findViewById(R.id.dialog_message);
        ImageView icon = (ImageView) view.findViewById(R.id.dialog_icon);

        //Set title, message and icon
        title.setText("Name change:");
        messageText.setText("Your changes won't take effect until the next time you make a story.");
        icon.setImageResource(R.drawable.info_icon);

        //onClick listener on OK button in dialog
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save username to shared preferences
                SharedPreferences settings = getSharedPreferences("userdata",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", userNameInput.getText().toString());
                editor.putString("gender", male_btn.isChecked() ? "male" : "female");
                // Apply username and gender changes
                editor.apply();

                //Go to Story library when pressed OK
                Intent intent = new Intent(Settings.this, StoryLibrary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                alertdialog.dismiss();

            }
        });

        //Show alert dialog
        alertdialog.show();
    }

    //Info dialog for deleting user info
    public void deleteUserInfoDialog() {

        //Make alert dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        //Inflate with custom dialog
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.info_dialog, null);
        //Set custom layout
        dialogBuilder.setView(view);
        //Create Dialog
        final AlertDialog alertdialog = dialogBuilder.create();

        //Set refrences
        Button ok_btn = (Button) view.findViewById(R.id.dialog_btn);
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        TextView messageText = (TextView) view.findViewById(R.id.dialog_message);
        ImageView icon = (ImageView) view.findViewById(R.id.dialog_icon);

        //Set title, message and icon
        title.setText("Reset application:");
        messageText.setText("Are you sure you want to reset the app?\nThis results in removing all your stories.");
        icon.setImageResource(R.drawable.delete);

        //onClick listener on OK button in dialog
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteUserContent();
                //Restart app when pressing OK
                Intent intent = new Intent(Settings.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("hasReset", true);
                finish();
                startActivity(intent);
                alertdialog.dismiss();

            }
        });

        //Show alert dialog
        alertdialog.show();
    }

    //onClick delete user
    public void resetApp(View view) {
        deleteUserInfoDialog();
    }

    //Delete all user information and stories
    private void deleteUserContent(){

        //Delete story_list file for StoryLibrary
        File story_list = new File(Settings.this.getExternalFilesDir(null).toString(), "story_list.txt");
        story_list.delete();
        //Remove username
        SharedPreferences settings = getSharedPreferences("userdata",MODE_PRIVATE);
        settings.edit().clear().commit();

        //Delete every story in user_stories folder
        File stories = new File(Settings.this.getExternalFilesDir("user_stories").toString());
        if (stories.isDirectory()) {
            String[] children = stories.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(stories, children[i]).delete();
            }
        }
        System.out.println("USER CONTENT DELETED!");
    }
}

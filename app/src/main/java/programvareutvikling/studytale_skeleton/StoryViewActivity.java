package programvareutvikling.studytale_skeleton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class StoryViewActivity extends AppCompatActivity {

    private String story_title = "unknown";
    private String category_name = "unknown";
    private String filename;
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        //Story information
        this.filename = getIntent().getStringExtra("story_filename");
        this.story_title = getIntent().getStringExtra("story_title");
        this.category_name = getIntent().getStringExtra("category_name");

        //Checking if there is a chapter to unlock
        checkUnlock();

        //Show unlocked chapters
        getChapters();

        //Set up menu
        setActionBarChild(this.story_title);
    }

    //Checking if there is a chapter to unlock
    private void checkUnlock() {


        //Checks if the "unlock"-value is true, if not stop
        if(getIntent().getBooleanExtra("unlock", false)) {
            String _chap_num = getIntent().getStringExtra("unlock_num");
            //Check if user is unlocking his/her first story
            if (_chap_num.matches("Chapter 1:")) {
                unlockChapter(_chap_num);
                unlcokSnackbarPopUp("You unlocked your first chapter!");
                //Set the "unlock"-value back to false
                getIntent().putExtra("unlock", false);
            }
            //Check if chapter > 1 can be unlocked. canUnlock() checks the location of the user
            else if (canUnlock()) {
                //If user is inside location unlock chapter and show pupup
                if(unlockChapter(_chap_num)) unlcokSnackbarPopUp("You unlocked a new chapter!");
                //Set the "unlock"-value back to false
                getIntent().putExtra("unlock", false);
            }
            //User is inside location but at the wrong time
            else
                Toast.makeText(this, "You need to be at school at \'LECTURE_TIME_HERE\' \nto unlock next chapter", Toast.LENGTH_LONG).show();
        }
    }

    //Check if user is at correct location
    private boolean canUnlock(){

        //Make a new GPSTracker object to find user location
        GPSTracker gps = new GPSTracker(StoryViewActivity.this);

        //Check if GPS-object can get user location
        if (gps.canGetLocation){

            //Get user location
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //Campus (GLØSHAUGEN) location
            //TODO: Change this to something more dynamicly
            double lat1 = 63.41;
            double lat2 = 63.42;
            double long1 = 10.39;
            double long2 = 10.41;

            //Checks if user is inside location
            if (latitude >= lat1 && latitude <= lat2 && longitude >= long1 && longitude <= long2){
                return true;
            }
        }
        return false;
    }

   //When on-screen back button is pressed
    @Override
    public void onBackPressed() {
        openActivity();
    }

    //Show unlocked chapters
    private void getChapters(){

        //Getting shared preferences for story file
        settings = getSharedPreferences(filename, MODE_PRIVATE);

        //Check if user is allowed to see chapter x. If so put into view
        for (int i = 1; i <11 ; i++) {

            String _chap_num = "Chapter " + Integer.toString(i) + ":";
            if(settings.getBoolean(_chap_num, false))generateChapter(_chap_num, readChapter(_chap_num, filename));
            else return;
        }
    }

    //Unlocks chapter x for current story
    private boolean unlockChapter(String chapter_num){

        //Get preferences for current story
        settings = getSharedPreferences(filename, MODE_PRIVATE);
        //If story already has been unlocked, stop
        if(settings.getBoolean(chapter_num, false)) return false;
        //Change chapter x value to true
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(chapter_num, true);
        //Commit changes to SharedPrefs
        editor.commit();
        return true;
    }

    //Inflate chapter into layout
    private void generateChapter(String chapter_num, String chapter_text){

        //Get layout to inflate into and the views to inflate
        LinearLayout layout = (LinearLayout) findViewById(R.id.chapter_view);
        View child = getLayoutInflater().inflate(R.layout.f_chapter, layout, false);

        //Set references
        TextView chapterTitle = (TextView) child.findViewById(R.id.chapter_num);
        TextView storyText = (TextView) child.findViewById(R.id.storyText);

        //Set chapter title and story text
        chapterTitle.setText(chapter_num);
        storyText.setText(chapter_text);

        //Inflate views to layout
        layout.addView(child);
    }

    //Get chapter text for current story
    private String readChapter(String chapter_num, String filename){

        String chapter_text = "";

        try {
            //Open file and generate a reader
            File file = new File(StoryViewActivity.this.getExternalFilesDir("/user_stories").toString(), filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            //Read given chapter into a String. Add paragraph for each line. Return chapter text
            while ((line = reader.readLine()) != null){

                if(line.matches(chapter_num)){

                    while ((line = reader.readLine()) != null){
                        if(line.matches("CHAPTER_END")) return chapter_text;
                        chapter_text += line + "\n\n";
                    }
                }
            }
        }catch (Exception e){
            return "Can't find your story";
        }
        //If the given chapter was not found
        return "UNKNOWN";
    }

    //Set up Toolbar/Menu for child
    private void setActionBarChild(String heading) {

        //Set references
        Toolbar myToolbar = (Toolbar) findViewById(R.id.menubar);
        String color_name;

        //Set toolbar color corsponding to the category
        if(this.category_name == null) color_name = "bg";
        else if(this.category_name.matches("fantasy")) color_name = "cat1";
        else if(this.category_name.matches("children")) color_name = "cat2";
        else if(this.category_name.matches("action")) color_name = "cat3";
        else if(this.category_name.matches("sexual")) color_name = "cat4";
        else color_name = "bg";

        //Set color
        int desiredColour = getResources().getColor(getResources().getIdentifier(color_name, "color", getPackageName()));
        myToolbar.setBackgroundColor(desiredColour);

        //Set Toolbar as active Actionbar for current activity
        setSupportActionBar(myToolbar);
        //Get Actionbar
        ActionBar actionbar = getSupportActionBar();
        //Enable back/home button
        actionbar.setDisplayHomeAsUpEnabled(true);
        //Set Heading
        actionbar.setTitle(heading);

        //Show menu
        actionbar.show();
    }

    //Choose actions for toolbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //Back/home button
            case android.R.id.home:
                openActivity();
                return true;

            //Edit title
            case R.id.edit:
                editDialog();
                return true;

            //Delete story
            case R.id.delete:
                deleteDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Inflate Toolbar with custom layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout_child, menu);
        return true;
    }

    //Edit dialog for chaning story name
    public void editDialog() {

        //Make alert dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //Inflate with custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.input_dialog, null);
        dialogBuilder.setView(dialogView);
        //Set references for input box
        final EditText edit_text = (EditText) dialogView.findViewById(R.id.edit);
        //Save old storyname if user regrets
        if(this.story_title == null){this.story_title = "Unknown title";}
        final String temp_old_name = this.story_title;
        edit_text.setText(temp_old_name);
        //Set focus and move cursor in input
        edit_text.requestFocus();
        if(temp_old_name != null) edit_text.setSelection(temp_old_name.length());


        //Set settings for alert dialog
        dialogBuilder.setTitle("Change story name");

        //Add DONE button
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //Check if the user has filled in text. Dismiss if true
                if (edit_text.getText().toString().length() > 0){
                    StoryViewActivity.this.story_title = edit_text.getText().toString();


                    //Check if user actually did any changes
                    if (!temp_old_name.matches(edit_text.getText().toString())){
                        setActionBarChild(StoryViewActivity.this.story_title);
                        //Snackbar popup if user regrets
                        SnackbarPopup(temp_old_name, "Story name changed", "edit_title");
                    }

                }
                else{
                    Toast.makeText(StoryViewActivity.this, "You need to fill in a name",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }
        });
        //Add cancel button
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.dismiss();

            }
        });

        //Show alert dialog
        AlertDialog alertdialog = dialogBuilder.create();
        alertdialog.show();
    }

    //Snackbar popup for edit text
    public void SnackbarPopup(final String old_name, String popup_msg, final String type) {

        //Create a new Snackbar with given text
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.constrainLayout), popup_msg, Snackbar.LENGTH_LONG);
        mySnackbar.setActionTextColor(getResources().getColor(R.color.bg_light));
        //Set Snackbar button and onClick event
        mySnackbar.setAction("UNDO?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type.matches("edit_title")) {
                    //Revert changes to title
                    StoryViewActivity.this.story_title = old_name;
                    setActionBarChild(old_name);
                }
                else {
                    //TODO: Undo delete story,Go to Story Library
                }
            }
        });

        mySnackbar.show();
    }

    //Snackbar popup for edit text
    public void unlcokSnackbarPopUp(String popup_msg) {

        //Create new Snackbar with given text
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.constrainLayout), popup_msg, Snackbar.LENGTH_INDEFINITE);
        mySnackbar.setActionTextColor(getResources().getColor(R.color.bg_light));
        //Set button text. No onClick event
        mySnackbar.setAction("WOHO!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
        mySnackbar.show();
    }

    //Delete dialog for removing story
    private void deleteDialog() {

        //Make alert dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //Set settings for alert dialog
        dialogBuilder.setTitle("Do you want to delete \n'" + story_title + "'?");

        //Add YES button
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
                Intent intent = getIntent();
                intent.setClass(StoryViewActivity.this, StoryLibrary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("delete", true);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        //Add CANCEL button
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        //Show alert dialog
        AlertDialog alertdialog = dialogBuilder.create();
        alertdialog.show();
    }

    //Check if a StoryLibrary need to be updated. Set flags
    private void openActivity(){
        Intent intent;

        //Reload StoryLibrary if a new story just got created
        if(getIntent().getBooleanExtra("new_story", false)){
            intent = new Intent(StoryViewActivity.this,StoryLibrary.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        }
        //Continue task if no story has been made
        else {
            intent = new Intent(StoryViewActivity.this, StoryLibrary.class);
            //If intent already runs pause StoryView and set StoryLibrary to top of stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
    }
}

package programvareutvikling.studytale_skeleton;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class StoryLibrary extends AppCompatActivity {

    private static boolean activityVisible;
    private SwipeMenuListView storiesView;
    private ArrayList<String> story_categories;
    private ArrayList<String> story_filenames;
    private String[] stories;
    private Integer[] imageID;
    private ArrayList<String> timeLeft;

    //Min and max time for time restrictions (15+/- lecture_time is default)
    private int min_time = 15;
    private int max_time = 15;

    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_CODE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_library);

        //Check if user has given permission to check location
        checkLocationPermissions();

        //Read story names
        readStoryNames();

        //Check if StoryView wants to delete story, if true, delete.
        if(getIntent().getBooleanExtra("delete", false)){
            deleteStory(getIntent().getIntExtra("position", -1));
            getIntent().putExtra("delete", false);
        }

        //Set up menu
        setActionBar("Story Library");

        //Set up list for stories
        setStoryList();

        //onClick listener for ListView
        storiesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Put info into intent for StoryViewActivity
                Intent intent = new Intent(StoryLibrary.this, StoryViewActivity.class);
                intent.putExtra("story_filename", story_filenames.get(position));
                intent.putExtra("story_title", stories[position]);
                intent.putExtra("category_name",story_categories.get(position));
                intent.putExtra("position", position);

                //Current filename to story
                String filename = story_filenames.get(position);
                //Check if user can unlock a chapter based on lecture time
                if (canUnlock(position, filename)){
                    //Tell StoryView that given chapter can be unlocked if user is at school
                    intent.putExtra("unlock", true);
                    intent.putExtra("unlock_num", getChapter(filename));
                }
                onPause();
                startActivity(intent);
            }
        });
    }

    //Check if user can unlock given story
    private boolean canUnlock(int position, String filename){

        //Get time text from ListView
        TextView time_text = (TextView) findViewById(position + 69); //hehe

        //Check if time text has told user that there is a new chapter
        if (time_text.getText().equals("NEW CHAPTER!")){

            //Check if user is within timelimits
            if(checkDate(filename)) return true;
            return false;
        }
        return false;
    }

    //Check if user is within timelimits of given story
    private boolean checkDate(String filename) {

        try {
            //Get next lecture
            SharedPreferences story_prefs = getSharedPreferences(filename, MODE_PRIVATE);
            String lecture = story_prefs.getString("next_lecture", null);

            //Format to match dates
            SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM-yyyy");

            //Create date object from next lecture
            Date lecture_date = format.parse(lecture);
            //Date current_time = new Date();

            //FOR DEBUGING
            //TODO: Commented code under is used for debuging and demo use
            // Set this to +/- min/max lecture_date to unlock chapters, comment out current_time over
            Date current_time = format.parse("08:15 7-4-2017");
            System.out.println(lecture_date.toString());

            //Create max and min date objects with range given
            Calendar min = Calendar.getInstance();
            min.setTime(lecture_date);
            min.add(Calendar.MINUTE, - min_time);

            Calendar max = Calendar.getInstance();
            max.setTime(lecture_date);
            max.add(Calendar.MINUTE, max_time);

            //Check if user is within time range, set new lecture if true
            if(current_time.after(min.getTime()) && current_time.before(max.getTime())){
                SharedPreferences.Editor editor = story_prefs.edit();
                editor.putString("lecture", getNextLecture());
                return true;
            }

            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("COULD NOT SET NEW LECTURE");
            return false;
        }
    }

    //Check if user has any chapters to unlock
    private String getChapter (String filename) {
        SharedPreferences story_prefs = getSharedPreferences(filename, MODE_PRIVATE);
        for (int i = 2; i < 11; i++) {
            String _chap = "Chapter " + Integer.toString(i) + ":";
            if (!story_prefs.getBoolean(_chap, false)) {
                return "Chapter " + Integer.toString(i) + ":";
            }
        }
        return "none";
    }

    //Get weekday x-days into the future
    private String getWeekday(int add_days){

        GregorianCalendar weekday = new GregorianCalendar();
        //Add "add_days" to the date
        weekday.add(Calendar.DATE, add_days);
        return weekday.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).toLowerCase();
    }

    //Get users next lecture
    private String getNextLecture(){

        String lecture = null;
        //Get weekday
        String weekday = getWeekday(1);
        //Counter if no lecture was found
        int counter = 2;
        //Get all lectures
        SharedPreferences lectures = getSharedPreferences("lectures", MODE_PRIVATE);
        //Get lecture from one day forward
        lecture = lectures.getString(weekday,null);

        //If no lecture was found on given day, check next day until one is found
        while (lecture != null){
            weekday = getWeekday(counter);
            lecture = lectures.getString(weekday,null);
            counter ++;
        }


        return lecture;
    }

    //Check if user has allowed app to get location
    private void checkLocationPermissions() {

        //Check if user has given permission. If not ask user.
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not activate location service. Check your device settings", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //Inflate Toolbar with custom layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout_main, menu);
        return true;
    }

    //onClick for settings button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //User pressed settings-button. Go to SettingsActivity
            case R.id.settings:
                onPause();
                startActivity(new Intent(StoryLibrary.this, Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Set up ListView for stories
    private void setStoryList(){

        //Set references
        storiesView = (SwipeMenuListView) findViewById(R.id.storiesView);
        LinearLayout empty_layout = (LinearLayout) findViewById(R.id.no_stories_layout);

        //Check if there is no stories, return to empty library
        if(this.stories == null){
            empty_layout.setVisibility(View.VISIBLE);
            return;
        }
        else if (this.stories.length == 0) {
            empty_layout.setVisibility(View.VISIBLE);
            return;
        }
        else {
            empty_layout.setVisibility(View.GONE);
        }

        //Make an ListAdapter for ListView with stories
        ListAdapter listAdapter = new StoryAdapter(this, this.stories, this.imageID, this.timeLeft, this.story_filenames);

        //Set ListView settings
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red_delete)));
                // set item width
                deleteItem.setWidth(getResources().getDisplayMetrics().widthPixels/3-60);
                // set a icon
                deleteItem.setIcon(R.drawable.delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        //add items to view
        storiesView.setMenuCreator(creator);


        //Listeneres for custom items
        storiesView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteDialog(position);
                        break;
                }
                return false;
            }
        });



        // Right
        storiesView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        // Left
        storiesView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        //Set adapter to ListView
        storiesView.setAdapter(listAdapter);
    }

    //Set up Toolbar/Menu for parent
    private void setActionBar(String heading) {

        //Set references
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.menubar);
        //Set Toolbar as active Actionbar for current activity
        setSupportActionBar(myToolbar);

        //Get Actionbar
        ActionBar actionbar = getSupportActionBar();
        //Enable back/home button
        actionbar.setDisplayHomeAsUpEnabled(false);
        //Set Heading
        actionbar.setTitle(heading);
        //Show menu
        actionbar.show();
    }

    //Read story titles and imageID's
    public void readStoryNames() {

        //ArrayList for the reader
        ArrayList<String> temp_stories= new ArrayList<>();
        ArrayList<Integer> temp_imageID = new ArrayList<>();
        story_filenames = new ArrayList<>();
        story_categories = new ArrayList<>();
        timeLeft = new ArrayList<>();

        String line = null;

        try {
            //Get file
            File file = new File(StoryLibrary.this.getExternalFilesDir(null), "story_list.txt");
            //Set up reader
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null){

                //Split line and extract values
                String[] split_line = line.split(", ");
                //Story title
                temp_stories.add(split_line[0]);
                //Story category
                story_categories.add(split_line[1]);
                //Story filename
                story_filenames.add(split_line[2]);
                //Next lecture
                timeLeft.add(split_line[3]);

                //make image names
                String image_name = (split_line[1] + "_icon");

                //Get image ID
                int resID = getResources().getIdentifier(image_name, "drawable", getPackageName());

                //If image is not found add unknown icon
                if(resID == 0) resID = getResources().getIdentifier("unknown_icon" , "drawable", getPackageName());
                temp_imageID.add(resID);
            }

            //Make String array for ListView
            stories = new String[temp_stories.size()];
            stories = temp_stories.toArray(stories);

            //Make Integer array for ListView
            imageID = new Integer[temp_imageID.size()];
            imageID = temp_imageID.toArray(imageID);

            //Close reader and stream
            reader.close();

        } catch (Exception e) {
            temp_stories.add("Can't read story titles");
        }
    }

    //Getter to check if activity is paused
    public static boolean isActivityPaused() {
        return activityVisible;
    }

    //Set status when activity is resumed
    public static void activityResumed() {
        activityVisible = false;
    }

    //Set status when activity is paused
    public static void activityPaused() {
        activityVisible = true;
    }

    //Override onResume to set status
    @Override
    protected void onResume() {
        super.onResume();
        this.activityResumed();
    }

    //Override onPause to set status
    @Override
    protected void onPause() {
        super.onPause();
        this.activityPaused();
    }

    //Delete dialog for removing story
    private void deleteDialog(final int position) {

        //Make alert dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //Set settings for alert dialog
        dialogBuilder.setTitle("Do you want to delete \n'" + this.stories[position] + "'?");

        //Add YES button
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteStory(position);
                finish();
                Intent intent = new Intent(StoryLibrary.this, StoryLibrary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    //onClick event on +(plus)button
    public void newStoryBtn(View view) {
        //Send user to ChooseCategoryActivity for adding a new story.
        Intent intent = new Intent(StoryLibrary.this, ChooseCategoryActivity.class);
        //Set "new_user"-value to false. Used in CreateStory
        intent.putExtra("new_user", false);
        onPause();
        startActivity(intent);

    }

    //Delete given story
    private void deleteStory(int position){

        //Stop if position is not defined
        if(position == -1) return;

        try {
            //Get file
            File file = new File(StoryLibrary.this.getExternalFilesDir(null), "story_list.txt");
            //Set up reader
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            ArrayList<String> _file_content = new ArrayList<>();
            //Get story data
            while ((line = reader.readLine()) != null){
                _file_content.add(line);
            }
            reader.close();

            //Remove story data
            _file_content.remove(position);
            story_categories.remove(position);
            story_filenames.remove(position);
            ArrayList<String> _temp_stories = new ArrayList<>();
            Collections.addAll(_temp_stories, stories);
            _temp_stories.remove(position);
            stories = _temp_stories.toArray(new String[_temp_stories.size()]);
            ArrayList<Integer> _temp_imageID = new ArrayList<>();
            Collections.addAll(_temp_imageID, imageID);
            _temp_imageID.remove(position);
            imageID = _temp_imageID.toArray(new Integer [_temp_imageID.size()]);

            //Write back old data
            FileWriter writer = new FileWriter(file, false);

            for (String _old_content:_file_content) {
                writer.write(_old_content + "\n");
            }
            writer.flush();
            writer.close();
            System.out.println("STORY DELETED");
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("COULD NOT READ STORY_LIST");
        }
    }
}
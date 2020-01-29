package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;


public class CreateStory extends AppCompatActivity {

    HashMap<String, ArrayList<String>> chapters;
    ArrayList<String> friends;
    ArrayList<String> items;
    ArrayList<String> enemies;
    ArrayList<String> textBoxes;
    String filename;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        WaveLoadingView loadingView = (WaveLoadingView) findViewById(R.id.loader);
        loadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
        loadingView.setAnimDuration(3000);

        //TODO: PUT SQL DATA INTO STORY

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //Generate chapters with user inputs
                generateChapters();

                //Save generated story to file
                saveStoryFile();

                //Add story to StoryLibrary-file
                saveToStoryLibrary();

                //Add story information for StoryViewActivity
                Intent intent = getIntent();
                intent.putExtra("new_story", true);
                intent.putExtra("story_filename", filename);
                intent.putExtra("unlock", true);
                intent.putExtra("unlock_num", "Chapter 1:");
                intent.setClass(CreateStory.this, StoryViewActivity.class);
                startActivity(intent);
            }
        }, 2500); //Delay showing "CreateStory" activity //use 2500ms

    }

    //Save generated story to file
    private void saveStoryFile(){

        String filename = getIntent().getStringExtra("filename");

        try {
            //Delete old story file
            File old_file = new File(CreateStory.this.getExternalFilesDir("/user_stories").toString() + filename);
            if(old_file.exists()){
                old_file.delete();
                System.out.println("Deleting old storyfile");
            }

            //Write back new data
            File dir = new File(CreateStory.this.getExternalFilesDir("/user_stories").toString());

            //Check if directory excist. If not make
            if (!dir.exists()) dir.mkdirs();

            //Write generated story to file
            if(dir.exists()){

                File file = new File(dir.getAbsolutePath(), filename);
                FileWriter writer = new FileWriter(file, false);

                String _chapter;
                ArrayList<String> _chapterText;
                //Add story title and category name to top of file
                writer.write(getIntent().getStringExtra("story_title") + "," + getIntent().getStringExtra("category_name") + "\n");

                for (int i = 1; i < 11; i++) {
                    _chapter = "Chapter " + Integer.toString(i) + ":";
                    if(i != 1) writer.write("CHAPTER_END\n");
                    //Add "CHAPTER_END" between each chapter
                    writer.write(_chapter + "\n");
                    _chapterText = chapters.get(_chapter);

                    for (String line:_chapterText) writer.write(line + "\n");
                    //Add "CHAPTER_END" at the end of the file
                    if( i == 10) writer.write("CHAPTER_END\n");
                }

                //Close and flush
                writer.flush();
                writer.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    //Generate chapter based on user input
    private void generateChapters() {

        //Get user inputs
        Intent intent = getIntent();
        this.friends = intent.getStringArrayListExtra("friends");
        this.items = intent.getStringArrayListExtra("items");
        this.enemies = intent.getStringArrayListExtra("enemies");
        this.textBoxes = intent.getStringArrayListExtra("textboxes");
        this.chapters = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("chapters");
        ArrayList<String> _chapterText = new ArrayList<>();
        SharedPreferences settings = getSharedPreferences("userdata", MODE_PRIVATE);
        String username = settings.getString("username", "username");

        //Fix indexes so every list has 10-items
        fixIndexes();

        //Used for UnitTesting when the Activity is beeing run by it's own.
        if(_chapterText == null || this.chapters == null) return;

        //Generate the story
        //Go through each line and change elements with given inputs by the user
        for (int chapter_num = 1; chapter_num < 11; chapter_num++) {

            try {
                _chapterText = chapters.get("Chapter " + chapter_num + ":");
            }catch (Exception e){e.printStackTrace();}

            for (int j = 0; j < _chapterText.size(); j++) {

                String _temp = _chapterText.get(j);
                StringBuilder SB = new StringBuilder(_temp);

                for (int i = 1; i < 11; i++) {
                    _temp = SB.toString();
                    SB.setLength(0);
                    SB.append((_temp.replace("friend" + Integer.toString(i), friends.get(i - 1))));

                    _temp = SB.toString();
                    SB.setLength(0);
                    SB.append((_temp.replace("item" + Integer.toString(i), items.get(i - 1))));

                    _temp = SB.toString();
                    SB.setLength(0);
                    SB.append((_temp.replace("enemy" + Integer.toString(i), enemies.get(i - 1))));

                    _temp = SB.toString();
                    SB.setLength(0);
                    SB.append((_temp.replace("textbox" + Integer.toString(i), textBoxes.get(i - 1))));

                    _temp = SB.toString();
                    SB.setLength(0);
                    SB.append((_temp.replace("user", username)));
                }

                _chapterText.set(j, SB.toString());
            }
            chapters.put("Chapter " + chapter_num + ":", _chapterText);
        }
    }

    //Adding story to library
    private void saveToStoryLibrary(){

        try {

            filename = getIntent().getStringExtra("filename");

            //Create new file obj, and a new file if not exist
            File file = new File(CreateStory.this.getExternalFilesDir(null), "story_list.txt");
            file.createNewFile();

            //Reading file to get old content
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> _old_content = new ArrayList<>();
            String line;

            //Read lines
            while ((line = reader.readLine()) != null){
                _old_content.add(line);
            }
            reader.close();

            //Write back new story and add the old stories
            FileWriter writer = new FileWriter(file, false);
            writer.write(getIntent().getStringExtra("story_title") + ", " + getIntent().getStringExtra("category_name")
                    +  ", " + filename + ", 08:00 05-04-2017" + "\n");

            for (String _old_line:_old_content) {
                writer.write(_old_line + "\n");
            }

            //Close and flush
            writer.flush();
            writer.close();

            setUnlockedChapters(filename);

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    //Disabling backbutton
    @Override
    public void onBackPressed() {
        return;
    }

    //Fixing index errors in generateStory
    private void fixIndexes(){

        //Used for UnitTesting when the Activity is beeing run by it's own.
        if(this.friends == null || this.items == null || this.enemies == null || this.textBoxes == null) return;

        //This is needed for not to get IndexOutOfBounds error
        //Yeah... not the best solution, but it works :)

        while (this.friends.size() < 11) this.friends.add("NONE");

        while (this.items.size() < 11) this.items.add("NONE");

        while (this.enemies.size() < 11) this.enemies.add("NONE");

        while (this.textBoxes.size() < 11) this.textBoxes.add("NONE");
    }

    //Set info about unlocked chapters for given story
    private void setUnlockedChapters(String filename){

        SharedPreferences settings = getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences lectures = getSharedPreferences("lectures", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Chapter 1:", false);
        editor.putBoolean("Chapter 2:", false);
        editor.putBoolean("Chapter 3:", false);
        editor.putBoolean("Chapter 4:", false);
        editor.putBoolean("Chapter 5:", false);
        editor.putBoolean("Chapter 6:", false);
        editor.putBoolean("Chapter 7:", false);
        editor.putBoolean("Chapter 8:", false);
        editor.putBoolean("Chapter 9:", false);
        editor.putBoolean("Chapter 10:", false);
        String next_lecture = lectures.getString(getWeekDay(),null);
        editor.putString("next_lecture", next_lecture);
        editor.putBoolean("can_unlock", false);
        editor.apply();
    }

    //Returing weekday + 1 day
    private String getWeekDay(){

        GregorianCalendar tomorrow = new GregorianCalendar();
        tomorrow.add(Calendar.DATE, 1);
        //TODO: Check if current date or tomorrow is correct for project

        return (tomorrow.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).toLowerCase());

    }
}

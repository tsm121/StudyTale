package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import me.itangqi.waveloadingview.WaveLoadingView;



public class GetStory extends AppCompatActivity {

    private ReadFile readfile;

    //SQL QUERIES
    //TODO: This is where SQL data extracted via ReadFile are going

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_story);

        WaveLoadingView loadingView = (WaveLoadingView) findViewById(R.id.loader);
        loadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
        loadingView.setCenterTitle("Searching for plot");
        loadingView.setBottomTitle("Theme: " + getIntent().getStringExtra("category_name"));
        loadingView.setAnimDuration(3000);

        //TODO: SQL QUERIES, SAVE RESPONSE DATA FROM SERVER

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                String category_name = getIntent().getStringExtra("category_name");
                //Get story from server with given category
                getStoryFromServer(category_name);
                Intent intent = intentData();
                intent.putExtra("category_name", category_name);
                //Start activity Customize
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

            }
        }, 2000); //Delay showing "GetStory" activity //use 2000
    }

    //Find a story from server, get filename and fileurl
    private void getStoryFromServer(String category_name){

        //TODO: USE URL FROM SQL QUERIES
        //These links are hardcoded since there is yet no working server
        String story_link = "http://m.uploadedit.com/ba3s/1493045828242.txt";
        String story_filename = "test_story.txt";
        //All stories after the first will be this:
        if(getIntent().hasExtra("new_user")){
            story_link = "http://m.uploadedit.com/ba3s/1492705876687.txt";
            story_filename = "test_story2.txt";
        }

        readStoryFile(story_filename, story_link);

    }

    //download and read story from server
    private void readStoryFile(String story_filename, String story_link){

        //Download and read file
        readfile = new ReadFile(story_filename, story_link, GetStory.this);

    }

    //Disabling backbutton
    @Override
    public void onBackPressed() {
    }

    //Send story data and start activity
    private Intent intentData(){

        Intent intent = new Intent(GetStory.this, CustomizeActivity.class);
        //Story settings needed for Customize activity
        intent.putExtra("story_title", readfile.getStoryTitle());
        intent.putExtra("num_friends",readfile.getNumFriends());
        intent.putExtra("num_items",readfile.getNumItems());
        intent.putExtra("num_enemies",readfile.getNumEnemies());
        intent.putExtra("num_textboxes",readfile.getNumTextboxes());
        intent.putExtra("friends_title",readfile.getFriendsTitle());
        intent.putExtra("items_title",readfile.getItemsTitle());
        intent.putExtra("enemies_title",readfile.getEnemiesTitle());
        intent.putExtra("textboxes_title",readfile.getTextboxesTitle());
        intent.putExtra("friends_hint",readfile.getFriendsHint());
        intent.putExtra("items_hint",readfile.getItemsHint());
        intent.putExtra("enemies_hint",readfile.getEnemiesHint());
        intent.putExtra("textboxes_hint",readfile.getTextboxesHint());

        //Story chapters and SQL queries needed for CreateStory activity
        intent.putExtra("chapters", readfile.getChapters());
        intent.putExtra("chapter_list", readfile.getChapterlist());
        intent.putExtra("sql_queries", readfile.getSqlQueries());

        //Filename needed for CreateStory
        intent.putExtra("filename", readfile.getFilename());

        if(getIntent().hasExtra("new_user")) intent.putExtra("new_user", false);

        return intent;
    }
}

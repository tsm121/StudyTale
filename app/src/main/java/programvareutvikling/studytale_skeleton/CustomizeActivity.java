package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;


public class CustomizeActivity extends AppCompatActivity {

    private LinearLayout layout;
    static private StepView sw;
    private boolean new_user = true;

    //Story settings
    private String story_title;
    private String category_name;
    private int int_brutal;
    private int int_sex;
    private int int_kind;
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> enemies = new ArrayList<>();
    private ArrayList<String>  textBoxes= new ArrayList<>();

    //Variables for dynamic UI
    private ArrayList<EditText> friend_inputs = new ArrayList<>();
    private ArrayList<EditText> item_inputs = new ArrayList<>();
    private ArrayList<EditText> nemesis_inputs = new ArrayList<>();
    private ArrayList<EditText> textBox = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        //New intent to get data from last activity
        Intent intent = getIntent();
        //Get activity data
        this.story_title = intent.getStringExtra("story_title");
        this.category_name = intent.getStringExtra("category_name");

        //Set references
        layout = (LinearLayout) findViewById(R.id.activity_customize);

        //Enable StepView or not
        chooseNextMove();

        //Generate dynamic inputs
        generateInputs();

    }

    //Set x-num of input fields
    private int addItemInput(int start_index, int num_items, int inflateable_layout, int input_id, String text_hint, int title_id, String text_title, ArrayList<EditText> array_list ){

        //remove title if there are no input fields
        if(num_items == 0 ){

            if(array_list == textBox){
                TextView tv = (TextView) findViewById(R.id.textboxes_text);
                tv.setVisibility(View.GONE);
            }

            else if(array_list == friend_inputs){
                TextView tv = (TextView) findViewById(R.id.friends_text);
                tv.setVisibility(View.GONE);
                start_index = 1;
            }

            else if(array_list == item_inputs){
                TextView tv = (TextView) findViewById(R.id.items_text);
                tv.setVisibility(View.GONE);
            }

            else if(array_list == nemesis_inputs){
                TextView tv = (TextView) findViewById(R.id.enemies_text);
                tv.setVisibility(View.GONE);
            }
        }

        //Inflate items into layout
        for (int item = 1; item < num_items + 1 ; item++) {
            //Make a new layout from f_name.xml
            View view = getLayoutInflater().inflate(inflateable_layout, layout, false);
            //set reference for EditText from f_name.xml
            EditText input_object = (EditText) view.findViewById(input_id);
            //set text hint
            if(num_items == 1){
                input_object.setHint(text_hint);
            }
            else {
                input_object.setHint(text_hint + Integer.toString(item));
            }
            //add custom id to get inuput later
            input_object.setId(start_index + item);
            //add input to array
            array_list.add(input_object);
            //Add the view to our Layout (LinearLayout in this case)
            layout.addView(view, start_index + item);
        }
        //Set title
        TextView title = (TextView)findViewById(title_id);
        title.setText(text_title);

        //return index for next input type
        return start_index + num_items + 1;
    }

    //Saving inputs
    private boolean saveInputs(){

        //Friends:
        for (EditText tv: friend_inputs) {
            if(friend_inputs.isEmpty()){
                continue;
            }
            //check if user has filled in anything
            String text = tv.getText().toString();
            if (!validateInputs(text)){
                return false;
            }

            friends.add(text);
        }

        //Items:
        for (EditText tv: item_inputs) {
            String text = tv.getText().toString();
            if (!validateInputs(text)){
                return false;
            }
            items.add(text);
        }

        //Enemies:
        for (EditText tv: nemesis_inputs) {
            String text = tv.getText().toString();
            if (!validateInputs(text)){
                return false;
            }
            enemies.add(text);
        }

        //Text boxes
        for (TextView tv: textBox) {
            String text = tv.getText().toString();
            if (!validateInputs(text)){
                return false;
            }
            textBoxes.add(text);
        }

        //Set references to sliders and slider texts
        SeekBar seekbar_brutal = (SeekBar) findViewById(R.id.seekbar_brutal);
        SeekBar seekbar_sex = (SeekBar) findViewById(R.id.seekbar_sex);
        SeekBar seekbar_kind = (SeekBar) findViewById(R.id.seekbar_kind);

        //Save seekbar progress
        int_brutal = seekbar_brutal.getProgress()/10;
        int_sex = seekbar_sex.getProgress()/10;
        int_kind = seekbar_kind.getProgress()/10;

        return true;
    }

    //onClick: save settings and open new story
    public void goToStory(View view) {

        //check if all fields was filled in
        if(!saveInputs()){

            return;
        };

        //save data and start avtivity for creating story
        //Ger previous intent
        Intent intent = getIntent();

        //Add userinfo for CreateStory
        intent.putExtra("friends", this.friends);
        intent.putExtra("items", this.items);
        intent.putExtra("enemies", this.enemies);
        intent.putExtra("textboxes", this.textBoxes);
        intent.putExtra("brutal", Integer.toString(this.int_brutal));
        intent.putExtra("sex", Integer.toString(this.int_sex));
        intent.putExtra("kind", Integer.toString(this.int_kind));


        //Edit destination on intent
        intent.setClass(CustomizeActivity.this, CreateStory.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

    }

    //validates if all fields has a input
    private boolean validateInputs(String text_input){

        //To turn of validation, comment out code below and set method to return true

          if (text_input.length() < 1){
            //Smal animation
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.nextBtn));
            Toast.makeText(this, "You need to fill out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //Generating dynamic UI
    private void generateInputs(){
        Intent intent = getIntent();

        TextView storyTitleText = (TextView) findViewById(R.id.story_title);
        storyTitleText.setText("You got the story:\n" + story_title);

        //Friends:
        int start_index = addItemInput(2, intent.getIntExtra("num_friends",1), R.layout.f_name, R.id.friend_name, intent.getStringExtra("friends_hint"), R.id.friends_text, intent.getStringExtra("friends_title"), friend_inputs);
        //Items:
        start_index = addItemInput(start_index, intent.getIntExtra("num_items",0), R.layout.f_items, R.id.item_name, intent.getStringExtra("items_hint"), R.id.items_text, intent.getStringExtra("items_title"), item_inputs);
        //Enemies
        start_index = addItemInput(start_index, intent.getIntExtra("num_enemies",0), R.layout.f_nemesis, R.id.nemesis_name, intent.getStringExtra("enemies_hint"), R.id.enemies_text, intent.getStringExtra("enemies_title"), nemesis_inputs);
        //Customable text boxes
        addItemInput(start_index, intent.getIntExtra("num_textboxes",0), R.layout.f_textfield, R.id.textField, intent.getStringExtra("textboxes_hint"), R.id.textboxes_text, intent.getStringExtra("textboxes_title"), textBox);
    }

    //Choose next move for activty. Check for new user or not
    private void chooseNextMove() {

        //Check if it's a new user or not
        if (getIntent().hasExtra("new_user")) {
            this.new_user = false;
            HorizontalStepView step_view = (HorizontalStepView)findViewById(R.id.step_view);
            step_view.setVisibility(View.GONE);
        }
        //Enable StepView
        else {
            //Get StepView obj from last activity
            this.sw = getIntent().getParcelableExtra("stepview_obj");
            sw.setSteps(3);
            //Set up step view
            HorizontalStepView stepView = (HorizontalStepView) findViewById(R.id.step_view);
            sw.setStepView(stepView, this);
        }
    }

    //Disabling backbutton
    @Override
    public void onBackPressed() {
        return;
    }
}
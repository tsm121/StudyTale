package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;

import java.util.ArrayList;

public class SelectLecturesActivity extends AppCompatActivity {

    //Data for user lectures
    private ArrayList<String> monday;
    private ArrayList<String> tuesday;
    private ArrayList<String> wednesday;
    private ArrayList<String> thursday;
    private ArrayList<String> friday;

    //Spinner references
    private Spinner mon_spinner;
    private Spinner tue_spinner;
    private Spinner wed_spinner;
    private Spinner thu_spinner;
    private Spinner fri_spinner;

    //Other settings
    private boolean from_settings;
    private boolean save;
    private StepView sw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lectures);

        //Get data from GetTimetable
        getData();

        //Setting up spinners with collected data
        setUpSpinners();

        //Set up stepvView
        setUpStepView();

    }

    //Setup for stepView
    private void setUpStepView() {

        if (getIntent().hasExtra("from_settings")){
            this.from_settings = true;
        }
        else {
            //Get StepView obj from last activity
            this.sw = getIntent().getParcelableExtra("stepview_obj");
            sw.setSteps(1);
            //Set up step view
            HorizontalStepView stepView = (HorizontalStepView) findViewById(R.id.step_view);
            sw.setStepView(stepView, this);
        }
    }

    //Setup for spinners with collected data from GetTimetable
    private void setUpSpinners(){

        //Check if lists have any null values. This is mainly used for debuging
        // and would not happen when user uses the app
        if (checkLists()) {
            this.save = false;
            return;
        }
        else{
            this.save = true;
        }

        //Monday
        this.mon_spinner = (Spinner) findViewById(R.id.spinner_monday);
        ArrayAdapter mon_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.monday);
        mon_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mon_spinner.setAdapter(mon_adapter);

        //Tuesday
        this.tue_spinner = (Spinner) findViewById(R.id.spinner_tuesday);
        ArrayAdapter tue_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.tuesday);
        tue_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tue_spinner.setAdapter(tue_adapter);

        //Wednesday
        this.wed_spinner = (Spinner) findViewById(R.id.spinner_wednesday);
        ArrayAdapter wed_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.wednesday);
        wed_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wed_spinner.setAdapter(wed_adapter);

        //Thursday
        this.thu_spinner = (Spinner) findViewById(R.id.spinner_thursday);
        ArrayAdapter thu_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.thursday);
        thu_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thu_spinner.setAdapter(thu_adapter);

        //Friday
        this.fri_spinner = (Spinner) findViewById(R.id.spinner_friday);
        ArrayAdapter fri_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.friday);
        fri_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fri_spinner.setAdapter(fri_adapter);

    }

    //Check to see if lists has any null values, used for debuging
    private boolean checkLists() {

        if(monday == null) return true;
        if(tuesday == null) return true;
        if(wednesday == null) return true;
        if(thursday == null) return true;
        if(friday == null) return true;
        else return false;
    }

    //Save users lecture choices
    public void saveUserLectures(View view) {

        if(getIntent().hasExtra("from_settings")){
            saveInfoDialog();
        }

        else {
            saveData();
            //Go to ChooseCategory for creating users first story
            Intent intent = new Intent(SelectLecturesActivity.this, ChooseCategoryActivity.class);
            //Set ChooseCategory on top of stack and as a new task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //Set step done
            sw.stepDone(1);
            //Sending StepView obj to next activiy
            intent.putExtra("stepview_obj",sw);
            startActivity(intent);
            finish();
        }
    }

    //Info dialog for saving new timetable
    public void saveInfoDialog() {

        //Make alert dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        //Inflate with custom dialog
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.save_dialog, null);
        //Set custom layout
        dialogBuilder.setView(view);
        //Create Dialog
        final AlertDialog alertdialog = dialogBuilder.create();

        //Set refrences
        Button confirm_btn = (Button) view.findViewById(R.id.confirm_btn);
        Button cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        TextView messageText = (TextView) view.findViewById(R.id.dialog_message);
        ImageView icon = (ImageView) view.findViewById(R.id.dialog_icon);

        //Set title, message and icon
        title.setText("Changed timetable:");
        messageText.setText("Your changes won't take effect until the next time you make a story.");
        icon.setImageResource(R.drawable.info_icon);

        //onClick listener on OK button in dialog
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
                //Go to Story library when pressed Confirm
                Intent intent = new Intent(SelectLecturesActivity.this, StoryLibrary.class);
                //Set Story Libary on top of stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                alertdialog.dismiss();

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Go to Story library when pressed Cancel
                Intent intent = new Intent(SelectLecturesActivity.this, StoryLibrary.class);
                //Set Story Libary on top of stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                alertdialog.dismiss();

            }
        });

        //Show alert dialog
        alertdialog.show();
    }

    //Save userdata to shered prefs.
    private void saveData(){

        if(!this.save) return;

        //Save username to shared preferences
        SharedPreferences settings = getSharedPreferences("lectures",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        //If given day has no lectures, insert null to sharedprefs.
        if(this.mon_spinner.getSelectedItem() != null){
            editor.putString("monday", this.mon_spinner.getSelectedItem().toString() );
        }
        else {
            editor.putString("monday", null);
        }

        if(this.tue_spinner.getSelectedItem() != null){
            editor.putString("tuesday", this.tue_spinner.getSelectedItem().toString());
        }
        else {
            editor.putString("tuesday", null);
        }

        if(this.wed_spinner.getSelectedItem() != null){
            editor.putString("wednesday", this.wed_spinner.getSelectedItem().toString());
        }
        else{
            editor.putString("wednesday", null);
        }
        if(this.thu_spinner.getSelectedItem() != null){
            editor.putString("thursday", this.thu_spinner.getSelectedItem().toString());
        }
        else{
            editor.putString("thursday", null);
        }
        if(this.fri_spinner.getSelectedItem() != null){
            editor.putString("friday", this.fri_spinner.getSelectedItem().toString());
        }
        else{
            editor.putString("friday", null);
        }
        // Commit username and gender
        editor.apply();
    }

    //Get data that was collected in GetTimetable activity
    private void getData(){

        this.monday = new ArrayList<>();
        this.monday = getIntent().getStringArrayListExtra("monday");

        this.tuesday = new ArrayList<>();
        this.tuesday = getIntent().getStringArrayListExtra("tuesday");

        this.wednesday = new ArrayList<>();
        this.wednesday = getIntent().getStringArrayListExtra("wednesday");

        this.thursday = new ArrayList<>();
        this.thursday = getIntent().getStringArrayListExtra("thursday");

        this.friday = new ArrayList<>();
        this.friday = getIntent().getStringArrayListExtra("friday");

    }
}

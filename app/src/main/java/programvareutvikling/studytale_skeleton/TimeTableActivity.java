package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;


public class TimeTableActivity extends AppCompatActivity {

    private boolean from_settings = false;
    private StepView sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        TextView textinfo_1024 = (TextView) findViewById(R.id.textinfo_1024);
        textinfo_1024.setMovementMethod(LinkMovementMethod.getInstance());


        addedTimetable();

        //Checks if the user comes from settings or start screen
        //Start screen -> continue to new story
        //Settings -> go back to story library
        //Enable StepView or not
        chooseNextMove();
    }

    //Chooses the next move based on where the user came from
    private void chooseNextMove(){

        //Check if user comes from settings
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

        //set references
        Button done_btn = (Button) findViewById(R.id.done_btn);
        final EditText input = (EditText) findViewById(R.id.username_1024);

        //Listener: start next activity
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.setClass(TimeTableActivity.this, GetTimetable.class);
                intent.putExtra("1024username",input.getText().toString());
                startActivity(intent);
            }
        });

    }

    //Info dialog for saving new timetable
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
        title.setText("Changed timetable:");
        messageText.setText("Your changes won't take effect until the next time you make a story.");
        icon.setImageResource(R.drawable.info_icon);

        //onClick listener on OK button in dialog
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                //Go to Story library when pressed OK
                intent.setClass(TimeTableActivity.this, StoryLibrary.class);
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

    //Set timetable value to true via shared prefs.
    private boolean addedTimetable(){
        SharedPreferences settings = getSharedPreferences("userdata",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("timetable", true);
        editor.apply();
        return true;
    }
}
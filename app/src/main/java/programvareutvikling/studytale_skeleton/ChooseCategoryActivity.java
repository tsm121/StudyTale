package programvareutvikling.studytale_skeleton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baoyachi.stepview.HorizontalStepView;


public class ChooseCategoryActivity extends AppCompatActivity {

    private boolean new_user = true;
    static private StepView sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        //Enable StepView or not
        chooseNextMove();
    }

    //onClick: Get category name
    public void CategoryChosen(View view) {

        String category_name;

        //Get what category the user pressed
        switch(view.getId())
        {
            case R.id.category_1:
                category_name = "fantasy";
                break;
            case R.id.category_2:
                category_name = "children";
                break;
            case R.id.category_3:
                category_name = "action";
                break;
            case R.id.category_4:
                category_name = "sexual";
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }


        //Start new activity, send category
        Intent intent = new Intent(ChooseCategoryActivity.this, GetStory.class);
        //Needed for contacting server
        intent.putExtra("category_name", category_name);

        //Send StepView obj and set step done if it's a new user
        if(new_user){
            sw.stepDone(2);
            intent.putExtra("stepview_obj", this.sw);
        }
        else if(!new_user){
            intent.putExtra("new_user", false);
        }

        //Start getStory activity
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

    }

    //Choose next move based on where the user came from.
    private void chooseNextMove() {

        //Check if it's a new user or not
        if (getIntent().hasExtra("new_user")) {
            this.new_user = false;
            HorizontalStepView stepView = (HorizontalStepView)findViewById(R.id.step_view);
            stepView.setVisibility(View.GONE);

        } else {
            //Get StepView obj from last activity
            this.sw = getIntent().getParcelableExtra("stepview_obj");
            sw.setSteps(2);
            //Set up step view
            HorizontalStepView stepView = (HorizontalStepView) findViewById(R.id.step_view);
            sw.setStepView(stepView, this);
        }
    }
}
package programvareutvikling.studytale_skeleton;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;


abstract public class StepView implements Parcelable {

    static public List<StepBean> steps = new ArrayList<>();

    //Set initilize steps
    static public void setStepView(HorizontalStepView view, Activity that){

        HorizontalStepView stepView = view;
        //Set StepView settings
        configStepView(that,stepView,steps);

    }

    //Set as current step
    static public void setSteps(int current_step){

        if(!steps.isEmpty()){

            StepBean stepBean = steps.get(current_step);
            stepBean.setState(0);
            steps.set(current_step,stepBean);

        }
        else {
            //0 = current, 1 = done, -1 = undone
            StepBean stepBean0 = new StepBean("Start",-1);
            StepBean stepBean1 = new StepBean("Timetable",-1);
            StepBean stepBean2 = new StepBean("Category",-1);
            StepBean stepBean3 = new StepBean("Settings",-1);
            steps.add(stepBean0);
            steps.add(stepBean1);
            steps.add(stepBean2);
            steps.add(stepBean3);

            setSteps(current_step);
        }

    }

    //Set a step as done
    static public void stepDone(int step_num){

        StepBean stepBean = steps.get(step_num);
        stepBean.setState(1);
        steps.set(step_num,stepBean);

    }

    //Reseting all steps
    static public void resetSteps(){
        for (StepBean bean : steps) {
            bean.setState(-1);
        }
    }

    //Set stepView prefs
    static public void configStepView(Activity that, HorizontalStepView stepView, List<StepBean> stepList){

        stepView
                .setStepViewTexts(stepList)
                .setTextSize(12)//set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(that, R.color.bg_light))//tepsViewIndicator
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(that, R.color.uncompleted_text_color))//StepsViewIndicator
                .setStepViewComplectedTextColor(ContextCompat.getColor(that, R.color.bg_light))//StepsView text
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(that, R.color.uncompleted_text_color))//StepsView text
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(that, R.drawable.complted))//StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(that, R.drawable.default_icon))//StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(that, R.drawable.attention));//StepsViewIndicator AttentionIcon

    }
}

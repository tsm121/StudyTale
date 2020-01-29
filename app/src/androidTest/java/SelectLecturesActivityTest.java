import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import programvareutvikling.studytale_skeleton.R;
import programvareutvikling.studytale_skeleton.SelectLecturesActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class SelectLecturesActivityTest {

    @Rule
    public ActivityTestRule selectLecturesRule = new ActivityTestRule(SelectLecturesActivity.class);

    //Check if stepView is enabled
    @Test
    public void checkStepView(){

        onView(withId(R.id.step_view)).check(matches(isEnabled()));

        onView(withId(R.id.step_view)).check(matches(isDisplayed()));

    }

    //Check if spinners (drop down menu) is displayed correctly
    @Test
    public void checkSpinnersAndButton(){

        onView(withId(R.id.spinner_monday)).check(matches(isEnabled())).check(matches(isDisplayed()));

        onView(withId(R.id.spinner_tuesday)).check(matches(isEnabled())).check(matches(isDisplayed()));

        onView(withId(R.id.spinner_wednesday)).check(matches(isEnabled())).check(matches(isDisplayed()));

        onView(withId(R.id.spinner_thursday)).check(matches(isEnabled())).check(matches(isDisplayed()));

        onView(withId(R.id.spinner_friday)).check(matches(isEnabled())).check(matches(isDisplayed()));


    }

    //Check if done button is displayed and working
    @Test
    public void checkButton(){
        onView(withId(R.id.done_btn)).perform(click());
    }

}

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import programvareutvikling.studytale_skeleton.CustomizeActivity;
import programvareutvikling.studytale_skeleton.R;


@RunWith(AndroidJUnit4.class)
@LargeTest

public class CustomizeActivityTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(CustomizeActivity.class);

    //Check if stepView is enabled
    @Test
    public void checkStepView(){

        onView(withId(R.id.step_view))
                .perform(scrollTo());
        onView(withId(R.id.step_view))
                .check(matches(isEnabled()));
        onView(withId(R.id.step_view))
                .check(matches(isDisplayed()));

    }

    //Check if seekbar_brutal (slider) is displayed correctly
    @Test
    public void testSeekbar_brutal() {

        onView(withId(R.id.seekbar_brutal))
                .perform(scrollTo());
        onView(withId(R.id.seekbar_brutal))
                .check(matches(isDisplayed()));
    }

    //Check if seekbar_sex (slider) is displayed correctly
    @Test
    public void testSeekbar_sexual() {

        onView(withId(R.id.seekbar_sex))
                .perform(scrollTo());
        onView(withId(R.id.seekbar_sex))
                .check(matches(isDisplayed()));
    }

    //Check if seekbar_kind (slider) is displayed correctly
    @Test
    public void testSeekbar_kindness() {

        onView(withId(R.id.seekbar_kind))
                .perform(scrollTo());
        onView(withId(R.id.seekbar_kind))
                .check(matches(isDisplayed()));
    }

    //Check if DONE button is displayed correctly
    @Test
    public void checkButton(){

        onView(withId(R.id.nextBtn))
                .perform(scrollTo(),closeSoftKeyboard());
        onView(withId(R.id.nextBtn))
                .check(matches(isDisplayed()));
    }


}

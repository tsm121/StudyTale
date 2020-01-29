import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import programvareutvikling.studytale_skeleton.R;
import programvareutvikling.studytale_skeleton.TimeTableActivity;

@RunWith(AndroidJUnit4.class)
@MediumTest

public class TimeTableTest {

    @Rule
    public ActivityTestRule timeTableRule = new ActivityTestRule(TimeTableActivity.class);

    //Check if input field for username is working and displayed correctly
    //also checks the done button at the bottom
    @Test
    public void testInputAndButton(){

        String username_1024 = "test";

        onView(withId(R.id.username_1024))
                .perform(typeText(username_1024), closeSoftKeyboard())
                .check(matches(allOf(withText(username_1024))));

        onView(withId(R.id.done_btn)).perform(click());
        pressBack();

    }

    //Checks if stepView is enabled
    @Test
    public void checkStepView(){

        onView(withId(R.id.step_view)).check(matches(isEnabled()));

        onView(withId(R.id.step_view)).check(matches(isDisplayed()));

    }
}

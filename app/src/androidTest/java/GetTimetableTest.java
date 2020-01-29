import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import programvareutvikling.studytale_skeleton.GetTimetable;
import programvareutvikling.studytale_skeleton.R;


@RunWith(AndroidJUnit4.class)
@SmallTest

public class GetTimetableTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(GetTimetable.class);

    //Check if loading screen is displayed correctly
    @Test
    public void CheckLoadingScreen(){

        onView(withId(R.id.loader)).check(matches(isEnabled()));

        onView(withId(R.id.loader)).check(matches(isDisplayed()));

    }

}

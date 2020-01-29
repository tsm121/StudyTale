import android.os.SystemClock;
import android.support.test.filters.SmallTest;
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
import static org.hamcrest.Matchers.allOf;

import programvareutvikling.studytale_skeleton.CreateStory;
import programvareutvikling.studytale_skeleton.R;


@RunWith(AndroidJUnit4.class)
@SmallTest

public class CreateStoryTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(CreateStory.class);

    //Check if loadingscreen is displayed
    //Time delay to close snackbar (popup) which is displayed after CreateStory activity is done
    @Test
    public void CheckLoadingScreen(){

        onView(withId(R.id.loader)).check(matches(isEnabled()));

        onView(withId(R.id.loader)).check(matches(isDisplayed()));

        SystemClock.sleep(2600);

        try {
        onView(allOf(withId(android.support.design.R.id.snackbar_action)))
                .perform(click());

        }catch (Exception e){e.printStackTrace();}

    }

}

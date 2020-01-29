import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import programvareutvikling.studytale_skeleton.R;
import programvareutvikling.studytale_skeleton.StoryLibrary;


@RunWith(AndroidJUnit4.class)
@SmallTest

public class StoryLibraryTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(StoryLibrary.class);

    //Checks if all widgets on screen are displayed correcly
    @Test
    public void CheckIfWidgetsIsDisplayed(){

        onView(withId(R.id.constrainLayout)).check(matches(isDisplayed()));

        onView(withId(R.id.menubar)).check(matches(isDisplayed()));

        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        onView(withId(R.id.no_stories_layout)).check(matches(isDisplayed()));

        onView(withId(R.id.fab)).check(matches(isDisplayed()));

    }

    //Check if menubar is displayed correctly and if settings button is displayed and working
    @Test
    public void CheckMenubarWithSettings(){

        onView(withId(R.id.menubar)).check(matches(isDisplayed()));

        onView(withId(R.id.settings))
                .check(matches(isDisplayed()))
                .perform(click());

        pressBack();

    }

    //Testing FAB button (floating action button) is displayed and working
    @Test
    public void TestNewStoryButton(){

        onView(withId(R.id.fab))
                .check(matches(isDisplayed()))
                .perform(click());

        pressBack();
    }

}

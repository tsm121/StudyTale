import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import programvareutvikling.studytale_skeleton.R;
import programvareutvikling.studytale_skeleton.StoryViewActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class StoryViewActivityTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(StoryViewActivity.class);

    //Check if menubar is displayed correctly
    // and if delete button in the extra menu is working and is displayed
    @Test
    public void CheckDeleteInMenubar(){


        onView(withId(R.id.menubar)).check(matches(isDisplayed())).perform(closeSoftKeyboard());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Delete story")).perform(click());

        onView(withId(android.R.id.button1)).perform(click());

    }

    //Check if menubar is displayed correctly
    //and if edit title button in the extra menu is working and is displayed
    //Checks if dialog (popup) is displayed and has working buttons
    //Also checks if snackbar (popup) is displayed and working
    @Test
    public void CheckEditTitleInMenubar_CheckSnackbarDisplayed(){

        String story_title = "New story title";

        onView(withId(R.id.menubar)).check(matches(isDisplayed())).perform(closeSoftKeyboard());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Edit title")).perform(click());

        onView(withId(R.id.edit))
                .perform(clearText())
                .perform(typeText(story_title), closeSoftKeyboard());

        onView(withId(android.R.id.button1)).perform(click());

        onView(allOf(withId(android.support.design.R.id.snackbar_text))).check(matches(isDisplayed()));

        onView(allOf(withId(android.support.design.R.id.snackbar_action)))
                .perform(click());
    }

    //Tests if home button is working (back button in menubar)
    @Test
    public void TestHomeButton(){

        onView(withContentDescription("Navigate up")).perform(click());


    }

}

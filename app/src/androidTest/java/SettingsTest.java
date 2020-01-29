import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import programvareutvikling.studytale_skeleton.R;
import programvareutvikling.studytale_skeleton.Settings;


@RunWith(AndroidJUnit4.class)
@SmallTest

public class SettingsTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(Settings.class);

    //Test input field for username and check if it maches what is written on screen
    //Also checks if save button is displayed and working
    @Test
    public void TestEditUserName(){

        String name = "James";

        onView(withId(R.id.edit_user_name_input))
                .check(matches(isDisplayed()))
                .perform(clearText())
                .perform(typeText(name), closeSoftKeyboard());

        onView(withId(R.id.save_btn))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Name change:")).check(matches(isDisplayed()));

        onView(withId(R.id.dialog_btn)).perform(click());

    }

    //Test input field with a invalid username. Check if app handles this correctly
    //and removes invalid characters
    //Also checks if save button is displayed and working
    @Test
    public void TestInvalidUserName(){

        String name = "James";
        String invalid_name = "James123$%";

        onView(withId(R.id.edit_user_name_input))
                .check(matches(isDisplayed()))
                .perform(clearText())
                .perform(typeText(invalid_name), closeSoftKeyboard());

        onView(withId(R.id.edit_user_name_input)).check(matches(withText(name)));

        onView(withId(R.id.save_btn))
                .check(matches(isDisplayed()))
                .perform(click());


    }

    //Tests if radiobuttons are working correctly and are displayed
    @Test
    public void selectGenderAndCheck(){

        onView(withId(R.id.girl_radiobtn)).perform(click(), closeSoftKeyboard());

        onView(withId(R.id.girl_radiobtn)).check(matches(isChecked()));

        onView(withId(R.id.boy_radiobtn)).check(matches(isNotChecked()));

        onView(withId(R.id.boy_radiobtn)).perform(click(), closeSoftKeyboard());

        onView(withId(R.id.boy_radiobtn)).check(matches(isChecked()));

        onView(withId(R.id.girl_radiobtn)).check(matches(isNotChecked()));

    }

    //Checks if edit timetable button is displayed and working correcly
    @Test
    public void CheckEditTimetable(){

        onView(withId(R.id.edit_timetable_btn))
                .check(matches(isDisplayed()))
                .perform(click());

    }

    //Check if reset user button is displayed and working correctly
    //Also checks if dialog (popup) is working correctly and if button is displayed and working
    @Test
    public void TestResetUser(){

        onView(withId(R.id.reset_btn))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Reset application:")).check(matches(isDisplayed()));

        onView(withId(R.id.dialog_btn)).perform(click());
    }

    //Checks if homebutton is working (back button in menubar)
    @Test
    public void TestHomeButton(){

        onView(withContentDescription("Navigate up")).perform(click());

    }

}

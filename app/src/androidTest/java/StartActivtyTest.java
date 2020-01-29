import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;

import static org.hamcrest.CoreMatchers.allOf;

import programvareutvikling.studytale_skeleton.R;
import programvareutvikling.studytale_skeleton.StartActivity;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartActivtyTest {

    @Rule
    public ActivityTestRule startActivityRule = new ActivityTestRule(StartActivity.class);

    //Check if stepView is enabled
    @Test
    public void checkStepView(){

        onView(withId(R.id.step_view)).check(matches(isEnabled()));

        onView(withId(R.id.step_view)).check(matches(isDisplayed()));

    }

    //Input name, check if correct, then input an invalid name
    // and check if the invalid chars don't get registred
    @Test
    public void inputAndCheckName(){

        String name = "James";
        String invalid_name ="James123$%";

        onView(withId(R.id.user_name_input)).perform(typeText(name), closeSoftKeyboard());

        onView(withId(R.id.user_name_input)).check(matches(allOf(withText(name))));

        onView(withId(R.id.user_name_input))
                .perform(replaceText(""))
                .perform(typeText(invalid_name), closeSoftKeyboard());

        onView(withId(R.id.user_name_input)).check(matches(allOf(withText(name))));

    }

    //Select gender, check that there is only one checked, and change back to default choice
    @Test
    public void selectGenderAndCheck(){

        onView(withId(R.id.user_name_input)).perform(closeSoftKeyboard());

        onView(withId(R.id.girl_radiobtn)).perform(click(), closeSoftKeyboard());

        onView(withId(R.id.girl_radiobtn)).check(matches(isChecked()));

        onView(withId(R.id.boy_radiobtn)).check(matches(isNotChecked()));

        onView(withId(R.id.boy_radiobtn)).perform(click(), closeSoftKeyboard());

        onView(withId(R.id.boy_radiobtn)).check(matches(isChecked()));

        onView(withId(R.id.girl_radiobtn)).check(matches(isNotChecked()));

    }

}

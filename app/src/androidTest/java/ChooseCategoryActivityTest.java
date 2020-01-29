import android.support.test.filters.LargeTest;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import programvareutvikling.studytale_skeleton.ChooseCategoryActivity;
import programvareutvikling.studytale_skeleton.R;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class ChooseCategoryActivityTest {

    @Rule
    public ActivityTestRule getTimetableRule = new ActivityTestRule(ChooseCategoryActivity.class);

    //Check if stepView is enabled
    @Test
    public void checkStepView(){

        onView(withId(R.id.step_view)).check(matches(isEnabled()));

        onView(withId(R.id.step_view)).check(matches(isDisplayed()));

    }

    //Check if category 1 button is displayed correctly
    @Test
    public void checkCat1(){

        onView(withId(R.id.category_1))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_1))
                .check(matches(withText("Fantasy")));

    }

    //Check if category 2 button is displayed correctly
    @Test
    public void checkCat2(){

        onView(withId(R.id.category_2))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_2))
                .check(matches(withText("Children")));

    }

    //Check if category 3 button is displayed correctly
    @Test
    public void checkCat3(){

        onView(withId(R.id.category_3))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_3))
                .check(matches(withText("Action")));

    }

    //Check if category 420+ button is displayed correctly
    @Test
    public void checkCat4(){

        onView(withId(R.id.category_4))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_4))
                .check(matches(withText("Sexual")));

    }

}

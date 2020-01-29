import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//Runs all UnitTests
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CustomizeActivityTest.class,
        GetTimetableTest.class,
        StoryViewActivityTest.class,
        StartActivtyTest.class,
        TimeTableTest.class,
        SelectLecturesActivityTest.class,
        ChooseCategoryActivityTest.class,
        GetStoryTest.class,
        StoryLibraryTest.class,
        SettingsTest.class,
        CreateStoryTest.class

})
public class AllTests {
}

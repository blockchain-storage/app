package nl.tudelft.cs4160.trustchain_android.GuiEspressoTest;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

import nl.tudelft.cs4160.trustchain_android.main.UserConfigurationActivity;

/**
 * Created by Laurens on 12/18/2017.
 */

public class UserConfigurationActivityTest {

    @Rule
    public ActivityTestRule<UserConfigurationActivity> mActivityRule = new ActivityTestRule<>(
            UserConfigurationActivity.class);

}

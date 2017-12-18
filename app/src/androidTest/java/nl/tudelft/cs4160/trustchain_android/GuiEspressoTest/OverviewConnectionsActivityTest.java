package nl.tudelft.cs4160.trustchain_android.GuiEspressoTest;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.main.OverviewConnectionsActivity;


/**
 * Created by Laurens on 12/18/2017.
 */

public class OverviewConnectionsActivityTest {

    @Rule
    public ActivityTestRule<OverviewConnectionsActivity> mActivityRule = new ActivityTestRule<>(
            OverviewConnectionsActivity.class);

//    @Before
//    public void initialize() {
//        // Eventually login if you need to login.
//    }

    @Test
    public void gotoBootstrapActivity(){
//        onView(withId(R.id.change_bootstrap))            // withId(R.id.my_view) is a ViewMatcher
//                .perform(click())               // click() is a ViewAction
//                .check(matches(isDisplayed())); // matches(isDisplayed()) is a ViewAssertion
        //TODO wait for the PR
    }

    @Test
    public void gotoTrustchainActivityTest() {
        // in this unit test it is not possible to go the trustchain activity
        // integration test needs to be made here.
        onView(withId(R.id.incoming_peer_connection_list_view))
                .perform(click());
        //TODO
    }

    @Test
    public void gotoChainExplorerActivity() {
        // Open the ActionBar
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // Click on the menu item
        onView(withText("My Chain"))
                .perform(click());
        // Show the chain on the screen.
        onView(withId(R.id.blocks_list)).check(matches(isDisplayed()));
    }
}

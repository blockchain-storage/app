package nl.tudelft.cs4160.trustchain_android;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

import nl.tudelft.cs4160.trustchain_android.main.OverviewConnectionsActivity;

/**
 * Created by Boning on 12/16/2017.
 */

public class TrustChainBlockTest {
    @Rule
    public ActivityTestRule<OverviewConnectionsActivity> mActivityRule =
            new ActivityTestRule<>(OverviewConnectionsActivity.class);


}

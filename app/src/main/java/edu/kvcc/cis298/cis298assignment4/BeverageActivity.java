//ANDY CULLEN
//ASSIGNMENT 4
//DUE 12/16/15

package edu.kvcc.cis298.cis298assignment4;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

//Class extnds from the singleFragmentActivity we created
public class BeverageActivity extends SingleFragmentActivity {

    //Key for use in sending data from the list activity to this activity
    private static final String EXTRA_BEVERAGE_ID = "edu.kvcc.cis298.cis298assignment4.beverage_id";

    //Static method to get a properly formatted intent to get this activity started
    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, BeverageActivity.class);
        intent.putExtra(EXTRA_BEVERAGE_ID, id);
        return intent;
    }

    //Overridden method originally defined in the singleFragmentActivity to create the fragment that will get hosted
    @Override
    protected Fragment createFragment() {
        String beverageId = getIntent().getStringExtra(EXTRA_BEVERAGE_ID);
        return BeverageFragment.newInstance(beverageId);
    }
}

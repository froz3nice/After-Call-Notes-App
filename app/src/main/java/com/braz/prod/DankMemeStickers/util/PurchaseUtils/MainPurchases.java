package com.braz.prod.DankMemeStickers.util.PurchaseUtils;

import android.widget.Toast;

import com.braz.prod.DankMemeStickers.Activities.MainActivity;
import com.braz.prod.DankMemeStickers.R;

public class MainPurchases {

    private MainActivity activity;
    private static final String SKU_REMOVE_ADS = "upgrade.to.pro.meme.stickers";//upgrade.to.pro.meme.stickers
    private static final int RC_REQUEST = 0x0123;
    IabHelper mHelper;

    public MainPurchases(MainActivity activity) {
        this.activity = activity;

        mHelper = new IabHelper(activity, activity.getString(R.string.billingKey));
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                if (mHelper == null)
                    return;
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            //Toast.makeText(context,"premium",Toast.LENGTH_SHORT).show();

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                return;
            }

            Purchase removeAdsPurchase = inventory.getPurchase(SKU_REMOVE_ADS);

            if (removeAdsPurchase != null) {
                activity.removeAds();
            }
        }
    };



    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                return;
            }

            if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
                // bought the premium upgrade!
                Toast.makeText(activity, "welcome to premium, m8 :)", Toast.LENGTH_SHORT).show();
                activity.removeAds();
            }
        }
    };

    public IabHelper getmHelper() {
        return mHelper;
    }

    public void setmHelper(IabHelper mHelper) {
        this.mHelper = mHelper;
    }

    // Called by button press
    public void buyProUpgrade() {
        try {
            mHelper.launchPurchaseFlow(activity, SKU_REMOVE_ADS, RC_REQUEST,
                    mPurchaseFinishedListener, "payLoad");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


}

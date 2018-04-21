package com.ute.dn.speaknow.common;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.common.collect.Lists;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Authentication {

    public static GoogleAccountCredential mCredential = null;
    private static Activity mActivity;
    private static Context mContext;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");


    public Authentication(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(mContext, scopes)
                .setBackOff(new ExponentialBackOff());
        getResultsFromApi();
    }

    public static void init(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(mContext, scopes)
                .setBackOff(new ExponentialBackOff());
        if (mCredential.getSelectedAccountName() == null) Log("getSelectedAccountName(): null");
        else Log("getSelectedAccountName(): " + mCredential.getSelectedAccountName());
        getResultsFromApi();
    }

    private static void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(mContext, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private static void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private static void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private static void chooseAccount() {
        Log("chooseAccount()");
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = mActivity.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            Log("chooseAccount() hasPermissions: accountName = " + (accountName == null ? "null" : accountName));
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Log("chooseAccount() setSelectedAccountName");
            } else {
                Log("chooseAccount() startActivityForResult");
                // Start a dialog from which the user can choose an account
                mActivity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            Log("chooseAccount() no hasPermissions");
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(mActivity, "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    private static boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private static void Log(String message) {
        Log.d("DemoAuthentication", "/" + message + "/");
    }
}

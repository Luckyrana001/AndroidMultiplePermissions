package org.androidluckyguy.permissions.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.androidluckyguy.permissions.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.androidluckyguy.permissions.MainActivity.SETTINGS_PERMISSIONS_REQUEST;



public class PermissionUtils
{

    Context context;
    Activity activity;

    PermissionResultCallback permissionResultCallback;


    ArrayList<String> permission_list=new ArrayList<>();
    ArrayList<String> listPermissionsNeeded=new ArrayList<>();



    String dialog_content="";
    int req_code;

    public PermissionUtils(Context context)
    {
        this.context=context;
        this.activity= (Activity) context;

        permissionResultCallback= (PermissionResultCallback) context;
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */

    public boolean check_permission(ArrayList<String> permissions, String dialog_content, int request_code)
    {
        this.permission_list=permissions;
        this.dialog_content=dialog_content;
        this.req_code=request_code;
        boolean permissionStatus = false;

        if(Build.VERSION.SDK_INT >= 23)
        {
            if (checkAndRequestPermissions(permissions, request_code))
            {
                permissionResultCallback.PermissionGranted(request_code);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
                permissionStatus = true;
            }
        }
        else
        {
            permissionResultCallback.PermissionGranted(request_code);

            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
            permissionStatus = true;
        }
      return  permissionStatus;
    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */

    private  boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {

        if(permissions.size()>0)
        {
            listPermissionsNeeded = new ArrayList<>();

            for(int i=0;i<permissions.size();i++)
            {
                int hasPermission = ContextCompat.checkSelfPermission(activity,permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),request_code);
                return false;
            }
        }

        return true;
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0)
                {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++)
                    {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions=new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++)
                    {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED)
                        {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,listPermissionsNeeded.get(i)))
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            else
                            {
                                Log.i("Go to settings","and enable permissions");
                                permissionResultCallback.NeverAskAgain(req_code);
                                //Toast.makeText(activity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();

                                askUserToAllowPermissionFromSetting();
                                return;
                            }
                        }

                    }

                    if(pending_permissions.size()>0)
                    {
                        showMessageOKCancel(dialog_content,pending_permissions);


                    }
                    else
                    {
                        Log.i("all","permissions granted");
                        Log.i("proceed","to next step");
                        permissionResultCallback.PermissionGranted(req_code);

                    }



                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param pending_permissions
     */
    private void showMessageOKCancel(String message,final ArrayList<String> pending_permissions) {
       /* new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.permission_required))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();*/
        new MaterialDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.permission_required))
                .content(message)
                 .positiveColorRes(R.color.colorPrimary)
                .positiveText(activity.getResources().getString(R.string.OK))
                .negativeText(activity.getResources().getString(R.string.CANCEL))
                .neutralColorRes(R.color.Salmon_fourth_addon_bottom)
                .negativeColorRes(R.color.Salmon_fourth_addon_bottom)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        check_permission(permission_list,dialog_content,req_code);
                    }
                })

                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Log.i("permisson","not fully given");
                        if(permission_list.size()==pending_permissions.size()) {
                            permissionResultCallback.PermissionDenied(req_code);
                        }else{
                            permissionResultCallback.PartialPermissionGranted(req_code,pending_permissions);}
                        dialog.cancel();
                    }
                }).show();




    }


    private void askUserToAllowPermissionFromSetting() {
        new MaterialDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.permission_required))
                .content(activity.getResources().getString(R.string.permission_from_setting_access))
                .positiveColorRes(R.color.dull_lime_second_add_on_top)
                .positiveText(activity.getResources().getString(R.string.OK))
                .negativeText(activity.getResources().getString(R.string.CANCEL))
                .neutralColorRes(R.color.Salmon_fourth_addon_bottom)
                .negativeColorRes(R.color.Salmon_fourth_addon_bottom)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivityForResult(intent, SETTINGS_PERMISSIONS_REQUEST);
                    }
                })

                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                }).show();


    }

}
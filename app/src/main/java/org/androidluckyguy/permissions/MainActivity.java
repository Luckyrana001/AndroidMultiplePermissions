package org.androidluckyguy.permissions;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.androidluckyguy.permissions.permission.PermissionResultCallback;
import org.androidluckyguy.permissions.permission.PermissionUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, PermissionResultCallback {

    ArrayList<String> permissionsArraylist =new ArrayList<>();

    PermissionUtils permissionUtils;

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String WRITE_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int SETTINGS_PERMISSIONS_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         // initialize permission utils class
        permissionUtils = new PermissionUtils(this);

        // add required permissions to the permission request array
        permissionsArraylist.add(CAMERA_PERMISSION);
        permissionsArraylist.add(WRITE_STORAGE_PERMISSION);
        permissionsArraylist.add(COARSE_LOCATION);



        Button checkPermissionBtn =  (Button)findViewById(R.id.checkPermissionsBtn);
        checkPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              boolean  status =   permissionUtils.check_permission(permissionsArraylist,getResources().getString(R.string.permission_must_required_msg),SETTINGS_PERMISSIONS_REQUEST);
                if(status){
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("Permission Alert")
                            .content("Already have access to all required permissions.")
                            .positiveColorRes(R.color.dull_lime_second_add_on_top)
                            .positiveText(getResources().getString(R.string.OK))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.cancel();
                                }
                            })

                         .show();

                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    // Callback functions


    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

}

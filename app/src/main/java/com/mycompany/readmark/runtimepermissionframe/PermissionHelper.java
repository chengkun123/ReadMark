package com.mycompany.readmark.runtimepermissionframe;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

/**
 * Created by Lenovo.
 */

public class PermissionHelper {
    //传入Fragment或者Activity
    private Object mObject;
    //请求码
    private int mRequestCode;
    //需要请求的权限
    private String[] mRequestPermission;

    private PermissionHelper(Object o){
        mObject = o;
    }

    public static void requestPermission(Activity activity, int requestCode, String[] permissions){
        PermissionHelper.with(activity).requestCode(requestCode).requestPermission(permissions);
    }

    public static PermissionHelper with(Activity activity){
        return new PermissionHelper(activity);
    }

    public static PermissionHelper with(Fragment fragment){
        return new PermissionHelper(fragment);
    }

    public static PermissionHelper with(View view){
        return new PermissionHelper(view);
    }

    public PermissionHelper requestCode(int requestCode){
        mRequestCode = requestCode;
        return this;
    }

    public PermissionHelper requestPermission(String... permissions){
        mRequestPermission = permissions;
        return this;
    }

    public void request(){

        //如果不是6.0以上
        if(!PermissionUtils.isOverMarshmallow()){
            //直接执行
            PermissionUtils.executeSuccessMethod(mObject, mRequestCode);
            return;
        }


        //如果是6.0以上

        //找到所有初始化的时候想要申请的动态权限中，被denied的权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermission(mObject, mRequestPermission);

        //如果都授权了，直接执行方法
        if(deniedPermissions.size() == 0){
            PermissionUtils.executeSuccessMethod(mObject, mRequestCode);
        }else{//去申请权限
            ActivityCompat.requestPermissions(PermissionUtils.getActivity(mObject)
                    , deniedPermissions.toArray(new String[deniedPermissions.size()])
                    , mRequestCode);

        }

    }

    /**
     * 在onRequestPermissionsResult方法中调用
     * @param object
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionResult(Object object, int requestCode
            , String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = PermissionUtils.getDeniedPermission(object, permissions);

        if(deniedPermissions.size() == 0){
            PermissionUtils.executeSuccessMethod(object, requestCode);
        }else{//有用户不同意的
            PermissionUtils.executeFailMethod(object, requestCode);
        }
    }

}

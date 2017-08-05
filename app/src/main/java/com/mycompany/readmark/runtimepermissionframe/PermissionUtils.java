package com.mycompany.readmark.runtimepermissionframe;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 类里面都是静态方法
 */

public class PermissionUtils {

    private PermissionUtils(){
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断是否是6.0以上
     * @return
     */
    public static boolean isOverMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * 无需申请权限，直接找到请求码对应方法，执行。
     * @param mReflect
     * @param requestCode
     */
    public static void executeSuccessMethod(Object mReflect, int requestCode) {
        // 获取所有方法
        Method[] methods = mReflect.getClass().getDeclaredMethods();
        // 找到标记成功的方法
        for(Method method : methods){
            PermissionSuccess sMethod = method.getAnnotation(PermissionSuccess.class);
            if(sMethod != null){
                int methodCode = sMethod.requestCode();
                //方法的注解里的请求码 和 传入参数的请求码相同，执行这个方法。
                if(methodCode == requestCode){
                    executeMethod(mReflect, method);
                }
            }
        }
    }

    /**
     * 执行一个方法
     * @param mReflect
     * @param method
     */
    private static void executeMethod(Object mReflect, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(mReflect, new Object[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取没有授予的权限
     * @param object
     * @param requestPermission
     * @return
     */
    public static List<String> getDeniedPermission(Object object, String[] requestPermission) {
        List<String> deniedPermissions = new ArrayList<>();
        for(String permission : requestPermission){
            //把没有授予的权限加入到集合
            if(ContextCompat.checkSelfPermission(getActivity(object), permission)
                    == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取Context
     * @param object
     * @return
     */
    public static Activity getActivity(Object object) {
        if(object instanceof Activity){
            return (Activity) object;
        }
        if(object instanceof Fragment){
            return ((Fragment)object).getActivity();
        }
        if(object instanceof View){
            return (Activity) ((View) object).getContext();
        }
        return null;
    }


    /**
     * 如果用户没有授权，执行对应方法
     * @param object
     * @param requestCode
     */
    public static void executeFailMethod(Object object, int requestCode) {
        // 获取所有方法
        Method[] methods = object.getClass().getDeclaredMethods();
        // 找到标记失败的方法
        for(Method method : methods){
            PermissionFail sMethod = method.getAnnotation(PermissionFail.class);
            if(sMethod != null){
                int methodCode = sMethod.requestCode();
                //方法的注解里的请求码 和 传入参数的请求码相同，执行这个方法。
                if(methodCode == requestCode){
                    executeMethod(object, method);
                }
            }
        }
    }
}

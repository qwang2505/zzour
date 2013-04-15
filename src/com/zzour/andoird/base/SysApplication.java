package com.zzour.andoird.base;

import java.util.LinkedList;     
import java.util.List;     
import android.app.Activity;   
import android.app.Application;
     
public class SysApplication extends Application {     
    private List<Activity> mList = new LinkedList<Activity>();   //用于存放每个Activity的List  
    private static SysApplication instance;    //SysApplication实例     
     
    private SysApplication() {     //私有构造器，防止外面实例化该对象，  
    }     
     
    public synchronized static SysApplication getInstance() {   //通过一个方法给外面提供实例  
        if (null == instance) {     
            instance = new SysApplication();     
        }     
        return instance;     
    }     
     
    // add Activity      
    public void addActivity(Activity activity) {     
        mList.add(activity);     
    }     
     
    public void exit() {    //遍历List，退出每一个Activity     
        try {     
            for (Activity activity : mList) {     
                if (activity != null)     
                    activity.finish();     
            }     
        } catch (Exception e) {     
            e.printStackTrace();     
        } finally {     
            System.exit(0);     
        }     
    }     
     
    @Override     
    public void onLowMemory() {     
        super.onLowMemory();         
        System.gc();   //告诉系统回收  
    }    
}

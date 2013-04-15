package com.zzour.andoird.base;

import java.util.LinkedList;     
import java.util.List;     
import android.app.Activity;   
import android.app.Application;
     
public class SysApplication extends Application {     
    private List<Activity> mList = new LinkedList<Activity>();   //���ڴ��ÿ��Activity��List  
    private static SysApplication instance;    //SysApplicationʵ��     
     
    private SysApplication() {     //˽�й���������ֹ����ʵ�����ö���  
    }     
     
    public synchronized static SysApplication getInstance() {   //ͨ��һ�������������ṩʵ��  
        if (null == instance) {     
            instance = new SysApplication();     
        }     
        return instance;     
    }     
     
    // add Activity      
    public void addActivity(Activity activity) {     
        mList.add(activity);     
    }     
     
    public void exit() {    //����List���˳�ÿһ��Activity     
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
        System.gc();   //����ϵͳ����  
    }    
}

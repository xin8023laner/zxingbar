package com.cc.zxingbar;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 耿叫龙 .
 * 8/8 添加内存泄漏检测（罗星源）
 */
public class BaseApplication extends Application
{

    public static BaseApplication baseApplication;
    private static List<Activity> stack = new ArrayList<>();
    private static long loginUserId;

    public static int SCAN_TYPE = 0;//程序运行的手机

    private int mFinalCount;//当前堆栈中活跃activity数

    public static List<Integer> operationList;//用于存储操作整个流程
    private static List<Integer> expressList;//规格列表




    public static void setOperationList(List<Integer> operationList) {
        BaseApplication.operationList = operationList;
    }



    public static List<Integer> getExpressList() {
        return expressList;
    }

    public static void setExpressList(List<Integer> expressList) {
        BaseApplication.expressList = expressList;
    }

    public static void setLoginUserId(long userId)
    {
        loginUserId = userId;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        baseApplication = this;



    }






    }



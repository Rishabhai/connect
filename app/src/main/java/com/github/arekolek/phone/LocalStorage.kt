package com.github.arekolek.phone

import android.content.Context
import android.content.SharedPreferences;

open class LocalStorage(context: Context) {

    private val ISUSERLOGIN:String = "isLoggedIn";
    private val ISUSERREADY:String = "isReady";


    var isloggedIn:SharedPreferences = context.getSharedPreferences(ISUSERLOGIN, 0);
    var isUserReady:SharedPreferences = context.getSharedPreferences(ISUSERLOGIN, 0);


    fun setIsLoginStatus(value:Boolean){
        var editor:SharedPreferences.Editor = isloggedIn.edit();
        editor.putBoolean(ISUSERLOGIN, value);
        editor.apply()
    }

     fun getIsLoginStatus()
    : Boolean {
         return isloggedIn.getBoolean(ISUSERLOGIN, false);
    }

    fun setIsReadyStatus(value:Boolean){
        var editor:SharedPreferences.Editor = isUserReady.edit();
        editor.putBoolean(ISUSERREADY, value);
        editor.apply()
    }

    fun getIsReadyStatus()
            : Boolean {
        return isUserReady.getBoolean(ISUSERREADY, false);
    }

}



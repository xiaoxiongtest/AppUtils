package com.apputils.example.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class SharedPreferencesUitl {
	private static SharedPreferences sharedPreferences;

	//��String(������,key,value)
	public static void saveStringData(Context context,String key,String value){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		sharedPreferences.edit().putString(key, value).commit();
	}
	//ȡ
	public static String getStringData(Context context,String key,String defValue){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defValue);
	}
}

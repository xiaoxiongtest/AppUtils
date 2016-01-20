package com.apputils.example.base;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BaseViewPagerAdapter extends PagerAdapter {
	private List<BasePager> mListViews; 
	
	 public BaseViewPagerAdapter(List<BasePager> mListViews) {  
         this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。  
     }  
	
	 @Override  
     public void destroyItem(ViewGroup container, int position, Object object)   {     
         container.removeView(mListViews.get(position).getRootView());//删除页卡  
     }  


     @Override  
     public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡         
          container.addView(mListViews.get(position).getRootView());//添加页卡  
          mListViews.get(position).initData();
          return mListViews.get(position).getRootView();  
     }  

     @Override  
     public int getCount() {           
         return  mListViews.size();//返回页卡的数量  
     }  
       
     @Override  
     public boolean isViewFromObject(View arg0, Object arg1) {             
         return arg0==arg1;//官方提示这样写  
     }

}

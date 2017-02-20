package com.example.try_no;

import android.app.Activity;
import android.os.Bundle;

public class AngryBirdActivity extends Activity {
	
	/**����С�����Ƥ����󳤶�*/
	public static final float RubberBandLength=50;
	
	/**����С��ĳ�ʼλ��*/
	public static float startX;
	public static float startY;
	
	/**���С�����Ч���÷�Χ��������ʱ��Ϊ�����ָ����С���С��
	 * �������õ���С���ܱ�һ���ķ�Χ�������϶�С��*/
	public static float touchDistance;
	
	MySurfaceView surfaceView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        surfaceView=new MySurfaceView(this);
        setContentView(surfaceView);
    }
}
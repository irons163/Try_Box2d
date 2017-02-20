package com.example.try_no;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Bird {
	private float x,y,r,angle;
	private Bitmap bmp;
	
	/**�Ƿ���*/
	private boolean isPressed;
	/**�Ƿ�����*/
	private boolean isReleased;
	/**�Ƿ��Ѳ�������������������������϶��������С����*/
	private boolean applyForce;
	
	Type type;
	
	public Bird(float x,float y,float r,Bitmap bmp,Type type)
	{
		this.x=x;
		this.y=y;
		this.r=r;
		this.bmp=bmp;
		
		isReleased=false;
		
		applyForce=false;
		
		this.type=type;
		
	}
	
	public void setAngle(float angle)
	{
		this.angle=angle;
	}
	
	public float getAngle()
	{
		return this.angle;
	}
	
	
	public boolean getApplyForce()
	{
		return this.applyForce;
	}
	
	public void setApplyForce(boolean isApplyForce)
	{
		this.applyForce=isApplyForce;
	}
	
	public void setX(float x)
	{
		this.x=x;
	}
	
	public void setY(float y)
	{
		this.y=y;
	}
	
	public float getX()
	{
		return this.x;
	}
	
	public float getY()
	{
		return this.y;
	}
	
	public float getR()
	{
		return this.r;
	}
	
	
	public Type getType()
	{
		return this.type;
	}
	
	public void draw(Canvas canvas,Paint paint)
	{
		/**���滭�����оֲ���ת��
		 * �����Ӱ���������� */
		canvas.save();
		canvas.rotate(angle,this.x,this.y);
		canvas.drawBitmap(this.bmp, this.x-this.r,this.y-this.r, paint);
		canvas.drawCircle(this.x,this.y, this.r, paint);
		
		/**������Ч�ĵ����Χ */
		canvas.drawCircle(this.x,this.y, AngryBirdActivity.touchDistance, paint);
		
		canvas.restore();

	}
	
	public boolean getIsReleased()
	{
		return this.isReleased;
	}
	
	public void setIsReleased(boolean isReleased)
	{
		this.isReleased=isReleased;
	}
	
	public boolean getIsPressed()
	{
		return this.isPressed;
	}
	
	public void setIsPressed(boolean isPressed)
	{
		this.isPressed=isPressed;
	}
	
	/**�ж��Ƿ����С��*/
	public boolean isPressed(MotionEvent event)
	{
		boolean res=false;
		if(Math.pow((event.getX()-this.x),2)+Math.pow((event.getY()-this.y),2)<Math.pow(AngryBirdActivity.touchDistance, 2))
		{
			res=true;
		}
		return res;
	}
	
	/**�϶�С��*/
	public void move(MotionEvent event)
	{
		
		if(Math.pow((event.getX()-AngryBirdActivity.startX), 2)+Math.pow((event.getY()-AngryBirdActivity.startY), 2)<=Math.pow(AngryBirdActivity.RubberBandLength, 2))
		{
			this.x=event.getX();
			this.y=event.getY();
		}
		else //���볬����Ƥ����󳤶�ʱ
		{
			float angle=(float) Math.atan2(event.getY()-AngryBirdActivity.startY,event.getX()-AngryBirdActivity.startX);
			
			this.x=(float) (AngryBirdActivity.startX+AngryBirdActivity.RubberBandLength*Math.cos(angle));
			this.y=(float) (AngryBirdActivity.startY+AngryBirdActivity.RubberBandLength*Math.sin(angle));
		}
	
	}

}

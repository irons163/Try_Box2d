package com.example.try_no2;

import org.loon.framework.android.game.physics.LBody;
import org.loon.framework.android.game.physics.LWorld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MySurfaceView extends SurfaceView implements Callback,Runnable,ContactListener{

	private SurfaceHolder sfh;
	
	/**總的運行執行緒*/
	private Thread th;
	/**執行緒運行標誌位元*/
	private boolean flag;
	
	private Canvas canvas;
	private Paint paint;
	
	private int screenW,screenH;
	
	/**Bird類，用以繪畫出小鳥*/
	Bird bird;
	
	/**touchEvent的優化，避免真機調試時頻繁回應*/
	byte[] lock = new byte[0];
	private final int timePause=50;
	
	/**物理世界聲明*/
//	World world;
	LWorld world;
//	AABB aabb;  //新版的JBox2D已經不需要AABB區域了
	Vector2 gravity;
	private final float RATE=40.0f; //物理世界與螢幕環境縮放比列
	float timeStep=1f/60f;	
	
	/**新的JBox2D增加到兩個控制反覆運算，參數均按照官方manual上的參數設置的 */
	int velocityIterations = 10;	
	int positionIterations = 8;

	public MySurfaceView(Context context) {
		super(context);
		
		sfh=this.getHolder();
		sfh.addCallback(this);
		
		paint=new Paint();
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);

//		aabb=new AABB(); 	//舊版JBox2D的創建方法
//		aabb.lowerBound.set(-100, -100);
//		aabb.upperBound.set(100,100);
		
		/**重力初始化*/
		gravity=new Vector2(0,-10f);
		
		/**創建物理世界*/
//		world=new World(gravity, true);
		world=new LWorld(0, 20, 1800, 1800, true, 1.0f);
		
		/**增加物理世界中的碰撞監聽*/
		world.setContactListener(this);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		/**得到螢幕大小*/
		this.screenW=this.getWidth();
		this.screenH=this.getHeight();

		/**初始化小鳥位置*/
		AngryBirdActivity.startX=100;
		AngryBirdActivity.startY=screenH-500;
		/**初始化橡皮筋長度*/
		AngryBirdActivity.touchDistance=0.2f*screenH;
		
		
		Bitmap bmpBird=BitmapFactory.decodeResource(this.getResources(), R.drawable.smallbird);
		
		bird=new Bird(AngryBirdActivity.startX,AngryBirdActivity.startY,bmpBird.getHeight()/2f,bmpBird,Type.redBird);		

		/** 創建四周的邊框，設置 isStatic為true，即在物理世界中是靜止的，
		 * Type設置為ground，避免被擊毀
		 * */
		createPolygon(5, 5, this.getWidth() - 10, 2, true,Type.ground);
		createPolygon(5, this.getHeight() - 10, this.getWidth() - 10, 2, true,Type.ground);
		createPolygon(5, 5, 2, this.getHeight() - 10, true,Type.ground);
		createPolygon(this.getWidth() - 10, 5, 2, this.getHeight() - 10, true,Type.ground);
		
		/**創建6個方形，isStatic設置為false，即在物理世界中是動態，收外力作用影響 */
		for(int i=0;i<6;i++)
		{
			createPolygon(screenW-250,screenH-200-20*i,20,20, false,Type.wood);
		}
		/**創建一個長條型，也是動態的 */
		createPolygon(screenW-380,screenH-250-20*6-10,80,10, false,Type.wood);
		
		/**啟動執行緒*/
		flag=true;
		th=new Thread(this);
		th.start();
		
	}
	
	/**創建圓形的body*/
	public Body createCircle(float x,float y,float r,boolean isStatic)
	{
		/**設置body形狀*/
	    CircleShape circle = new CircleShape();
	    /**半徑，要將螢幕的參數轉化到物理世界中 */
	    circle.setRadius(r/RATE);
		
	    /**設置FixtureDef */
		FixtureDef fDef=new FixtureDef();
		if(isStatic)
		{
			/**密度為0時，在物理世界中不受外力影響，為靜止的 */
			fDef.density=0;
		}
		else
		{
			/**密度不為0時，在物理世界中會受外力影響 */
			fDef.density=1;
		}
		/**設置摩擦力，範圍為 0～1 */
		fDef.friction=1.0f;
		/**設置物體碰撞的回復力，?翟醬螅鍰逶接械?*/
		fDef.restitution=0.3f;
		/**添加形狀*/
		fDef.shape=circle;

	    /**設置BodyDef */
		BodyDef bodyDef=new BodyDef();
		
		/**此處一定要設置，即使density不為0，
		 * 若此處不將body.type設置為BodyType.DYNAMIC,物體亦會靜止
		 * */
		bodyDef.type=isStatic?BodyType.StaticBody:BodyType.DynamicBody;
		/**設置body位置，要將螢幕的參數轉化到物理世界中 */
		bodyDef.position.set((x)/RATE, (y)/RATE);
		
		/**創建body*/
		Body body=world.createBody(bodyDef);
		
		/**添加 m_userData */
		body.setUserData(bird);
		
	//	body.createShape(fDef); //舊版JBox2D的創建方法
		
		/**為body創建Fixture*/
		body.createFixture(fDef); 
		
	//	body.setMassFromShapes();	//舊版JBox2D的創建方法
		
		return body;
	}
	
	public Body createPolygon(float x,float y,float width,float height,boolean isStatic,Type type)
	{
		PolygonShape polygon =new PolygonShape();
		
		polygon.setAsBox(width/2/RATE, height/2/RATE);
		
		FixtureDef fDef=new FixtureDef();
		if(isStatic)
		{
			fDef.density=0;
		}
		else
		{
			fDef.density=1;
		}
		fDef.friction=1.0f;
		fDef.restitution=0.0f;
		
		fDef.shape=polygon;

		BodyDef bodyDef=new BodyDef();
		
		bodyDef.type=isStatic?BodyType.StaticBody:BodyType.DynamicBody;//new
		
		bodyDef.position.set((x+width/2)/RATE,(y+height/2)/RATE );
		
		Body body=world.createBody(bodyDef);

		body.setUserData(new MyRect(x,y,width,height,type));
	
	//	body.createShape(polygonDef);
	//	body.setMassFromShapes();
		body.createFixture(fDef);
		
		return body;

	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		flag=false;
	}

	
	public void draw()
	{
		try
		{
			canvas=sfh.lockCanvas();
			if(canvas!=null)
			{
				/**用白色填充畫布*/
				canvas.drawColor(Color.WHITE);
				/**畫出小鳥*/
				bird.draw(canvas, paint);

				/**如果小鳥還沒被發射，畫出拖動的橡皮筋軌跡*/
				if(!bird.getIsReleased())
				{
					canvas.drawLine(AngryBirdActivity.startX, AngryBirdActivity.startY, bird.getX(), bird.getY(), paint);
				}

				/**遍歷物理世界，畫出Rect */
//				Body body = world.getBodyList();
				for (int i = 1; i < world.getBodyCount(); i++) {
					LBody body = world.getBodyList().get(i);
					if ((body.getUserData()) instanceof MyRect) {
						MyRect rect = (MyRect) (body.getUserData());
						rect.draw(canvas, paint);
					}
//					body = body.m_next;
				}
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(canvas!=null)
			{
				sfh.unlockCanvasAndPost(canvas);
			}
		}
		
	}
	
	public void logic()
	{
		world.step(timeStep, velocityIterations,positionIterations);// 物理世界進行類比

		/**遍歷物理世界中的body，將物理世界模擬出的值回饋給螢幕，
		 * 改變bird和rect的參數
		 * */
//		Body body = world.getBodyList();	
		for (int i = 1; i < world.getBodyCount(); i++) {
			LBody body = world.getBodyList().get(i);
			if ((body.getUserData()) instanceof MyRect) {
				MyRect rect = (MyRect) (body.getUserData());
				rect.setX(body.getPosition().x * RATE - (rect.getWidth()/2));
				rect.setY(body.getPosition().y * RATE - (rect.getHeight()/2));
				rect.setAngle((float)(body.getAngle()*180/Math.PI));
			}
			else if ((body.getUserData()) instanceof Bird) {
					Bird bird = (Bird) (body.getUserData());
					bird.setX(body.getPosition().x * RATE );
					bird.setY(body.getPosition().y * RATE );
					bird.setAngle((float)(body.getAngle()*180/Math.PI));
			}
			else // body.m_userData==null時，將body銷毀，表示被擊毀
			{
				world.destroyBody(body);
			}
//			body = body.m_next;
		}
		
		/**發射小鳥，且只有一次，發射過後，不能再拖動了*/
		if(bird.getIsReleased()&&!bird.getApplyForce())
		{
			/**發射時才創建一個body*/
			Body birdBody=createCircle(bird.getX(),bird.getY(),bird.getR(),false);
			
			/**設置bullet屬性為true,否則速度過快時可能會有穿越現象 */
			birdBody.setBullet(true);
			
			/**發射力量控制*/
			float forceRate=200f;
			
			/**根據橡皮筋長度和角度設置發射力*/
			float angle=(float) Math.atan2(bird.getY()-AngryBirdActivity.startY,bird.getX()-AngryBirdActivity.startX);
			float forceX=-(float) (Math.sqrt(Math.pow(bird.getX()-AngryBirdActivity.startX, 2))*Math.cos(angle));
			float forceY=-(float) (Math.sqrt(Math.pow(bird.getY()-AngryBirdActivity.startY, 2))*Math.sin(angle));
		
			Vector2 force=new Vector2(forceX*forceRate,forceY*forceRate);
			
			/**對body應用作用力 */
			birdBody.applyForce(force, birdBody.getWorldCenter());

			/**設置已經作用過力，發射後，不能再拖動了 */
			bird.setApplyForce(true);
		}

	}
	
	
	@Override
	public void run() {
		while(flag)
		{
			long start=System.currentTimeMillis();
			draw();
			logic();
			long end=System.currentTimeMillis();
			
			try
			{
				if(end-start<50)
				{
					Thread.sleep(50-(end-start));
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			/**小鳥未發射時點擊它*/
			if(bird.isPressed(event)&&!bird.getIsReleased())
			{
				bird.setIsPressed(true);
			}
		}
		else if(event.getAction()==MotionEvent.ACTION_MOVE)
		{
			/**小鳥未發射時拖動 */
			if(bird.getIsPressed())
			{
				bird.move(event);
			}
		}
		else if(event.getAction()==MotionEvent.ACTION_UP)
		{
			if(bird.getIsPressed())
			{
				bird.setIsReleased(true);
				bird.setIsPressed(false);
			}

		}

		/**對touchEvent的優化，防止真機調試時過於頻繁的回應 */
		synchronized(lock)
		{
			try
			{
				lock.wait(timePause);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return true;
		
	}

	@Override
	public void beginContact(Contact arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub

		/**碰撞事件的檢測，參數是調試出來的 */
		if(arg1.getNormalImpulses()[0]>5)
		{
			if ( (arg0.getFixtureA().getBody().getUserData())instanceof MyRect)
			{

				MyRect rect=(MyRect)(arg0.getFixtureA().getBody().getUserData());

				/**只有這幾種類型會被擊毀 */
				if(rect.getType()==Type.stone
				||rect.getType()==Type.wood
				||rect.getType()==Type.pig
				||rect.getType()==Type.glass)
				{
					arg0.getFixtureA().getBody().setUserData(null);
				}
			}
			
			if ( (arg0.getFixtureB().getBody().getUserData())instanceof MyRect)
			{
				
				MyRect rect=(MyRect)(arg0.getFixtureB().getBody().getUserData());

				if(rect.getType()==Type.stone
				||rect.getType()==Type.wood
				||rect.getType()==Type.pig
				||rect.getType()==Type.glass)
				{
					arg0.getFixtureB().getBody().setUserData(null);
				}
			}
		
		}
	
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		// TODO Auto-generated method stub
		
	}

}



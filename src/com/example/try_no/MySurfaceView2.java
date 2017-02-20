package com.example.try_no;


import java.util.List;

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

public class MySurfaceView2 extends SurfaceView implements Callback,Runnable,ContactListener{

	private SurfaceHolder sfh;
	
	/**軞腔堍俴盄最*/
	private Thread th;
	/**盄最堍俴梓祩弇*/
	private boolean flag;
	
	private Canvas canvas;
	private Paint paint;
	
	private int screenW,screenH;
	
	/**Bird濬ㄛ蚚眕餅賒堤苤纏*/
	Bird bird;
	
	/**touchEvent腔蚥趙ㄛ旌轎淩儂覃彸奀楛砒茼*/
	byte[] lock = new byte[0];
	private final int timePause=50;
	
	/**昜燴岍賜汒隴*/
//	World world;
	LWorld world;
	
//	AABB aabb;  //陔唳腔JBox2D眒冪祥剒猁AABB郖賸
	Vector2 gravity;
	private final float RATE=30.0f; //昜燴岍賜迵躉遠噫坫溫掀蹈
	float timeStep=1f/60f;	
	
	/**陔腔JBox2D崝樓善謗跺諷秶詞測ㄛ統杅歙偌桽夥源manual奻腔統杅扢离腔 */
	int velocityIterations = 10;	
	int positionIterations = 8;

	public MySurfaceView2(Context context) {
		super(context);
		
		sfh=this.getHolder();
		sfh.addCallback(this);
		
		paint=new Paint();
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);

//		aabb=new AABB(); 	//導唳JBox2D腔斐膘源楊
//		aabb.lowerBound.set(-100, -100);
//		aabb.upperBound.set(100,100);
		
		/**笭薯場宎趙*/
		gravity=new Vector2(0,10f);
		
		/**斐膘昜燴岍賜*/
//		world=new World(gravity, true);
		
		world = new LWorld(50, 50, 400, 400, true, 1.0f);
		
		/**崝樓昜燴岍賜笢腔癲袉潼泭*/
		world.setContactListener(this);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		/**腕善躉湮苤*/
		this.screenW=this.getWidth();
		this.screenH=this.getHeight();

		/**場宎趙苤纏弇离*/
		AngryBirdActivity.startX=100;
		AngryBirdActivity.startY=screenH-100;
		/**場宎趙砎踐酗僅*/
		AngryBirdActivity.touchDistance=0.2f*screenH;
		
		
		Bitmap bmpBird=BitmapFactory.decodeResource(this.getResources(), R.drawable.smallbird);
		
		bird=new Bird(AngryBirdActivity.startX,AngryBirdActivity.startY,bmpBird.getHeight()/2f,bmpBird,Type.redBird);		

		/** 斐膘侐笚腔晚遺ㄛ扢离 isStatic峈trueㄛ撈婓昜燴岍賜笢岆噙砦腔ㄛ
		 * Type扢离峈groundㄛ旌轎掩僻障
		 * */
		createPolygon(5, 5, this.getWidth() - 10, 2, true,Type.ground);
		createPolygon(5, this.getHeight() - 10, this.getWidth() - 10, 2, true,Type.ground);
		createPolygon(5, 5, 2, this.getHeight() - 10, true,Type.ground);
		createPolygon(this.getWidth() - 10, 5, 2, this.getHeight() - 10, true,Type.ground);
		
		/**斐膘6跺源倛ㄛisStatic扢离峈falseㄛ撈婓昜燴岍賜笢岆雄怓ㄛ彶俋薯釬蚚荌砒 */
		for(int i=0;i<6;i++)
		{
			createPolygon(screenW-150,screenH-50-20*i,20,20, false,Type.wood);
		}
		/**斐膘珨跺酗沭倰ㄛ珩岆雄怓腔 */
		createPolygon(screenW-180,screenH-50-20*6-10,80,10, false,Type.wood);
		
		/**雄盄最*/
		flag=true;
		th=new Thread(this);
		th.start();
		
	}
	
	/**斐膘埴倛腔body*/
	public Body createCircle(float x,float y,float r,boolean isStatic)
	{
		/**扢离body倛袨*/
	    CircleShape circle = new CircleShape();
	    /**圉噤ㄛ猁蔚躉腔統杅蛌趙善昜燴岍賜笢 */
	    circle.setRadius(r/RATE) ;
		
	    /**扢离FixtureDef */
		FixtureDef fDef=new FixtureDef();
		if(isStatic)
		{
			/**躇僅峈0奀ㄛ婓昜燴岍賜笢祥忳俋薯荌砒ㄛ峈噙砦腔 */
			fDef.density=0;
		}
		else
		{
			/**躇僅祥峈0奀ㄛ婓昜燴岍賜笢頗忳俋薯荌砒 */
			fDef.density=1;
		}
		/**扢离藻笠薯ㄛ毓峓峈 0‵1 */
		fDef.friction=1.0f;
		/**扢离昜极癲袉腔隙葩薯ㄛ硉埣湮ㄛ昜极埣衄粟俶 */
		fDef.restitution=0.3f;
		/**氝樓倛袨*/
		fDef.shape=circle;

	    /**扢离BodyDef */
		BodyDef bodyDef=new BodyDef();
		
		/**森揭珨隅猁扢离ㄛ撈妏density祥峈0ㄛ
		 * 森揭祥蔚body.type扢离峈BodyType.DYNAMIC,昜极砫頗噙砦
		 * */
		bodyDef.type=isStatic?BodyType.StaticBody:BodyType.DynamicBody;
		/**扢离body弇离ㄛ猁蔚躉腔統杅蛌趙善昜燴岍賜笢 */
		bodyDef.position.set((x)/RATE, (y)/RATE);
		
		/**斐膘body*/
		Body body=world.createBody(bodyDef);
		
		/**氝樓 m_userData */
		body.setUserData(bird);
		
	//	body.createShape(fDef); //導唳JBox2D腔斐膘源楊
		
		/**峈body斐膘Fixture*/
		body.createFixture(fDef); 
		
	//	body.setMassFromShapes();	//導唳JBox2D腔斐膘源楊
		
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
				/**蚚啞伎沓喃賒票*/
				canvas.drawColor(Color.WHITE);
				/**賒堤苤纏*/
				bird.draw(canvas, paint);

				/**彆苤纏遜羶掩楷扞ㄛ賒堤迍雄腔砎踐寢慫*/
				if(!bird.getIsReleased())
				{
					canvas.drawLine(AngryBirdActivity.startX, AngryBirdActivity.startY, bird.getX(), bird.getY(), paint);
				}

				/**梢盪昜燴岍賜ㄛ賒堤Rect */
//				Body body = world.getBodyList();
				
				
				
//				for (int i = 1; i < world.getBodyCount(); i++) {
//					Body body = (Body)world.getContactList().get(i);
//					if ((body.getUserData()) instanceof MyRect) {
//						MyRect rect = (MyRect) (body.getUserData());
//						rect.draw(canvas, paint);
//					}
//					body = body.;
//				}
				
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
		world.step(timeStep, velocityIterations,positionIterations);// 昜燴岍賜輛俴耀攜

		/**梢盪昜燴岍賜笢腔bodyㄛ蔚昜燴岍賜溘淩堤腔硉毀嚏跤躉ㄛ
		 * 蜊曹bird睿rect腔統杅
		 * */
		List<LBody> bodys = world.getBodyList();	
		for (int i = 1; i < world.getBodyCount(); i++) {
			LBody body = world.getBodyList().get(i);
			if (body.getUserData() instanceof MyRect) {
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
			else // body.m_userData==null奀ㄛ蔚body种障ㄛ桶尨掩僻障
			{
				world.destroyBody(body);
			}
//			body = body.m_next;
		}
		
		/**楷扞苤纏ㄛ硐衄珨棒ㄛ楷扞徹綴ㄛ祥夔婬迍雄賸*/
		if(bird.getIsReleased()&&!bird.getApplyForce())
		{
			/**楷扞奀符斐膘珨跺body*/
			Body birdBody=createCircle(bird.getX(),bird.getY(),bird.getR(),false);
			
			/**扢离bullet扽俶峈true,瘁寀厒僅徹辦奀褫夔頗衄援埣珋砓 */
			birdBody.setBullet(true);
			
			/**楷扞薯講諷秶*/
			float forceRate=50f;
			
			/**跦擂砎踐酗僅睿褒僅扢离楷扞薯*/
			float angle=(float) Math.atan2(bird.getY()-AngryBirdActivity.startY,bird.getX()-AngryBirdActivity.startX);
			float forceX=-(float) (Math.sqrt(Math.pow(bird.getX()-AngryBirdActivity.startX, 2))*Math.cos(angle));
			float forceY=-(float) (Math.sqrt(Math.pow(bird.getY()-AngryBirdActivity.startY, 2))*Math.sin(angle));
		
			Vector2 force=new Vector2(forceX*forceRate,forceY*forceRate);
			
			/**勤body茼蚚釬蚚薯 */
			birdBody.applyForce(force, birdBody.getWorldCenter());

			/**扢离眒冪釬蚚徹薯ㄛ楷扞綴ㄛ祥夔婬迍雄賸 */
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
			/**苤纏帤楷扞奀萸僻坳*/
			if(bird.isPressed(event)&&!bird.getIsReleased())
			{
				bird.setIsPressed(true);
			}
		}
		else if(event.getAction()==MotionEvent.ACTION_MOVE)
		{
			/**苤纏帤楷扞奀迍雄 */
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

		/**勤touchEvent腔蚥趙ㄛ滅砦淩儂覃彸奀徹衾楛腔砒茼 */
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
		/**癲袉岈璃腔潰聆ㄛ統杅岆覃彸堤懂腔 */
//		if(arg1.normalImpulses[0]>5)
//		{
//			if ( (arg0.getFixtureA().getBody().getUserData())instanceof MyRect)
//			{
//
//				MyRect rect=(MyRect)(arg0.getFixtureA().getBody().getUserData());
//
//				/**硐衄涴撓笱濬倰頗掩僻障 */
//				if(rect.getType()==Type.stone
//				||rect.getType()==Type.wood
//				||rect.getType()==Type.pig
//				||rect.getType()==Type.glass)
//				{
//					arg0.getFixtureA().getBody().m_userData=null;
//				}
//			}
//			
//			if ( (arg0.getFixtureB().getBody().getUserData())instanceof MyRect)
//			{
//				
//				MyRect rect=(MyRect)(arg0.getFixtureB().getBody().getUserData());
//
//				if(rect.getType()==Type.stone
//				||rect.getType()==Type.wood
//				||rect.getType()==Type.pig
//				||rect.getType()==Type.glass)
//				{
//					arg0.getFixtureB().getBody().m_userData=null;
//				}
//			}
//		
//		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}


}

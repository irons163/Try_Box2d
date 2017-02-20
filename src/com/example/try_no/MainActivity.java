package com.example.try_no;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Vector2 gravity = new Vector2(0f, 10f); 
		World world = new World(gravity, false);
		world.setContactListener(new ContactListener() {
			
			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}
	
//    public Body createPolygon(float x,float y,float width,float height,boolean isStatic,Type type)  
//    {  
//        PolygonShape polygon =new PolygonShape();  
//          
//        polygon.setAsBox(width/2/RATE, height/2/RATE);  
//          
//        FixtureDef fDef=new FixtureDef();  
//        if(isStatic)  
//        {  
//            fDef.density=0;  
//        }  
//        else  
//        {  
//            fDef.density=1;  
//        }  
//        fDef.friction=1.0f;  
//        fDef.restitution=0.0f;  
//          
//        fDef.shape=polygon;  
//  
//        BodyDef bodyDef=new BodyDef();  
//          
//        bodyDef.type=isStatic?BodyType.STATIC:BodyType.DYNAMIC;//new  
//          
//        bodyDef.position.set((x+width/2)/RATE,(y+height/2)/RATE );  
//          
//        Body body=world.createBody(bodyDef);  
//  
//        body.m_userData=new MyRect(x,y,width,height,type);  
//      
//    //  body.createShape(polygonDef);  
//    //  body.setMassFromShapes();  
//        body.createFixture(fDef);  
//          
//        return body;  
//  
//    }  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

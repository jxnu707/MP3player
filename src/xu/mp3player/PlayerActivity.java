package xu.mp3player;

import xu.model.Mp3Info;
import xu.mp3player.R;
import xu.mp3player.service.Playservice;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends Activity implements OnTouchListener,OnGestureListener
{
//	ImageButton startButton=null;
//	ImageButton pauseButton=null;
//	ImageButton stopButton=null;
	Mp3Info mp3Info=null;
	TextView textview=null;
	TextView textview1=null;
	TextReceiver textReceiver=null;
	GestureDetector gd;
	SeekBar seekBar;
	
	
	
	boolean exit=false;
	// 计算点击的次数
		private int count = 0;
		// 第一次点击的时间 long型
		private long firstClick = 0;
		// 最后一次点击的时间
		private long lastClick = 0;
		
		SensorManager sm;
//		private long currentTime=0;
//		private long lastTime=0;//记录传感器变化的时间变量
//		private float lastX,lastY,lastZ;//记录上一次震动位置
		//private float currentShake,totalShake;//记录震动幅度

	@Override
	protected void onResume() {
		
		
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
//		startButton=(ImageButton)findViewById(R.id.startbutton);
//		pauseButton=(ImageButton)findViewById(R.id.pausebutton);
//		stopButton=(ImageButton)findViewById(R.id.stopbutton);
		textview=(TextView)findViewById(R.id.textView);
		textview1=(TextView)findViewById(R.id.textView1);
//		View v=findViewById(R.layout.player);
//		v.getBackground().setAlpha(100);
		
	    gd=new GestureDetector((OnGestureListener)this);
	        LinearLayout ll=(LinearLayout)findViewById(R.id.playerlayout);
	        ll.setOnTouchListener(this);
	        ll.setLongClickable(true);
	        
	    seekBar=(SeekBar) findViewById(R.id.seekBar);
		
	}
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if (arg1.getAction() == MotionEvent.ACTION_DOWN)
		{
			// 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
			if (firstClick != 0 && System.currentTimeMillis() - firstClick > 500)
			{
				count = 0;
			}
			count++;
			if (count == 1)
			{
				firstClick = System.currentTimeMillis();
			} 
			else if (count == 2)
			{
				lastClick = System.currentTimeMillis();
				// 两次点击小于500ms 也就是连续点击
				if (lastClick - firstClick < 500)
				{
					Intent intent=new Intent();
					intent.putExtra("MSG", APPConstant.playMsg.PAUSE_MSG);
					intent.setFlags(APPConstant.intentKind.PlayerActivity);
					intent.setClass(PlayerActivity.this, Playservice.class);
					startService(intent);
				}
				clear();
			}
		}

		
		return gd.onTouchEvent(arg1);
	}

	private int minDistance=50;
	private int minVelocity=0;
	
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,//start arg0,end arg1,xspeed arg2,yspeed arg3
			float arg3) {
		// TODO Auto-generated method stub
		if((arg0.getX()-arg1.getX()>minDistance)&&Math.abs(arg2)>minVelocity){//left vertical move
			//change song
			Intent intent=new Intent(PlayerActivity.this,Playservice.class);
			intent.putExtra("MSG", APPConstant.playControl.PLAY_FRONT);
			startService(intent);
			System.out.println("left move");
		}
		
		else if(arg1.getX()-arg0.getX()>minDistance && Math.abs(arg2)>minVelocity){//right vertical move
			//change song
			Intent intent=new Intent(PlayerActivity.this,Playservice.class);
			intent.putExtra("MSG", APPConstant.playControl.PLAY_NEXT);
			startService(intent);
			System.out.println("right move");
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(textReceiver);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
		textReceiver=new TextReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("xu.setText");
		registerReceiver(textReceiver, filter);
		
		super.onStart();
	}

	class TextReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String title;
			String text=arg1.getStringExtra("text");
			int flag=arg1.getIntExtra("flag", 0);
			//System.out.println("flag= "+flag);
			if(flag==1){
				title=arg1.getStringExtra("title");
				System.out.println("title= "+title);
				textview1.setText(title);
			}
			textview.setText(text);
		}
		
	}

	protected void stop(){
		Intent intent=new Intent();
		intent.putExtra("MSG", APPConstant.playMsg.STOP_MSG);
		intent.setFlags(APPConstant.intentKind.PlayerActivity);
		intent.setClass(PlayerActivity.this, Playservice.class);
		startService(intent);
		//handler.removeCallbacks(updatetimeCallBack);
	}

	private void clear(){
		// 清空状态
		count = 0;
		firstClick = 0;
		lastClick = 0;
				
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("longpress!");
		new AlertDialog.Builder(this).setMessage("Do you want to exit?").setPositiveButton("Yes", 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("MSG", APPConstant.playMsg.STOP_MSG);
				intent.setFlags(APPConstant.intentKind.PlayerActivity);
				intent.setClass(PlayerActivity.this, Playservice.class);
				startService(intent);
				exit=true;
				if(exit==true){
					
					getParent().finish();
				}
				}
		}).setNegativeButton("No", null).show();
		
		
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(sm!=null)
		this.unregisterReceiver(textReceiver);
		super.onStop();
	}
	
}
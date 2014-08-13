package xu.mp3player.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;
import xu.lrc.LrcProcessor;
import xu.model.Mp3Info;
import xu.mp3player.APPConstant;
import xu.mp3player.Mp3ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;


/*
 * 接受PlayerActivity中发送来的intent
 * 根据这个intent中传来的指令参数（MSG）执行对应的操作
 */

public class Playservice extends Service
{
	
	private ArrayList<Queue> Queues=null;
    private long CurrentTimeMill=0;//记录一首歌曲从第一次开始播放到最后结束的时间轴
    private long NextTimeMill=0;//下一个要更新歌词的时间
    private long begin=0;//开始播放的时间点（包括暂停后重启开始播放的时间）
   //private boolean isplaying=false;
    private Handler handler=new Handler();
    private UpdateTimeCallBack updatetimeCallBack=null;
    private long Pausetime=0;//暂停的时间点
    private String message=null;//某个时间点对应的歌词内容
    private Mp3Info mp3info;
    //private TextView textView=(TextView)findViewById(R.id.textview);
    	
	private boolean isplaying=false;
	private boolean ispause=false;
	private boolean isreleased=false;
	private MediaPlayer mediaPlayer=null;
	
	
	private SensorManager sm;
	private long currentTime=0;
	private long lastTime=0;//记录传感器变化的时间变量
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		sm=(SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		super.onCreate();
	}
	


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("the service has been destroied!");
		sm.unregisterListener(sensorEventListener);
		super.onDestroy();
	}

	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		int MSG=0;
		
		if(intent!=null&&intent.getFlags()==APPConstant.intentKind.Mp3ListActivity){
			if(mediaPlayer!=null)
				mediaPlayer.release();
			mp3info=(Mp3Info) intent.getSerializableExtra("mp3Info");
			
			mediaPlayer=MediaPlayer.create(this, Uri.parse(getMp3path(mp3info)));
		}
		
		//creat（context，uri）根据uri指定的资源创建一个MediaPlayer的实例
		//mediaPlayer=MediaPlayer.create(this, Uri.parse("file://"+path));
		
		if(mediaPlayer!=null){
			mediaPlayer.setOnCompletionListener(new CompletionListener());
		}
		
		
		if(mp3info!=null)
			System.out.println("mp3info is not null,in onStartCommand");
		if(intent!=null)
	    MSG=intent.getIntExtra("MSG", 0);
		if(MSG==APPConstant.playMsg.PLAY_MSG)
			start();
		else if(MSG==APPConstant.playMsg.PAUSE_MSG)
			pause();
		else if(MSG==APPConstant.playMsg.STOP_MSG)
			stop();
		else if(MSG==APPConstant.playControl.PLAY_NEXT)
			playNext();
		else if(MSG==APPConstant.playControl.PLAY_FRONT)
			palyFront();
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void start()
	{
		Intent textIntent=new Intent();
		textIntent.setAction("xu.setText");
		textIntent.putExtra("flag", 1);
//		System.out.println("playservice flags= "+textIntent.getIntExtra("flag", 0));
		textIntent.putExtra("text", "");
		textIntent.putExtra("title", mp3info.getMp3Name());
		sendBroadcast(textIntent);
		boolean lrcFileFind=true;
		//if(!isplaying)
		{
			handler.removeCallbacks(updatetimeCallBack);
//			System.out.println("removeCallBacks!");
			//isreleased=false;
//			String path=getMp3path(mp3Info);
//			if(mediaPlayer!=null)
//			{
//				mediaPlayer.release();
//			
//			}
//			//creat（context，uri）根据uri指定的资源创建一个MediaPlayer的实例
//			//mediaPlayer=MediaPlayer.create(this, Uri.parse("file://"+path));
//			mediaPlayer=MediaPlayer.create(this, Uri.parse(path));
//			mediaPlayer.setOnCompletionListener(new CompletionListener());
			
			try 
			{
				
				String lrcName=mp3info.getLrcName();
				//System.out.println(lrcName+" onResume");
				prepareLrc(lrcName);
				
			} 
			catch (Exception e) 
			{
				Intent textEIntent=new Intent();
				textIntent.setAction("xu.setText");
				textIntent.putExtra("text", "Sorry,can't find lrc file that match the name of this song!");
				sendBroadcast(textIntent);
				//textview.setText("Sorry,can't find lrc file that match the name of this song!");
				lrcFileFind=false;
				e.printStackTrace();
			}
			
			mediaPlayer.start();
			
			if(lrcFileFind){
				begin=System.currentTimeMillis();
				//System.out.println("lrcFileFind");
				//if(updatetimeCallBack==null)
					//System.out.println("Here!!");
				handler.postDelayed(updatetimeCallBack, 5);
			}
			
			ispause=false;
			isplaying=true;
			isreleased=false;
			//取消循环播放
			mediaPlayer.setLooping(false);
		}
	}
	
	private void pause()
	{
		if(mediaPlayer!=null)
		{
			if(!isreleased)
			{
				if(!ispause)
				{
					
					mediaPlayer.pause();
					if(isplaying)
					{
						//停止线程 即停止更新歌词工作
						handler.removeCallbacks(updatetimeCallBack);
						Pausetime=System.currentTimeMillis();
						ispause=true;
					}
				
				}
				else
				{
					mediaPlayer.start();
					//System.out.println("unpause continue updating lrc");
					
					handler.postDelayed(updatetimeCallBack, 5);
					begin=System.currentTimeMillis()-Pausetime+ begin;//暂停后重新播放的时间点
					
					ispause=false;
					isplaying=true;
				}
				//isplaying = isplaying ? false : true;
			}
		}
	}
	
	private void stop()
	{
		if(mediaPlayer!=null)
		{
			if(isplaying)
			{
				if(!isreleased)
				{
					mediaPlayer.release();
					isreleased=true;
				}
				isplaying=false;
			}
			handler.removeCallbacks(updatetimeCallBack);
			this.stopSelf();
			System.out.println("service stop");
		}
		
	}
	
	//根据intent传来的Mp3info（mp3Info）来得到该MP3文件的路径
	public String getMp3path(Mp3Info mp3info)
	{
		String path;
		//path=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mp3/"+mp3info.getMp3Name();
		path=mp3info.getUrl();
		return path;
	}
	
	private void playNext(){
		Mp3ListActivity.position++;
		mediaPlayer.release();
		isreleased=true;
		if(Mp3ListActivity.position>Mp3ListActivity.mp3InfosList.size()-1)
			Mp3ListActivity.position=0;
	
		 mp3info=Mp3ListActivity.mp3InfosList.get(Mp3ListActivity.position);
		mediaPlayer=MediaPlayer.create(getBaseContext(), Uri.parse(getMp3path(mp3info)));
		if(mediaPlayer!=null){
			mediaPlayer.setOnCompletionListener(new CompletionListener());
		}
		start();
		System.out.println("playing next");
		isplaying=true;
	}
	
	
	private void palyFront(){
		Mp3ListActivity.position--;
		mediaPlayer.release();
		isreleased=true;
		if(Mp3ListActivity.position==-1)
			Mp3ListActivity.position=Mp3ListActivity.mp3InfosList.size()-1;
		
		 mp3info=Mp3ListActivity.mp3InfosList.get(Mp3ListActivity.position);
		mediaPlayer=MediaPlayer.create(getBaseContext(), Uri.parse(getMp3path(mp3info)));
		start();
		isplaying=true;
		
	}

	//将lrc文件解析至两个队列中 并进行一些参数的初始化工作
		public void prepareLrc(String LrcName) throws Exception
		{
			Queues=new ArrayList<Queue>();
			//if(mp3Info==null)
				//System.out.println("here!!!!");
			//System.out.println("in prepareLrc!");
			InputStream inputstream;
			try {
				inputstream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()+File.separator+"ttpod/lyric/"+mp3info.getLrcName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				inputstream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()+File.separator+"Music/lyric/"+mp3info.getMp3Name()+".lrc");
				e.printStackTrace();
			}
			//System.out.println("ready new a LrcProcessor");
			LrcProcessor lrcProcessor =new LrcProcessor();
			//System.out.println("ready go into process");
			Queues=lrcProcessor.process(inputstream);
			//System.out.println("has gone into process");
			updatetimeCallBack=new UpdateTimeCallBack(Queues);
			//System.out.println("new a UpdateTimeCallBack");
			begin=0;
			CurrentTimeMill=0;
			NextTimeMill=0;
		}
		
		//后台管理线程 定时根据系统时间和歌曲播放时间 检查是否需要更新歌词
		public class UpdateTimeCallBack implements Runnable 
		{
			int i=0;
			private Queue times=null;
			private Queue messages=null;
			//获得要管理的两个队列
			public UpdateTimeCallBack(ArrayList<Queue> Queues)
			{
				times=Queues.get(0);
				messages=Queues.get(1);
				
			}
			
			@Override
			public void run() 
			{
				if(i==0)
					System.out.println("in run()");
				//计算偏移量 即这首歌播放了多长的时间
				long offset=System.currentTimeMillis() - begin;
				if(CurrentTimeMill==0)
				{
					if(!messages.isEmpty()&&!times.isEmpty()){
						message=(String) messages.poll();
						NextTimeMill=(Long) times.poll();
					}
					
				}
				if(offset>=NextTimeMill)
				{
					Intent textIntent=new Intent();
					textIntent.setAction("xu.setText");
					textIntent.putExtra("text", message);
					sendBroadcast(textIntent);
					//System.out.println("lrc has been sent");
					//textview.setText(message);
					if(!messages.isEmpty()&&!times.isEmpty()){
						message=(String) messages.poll();
						NextTimeMill=(Long) times.poll();
					}
					
				}
				i=1;
				//该歌曲的总处理时间（没有release之前）+10毫秒
				CurrentTimeMill=CurrentTimeMill+10;
				//10毫秒检查一次
				handler.postDelayed(updatetimeCallBack, 10);
			}
			
		}
		
		class CompletionListener implements OnCompletionListener{

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				System.out.println("music has finished!");
				//handler.removeCallbacks(updatetimeCallBack);
				playNext();
			}
			
		}
	
		private SensorEventListener sensorEventListener = new SensorEventListener() { 
			 
	        @Override 
	        public void onSensorChanged(SensorEvent event) { 
	            //System.out.println("in onsensorchanged!");
	        	// 传感器信息改变时执行该方法 
	        	
//	        	currentTime=System.currentTimeMillis();
//	        	if(lastX==0&&lastY==0&&lastZ==0)
//	        	if(isShaked)
//	        		lastTime=currentTime;
	            float[] values = event.values; 
	            float x = values[0]; // x轴方向的重力加速度，向右为正 
	            float y = values[1]; // y轴方向的重力加速度，向前为正 
	            float z = values[2]; // z轴方向的重力加速度，向上为正 
//	            for(int i=0;i<3;i++)
//	            	System.out.println(values[i]);
	            //Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z); 
	            // 一般在这三个方向的重力加速度达到13就达到了摇晃手机的状态。 
	            int medumValue = 13;// 
	            if ((Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue)) { 

	            	currentTime=System.currentTimeMillis();
	            	//System.out.println("current= "+currentTime);
	            	if(currentTime-lastTime>2000){
	            		System.out.println("shake ~!");
	            		//isShaked=true;
	            		lastTime=currentTime;
	            		Intent intent=new Intent();
	            		intent.setFlags(APPConstant.intentKind.PlayerActivity);
	            		intent.putExtra("MSG",APPConstant.playControl.PLAY_NEXT);
	            		intent.setClass(getApplicationContext(), Playservice.class);
	            		startService(intent);
	            	}
	            	
	            	//textview.setText("shaked!");
	            } 
	        }

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			} 
	 };
}

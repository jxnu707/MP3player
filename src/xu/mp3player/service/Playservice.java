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
 * ����PlayerActivity�з�������intent
 * �������intent�д�����ָ�������MSG��ִ�ж�Ӧ�Ĳ���
 */

public class Playservice extends Service
{
	
	private ArrayList<Queue> Queues=null;
    private long CurrentTimeMill=0;//��¼һ�׸����ӵ�һ�ο�ʼ���ŵ���������ʱ����
    private long NextTimeMill=0;//��һ��Ҫ���¸�ʵ�ʱ��
    private long begin=0;//��ʼ���ŵ�ʱ��㣨������ͣ��������ʼ���ŵ�ʱ�䣩
   //private boolean isplaying=false;
    private Handler handler=new Handler();
    private UpdateTimeCallBack updatetimeCallBack=null;
    private long Pausetime=0;//��ͣ��ʱ���
    private String message=null;//ĳ��ʱ����Ӧ�ĸ������
    private Mp3Info mp3info;
    //private TextView textView=(TextView)findViewById(R.id.textview);
    	
	private boolean isplaying=false;
	private boolean ispause=false;
	private boolean isreleased=false;
	private MediaPlayer mediaPlayer=null;
	
	
	private SensorManager sm;
	private long currentTime=0;
	private long lastTime=0;//��¼�������仯��ʱ�����
	
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
		
		//creat��context��uri������uriָ������Դ����һ��MediaPlayer��ʵ��
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
//			//creat��context��uri������uriָ������Դ����һ��MediaPlayer��ʵ��
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
			//ȡ��ѭ������
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
						//ֹͣ�߳� ��ֹͣ���¸�ʹ���
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
					begin=System.currentTimeMillis()-Pausetime+ begin;//��ͣ�����²��ŵ�ʱ���
					
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
	
	//����intent������Mp3info��mp3Info�����õ���MP3�ļ���·��
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

	//��lrc�ļ����������������� ������һЩ�����ĳ�ʼ������
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
		
		//��̨�����߳� ��ʱ����ϵͳʱ��͸�������ʱ�� ����Ƿ���Ҫ���¸��
		public class UpdateTimeCallBack implements Runnable 
		{
			int i=0;
			private Queue times=null;
			private Queue messages=null;
			//���Ҫ�������������
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
				//����ƫ���� �����׸貥���˶೤��ʱ��
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
				//�ø������ܴ���ʱ�䣨û��release֮ǰ��+10����
				CurrentTimeMill=CurrentTimeMill+10;
				//10������һ��
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
	        	// ��������Ϣ�ı�ʱִ�и÷��� 
	        	
//	        	currentTime=System.currentTimeMillis();
//	        	if(lastX==0&&lastY==0&&lastZ==0)
//	        	if(isShaked)
//	        		lastTime=currentTime;
	            float[] values = event.values; 
	            float x = values[0]; // x�᷽����������ٶȣ�����Ϊ�� 
	            float y = values[1]; // y�᷽����������ٶȣ���ǰΪ�� 
	            float z = values[2]; // z�᷽����������ٶȣ�����Ϊ�� 
//	            for(int i=0;i<3;i++)
//	            	System.out.println(values[i]);
	            //Log.i(TAG, "x�᷽����������ٶ�" + x +  "��y�᷽����������ٶ�" + y +  "��z�᷽����������ٶ�" + z); 
	            // һ����������������������ٶȴﵽ13�ʹﵽ��ҡ���ֻ���״̬�� 
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

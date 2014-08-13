package xu.mp3player;

import xu.mp3player.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity
{
	public static TabHost tabHost;
	public static int index;
	public static boolean EXIT=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    //指定布局
		setContentView(R.layout.main);
		//获取TabHost
		tabHost=getTabHost();
		/*
		 * 分别设置这个TabHost的分页（一个TabHost.TabSpec）：
		 * 		每个分页有一个类似标签的indicator需要设置
		 * 		每个分页的内容需要设置
		 * 		用Intent作为内容Intent（调用一个Activity）来填充分页内容
		 */
		
		TabHost.TabSpec RemotetabSpec=tabHost.newTabSpec("LocalFiles");
		//调用系统的资源
		Resources res=getResources();
		RemotetabSpec.setIndicator("LocalFiles",res.getDrawable(android.R.drawable.stat_sys_download));
		//设置内容Intent
		Intent remoteIntent=new Intent();
		remoteIntent.setClass(this, Mp3ListActivity.class);
		//填充分页内容
		RemotetabSpec.setContent(remoteIntent);
		//最后 别忘把设置好的分页添加到这个TabHost中
		tabHost.addTab(RemotetabSpec);
		
		TabHost.TabSpec LocaltabSpec=tabHost.newTabSpec("Playing");
		LocaltabSpec.setIndicator("Playing",res.getDrawable(android.R.drawable.stat_sys_upload));
		Intent localIntent=new Intent();
		//localIntent.setClass(this, LocalActivity.class);
		localIntent.setClass(this,PlayerActivity.class);
		LocaltabSpec.setContent(localIntent);
		tabHost.addTab(LocaltabSpec);
	}


}

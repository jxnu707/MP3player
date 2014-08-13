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
	    //ָ������
		setContentView(R.layout.main);
		//��ȡTabHost
		tabHost=getTabHost();
		/*
		 * �ֱ��������TabHost�ķ�ҳ��һ��TabHost.TabSpec����
		 * 		ÿ����ҳ��һ�����Ʊ�ǩ��indicator��Ҫ����
		 * 		ÿ����ҳ��������Ҫ����
		 * 		��Intent��Ϊ����Intent������һ��Activity��������ҳ����
		 */
		
		TabHost.TabSpec RemotetabSpec=tabHost.newTabSpec("LocalFiles");
		//����ϵͳ����Դ
		Resources res=getResources();
		RemotetabSpec.setIndicator("LocalFiles",res.getDrawable(android.R.drawable.stat_sys_download));
		//��������Intent
		Intent remoteIntent=new Intent();
		remoteIntent.setClass(this, Mp3ListActivity.class);
		//����ҳ����
		RemotetabSpec.setContent(remoteIntent);
		//��� ���������úõķ�ҳ��ӵ����TabHost��
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

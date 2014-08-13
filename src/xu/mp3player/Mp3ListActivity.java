package xu.mp3player;


import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import xu.mp3player.R;
import xu.mp3player.service.Playservice;
import xu.model.Mp3Info;
import xu.utils.AudioHunter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.storage.OnObbStateChangeListener;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Mp3ListActivity extends ListActivity {
	private static final int UPDATE = 1;
	private static final int ABOUT = 2;
	public static List<Mp3Info> mp3InfosList = null;
	private Intent listIntent=new Intent();
	private ListView listView;
	public static int position;
	/**
	 * 在用点击MENU按钮之后，会调用该方法，我们可以在这个方法当中加入自己的按钮控件
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, UPDATE, 1, R.string.mp3list_update);
		menu.add(0, ABOUT, 2, R.string.mp3list_about);
		return super.onCreateOptionsMenu(menu);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remotemp3);
		listView=getListView();
		listView.setLongClickable(true);
		listView.setOnCreateContextMenuListener(new ContextMenuListner());
		updateListView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == UPDATE) {
			updateListView();
			System.out.println("click update button");
		} else if (item.getItemId() == ABOUT) {
			// 用户点击了关于按钮
			System.out.println("click about button!");
		}
		return super.onOptionsItemSelected(item);
	}
	private SimpleAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos){
		// 生成一个List对象，并按照SimpleAdapter的标准，将mp3Infos当中的数据添加到List当中去
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mp3_name", mp3Info.getMp3Name());
			map.put("mp3_aritst", mp3Info.getArist());
			map.put("mp3_duration", String.valueOf(mp3Info.getDuration()));
			list.add(map);
		}
		// 创建一个SimpleAdapter对象
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,
				R.layout.mp3info_item, new String[] { "mp3_name", "mp3_artist","mp3_duration"},
				new int[] { R.id.mp3_name, R.id.mp3_artist,R.id.mp3_duration });
		// 将这个SimpleAdapter对象设置到ListActivity当中
		return simpleAdapter;
	}
	private void updateListView() {
		// 用户点击了更新列表按钮
		// 下载包含所有Mp3基本信息的xml文件
		//String xml = downloadXML("http://10.3.129.117:8080/mp3/resources.xml");
		//String xml = downloadXML(APPConstant.URL.Base_URL+"resources.xml");
		// 对xml文件进行解析，并将解析的结果放置到Mp3Info对象当中，最后将这些Mp3Info对象放置到List当中
		//mp3Infos = parse(xml);
		mp3InfosList=AudioHunter.findAudioFilesInSDCard(getApplicationContext());
		SimpleAdapter simpleAdapter = buildSimpleAdapter(mp3InfosList);
		setListAdapter(simpleAdapter);
		System.out.println("already updated");
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		Mp3ListActivity.position=position;
		//根据用户点击列表当中的位置来得到响应的Mp3Info对象
		Mp3Info mp3Info = mp3InfosList.get(position);
		if(mp3Info==null){
			System.out.println("Mp3ListActivity Here!");
		}
		else
			System.out.println("you click the "+position+"item "+mp3Info);
		//生成Intent对象
		//Intent intent = new Intent();
		//将Mp3Info对象存入到Intent对象当中
		listIntent.putExtra("mp3Info", mp3Info);
		listIntent.putExtra("MSG", APPConstant.playMsg.PLAY_MSG);
		listIntent.setFlags(APPConstant.intentKind.Mp3ListActivity);
		
     	//intent.setClass(this, Playservice.class);
		//启动Service
		//startService(intent);
		listIntent.setClass(this, Playservice.class);
		startService(listIntent);
//		intent.setClass(this, MainActivity.class);
//		startActivity(intent);
		MainActivity.tabHost.setCurrentTab(1);
		//MainActivity.tabHost.setCurrentTabByTag("Playing");
		super.onListItemClick(l, v, position, id);
	}

	private class ContextMenuListner implements OnCreateContextMenuListener{

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			menu.setHeaderTitle("ContextMenu");
			menu.add("Location");
			menu.add("Delete");
			menu.add("Details");
			
		}

		
	}
	
}
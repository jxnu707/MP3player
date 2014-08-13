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
	 * ���õ��MENU��ť֮�󣬻���ø÷��������ǿ���������������м����Լ��İ�ť�ؼ�
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
			// �û�����˹��ڰ�ť
			System.out.println("click about button!");
		}
		return super.onOptionsItemSelected(item);
	}
	private SimpleAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos){
		// ����һ��List���󣬲�����SimpleAdapter�ı�׼����mp3Infos���е�������ӵ�List����ȥ
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mp3_name", mp3Info.getMp3Name());
			map.put("mp3_aritst", mp3Info.getArist());
			map.put("mp3_duration", String.valueOf(mp3Info.getDuration()));
			list.add(map);
		}
		// ����һ��SimpleAdapter����
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,
				R.layout.mp3info_item, new String[] { "mp3_name", "mp3_artist","mp3_duration"},
				new int[] { R.id.mp3_name, R.id.mp3_artist,R.id.mp3_duration });
		// �����SimpleAdapter�������õ�ListActivity����
		return simpleAdapter;
	}
	private void updateListView() {
		// �û�����˸����б�ť
		// ���ذ�������Mp3������Ϣ��xml�ļ�
		//String xml = downloadXML("http://10.3.129.117:8080/mp3/resources.xml");
		//String xml = downloadXML(APPConstant.URL.Base_URL+"resources.xml");
		// ��xml�ļ����н��������������Ľ�����õ�Mp3Info�����У������ЩMp3Info������õ�List����
		//mp3Infos = parse(xml);
		mp3InfosList=AudioHunter.findAudioFilesInSDCard(getApplicationContext());
		SimpleAdapter simpleAdapter = buildSimpleAdapter(mp3InfosList);
		setListAdapter(simpleAdapter);
		System.out.println("already updated");
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		Mp3ListActivity.position=position;
		//�����û�����б��е�λ�����õ���Ӧ��Mp3Info����
		Mp3Info mp3Info = mp3InfosList.get(position);
		if(mp3Info==null){
			System.out.println("Mp3ListActivity Here!");
		}
		else
			System.out.println("you click the "+position+"item "+mp3Info);
		//����Intent����
		//Intent intent = new Intent();
		//��Mp3Info������뵽Intent������
		listIntent.putExtra("mp3Info", mp3Info);
		listIntent.putExtra("MSG", APPConstant.playMsg.PLAY_MSG);
		listIntent.setFlags(APPConstant.intentKind.Mp3ListActivity);
		
     	//intent.setClass(this, Playservice.class);
		//����Service
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
package xu.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import xu.model.Mp3Info;

public class AudioHunter {

	public static List<Mp3Info> findAudioFilesInSDCard(Context context) {
		ArrayList<Mp3Info> mp3Infos=new ArrayList<Mp3Info>() ;
		//搜索SD卡上所有的MP3文件信息
		Cursor cursor= context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	    for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
	    	Mp3Info info=new Mp3Info();
	    	info.setId(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))));
	    	info.setMp3Name(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))); 
	    	info.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
	    	info.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
	    	//info.setArist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
	    	//info.setLrcName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))+".lrc");
	    	info.setLrcName(info.getMp3Name()+".lrc");
	    	mp3Infos.add(info);
	    }
		
		return mp3Infos;
	}
	
}

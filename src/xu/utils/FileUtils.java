package xu.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import xu.model.Mp3Info;


import android.os.Environment;

public class FileUtils {
	private String SDCardRoot;

	public FileUtils() {
		//�õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}
	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName,String dir) throws IOException {
		File file = new File(SDCardRoot+ dir + File.separator + fileName);
		System.out.println("file---->" + file);
		file.createNewFile();
		return file;
	}
	
	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		System.out.println(dirFile.mkdirs());
		return dirFile;
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 */
	public boolean isFileExist(String fileName,String path){
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}
	
	/**
	 * ��һ��InputStream���������д�뵽SD����
	 */
	public File write2SDFromInput(String path,String fileName,InputStream input){
		
		File file = null;
		OutputStream output = null;
		try{
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];
			int temp ;
			while((temp = input.read(buffer)) != -1){
				output.write(buffer,0,temp);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}

	//pathΪҪ�������ļ���
	public List<Mp3Info> getMp3Files(String path)
	{
		List<Mp3Info> mp3infos=new ArrayList<Mp3Info>();
		//Ҫ�������ļ�·��������ֱ��˵��MP3�ļ����ڵ��ļ��� �£�
		File file=new File(SDCardRoot+File.separator+path);
		//�����ļ����µ������ļ�����һ��File�����еȴ�����ɸѡ
		File[] files=file.listFiles();
		FileUtils fileUtils=new FileUtils();
		if (files==null)
			System.out.println("here!");
		for(int i=0;i<files.length;i++)
		{
			if(files[i].getName().endsWith("mp3"));
			{
				//�����MP3�ļ��ͷŽ�һ��List<Mp3Info>�еȴ�����
				Mp3Info mp3Info=new Mp3Info();
				mp3Info.setMp3Name(files[i].getName());
				mp3Info.setMp3Size(files[i].length()+"");
				String temp[]=mp3Info.getMp3Name().split("\\.");
				String eLrcName=temp[0]+".lrc";
				if(fileUtils.isFileExist(eLrcName, "/mp3"))
				{
					mp3Info.setLrcName(eLrcName);
				}
				mp3infos.add(mp3Info);
			}
		}
		return mp3infos;
	}
}
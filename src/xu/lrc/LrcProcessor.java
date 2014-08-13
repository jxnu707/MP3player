package xu.lrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcProcessor 
{
	//返回一个ArrayList 包含handler要用到的两个队列
	public ArrayList<Queue> process(InputStream inputstream) 
	{
		System.out.println("int process!");
		ArrayList<Queue> Queues=new ArrayList<Queue> ();//存放时间点队列timeMills 和 该时间点对应的歌词内容队列messages
		Queue<Long> timeMills=new LinkedList<Long>();
		Queue<String> messages=new LinkedList<String>();
		//解析inputstream内容
		InputStreamReader inputReader = new InputStreamReader(inputstream);
		BufferedReader br = new BufferedReader(inputReader);
		String temp=null;
		int i=0;//循环次数
		Pattern p=Pattern.compile("\\[([^\\]]+)\\]");//一个用于匹配[00:00.00]时间格式的正则表达式
		String result=null;//存放每次扫描一行得到的歌词内容信息
		boolean b=true;
		
		try {
			while((temp=br.readLine())!=null)
			{
				i++;
				Matcher m=p.matcher(temp);//根据P这个正则模式 得到一个与之对应的匹配器M
				
				if(m.find())//如果在temp中匹配上了
				{
					if(result!=null)
						messages.add(result);
					
					
						String timeStr=m.group();//提取所匹配到的时间内容
						try{
							Long timemill=time2long(timeStr.substring(1,timeStr.length()-1));//去掉timeStr两端的空格 并转化为Long型的毫秒数
							if(b)
							{
								timeMills.offer(timemill);
							}	
						}
						catch(NumberFormatException e){
							e.printStackTrace();
							continue;
						}
						
					
					String msg=temp.substring(10);//从temp的第10个位置上开始提取歌词内容
					result=""+msg+"\n";
				}
				else
					//没有匹配到 就说明全部是歌词内容
					result=result+temp+"\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		messages.add(result);
		Queues.add(timeMills);
		Queues.add(messages);
		
//		System.out.println("LrcProcessor.timeMills--->"+timeMills.toString());
//		System.out.println("LrcProcessor.messages---->"+messages.toString());
//		System.out.println("LrcProcessor.i---->"+i);
		
		return Queues;
	}
	
	//时间格式 转化为毫秒数
	public Long time2long(String timeStr)
	{
		String s[] = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		String ss[] = s[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		return min * 60 * 1000 + sec * 1000 + mill * 10L;
	}
}

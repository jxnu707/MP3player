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
	//����һ��ArrayList ����handlerҪ�õ�����������
	public ArrayList<Queue> process(InputStream inputstream) 
	{
		System.out.println("int process!");
		ArrayList<Queue> Queues=new ArrayList<Queue> ();//���ʱ������timeMills �� ��ʱ����Ӧ�ĸ�����ݶ���messages
		Queue<Long> timeMills=new LinkedList<Long>();
		Queue<String> messages=new LinkedList<String>();
		//����inputstream����
		InputStreamReader inputReader = new InputStreamReader(inputstream);
		BufferedReader br = new BufferedReader(inputReader);
		String temp=null;
		int i=0;//ѭ������
		Pattern p=Pattern.compile("\\[([^\\]]+)\\]");//һ������ƥ��[00:00.00]ʱ���ʽ��������ʽ
		String result=null;//���ÿ��ɨ��һ�еõ��ĸ��������Ϣ
		boolean b=true;
		
		try {
			while((temp=br.readLine())!=null)
			{
				i++;
				Matcher m=p.matcher(temp);//����P�������ģʽ �õ�һ����֮��Ӧ��ƥ����M
				
				if(m.find())//�����temp��ƥ������
				{
					if(result!=null)
						messages.add(result);
					
					
						String timeStr=m.group();//��ȡ��ƥ�䵽��ʱ������
						try{
							Long timemill=time2long(timeStr.substring(1,timeStr.length()-1));//ȥ��timeStr���˵Ŀո� ��ת��ΪLong�͵ĺ�����
							if(b)
							{
								timeMills.offer(timemill);
							}	
						}
						catch(NumberFormatException e){
							e.printStackTrace();
							continue;
						}
						
					
					String msg=temp.substring(10);//��temp�ĵ�10��λ���Ͽ�ʼ��ȡ�������
					result=""+msg+"\n";
				}
				else
					//û��ƥ�䵽 ��˵��ȫ���Ǹ������
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
	
	//ʱ���ʽ ת��Ϊ������
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

package xu.model;

import java.io.Serializable;

public class Mp3Info implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String mp3Name;
	private String mp3Size;
	private String lrcName;
	private String lrcSize;
	private int duration;
	private String url;
	private String arist;

	public String getArist() {
		return arist;
	}

	public void setArist(String arist) {
		this.arist = arist;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Mp3Info() {
		super();
	}

	@Override
	public String toString() {
		return "Mp3Info [id=" + id + ", mp3Name=" + mp3Name + ",Duration=" + duration
				+ ", Arist="+arist+", LrcName="+lrcName+"]";
	}

	public String getMp3Name() {
		return mp3Name;
	}

	public Mp3Info(String id, String mp3Name, String mp3Size, String lrcName,
			String lrcSize) {
		super();
		this.id = id;
		this.mp3Name = mp3Name;
		this.mp3Size = mp3Size;
		this.lrcName = lrcName;
		this.lrcSize = lrcSize;
	}

	public void setMp3Name(String mp3Name) {
		this.mp3Name = mp3Name.replace(".mp3", "");
	}

	public String getMp3Size() {
		return mp3Size;
	}

	public void setMp3Size(String mp3Size) {
		this.mp3Size = mp3Size;
	}

	public String getLrcName() {
		return lrcName;
	}


	public void setLrcName(String str) {
		this.lrcName = str;
	}
	
	public String getLrcSize() {
		return lrcSize;
	}

	public void setLrcSize(String lrcSize) {
		this.lrcSize = lrcSize;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	

}

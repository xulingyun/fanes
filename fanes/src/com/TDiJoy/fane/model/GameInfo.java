package com.TDiJoy.fane.model;

import java.io.Serializable;

public class GameInfo extends CellData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int uuid;
	public String name;
	public String procname;
	public int ctrltype;
	public int category;
	public String version;
	public int versioncode;
	public int size;
	public String price;
	public String language;
	public int age;
	public String icon_square;
	public String icon_rectangle;
	public String icon_poster;
	public String preview;
	//”Œœ∑‘§¿¿ΩÿÕº ˝¡ø
	public int preview_number;
	public String config;
	public String desc;
	
	public boolean isFlingEnable() {
		return (this.ctrltype & 1) == 1;
	}
}

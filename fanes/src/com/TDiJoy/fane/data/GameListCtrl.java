package com.TDiJoy.fane.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.SparseArray;

import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.request.FARequestManager;
import com.TDiJoy.fane.util.Constants;

//download and parse applisk.xml
public final class GameListCtrl implements Constants{

	private static String 			_TAG = "GameListCtrl";
	private static String 			_GameListUrl = "http://download.3dijoystore.com:3280/tdijoyfane/applist_new.xml";
	private static String 			_appListName = "applist.xml";
	private static String 			_ApkType = "application/vnd.android.package-archive";

	private static String			_VersionFileName = "version";
	
	private String					_DataPath = "";
	private Context 				_Context;
	
	private List<ApplicationInfo> 	_applicationInfos;

	private List<GameListInfo>		_GameListInfos;
	private SelfUpdateInfo 			_SelfUpdateInfo; 
	private AdInfo					_AdInfo;
	private boolean 				_GameListInfosEnable = false;
	private List<Integer>			_GameListPopular;
	private List<Integer>			_GameListRecommended;
	private List<Integer>			_GameListHome;
	private List<Integer>			_GameListIptvGame;
	private List<Integer>			_GameListInstalled;
	
	private HttpDownLoad			_ApkDownloader;
	private boolean					_ApkDownloading = false;
	private int						_ApkDownloadState = 0;	//0 - idle; 1 - downloading; 2 - download success; -1 - download fail
	
	static {
		// mark ---gameList地址区分---
		if (PLAT_TYPE == VER_HUI_TONG) {
			_GameListUrl = "http://download.3dijoystore.com:3280/tdijoyfane/applist_ht.xml";
		}
		else if (PLAT_TYPE == VER_KBL) {
			_GameListUrl = "http://download.3dijoystore.com:3280/tdijoyfane/applist_kbl.xml";
		}
		else if (PLAT_TYPE == VER_JX_1) {
			_GameListUrl = "http://download.3dijoystore.com:3280/tdijoyfane/applist_c1.xml";
		}
		else if (PLAT_TYPE == VER_ALL_U) {
			_GameListUrl = "http://download.3dijoystore.com:3280/tdijoyfane/applist-u_new.xml";
		}
		else if (PLAT_TYPE == VER_LETV) {
//			_GameListUrl = "http://download.3dijoystore.com:3280/tdijoyfane/applist-u_letv.xml";
//			_GameListUrl = "http://ott.winside.cn/tdijoyfane/applist-u_letv.xml";

//			外网服务器停用
//			_GameListUrl = "http://211.144.119.65:8543/ottservice4client/game_getData.action";
			
//			_GameListUrl = "http://183.57.44.130:8543/ottservice4client/game_getData.action";
			_GameListUrl = "http://183.57.44.130:8543/ottservice4client/applist.xml";

		}
	}
	
	public GameListCtrl (Context context)
	{
		_Context = context;
		_DataPath = GetDataDir ();
		if (_ApkDownloader == null)
			_ApkDownloader = new HttpDownLoad ();
	}
	
	//获取gamelist所有游戏数，包括已安装和未安装
	public int GetGameNumber ()
	{
		if (!_GameListInfosEnable) {return 0;}
		return _GameListInfos.size();
	}
	
	//通过游戏类型获取游戏列表
/*	category: 
	1 - 角色扮演
	2 - 动作冒险
	3 - 休闲益智
	4 - 策略塔防
	5 - 经营养成
	6 - 赛车竞速
	7 - 体育运动
	8 - 飞行射击
	9 - 桌面棋牌
	10- 网络游戏
	11- 其它类型
*/
	public List<GameInfo> GetGamelist_Category (int category)
	{
		if (!_GameListInfosEnable) {return null;}

		List<GameInfo> 	gamelist_uuid;
		gamelist_uuid = new ArrayList<GameInfo>();  
		for(int i=0; i<_GameListInfos.size(); i++)
		{
			if(_GameListInfos.get(i).category == category) 
			{
				GameInfo		ifo;
				ifo = new GameInfo(); 
				 
				ifo.uuid = _GameListInfos.get(i).uid;
				ifo.name = _GameListInfos.get(i).name;
				ifo.procname = _GameListInfos.get(i).procname;
				ifo.ctrltype = _GameListInfos.get(i).ctrltype;
				ifo.category = _GameListInfos.get(i).category;
				ifo.version = _GameListInfos.get(i).version;
				ifo.versioncode = _GameListInfos.get(i).versioncode;
				ifo.size = _GameListInfos.get(i).size;
				ifo.price = _GameListInfos.get(i).price;
				ifo.language = _GameListInfos.get(i).language;
				ifo.age = _GameListInfos.get(i).age;
				 
				String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				
				ifo.icon_square = FileName_square;
				ifo.icon_rectangle = FileName_rect;
				ifo.icon_poster = FileName_poster;
				ifo.preview = FileName_preview;
				ifo.preview_number = _GameListInfos.get(i).dlpv_num;
				
				ifo.config = FileName_config;
				ifo.desc = FileName_desc;
				 
				gamelist_uuid.add(ifo);  
				ifo=null;  				 
			}
		}		 
		return gamelist_uuid;
	}
	
	//获取游戏类型列表，没有游戏的不放入
	public List<Integer> GetGameTypeList() {
		SparseArray<Boolean> mark = new SparseArray<Boolean>();
		for (int i = 0; i<_GameListInfos.size(); i++) {
			mark.put(_GameListInfos.get(i).category, true);
		}
		List<Integer> types = new ArrayList<Integer>();
		for (int i = 1; i <= 11; i++) {
			if (mark.get(i, false))
				types.add(i);
		}
		return types;
	}
	
	//获取一周热门游戏列表
	public List<GameInfo> GetGamelist_Popular ()
	{
		if (!_GameListInfosEnable) {return null;}
		
		List<GameInfo> 	gamelist_uuid;
		gamelist_uuid = new ArrayList<GameInfo>();  		
		
		for(int i=0; i<_GameListPopular.size(); i++)
		{
			int id = UuidToIndex(_GameListPopular.get(i));
			if (id == -1) {continue;}

			GameInfo		ifo;
			ifo = new GameInfo(); 
			 
			ifo.uuid = _GameListInfos.get(id).uid;
			ifo.name = _GameListInfos.get(id).name;
			ifo.procname = _GameListInfos.get(id).procname;
			ifo.ctrltype = _GameListInfos.get(id).ctrltype;
			ifo.category = _GameListInfos.get(id).category;
			ifo.version = _GameListInfos.get(id).version;
			ifo.versioncode = _GameListInfos.get(id).versioncode;
			ifo.size = _GameListInfos.get(id).size;
			ifo.price = _GameListInfos.get(id).price;
			ifo.language = _GameListInfos.get(id).language;
			ifo.age = _GameListInfos.get(id).age;
			 
			String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			
			ifo.icon_square = FileName_square;
			ifo.icon_rectangle = FileName_rect;
			ifo.icon_poster = FileName_poster;
			ifo.preview = FileName_preview;
			ifo.preview_number = _GameListInfos.get(id).dlpv_num;
			
			ifo.config = FileName_config;
			ifo.desc = FileName_desc;

			gamelist_uuid.add(ifo);  
			ifo=null;  						
		}
				
		return gamelist_uuid;
	}
	
	//获取精品推荐游戏列表
	public List<GameInfo> GetGamelist_Recommended ()
	{
		if (!_GameListInfosEnable) {return null;}
				
		List<GameInfo> 	gamelist_uuid;
		gamelist_uuid = new ArrayList<GameInfo>();  		
		
		for(int i=0; i<_GameListRecommended.size(); i++)
		{
			int id = UuidToIndex(_GameListRecommended.get(i));
			if (id == -1) {continue;}

			GameInfo		ifo;
			ifo = new GameInfo(); 
			 
			ifo.uuid = _GameListInfos.get(id).uid;
			ifo.name = _GameListInfos.get(id).name;
			ifo.procname = _GameListInfos.get(id).procname;
			ifo.ctrltype = _GameListInfos.get(id).ctrltype;
			ifo.category = _GameListInfos.get(id).category;
			ifo.version = _GameListInfos.get(id).version;
			ifo.versioncode = _GameListInfos.get(id).versioncode;
			ifo.size = _GameListInfos.get(id).size;
			ifo.price = _GameListInfos.get(id).price;
			ifo.language = _GameListInfos.get(id).language;
			ifo.age = _GameListInfos.get(id).age;
			 
			String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			
			ifo.icon_square = FileName_square;
			ifo.icon_rectangle = FileName_rect;
			ifo.icon_poster = FileName_poster;
			ifo.preview = FileName_preview;
			ifo.preview_number = _GameListInfos.get(id).dlpv_num;
			
			ifo.config = FileName_config;
			ifo.desc = FileName_desc;
			 
			gamelist_uuid.add(ifo);  
			ifo=null;  						
		}
				
		return gamelist_uuid;
	}
	
	//获取已安装游戏列表
	public List<GameInfo> GetGamelist_Installed ()
	{
		if (!_GameListInfosEnable) {return null;}

		if (_applicationInfos == null) {UpdateInstalledAppList ();}
		
		List<GameInfo> 	gamelist_uuid;
		gamelist_uuid = new ArrayList<GameInfo>();  
		
//		if (_GameListInstalled == null)
//		{
//			_GameListInstalled = new ArrayList<Integer>();  
			for(int i=0; i<_GameListInfos.size(); i++)
			{
				for(int j = 0; j < _applicationInfos.size(); j++)
				{
					if(_applicationInfos.get(j).processName.equals(_GameListInfos.get(i).procname))
					{
						GameInfo		ifo;
						ifo = new GameInfo(); 
						 
						ifo.uuid = _GameListInfos.get(i).uid;
						ifo.name = _GameListInfos.get(i).name;
						ifo.procname = _GameListInfos.get(i).procname;
						ifo.ctrltype = _GameListInfos.get(i).ctrltype;
						ifo.category = _GameListInfos.get(i).category;
						ifo.version = _GameListInfos.get(i).version;
						ifo.versioncode = _GameListInfos.get(i).versioncode;
						ifo.size = _GameListInfos.get(i).size;
						ifo.price = _GameListInfos.get(i).price;
						ifo.language = _GameListInfos.get(i).language;
						ifo.age = _GameListInfos.get(i).age;
						 
						String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
						String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
						String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
						String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
						String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
						String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
						
						ifo.icon_square = FileName_square;
						ifo.icon_rectangle = FileName_rect;
						ifo.icon_poster = FileName_poster;
						ifo.preview = FileName_preview;
						ifo.preview_number = _GameListInfos.get(i).dlpv_num;
						
						ifo.config = FileName_config;
						ifo.desc = FileName_desc;
						
						gamelist_uuid.add(ifo);  
						ifo=null;  								
					}
				}		
//			}				
		}
		return gamelist_uuid;
	}
	
	//获取猜你喜欢游戏列表
	public List<GameInfo> GetGamelist_GuessYouLike (int uuid)
	{
		if (!_GameListInfosEnable) {return null;}
				
		List<GameInfo> 	gamelist_uuid;
		gamelist_uuid = new ArrayList<GameInfo>();  		
				
		int cct = GetGameCategory(uuid);
		int listcount = 0;
		
		//获取同类型游戏
		for(int i=0; i<_GameListInfos.size(); i++)
		{
			if(_GameListInfos.get(i).category == cct) 
			{
				if (_GameListInfos.get(i).uid == uuid) {continue;}
				
				GameInfo		ifo;
				ifo = new GameInfo(); 
				 
				ifo.uuid = _GameListInfos.get(i).uid;
				ifo.name = _GameListInfos.get(i).name;
				ifo.procname = _GameListInfos.get(i).procname;
				ifo.ctrltype = _GameListInfos.get(i).ctrltype;
				ifo.category = _GameListInfos.get(i).category;
				ifo.version = _GameListInfos.get(i).version;
				ifo.versioncode = _GameListInfos.get(i).versioncode;
				ifo.size = _GameListInfos.get(i).size;
				ifo.price = _GameListInfos.get(i).price;
				ifo.language = _GameListInfos.get(i).language;
				ifo.age = _GameListInfos.get(i).age;
				 
				String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				
				ifo.icon_square = FileName_square;
				ifo.icon_rectangle = FileName_rect;
				ifo.icon_poster = FileName_poster;
				ifo.preview = FileName_preview;
				ifo.preview_number = _GameListInfos.get(i).dlpv_num;
				
				ifo.config = FileName_config;
				ifo.desc = FileName_desc;
				
				gamelist_uuid.add(ifo);  
				ifo=null;  				 

				listcount ++;
				if (listcount == 7) {break;}
			}
		}		 
		
		//若不足7个游戏,补足
		if (listcount < 7)
		{
			int rid = 0;
			if (uuid < 10) 	{rid = uuid;}
			else 			{rid = uuid % 10;}
			
			for (int i=listcount; i<7; i++)
			{
				if (rid >= _GameListRecommended.size()) {rid = 0;}
	
				int id = UuidToIndex(_GameListRecommended.get(rid));
				rid ++;
				if (id == -1) {continue;}

				GameInfo		ifo;
				ifo = new GameInfo(); 
				 
				ifo.uuid = _GameListInfos.get(id).uid;
				ifo.name = _GameListInfos.get(id).name;
				ifo.procname = _GameListInfos.get(id).procname;
				ifo.ctrltype = _GameListInfos.get(id).ctrltype;
				ifo.category = _GameListInfos.get(id).category;
				ifo.version = _GameListInfos.get(id).version;
				ifo.versioncode = _GameListInfos.get(id).versioncode;
				ifo.size = _GameListInfos.get(id).size;
				ifo.price = _GameListInfos.get(id).price;
				ifo.language = _GameListInfos.get(id).language;
				ifo.age = _GameListInfos.get(id).age;
				 
				String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				
				ifo.icon_square = FileName_square;
				ifo.icon_rectangle = FileName_rect;
				ifo.icon_poster = FileName_poster;
				ifo.preview = FileName_preview;
				ifo.preview_number = _GameListInfos.get(id).dlpv_num;
				
				ifo.config = FileName_config;
				ifo.desc = FileName_desc;
				 
				gamelist_uuid.add(ifo);  
				ifo=null;				
			}
		}
		
		return gamelist_uuid;	
	}
		
	//获取首页游戏列表
	public List<GameInfo> GetGamelist_Home ()
	{
		if (!_GameListInfosEnable) {return null;}
				
		List<GameInfo> 	gamelist_uuid;
		gamelist_uuid = new ArrayList<GameInfo>();  		
				
		for(int i=0; i<_GameListHome.size(); i++)
		{
			int id = UuidToIndex(_GameListHome.get(i));
			if (id == -1) {continue;}

			GameInfo		ifo;
			ifo = new GameInfo(); 
			 
			ifo.uuid = _GameListInfos.get(id).uid;
			ifo.name = _GameListInfos.get(id).name;
			ifo.procname = _GameListInfos.get(id).procname;
			ifo.ctrltype = _GameListInfos.get(id).ctrltype;
			ifo.category = _GameListInfos.get(id).category;
			ifo.version = _GameListInfos.get(id).version;
			ifo.versioncode = _GameListInfos.get(id).versioncode;
			ifo.size = _GameListInfos.get(id).size;
			ifo.price = _GameListInfos.get(id).price;
			ifo.language = _GameListInfos.get(id).language;
			ifo.age = _GameListInfos.get(id).age;
			 
			String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
			
			ifo.icon_square = FileName_square;
			ifo.icon_rectangle = FileName_rect;
			ifo.icon_poster = FileName_poster;
			ifo.preview = FileName_preview;
			ifo.preview_number = _GameListInfos.get(id).dlpv_num;
			
			ifo.config = FileName_config;
			ifo.desc = FileName_desc;
			 
			gamelist_uuid.add(ifo);  
			ifo=null;  						
		}		
		
		return gamelist_uuid;	
	}	
	
	
		//获取IPTV游戏列表
		public List<GameInfo> GetGamelist_IptvGame ()
		{
			if (!_GameListInfosEnable) {return null;}
					
			List<GameInfo> 	gamelist_uuid;
			gamelist_uuid = new ArrayList<GameInfo>();  		
					
			for(int i=0; i<_GameListIptvGame.size(); i++)
			{
				int id = UuidToIndex(_GameListIptvGame.get(i));
				if (id == -1) {continue;}

				GameInfo		ifo;
				ifo = new GameInfo(); 
				 
				ifo.uuid = _GameListInfos.get(id).uid;
				ifo.name = _GameListInfos.get(id).name;
				ifo.procname = _GameListInfos.get(id).procname;
				ifo.ctrltype = _GameListInfos.get(id).ctrltype;
				ifo.category = _GameListInfos.get(id).category;
				ifo.version = _GameListInfos.get(id).version;
				ifo.versioncode = _GameListInfos.get(id).versioncode;
				ifo.size = _GameListInfos.get(id).size;
				ifo.price = _GameListInfos.get(id).price;
				ifo.language = _GameListInfos.get(id).language;
				ifo.age = _GameListInfos.get(id).age;
				 
				String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(id).directory, _GameListInfos.get(id).directory);
				
				ifo.icon_square = FileName_square;
				ifo.icon_rectangle = FileName_rect;
				ifo.icon_poster = FileName_poster;
				ifo.preview = FileName_preview;
				ifo.preview_number = _GameListInfos.get(id).dlpv_num;
				
				ifo.config = FileName_config;
				ifo.desc = FileName_desc;
				 
				gamelist_uuid.add(ifo);  
				ifo=null;  						
			}		
			
			return gamelist_uuid;	
		}	
	
	
	
	
	
	public GameInfo GetGameInfo_ProcessName (String procname)
	{
		if (!_GameListInfosEnable) {return null;}
		
		for(int i=0; i<_GameListInfos.size(); i++)
		{
			if(_GameListInfos.get(i).procname.equals(procname))
			{
				GameInfo		ifo;
				ifo = new GameInfo(); 
					 
				ifo.uuid = _GameListInfos.get(i).uid;
				ifo.name = _GameListInfos.get(i).name;
				ifo.procname = _GameListInfos.get(i).procname;
				ifo.ctrltype = _GameListInfos.get(i).ctrltype;
				ifo.category = _GameListInfos.get(i).category;
				ifo.version = _GameListInfos.get(i).version;
				ifo.versioncode = _GameListInfos.get(i).versioncode;
				ifo.size = _GameListInfos.get(i).size;
				ifo.price = _GameListInfos.get(i).price;
				ifo.language = _GameListInfos.get(i).language;
				ifo.age = _GameListInfos.get(i).age;
					 
				String FileName_square = String.format ("%s/%s/%s_1.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_rect = String.format ("%s/%s/%s_2.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_poster = String.format ("%s/%s/%s_3.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_preview = String.format ("%s/%s/%spre.png", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_config = String.format ("%s/%s/%s.cfg", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
				String FileName_desc = String.format ("%s/%s/%s.txt", _DataPath, _GameListInfos.get(i).directory, _GameListInfos.get(i).directory);
					
				ifo.icon_square = FileName_square;
				ifo.icon_rectangle = FileName_rect;
				ifo.icon_poster = FileName_poster;
				ifo.preview = FileName_preview;
				ifo.preview_number = _GameListInfos.get(i).dlpv_num;
					
				ifo.config = FileName_config;
				ifo.desc = FileName_desc;
					
				return ifo;  								
			}
		}		
		return null;
	}

	
//////////////////////
/*	
	//获取游戏uuid（唯一识别码）
	public int GetGameUid (int index)
	{
		if (!_GameListInfosEnable) {return -1;}
		if (index >= _GameListInfos.size()) {return -1;}
		return _GameListInfos.get(index).uid;		
	}
*/	
	//获取游戏控制方式
	public int GetGameCtrltype (int uuid)
	{
		if (!_GameListInfosEnable) {return -1;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return -1;}
		return _GameListInfos.get(index).ctrltype;			
	}
	
	//获取游戏类型
	public int GetGameCategory (int uuid)
	{
		if (!_GameListInfosEnable) {return -1;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return -1;}
		return _GameListInfos.get(index).category;			
	}
	
	//获取游戏name
	public String GetGameName (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		return _GameListInfos.get(index).name;
	}
	
	//获取游戏process name
	public String GetGameProcessName (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		return _GameListInfos.get(index).procname;
	}
	
	//获取游戏version
	public String GetGameVersion (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		return _GameListInfos.get(index).version;
	}
	
	//获取游戏version code
	public int GetGameVersioncode (int uuid)
	{
		if (!_GameListInfosEnable) {return -1;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return -1;}
		return _GameListInfos.get(index).versioncode;			
	}
	
	//获取游戏安装包大小
	public int GetGameApkSize (int uuid)
	{
		if (!_GameListInfosEnable) {return 0;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return 0;}
		return _GameListInfos.get(index).size;			
	}
	
	//获取游戏购买价格
	public String GetGamePrice (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		return _GameListInfos.get(index).price;			
	}
	
	//获取游戏语言
	public String GetGameLanguage (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		return _GameListInfos.get(index).language;			
	}
	
	//获取游戏适合年龄
	public int GetGameAge (int uuid)
	{
		if (!_GameListInfosEnable) {return 0;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return 0;}
		return _GameListInfos.get(index).age;			
	}
		
	//获取游戏图标文件路径，3种尺寸可选，通过type选择，若文件不存在，返回文件应该所在的位置，并开始在后台下载
	//type: 1 -- 3
	public String GetGameIcon (int uuid, int type)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		if ((type > 3) || (type < 1)) {return "";}
		if (_GameListInfos.get(index).directory == "") {return "";}
		if (_GameListInfos.get(index).dlres == "") {return "";}
		
		String Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		String FileName = String.format ("%s_%d.png", _GameListInfos.get(index).directory, type);
		System.out.println("获取图片路径 " +FileName);
		if (!IsFileExist(Localpath, FileName)) 
		{
	        Thread thread = new Thread(new CDownloadFiles(_GameListInfos.get(index).dlres + FileName, FileName, Localpath));
	        thread.start();
	    }
		return Localpath + FileName;
	}
	
	//获取游戏预览图文件路径，最多支持6张预览图，通过id选择，若文件不存在，返回空
	//id: 1 -- 6 
	public String GetGamePreview (int uuid, int id)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		if ((id > 6) || (id < 1)) {return "";}
		if (_GameListInfos.get(index).directory == "") {return "";}
		if (_GameListInfos.get(index).dlres == "") {return "";}
		
		String Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		String FileName = String.format ("%spre_%d.png", _GameListInfos.get(index).directory, id);

//		if (!IsFileExist(Localpath, FileName)) 
//		{
//	        Thread thread = new Thread(new CDownloadFiles(_GameListInfos.get(index).dlres + FileName, FileName, Localpath));
//	        thread.start();
//	    }
		return Localpath + FileName;
	}	
	
	//获取游戏配置文件路径，若文件不存在，返回空（只有第三方游戏才有配置文件）
	public String GetGameConfig (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		if (_GameListInfos.get(index).directory == "") {return "";}
		if (_GameListInfos.get(index).dlres == "") {return "";}
		
		String Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		String FileName = String.format ("%s.cfg", _GameListInfos.get(index).directory);
		
		if (IsFileExist(Localpath, FileName)) {return Localpath + FileName;}
		
		return "";
	}		
	
	//获取游戏描述详情文件路径，若文件不存在，返回空
	public String GetGameDescribe (int uuid)
	{
		if (!_GameListInfosEnable) {return "";}
		int index = UuidToIndex (uuid);
		if (index < 0) {return "";}
		if (_GameListInfos.get(index).directory == "") {return "";}
		if (_GameListInfos.get(index).dlres == "") {return "";}
		
		String Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		String FileName = String.format ("%s.txt", _GameListInfos.get(index).directory);
		
		if (IsFileExist(Localpath, FileName)) {return Localpath + FileName;}
		
		return "";
	}	
	
	//检查游戏是否已安装
	public boolean GetGameInstallCheck (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return false;}
		if (_GameListInfos.get(index).procname == "") {return false;}
		
		if (_applicationInfos == null) {UpdateInstalledAppList ();}
		System.out.println("选中的游戏的class名  = " +_GameListInfos.get(index).procname);
		
		for(int i = 0; i < _applicationInfos.size(); i++)
		{
			Log.e("3DiJoy", _applicationInfos.get(i).processName);			
			if(_applicationInfos.get(i).processName.equals(_GameListInfos.get(index).procname))
			{
				Log.e("3DiJoy", "Java: [installed]");
				return true;
			}
		}
		
		return false;
	}	
	
	public boolean GetGameInstallCheck (String proc)
	{
		if (!_GameListInfosEnable) {return false;}
		if (_applicationInfos == null) {UpdateInstalledAppList ();}
		
		for(int i = 0; i < _applicationInfos.size(); i++)
		{
			//Log.e("3DiJoy", applicationInfos.get(i).processName);			
			if(_applicationInfos.get(i).processName.equals(proc))
			{
				//Log.e("3DiJoy", "Java: [installed]");
				return true;
			}
		}		
		return false;
	}


	//检查游戏是否需要更新
	public boolean GetGameUpdateCheck (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return false;}
		if (_GameListInfos.get(index).procname == "") {return false;}		
		
    	String 	appVerName;
    	int 	appVercode;
    	try {
    		PackageInfo info = _Context.getPackageManager().getPackageInfo(_GameListInfos.get(index).procname, PackageManager.GET_ACTIVITIES);
    		appVerName = info.versionName;
    		appVercode = info.versionCode;
    	} 
    	catch (NameNotFoundException e) {e.printStackTrace(); return false;}	
		
		if (_GameListInfos.get(index).versioncode != 0)
		{
			return (_GameListInfos.get(index).versioncode > appVercode);
		}
		return UpdateJudgement (appVerName, _GameListInfos.get(index).version);
	}

	public int GetRemotePrice ()
	{
		if (!_GameListInfosEnable) {return 299;}
		return _AdInfo.price;
	}
	
	//获取广告图片位置，用于视频下载完成之前显示。若图片不存在，返回文件应该所在的位置，并开始在后台下载
	//type: 1 - 首页视频预览图； 2 - 手柄推荐面片； 3 - 手柄推荐视频预览图
	public String GetAdImage(int type)
	{
		if (!_GameListInfosEnable) {return "";}
		
		String Localpath = _DataPath + "/_ad/";
		String fName;
		String dlurl;
		switch (type)
		{
		case 1:
			{
				if (_AdInfo.image_a == "") {return "";}
				fName = _AdInfo.image_a;
				break;
			}
		case 2:
			{
				if (_AdInfo.image_b == "") {return "";}
				fName = _AdInfo.image_b;
				break;
			}
		case 3:
			{
				if (_AdInfo.image_c == "") {return "";}
				fName = _AdInfo.image_c;
				break;
			}
		default:
			return "";
		}
		
		dlurl = fName;
		fName = fName.trim();
		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);
		
		//若文件不存在，在后台开始下载
		if (!IsFileExist(Localpath, FileName)) 
		{
	        Thread thread = new Thread(new CDownloadFiles(dlurl, FileName, Localpath));
	        thread.start();
	    }
		return Localpath + FileName;		
	}
	
	//获取广告视频位置。若视频文件不存在，返回文件应该所在的位置，并开始在后台下载
	//type: 1 - 首页视频； 2 - 手柄推荐视频
	public String GetAdVideo(int type)
	{
		if (!_GameListInfosEnable) {return "";}
		
		String Localpath = _DataPath + "/_ad/";
		String fName;
		String dlurl;
		switch (type)
		{
		case 1:
			{
				if (_AdInfo.video_a == "") {return "";}
				fName = _AdInfo.video_a;
				break;
			}
		case 2:
			{
				if (_AdInfo.video_b == "") {return "";}
				fName = _AdInfo.video_b;
				break;
			}
		default:
			return "";
		}
		
		dlurl = fName;
		fName = fName.trim();
		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);
		
		//若文件不存在，在后台开始下载
		if (!IsFileExist(Localpath, FileName)) 
		{
	        Thread thread = new Thread(new CDownloadFiles(dlurl, FileName, Localpath));
	        thread.start();
	    }
		return Localpath + FileName;		
	}
	
	//获取大厅自我更新状态
	// 0 - 未检查更新;  1 - 下载中;  2 - 已下载完成，可以更新;  -1 - 不需要更新;
	public int GetSelfUpdateState ()
	{
		return _SelfUpdateInfo.updatestate;
	}
	
	public boolean InstallSelfUpdate ()
	{
		if (_SelfUpdateInfo.dlapk == "") {_SelfUpdateInfo.updatestate = -1; return false;}
		if (_SelfUpdateInfo.updatestate != 2) {return false;}
		
		String	fName = _SelfUpdateInfo.dlapk;
		fName = fName.trim();
		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);		

		//Log.i(_TAG, "self update: " + fName + " -- " + FileName);
		
		String 	apkpath = _DataPath + "/" + FileName;
		File apkfile = new File (apkpath);
		if (!apkfile.exists()) {return false;}		
		
		//Log.i(_TAG, "self update, file: " + apkpath);
		
		Intent	intent = new Intent ();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		intent.setDataAndType(Uri.fromFile(apkfile), _ApkType);
		_Context.startActivity(intent);
		return true;
	}
	
	//刷新已安装游戏列表(安装返回后调用)
	public void UpdateInstalledAppList ()
	{
		_applicationInfos =  _Context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
		System.out.println(_applicationInfos);
		_GameListInstalled = null;
	}	
	
	//检查资源文件更新，开机后只检查一次。 本函数阻塞，须等待所有资源文件下载完成才返回
	public boolean GameResourceUpdate (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return false;}
		if (_GameListInfos.get(index).dlres == "") {return false;}		
		if (_GameListInfos.get(index).directory == "") {return false;}		
		if (_GameListInfos.get(index).resChecked) {return true;}	//已经检查过更新		
		
		String 	Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		
		String	dlresverUrl = _GameListInfos.get(index).dlres + _VersionFileName;
		
		HttpDownLoad	downloader = new HttpDownLoad ();
		int res = downloader.DlFile(dlresverUrl, Localpath, "tmp_version", true);	
		
		if (IsFileExist (Localpath, _VersionFileName) && IsFileExist (Localpath, "tmp_version"))
		{
			if (IsFileTheSame (Localpath + "tmp_version", Localpath + _VersionFileName)) {return true;}
		}
		
		String 	cfgFileName = String.format ("%s.cfg", _GameListInfos.get(index).directory);
		String	cfgUrl = _GameListInfos.get(index).dlres + cfgFileName;
		res = downloader.DlFile(cfgUrl, Localpath, cfgFileName, true);	
		
		String 	txtFileName = String.format ("%s.txt", _GameListInfos.get(index).directory);
		String	txtUrl = _GameListInfos.get(index).dlres + txtFileName;
		res = downloader.DlFile(txtUrl, Localpath, txtFileName, true);	
		
		Thread thread = new Thread(new CDownloadRes(index, Localpath));
        thread.start();
		
		return true;
	}
	
//////////////////////
		
	//安装apk文件
	public boolean ApkInstall (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return false;}		
		if (_GameListInfos.get(index).directory == "") {return false;}
		if (_GameListInfos.get(index).dlapk == "") {return false;}
		
		String Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		
		String	fName = _GameListInfos.get(index).dlapk;
		fName = fName.trim();
		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);
		//Log.i ("ApkInstall", "filename: ==" + FileName + "==");
		
		String apkpath = Localpath + FileName;
		File apkfile = new File (apkpath);
		if (!apkfile.exists()) {return false;}

    	//String command = "chmod 777 " + apkpath;
    	//try {
    	//	Runtime.getRuntime().exec(command);
    	//} 
    	//catch (Exception e) {e.printStackTrace();}		
		
		Intent	intent = new Intent ();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		intent.setDataAndType(Uri.fromFile(apkfile), _ApkType);
		_Context.startActivity(intent);
			
		//Log.i(_TAG, "InstallApk Quit");
		return true;
	}
		
	//卸载apk
	public boolean ApkUninstall (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		String procname = GetGameProcessName (uuid);
		if (procname == "") {return false;}
		
		try
		{
    		ApplicationInfo info = _Context.getPackageManager().getApplicationInfo(procname, PackageManager.GET_META_DATA);
    		Uri packageURI = Uri.parse("package:" + info.processName);     
    		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);     
    		uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		_Context.startActivity(uninstallIntent);      		
    	}
    	catch(PackageManager.NameNotFoundException e) {return false;}
		return true;
	}
	
	//删除下载的apk文件
	public void ApkDeleteDlFile (int uuid)
	{
		if (!_GameListInfosEnable) {return;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return;}		
		if (_GameListInfos.get(index).directory == "") {return;}
		if (_GameListInfos.get(index).dlapk == "") {return;}
		
		String Localpath = _DataPath + "/" + _GameListInfos.get(index).directory + "/";
		
		String	fName = _GameListInfos.get(index).dlapk;
		fName = fName.trim();
		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);
		//Log.i ("ApkInstall", "filename: ==" + FileName + "==");
		
		String apkpath = Localpath + FileName;		
		
		File apkfile = new File (apkpath);		
		if (!apkfile.exists()) {return;}
		apkfile.delete();
	}	
	
	//启动游戏
	public boolean LaunchGameProg (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		if (!GetGameInstallCheck (uuid)) {return false;}
		String procname = GetGameProcessName (uuid);
		if (procname == "") {return false;}
    	try 
    	{
    		PackageInfo pInfo =  _Context.getPackageManager().getPackageInfo(procname, PackageManager.GET_ACTIVITIES);
			//ApplicationInfo info = GL2JNIActivity.pointer.getPackageManager().getApplicationInfo(ProcessName, PackageManager.GET_META_DATA);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pInfo.packageName);
            List<ResolveInfo> apps = _Context.getPackageManager().queryIntentActivities(resolveIntent, PackageManager.GET_GIDS);
            System.out.println("apps:" + apps.size());
            ResolveInfo ri = apps.iterator().next();

            if(ri != null) 
            {
                String packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                System.out.println("packageName:" + packageName);
                System.out.println("className:" + className);
                intent.setComponent(new ComponentName(packageName, className));
                intent.putExtra("KeyString", "FaneSendKey:0");
                //IPTV游戏传入userID
                intent.putExtra("userID", FARequestManager.sharedInstance().getAid());
                _Context.startActivity(intent);
            }
        } 
    	catch(Exception except) {except.printStackTrace(); return false;}
		return true;
	}				
	
//////////////////////

	//下载游戏apk文件，非阻塞
	public boolean DownloadApkStart (int uuid)
	{
		if (!_GameListInfosEnable) {return false;}
		if (_ApkDownloading) {return false;}
		int index = UuidToIndex (uuid);
		if (index < 0) {return false;}
		if (_GameListInfos.get(index).dlapk == "") {return false;}
		
        Thread thread = new Thread(new CDownloadApk(index));
        thread.start();				
		return true;
	}

	//取消下载游戏apk文件
	public void DownloadApkCancel ()
	{
		if (_ApkDownloader == null) {return;}
		_ApkDownloader.DlStop();	
	}
	
	//获取游戏apk下载进度
	public float DownloadApkGetProcess ()
	{
		if (_ApkDownloader == null) {Log.i("12345","32165489");return 1.f;}
		
//		Log.i("12345","===================: "+_ApkDownloader.DlGetProcess());
		return _ApkDownloader.DlGetProcess();
	}

	//获取游戏apk下载状态，判断是否完成
	//0 - 未下载; 1 - 下载中; 2 - 下载完成; -1 - 下载失败
	public int DownloadApkGetState ()
	{
		if (_ApkDownloader == null) {return 0;}
		return _ApkDownloadState;
	}

	/**
	 * 更新gamelist，大厅启动时调用，阻塞，须等待gamelist下载并解析完成
	 * return: 	-1 - 没有初始化
	 * 			-2 - 下载gamelist失败，本地也没有可用gamelist
	 * 			 1  - 更新 gamelist成功
	 *			 2  - 更新gamelist失败（下载失败），使用本地gamelist
	 */
	public int GameListUpdate ()
	{
		if (_DataPath == "") {return -1;}
		
		HttpDownLoad	downloader = new HttpDownLoad ();
		int res = downloader.DlFileXML(_GameListUrl, _DataPath, _appListName, true);
		
		Log.i(_TAG, "DlFile: " + res);
		
		String 		file = _DataPath + "/" + _appListName;
		boolean result = GameListParse (file);

		if (res == 0)
		{
			if (result) {Log.i(_TAG, "DoSelfUpdate: " + DownloadSelfUpdate());}
			return 1;
		}
		else
		{
			if (result) {return 2;}
		}

		return -2;
/*
		Log.i(_TAG, "=======update info=======");
		Log.i(_TAG, "version: " + _SelfUpdateInfo.version);
		Log.i(_TAG, "version code: " + _SelfUpdateInfo.versioncode);
		Log.i(_TAG, "download: " + _SelfUpdateInfo.dlapk);
		Log.i(_TAG, "=======end of update info=======");
		
		Log.i(_TAG, "=======ad info=======");
		Log.i(_TAG, "price: " + _AdInfo.price);
		Log.i(_TAG, "video: " + _AdInfo.video_a);
		Log.i(_TAG, "image: " + _AdInfo.image_a);
		Log.i(_TAG, "=======end of ad info=======");
				
		Log.i(_TAG, "gamelistinfo number: " + _GameListInfos.size());
		for (int i=0; i<_GameListInfos.size(); i++)
		{
			Log.i(_TAG, "==============" + _GameListInfos.get(i).arrayid + "==============");
			Log.i(_TAG, "uid: " + _GameListInfos.get(i).uid);
			Log.i(_TAG, "ctrltype: " + _GameListInfos.get(i).ctrltype);
			Log.i(_TAG, "category: " + _GameListInfos.get(i).category);
			Log.i(_TAG, "name: " + _GameListInfos.get(i).name);
			Log.i(_TAG, "directory: " + _GameListInfos.get(i).directory);
			Log.i(_TAG, "procname: " + _GameListInfos.get(i).procname);
			Log.i(_TAG, "version: " + _GameListInfos.get(i).version);
			Log.i(_TAG, "version code: " + _GameListInfos.get(i).versioncode);
			Log.i(_TAG, "apk: " + _GameListInfos.get(i).dlapk);
			Log.i(_TAG, "res: " + _GameListInfos.get(i).dlres);
			Log.i(_TAG, "=============================");
		}	
*/		
		//Log.i(_TAG, "gamelistinfo update finish");
		
		//return result;
	}

////////////////////////////
	
	private int UuidToIndex (int Uuid)
	{		
		for(int i = 0; i < _GameListInfos.size(); i++)
		{
			if(_GameListInfos.get(i).uid == Uuid) {return i;}
		}
		return -1;
	}
	
	//get data directory
	private String GetDataDir ()
	{
		boolean ExtEnable = false;
		
        String state = Environment.getExternalStorageState();  
        if(Environment.MEDIA_MOUNTED.equals(state)) {  
            File sdcardDir = Environment.getExternalStorageDirectory();  
            StatFs sdsf = new StatFs(sdcardDir.getPath());  
            if (sdsf.getBlockCount() != 0) 
            {
            	long BlockSize = sdsf.getBlockSize();
            	long AvailableBlocks = sdsf.getAvailableBlocks();
                long external_availablesize = BlockSize * AvailableBlocks; 
                //Log.i("1324", "external_availablesize:" + external_availablesize + " == " + BlockSize + " == " + AvailableBlocks);
            	if (external_availablesize > 10485760) {ExtEnable = true;}
            }
        }
		
		File data_file = ExtEnable ? _Context.getExternalFilesDir(null) : _Context.getFilesDir();;
		if (data_file == null) {
			data_file = _Context.getFilesDir();
		}
		String data_path = data_file.getAbsolutePath();
		Log.i (_TAG, "GetDataDir: " + data_path);
		return data_path;
	}	
		
	private boolean IsFileExist (String path, String fileName)
	{
		File file = new File(path, fileName);  
		if (file != null) {return file.exists();}  
		return false;
	}
	
	private boolean IsFileTheSame(String file1, String file2)
    {
		boolean res = true;
        try
        {
            BufferedInputStream inFile1 = new BufferedInputStream(new FileInputStream(file1));
            BufferedInputStream inFile2 = new BufferedInputStream(new FileInputStream(file2));
            
            long startTime = System.currentTimeMillis();
            //比较文件的长度是否一样
            if(inFile1.available() == inFile2.available())
            {
                while(inFile1.read() != -1 && inFile2.read() != -1)
                {
                    if(inFile1.read() != inFile2.read())
                    {
                    	res = false;
                        //System.out.println("Files not same");
                        break;
                    }
                }
                //System.out.println("two files are same !");
            }
            else
            {
            	res = false;
                //System.out.println("two files are different !");
            }
            inFile1.close();
            inFile2.close();
            //System.out.println("Time Consumed: "+(System.currentTimeMillis() - startTime) + "ms");
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e)			{e.printStackTrace();}

        return res;
    }
		
	private boolean UpdateJudgement (String local, String remote)
	{
		int localpos_start = 0;
		int remotepos_start = 0;

		boolean exitwhile = true;
		while (exitwhile)
		{
			int localpos_end = local.indexOf(".", localpos_start+1);
			int remotepos_end = remote.indexOf(".", remotepos_start+1);
			if ((localpos_end == -1) || (remotepos_end == -1)) {exitwhile = false;}
			
			int localversion = (localpos_end == -1) ? (int) Long.parseLong(local.substring((localpos_start == 0)?(localpos_start):(localpos_start+1))) : 
				(int) Long.parseLong(local.substring((localpos_start == 0)?(localpos_start):(localpos_start+1), localpos_end));  
			int remoteversion = (remotepos_end == -1) ? (int) Long.parseLong(local.substring((remotepos_start == 0)?(remotepos_start):(remotepos_start+1))) : 
				(int) Long.parseLong(local.substring((remotepos_start == 0)?(remotepos_start):(remotepos_start+1), remotepos_end));  
			
			//Log.i(_TAG, "version check: local ["+localpos_start+"-"+localpos_end +"]: "+ localversion + 
			//			"remote ["+remotepos_start+"-"+remotepos_end +"]: "+ remoteversion);
			
			if (localversion < remoteversion) {return true;}
			
			localpos_start = localpos_end;
			remotepos_start = remotepos_end;
		}
		
		return false;
	}	
	
	private boolean SelfUpdateCheck ()
	{
		PackageInfo info;  
		String sVersionName;
		int sVersionCode;
		//String sPackageNames;
		
		try {  
		    info = _Context.getPackageManager().getPackageInfo(_Context.getPackageName(), 0);  
		    sVersionName = info.versionName;  		    // 当前应用的版本名称  
		    sVersionCode = info.versionCode;  		    // 当前版本的版本号  
		    //sPackageNames = info.packageName;  		    // 当前版本的包名  
		} 
		catch (NameNotFoundException e) {e.printStackTrace(); return false;} 
				
		//Log.i(_TAG, "local: " + sVersionName + " - " + sVersionCode);
		//Log.i(_TAG, "remote: " + _SelfUpdateInfo.version + " - " + _SelfUpdateInfo.versioncode);
		
		if (_SelfUpdateInfo.versioncode != 0)
		{
			return (_SelfUpdateInfo.versioncode > sVersionCode);
		}
		if (UpdateJudgement (sVersionName, _SelfUpdateInfo.version)) {return true;}
		
		return false;
	}

	private boolean DownloadSelfUpdate ()
	{
		if (_SelfUpdateInfo.dlapk == "") {_SelfUpdateInfo.updatestate = -1; return false;}
		if (!SelfUpdateCheck ()) {_SelfUpdateInfo.updatestate = -1; return false;}
		
		String	fName = _SelfUpdateInfo.dlapk;
		fName = fName.trim();
		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);		
		
		String 	LocalFullpath = _DataPath + "/" + FileName;
		
		boolean	needdownload = true;
		if (IsFileExist (_DataPath, FileName))
		{
			PackageInfo info;
		    info = _Context.getPackageManager().getPackageArchiveInfo(LocalFullpath, PackageManager.GET_ACTIVITIES);  
		    //Log.i (_TAG, "local version code: " + info.versionCode);
			if (_SelfUpdateInfo.versioncode == info.versionCode) 
			{
				_SelfUpdateInfo.updatestate = 2; 
				needdownload = false;
			}
		}
		
		if (needdownload)
		{
		    //Log.i (_TAG, "Self update, start download new apk");
	        Thread thread = new Thread(new CDownloadApkSelf(FileName, _DataPath));
	        thread.start();				
		}		
		return true;
	}
	
	private class CDownloadApk implements Runnable {
		private int id;
		
		public CDownloadApk (int index)
		{
			this.id = index;
		}
		
		@Override
		public void run() 
		{
			_ApkDownloading = true;
			_ApkDownloadState = 1;

			String dlurl = _GameListInfos.get(id).dlapk;
    		String Localpath = _DataPath + "/" + _GameListInfos.get(id).directory + "/";
    		
    		String	fName = _GameListInfos.get(id).dlapk;
    		fName = fName.trim();
    		String 	FileName = fName.substring(fName.lastIndexOf("/") + 1);
    		
    		//Log.v("DlFile res", "++++++++++++++++++++++++++++++++++");
    		int res = _ApkDownloader.DlFile(dlurl, Localpath, FileName, true);	
    		//Log.v("DlFile res", "----------------------------------");
    		//Log.v("DlFile res", res+"");
    		_ApkDownloadState = (res == 0)? 2 : (res == -3 ? -3 : (res == -2 ? -2 : -1));
    		//Log.v("DlFile res", "_ApkDownloadState: " + _ApkDownloadState);
			_ApkDownloading = false;
		}
	};
		
	private class CDownloadApkSelf implements Runnable {
		
		private String dlFileName;
		private String dlFilePath;
		
		public CDownloadApkSelf (String filename, String filepath)
		{	
			this.dlFileName = filename;
			this.dlFilePath = filepath;
		}
		
		@Override
		public void run() 
		{
			HttpDownLoad			Downloader;	
			Downloader = new HttpDownLoad ();
			_SelfUpdateInfo.updatestate = 1;
			String dlurl = _SelfUpdateInfo.dlapk;
    		int res = Downloader.DlFile(dlurl, dlFilePath, dlFileName, true);	
    		_SelfUpdateInfo.updatestate = (res == 0)? 2 : -1;
		}
	};	
		
	private class CDownloadFiles implements Runnable {
		
		private String dlUrl;
		private String dlFileName;
		private String dlFilePath;
		
		public CDownloadFiles (String url, String filename, String filepath)
		{	
			this.dlFileName = filename;
			this.dlFilePath = filepath;
			this.dlUrl = url;
		}
		
		@Override
		public void run() 
		{
			HttpDownLoad			Downloader;	
			Downloader = new HttpDownLoad ();
			Downloader.DlFile(dlUrl, dlFilePath, dlFileName, true);	
		}
	};	
	private class CDownloadRes implements Runnable {
		
		private int index;
		private String localpath;
		
		public CDownloadRes (int dlindex, String dllocalpath)
		{	
			this.index = dlindex;
			this.localpath = dllocalpath;
		}
		
		@Override
		public void run() 
		{
			int 			res = 0;
			HttpDownLoad	Downloader;	
			Downloader = new HttpDownLoad ();
						
			for (int i=1; i<4; i++)
			{
				String 	iconFileName = String.format ("%s_%d.png", _GameListInfos.get(index).directory, i);
				String	iconUrl = _GameListInfos.get(index).dlres + iconFileName;
				res = Downloader.DlFile(iconUrl, localpath, iconFileName, true);
				if (res != 0) {return;}
			}
			
			for (int i=1; i<7; i++)
			{
				String 	preFileName = String.format ("%spre_%d.png", _GameListInfos.get(index).directory, i);
				String	preUrl = _GameListInfos.get(index).dlres + preFileName;
				res = Downloader.DlFile(preUrl, localpath, preFileName, true);
				if (res != 0) {return;}
			}
					
			//update version file
			File from =new File(localpath, "tmp_version");
			File to =new File(localpath, _VersionFileName);
			from.renameTo(to);
			
			_GameListInfos.get(index).resChecked = true;			
		}
	};
		
	private boolean GameListParse (String file)
	{
		//open file
		File		mfile = new File(file);
		if (mfile == null) {return false;}
		InputStream minput = null;
		try 
		{
			minput = new FileInputStream(mfile);
		} 
		catch (FileNotFoundException e) {e.printStackTrace(); return false;}  
		if (minput == null) {return false;}
		
		//start parse
		try 
		{
			SAXParserFactory 		factory = SAXParserFactory.newInstance(); 
			SAXParser 				parser = factory.newSAXParser();
			GameListInfoHandle		GameListInfoH = new GameListInfoHandle ();
			parser.parse(minput, GameListInfoH);
		
			_SelfUpdateInfo = GameListInfoH.getSelfUpdateInfo();
			_AdInfo = GameListInfoH.getAdInfo ();
			_GameListInfos = GameListInfoH.getGameListInfo();
			_GameListPopular = GameListInfoH.getGamelistPopular();
			_GameListRecommended = GameListInfoH.getGamelistRecommended();
			_GameListHome = GameListInfoH.getGamelistHome();
			_GameListIptvGame = GameListInfoH.getGamelistIptvGame();
			_GameListInfosEnable = true;
		} 
		catch (ParserConfigurationException e) {e.printStackTrace(); return false;} 
		catch (SAXException e) {e.printStackTrace(); return false;}
		catch (IOException e) {e.printStackTrace(); return false;}
				
		return true;
	}
	
//////////////////////////
			
	class GameListInfo {
		public int			arrayid = 0;
		public int 			uid = 0;
		public int			ctrltype = 0;
		public int			category = 0;
		public String		name = "";
		public String		directory = "";
		public String		procname = "";
		public String		version = "";
		public int			versioncode = 0;
		public int			size = 0;
		public String		price = "";
		public String		language = "";
		public int			age = 0;
		public String		dlapk = "";
		public String		dlres = "";
		public int			dlpv_num = 0;
		
		public boolean		resChecked = false; //本次启动后已检查过更新
	}

	class SelfUpdateInfo {
		public String		version = "";
		public int			versioncode = 0;
		public String		dlapk = "";

		public int 			updatestate = 0; // 0 - unchecked;  1 - downloading;  2 - ready for update;  -1 - do not need update;
	}
	
	class AdInfo {
		public int			price = 0;
		public String		image_a = "";
		public String		image_b = "";
		public String		image_c = "";
		public String		video_a = "";	//推广广告url
		public String		video_b = "";	//手柄介绍广告url
	}
	
//////////////////////////
	
	//XML, parse applist handler
	private class GameListInfoHandle extends DefaultHandler  
	{  
		private List<GameListInfo> 	_GameListInfos;
		private GameListInfo 		_GameListInfo;  
		private SelfUpdateInfo 		_SelfUpdateInfo;  
		private AdInfo 				_AdInfo;  
		private int					_Popular[];
		private int					_Recommended[];
		private int					_Home[];
		private int					_IptvGame[];
		private int					_GameListInfoCount = 0;
	
		public List<GameListInfo> getGameListInfo()  
		{  
			return _GameListInfos;  
		}  
		
		public SelfUpdateInfo getSelfUpdateInfo ()
		{
			return _SelfUpdateInfo;
		}
		
		public AdInfo getAdInfo ()
		{
			return _AdInfo;
		}
		
		public List<Integer> getGamelistPopular ()
		{			
			List<Integer>		list;
			list = new ArrayList<Integer>();
			for (int i=0; i<10; i++)
			{
				list.add(_Popular[i]);  	
			}
			return list;
		}
		
		public List<Integer> getGamelistRecommended ()
		{			
			List<Integer>		list;
			list = new ArrayList<Integer>();
			for (int i=0; i<10; i++)
			{
				list.add(_Recommended[i]);  	
			}
			return list;
		}
		
		public List<Integer> getGamelistHome ()
		{			
			List<Integer>		list;
			list = new ArrayList<Integer>();
			for (int i=0; i<_Home.length; i++)
			{
				list.add(_Home[i]);  	
			}
			return list;
		}
		
		public List<Integer> getGamelistIptvGame ()
		{			
			List<Integer>		list;
			list = new ArrayList<Integer>();
			for (int i=0; i<_IptvGame.length; i++)
			{
				list.add(_IptvGame[i]);  	
			}
			return list;
		}
		
		@Override  
		public void characters(char[] ch, int start, int length)  throws SAXException  
		{  
		}  

		@Override  
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException  
		{  
			if("app".equals(localName))  
			{  
				_GameListInfo = new GameListInfo(); 
				_GameListInfo.arrayid = (_GameListInfoCount);
				_GameListInfoCount ++;
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("uid".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.uid = (new Integer (attributes.getValue(i)));  
					}  
					else if("ctrltype".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.ctrltype = (new Integer (attributes.getValue(i)));  
					}  
					else if("category".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.category = (new Integer (attributes.getValue(i)));  
					}  
					else if("name".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.name = (attributes.getValue(i));  
					}
					else if("directory".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.directory = (attributes.getValue(i));  
					}  
					else if("procname".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.procname = (attributes.getValue(i));  
					}  
					else if("version".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.version = (attributes.getValue(i));  
					}  
					else if("versioncode".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.versioncode = (new Integer (attributes.getValue(i)));  
					}  
					else if("size".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.size = (new Integer (attributes.getValue(i)));  
					}  
					else if("price".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.price = (attributes.getValue(i));  
					}  
					else if("language".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.language = (attributes.getValue(i));  
					}  
					else if("age".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.age = (new Integer (attributes.getValue(i)));
					}  
					else if("dlapk".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.dlapk = (attributes.getValue(i));  
					}  
					else if("dlres".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.dlres = (attributes.getValue(i));  
					}
					else if("dlpv_num".equals(attributes.getLocalName(i)))  
					{  
						_GameListInfo.dlpv_num = (new Integer (attributes.getValue(i)));
					}  
				}
			}
			else if("self".equals(localName))
			{
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("version".equals(attributes.getLocalName(i)))  
					{  
						_SelfUpdateInfo.version = (attributes.getValue(i));  
					}  
					else if("versioncode".equals(attributes.getLocalName(i)))  
					{  
						_SelfUpdateInfo.versioncode = (new Integer (attributes.getValue(i)));  
					}  
					else if("dlapk".equals(attributes.getLocalName(i)))  
					{  
						_SelfUpdateInfo.dlapk = (attributes.getValue(i));  
					}
				}
			}
			else if("ad".equals(localName))
			{
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("price".equals(attributes.getLocalName(i)))  
					{  
						_AdInfo.price = (new Integer (attributes.getValue(i)));  
					}  
					else if("video_a".equals(attributes.getLocalName(i)))  
					{  
						_AdInfo.video_a = (attributes.getValue(i));  
					}  
					else if("image_a".equals(attributes.getLocalName(i)))  
					{  
						_AdInfo.image_a = (attributes.getValue(i));  
					}
					else if("video_b".equals(attributes.getLocalName(i)))  
					{  
						_AdInfo.video_b = (attributes.getValue(i));  
					}  
					else if("image_b".equals(attributes.getLocalName(i)))  
					{  
						_AdInfo.image_b = (attributes.getValue(i));  
					}
					else if("image_c".equals(attributes.getLocalName(i)))  
					{  
						_AdInfo.image_c = (attributes.getValue(i));  
					}
				}
			}
			else if("popular".equals(localName))
			{
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("game1".equals(attributes.getLocalName(i)))  
					{  
						_Popular[0] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game2".equals(attributes.getLocalName(i)))  
					{  
						_Popular[1] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game3".equals(attributes.getLocalName(i)))  
					{  
						_Popular[2] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game4".equals(attributes.getLocalName(i)))  
					{  
						_Popular[3] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game5".equals(attributes.getLocalName(i)))  
					{  
						_Popular[4] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game6".equals(attributes.getLocalName(i)))  
					{  
						_Popular[5] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game7".equals(attributes.getLocalName(i)))  
					{  
						_Popular[6] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game8".equals(attributes.getLocalName(i)))  
					{  
						_Popular[7] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game9".equals(attributes.getLocalName(i)))  
					{  
						_Popular[8] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game10".equals(attributes.getLocalName(i)))  
					{  
						_Popular[9] = (new Integer (attributes.getValue(i)));  
					}  
				}
			}
			else if("recommended".equals(localName))
			{
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("game1".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[0] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game2".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[1] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game3".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[2] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game4".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[3] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game5".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[4] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game6".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[5] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game7".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[6] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game8".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[7] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game9".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[8] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game10".equals(attributes.getLocalName(i)))  
					{  
						_Recommended[9] = (new Integer (attributes.getValue(i)));  
					}  
				}
			}
			else if("home".equals(localName))
			{
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("game1".equals(attributes.getLocalName(i)))  
					{  
						_Home[0] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game2".equals(attributes.getLocalName(i)))  
					{  
						_Home[1] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game3".equals(attributes.getLocalName(i)))  
					{  
						_Home[2] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game4".equals(attributes.getLocalName(i)))  
					{  
						_Home[3] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game5".equals(attributes.getLocalName(i)))  
					{  
						_Home[4] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game6".equals(attributes.getLocalName(i)))  
					{  
						_Home[5] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game7".equals(attributes.getLocalName(i)))  
					{  
						_Home[6] = (new Integer (attributes.getValue(i)));  
					}
					else if("game8".equals(attributes.getLocalName(i)))  
					{  
						_Home[7] = (new Integer (attributes.getValue(i)));  
					}
					else if("game9".equals(attributes.getLocalName(i)))  
					{  
						_Home[8] = (new Integer (attributes.getValue(i)));  
					}
					else if("game10".equals(attributes.getLocalName(i)))  
					{  
						_Home[9] = (new Integer (attributes.getValue(i)));  
					}
					else if("game11".equals(attributes.getLocalName(i)))  
					{  
						_Home[10] = (new Integer (attributes.getValue(i)));  
					}
				}
			}
			else if("iptvgame".equals(localName)){
				for (int i=0; i<attributes.getLength(); i++)
				{
					if("game1".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[0] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game2".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[1] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game3".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[2] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game4".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[3] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game5".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[4] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game6".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[5] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game7".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[6] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game8".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[7] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game9".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[8] = (new Integer (attributes.getValue(i)));  
					}  
					else if("game10".equals(attributes.getLocalName(i)))  
					{  
						_IptvGame[9] = (new Integer (attributes.getValue(i)));  
					}  
				}
			}
		}  

		@Override  
		public void startDocument() throws SAXException  
		{  
			_GameListInfos = new ArrayList<GameListInfo>();  
			_SelfUpdateInfo = new SelfUpdateInfo(); 
			_AdInfo = new AdInfo(); 
			_Popular = new int[10];
			_Recommended = new int[10];			
			_Home = new int[11];			
			_IptvGame = new int[10];			
		}  
		
		@Override  
		public void endElement(String uri, String localName, String qName) throws SAXException  
		{  
			if("app".equals(localName))  
			{  
				_GameListInfos.add(_GameListInfo);  
				_GameListInfo=null;  
			}
	    }  
	}   
}

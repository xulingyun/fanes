package com.TDiJoy.fane;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.VideoView;

import com.TDiJoy.fane.R;

public class FullVideoActivity extends Activity {
	private VideoView videoView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_video);
		
		Bundle bundle = getIntent().getExtras();
		String videopath = bundle.getString("path");
//		Log.v("", "videopath : " + videopath);
		
		videoView = (VideoView) findViewById(R.id.videoView1);
		
		VideoView v = new VideoView(this);
		
//		final String path = Environment.getExternalStorageDirectory().getPath() + "/v1.mp4";
//		videoView.setVideoPath(path);
		
		videoView.setVideoPath(videopath);
		
        videoView.start();
        
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
//						videoView.setVideoPath(path);
//						videoView.start();
						FullVideoActivity.this.finish();
						FullVideoActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
				});
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
//				Log.v("", "videoview : "+videoView.getMeasuredWidth() + "," + videoView.getMeasuredHeight());
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.v("", "videoview : "+videoView.getMeasuredWidth() + "," + videoView.getMeasuredHeight());
		this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		return true;
	}
	
}

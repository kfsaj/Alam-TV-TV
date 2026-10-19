package com.alamtv.app;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
public class PlayerActivity extends Activity {
 private ExoPlayer player;
 @Override public void onCreate(Bundle b){super.onCreate(b);
   PlayerView view=new PlayerView(this); view.setBackgroundColor(0xff050914); view.setUseController(true);
   setContentView(view); String url=getIntent().getStringExtra("url");
   if(url==null || !url.startsWith("https://")){Toast.makeText(this,"رابط HTTPS مطلوب",Toast.LENGTH_LONG).show();finish();return;}
   player=new ExoPlayer.Builder(this).build();view.setPlayer(player);
   player.addListener(new Player.Listener(){@Override public void onPlayerError(androidx.media3.common.PlaybackException e){Toast.makeText(PlayerActivity.this,"تعذر تشغيل البث: "+e.getErrorCodeName(),Toast.LENGTH_LONG).show();}});
   player.setMediaItem(MediaItem.fromUri(url));player.prepare();player.play();
   view.requestFocus();
 }
 @Override public void onStop(){super.onStop(); if(player!=null){player.release();player=null;}}
}

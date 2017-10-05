package ufmg_leic.meca;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.VideoView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle sa) {
        super.onCreate(sa);

        getWindow( ).setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        setContentView( R.layout.activity_splash_screen );

        try{

            VideoView videoView = (VideoView)findViewById(R.id.videoView);
            Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashscreenvid);

            videoView.setVideoURI(path);

            videoView.setOnCompletionListener( new MediaPlayer.OnCompletionListener(){

                @Override
                public void onCompletion( MediaPlayer mediaPlayer ){
                    jump();
                }
            });

            videoView.start();

        }catch(Exception e){
            jump();
        }
    }


    private void jump( ){

        if( isFinishing() ) return;
        startActivity(new Intent( this, MenuInicial.class ));
        finish();
    }
}

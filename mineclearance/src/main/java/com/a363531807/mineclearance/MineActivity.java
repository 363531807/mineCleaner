package com.a363531807.mineclearance;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 363531807 on 2015/6/9.
 */
public class MineActivity extends Activity  {
    private MineSurface mMineSurface;
    private ImageView mIVState;
    private Chronometer mTime;
    private TextView mMineLeave;
    private MediaPlayer mPlayer;
    private Intent mIntent;
    private Users mUsers;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.minelayout);
        mIntent=getIntent();
        mUsers=(Users)mIntent.getBundleExtra("Bundle").getSerializable("Users");
        if (mUsers.mBGmusic) {
            mPlayer = MediaPlayer.create(this, R.raw.bgmusic);
            mPlayer.setLooping(true);
            mPlayer.start();
        }
        mMineLeave =(TextView)findViewById(R.id.tvmineleave);
        mTime =(Chronometer)findViewById(R.id.cmtime);
        mIVState=(ImageView)findViewById(R.id.ivstate);
        mIVState.setImageResource(R.drawable.common_smile_selector);
        mMineSurface=new MineSurface(MineActivity.this,mUsers,mMineLeave,mTime,mIVState);
                LinearLayout _linerLayout =(LinearLayout)findViewById(R.id.llmainlayout);
        _linerLayout.addView(mMineSurface, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mIVState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMineSurface.surfaceRestart();
            }
        });

    }


    @Override
    public void onBackPressed() {
        Bundle _buldle=new Bundle();
        _buldle.putSerializable("Users",mMineSurface.mUsers);
        setResult(1425,mIntent.putExtra("Bundle",_buldle));
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUsers.mBGmusic)
        mPlayer.stop();
        }

}

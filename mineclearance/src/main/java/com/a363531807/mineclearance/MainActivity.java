package com.a363531807.mineclearance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 363531807 on 2015/5/27.
 */
public class MainActivity extends Activity {
    private  SharedPreferences mPreferences;
    private  EditText mName;
    private  EditText mRow;
    private  EditText mCol;
    private  EditText mMine;
    private  CheckBox mCheck;
    private TextView mBesttime;
    private Users mUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        mUsers=new Users();
        mPreferences=getSharedPreferences("stqbill",MODE_PRIVATE);
        mName =(EditText)findViewById(R.id.etmyname);
        mRow =(EditText)findViewById(R.id.myrownum);
        mCol =(EditText)findViewById(R.id.mycolnum);
        mMine =(EditText)findViewById(R.id.myminenum);
        mCheck =(CheckBox)findViewById(R.id.mybackmusic);
        mBesttime=(TextView)findViewById(R.id.mybestscore);
        mUsers.mName=mPreferences.getString("name", "stqbill");
        mName.setText(mUsers.mName);
        mUsers.mRowNum=mPreferences.getInt("row", 10);
        mRow.setText(""+mUsers.mRowNum);
        mUsers.mColNum=mPreferences.getInt("col", 6);
        mCol.setText(""+mUsers.mColNum);
        mUsers.mMineNum=mPreferences.getInt("mine", 10);
        mMine.setText(""+mUsers.mMineNum);
        mUsers.mBGmusic=mPreferences.getBoolean("music", true);
        mCheck.setChecked(mUsers.mBGmusic);
        mUsers.mSBestTime=mPreferences.getString("besttime","无");
        mBesttime.setText(mUsers.mSBestTime);
        mUsers.mLBestTime=mPreferences.getLong("lbesttime",Long.MAX_VALUE);
        Button _start=(Button)findViewById(R.id.start);
        _start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int _int;
                String _string=mName.getText().toString().trim();
                if (!_string.equals(mUsers.mName)){
                    mUsers.mLBestTime=Long.MAX_VALUE;
                    mUsers.mSBestTime="无";
                    mBesttime.setText(mUsers.mSBestTime);
                    mUsers.mName=_string;
                }
                if (mUsers.mName.isEmpty()){
                    showMsg("总得起个名字吧！");
                    return;
                }

                _int=getLeagleInt(mRow.getText().toString(),1,16);
                if (_int!=mUsers.mRowNum){
                    mUsers.mLBestTime=Long.MAX_VALUE;
                    mUsers.mSBestTime="无";
                    mBesttime.setText(mUsers.mSBestTime);
                    mUsers.mRowNum=_int;
                }
                if (mUsers.mRowNum==-1){
                    showMsg("行数需在1~16之间，否则我会不美观哦！");
                    return;
                }
                _int=getLeagleInt(mCol.getText().toString(), 1,12);
                if (_int!=mUsers.mColNum){
                    mUsers.mLBestTime=Long.MAX_VALUE;
                    mUsers.mSBestTime="无";
                    mBesttime.setText(mUsers.mSBestTime);
                    mUsers.mColNum=_int;
                }
                if (mUsers.mColNum==-1){
                    showMsg("列数需在1~12之间，否则我会不美观哦！");
                    return;
                }
               _int = getLeagleInt(mMine.getText().toString(),0,mUsers.mRowNum*mUsers.mColNum);
                if (_int!=mUsers.mMineNum){
                    mUsers.mLBestTime=Long.MAX_VALUE;
                    mUsers.mSBestTime="无";
                    mBesttime.setText(mUsers.mSBestTime);
                    mUsers.mMineNum=_int;
                }
                if (mUsers.mMineNum==-1){
                    showMsg("雷的数目不大对哦！");
                    return;
                }
                mUsers.mBGmusic = mCheck.isChecked();
                Intent _intent=new Intent(MainActivity.this,MineActivity.class);
                Bundle _bundle=new Bundle();
                _bundle.putSerializable("Users",mUsers);
                _intent.putExtra("Bundle",_bundle);
                startActivityForResult(_intent,1425);
            }

        });

    }
    public int getLeagleInt(String string,int min,int max){
        string=string.trim();
        if (string!=null&&!string.isEmpty()){
            int _result=Integer.parseInt(string);
            if(_result>=min&&_result<=max){
                return _result;
            }
        }
        return -1;
    }
    public void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==resultCode){
           mUsers=(Users)data.getBundleExtra("Bundle").getSerializable("Users");
            mBesttime.setText(mUsers.mSBestTime);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor _editor =mPreferences.edit();
        _editor.putBoolean("music", mCheck.isChecked());
        _editor.putString("name", mName.getText().toString().trim());
        _editor.putInt("row", Integer.parseInt(mRow.getText().toString().trim()));
        _editor.putInt("col", Integer.parseInt(mCol.getText().toString().trim()));
        _editor.putInt("mine", Integer.parseInt(mMine.getText().toString().trim()));
        _editor.putString("besttime",mUsers.mSBestTime);
        _editor.putLong("lbesttime",mUsers.mLBestTime);
        _editor.commit();
    }

}

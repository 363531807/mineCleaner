package com.a363531807.mineclearance;

import java.io.Serializable;

/**
 * Created by 363531807 on 2015/6/10.
 */
public class Users implements Serializable {
    public long mLBestTime;
    public String mSBestTime;
    public String mName;
    public int mRowNum;
    public int mColNum;
    public int mMineNum;
    public boolean mBGmusic;

    @Override
    public String toString() {
        //return super.toString();
        return mName;
    }

}

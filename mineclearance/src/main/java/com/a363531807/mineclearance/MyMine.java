package com.a363531807.mineclearance;

/**
 * Created by 363531807 on 2015/5/30.
 */
public class MyMine {
    //索引
    private int mID;
    //是否是雷
    private boolean mIsMinne;
    //显示状态0：覆盖，1：按压，2裸露，3：红旗，4：疑问标记,5.踩中雷，6.标记错误；
    private int mState;
    //x坐标
    private int mScaleX;
    //y坐标
    private int mScaleY;
    //周边雷的数目
    private int mLabourNum;
    //雷数为0的邻居
    private int[] emptyLabour;
    public MyMine(){
        mIsMinne=false;
        mState=0;
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public boolean getIsMinne() {
        return mIsMinne;
    }

    public void setIsMinne(boolean isMinne) {
        mIsMinne = isMinne;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public int getScaleX() {
        return mScaleX;
    }

    public void setScaleX(int scaleX) {
        mScaleX = scaleX;
    }

    public int getScaleY() {
        return mScaleY;
    }

    public void setScaleY(int scaleY) {
        mScaleY = scaleY;
    }

    public int getLabourNum() {
        return mLabourNum;
    }

    public void setLabourNum(int labourNum) {
        mLabourNum = labourNum;
    }

    public int[] getEmptyLabour() {
        return emptyLabour;
    }

    public void setEmptyLabour(int[] emptyLabour) {
        this.emptyLabour=emptyLabour;
    }

}

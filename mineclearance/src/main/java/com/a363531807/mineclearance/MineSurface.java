package com.a363531807.mineclearance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 363531807 on 2015/5/30.
 */
public class MineSurface extends SurfaceView implements SurfaceHolder.Callback{
    class MyMineHolder {
        //雷区的实际起始X坐标
       public int mStartX;
        //雷区的实际起始Y坐标
       public int mStartY;
        //雷区的实际高度
       public int mHight;
        //雷区的实际宽度
       public int mWidth;
        //每一个大格的边长
       public int mSpace;
        //内边界宽度
       public int mPaddingMine =5;
        //雷的实际边长
       public int mSpacingMine;
        //原始格子数目
       public int mRawGridNumber;
        //剩余雷的数目
       public int mNewMineNumber;
        //剩余未触发的格子数目
       public int mNewGridNumber;
       public int mPoint=-1;
    }
    class MyView{
        public TextView mTVMineLeave;
        public Chronometer mChronometer;
        public ImageView mSmile;
        public boolean mChornIsStart=false;
    }
    //界面浮雕效果类
    private EmbossMaskFilter mEmboss;
    private Bitmap mBackground ;
    private MyMineHolder mMineHolder;
    private SurfaceHolder mHolder;
    private MyView mMyView;
    private Context mContext;
    private MyMine mMyMine[];
    private MyOnTouchListener mMyOnTouchListener;
    public Users mUsers;
    private int mCount=0;
    private boolean mIsSurfaceChange;
    private boolean mIsFirstTime;
    public MineSurface(Context context,Users users,
                       TextView textView,Chronometer chronometer,ImageView imageView) {
        super(context);
        mMineHolder=new MyMineHolder();
        mMyView=new MyView();
        mHolder =getHolder();
        mHolder.addCallback(this);
        mContext=context;
        mUsers=users;
        mMineHolder.mRawGridNumber=mUsers.mRowNum*mUsers.mColNum;
        mMyView.mTVMineLeave=textView;
        mMyView.mChronometer=chronometer;
        mMyView.mSmile=imageView;
        mEmboss=createEmboss();
        mIsFirstTime =true;
    }
    public Bitmap getBackgroundBitmap(){
        //背景可以随意换，并且都能自动实现到每一个格子。
      //  return getBitmapByID(R.drawable.mainbackground);
        return getBitmapByID(R.drawable.zaoliyin);
    }

    public Bitmap getBitmapByID(int ID){
        Bitmap _bitmap= BitmapFactory.decodeResource(getResources(),ID);
        return _bitmap;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       // Log.i("stqbill", "surfacecreate");
        if (mIsFirstTime){
            //计算实际雷区的实际尺寸，达到自适应界面的效果
            if (getWidth()*mUsers.mRowNum<getHeight()*mUsers.mColNum){
                mMineHolder.mSpace=getWidth()/mUsers.mColNum;
                mMineHolder.mStartX=0;
                mMineHolder.mWidth=getWidth();
                mMineHolder.mStartY=(getHeight()-mMineHolder.mSpace*mUsers.mRowNum)/2;
                mMineHolder.mHight=mMineHolder.mStartY+mMineHolder.mSpace*mUsers.mRowNum;
            }   else {
                mMineHolder.mSpace=getHeight()/mUsers.mRowNum;
                mMineHolder.mStartY=0;
                mMineHolder.mHight=getHeight();
                mMineHolder.mStartX=(getWidth()-mMineHolder.mSpace*mUsers.mColNum)/2;
                mMineHolder.mWidth=mMineHolder.mStartX+mMineHolder.mSpace*mUsers.mColNum;
            }
            mMineHolder.mSpacingMine=mMineHolder.mSpace-2* mMineHolder.mPaddingMine;
            mBackground=getBackgroundBitmap();
            Matrix _m=new Matrix();
            _m.postScale((float) getWidth() / mBackground.getWidth(),
                    (float) getHeight() / mBackground.getHeight());
            mBackground=Bitmap.createBitmap(mBackground,0,0,
                    mBackground.getWidth(),mBackground.getHeight(),_m,true);
            mMyOnTouchListener=new MyOnTouchListener();
            mMyMine =new MyMine[mMineHolder.mRawGridNumber];
            createMine();
            mMineHolder.mNewGridNumber=mMineHolder.mRawGridNumber;
            mMineHolder.mNewMineNumber=mUsers.mMineNum;
            mMyView.mTVMineLeave.setText("Mine:" + mMineHolder.mNewMineNumber);
            //mMyView.mChronometer.stop();
            mMyView.mChornIsStart=false;
            mMyView.mChronometer.setBase(SystemClock.elapsedRealtime());
            surCreate();
            setOnTouchListener(mMyOnTouchListener);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.i("stqbill","surfaceChanged");
        mIsSurfaceChange =true;
        if (!mIsFirstTime){
            //计算实际雷区的实际尺寸，达到自适应界面的效果
            if (getWidth()*mUsers.mRowNum<getHeight()*mUsers.mColNum){
                mMineHolder.mSpace=getWidth()/mUsers.mColNum;
                mMineHolder.mStartX=0;
                mMineHolder.mWidth=getWidth();
                mMineHolder.mStartY=(getHeight()-mMineHolder.mSpace*mUsers.mRowNum)/2;
                mMineHolder.mHight=mMineHolder.mStartY+mMineHolder.mSpace*mUsers.mRowNum;
            }   else {
                mMineHolder.mSpace=getHeight()/mUsers.mRowNum;
                mMineHolder.mStartY=0;
                mMineHolder.mHight=getHeight();
                mMineHolder.mStartX=(getWidth()-mMineHolder.mSpace*mUsers.mColNum)/2;
                mMineHolder.mWidth=mMineHolder.mStartX+mMineHolder.mSpace*mUsers.mColNum;
            }
            mMineHolder.mSpacingMine=mMineHolder.mSpace-2* mMineHolder.mPaddingMine;
            mBackground=getBackgroundBitmap();
            Matrix _m=new Matrix();
            _m.postScale((float) getWidth() / mBackground.getWidth(),
                    (float) getHeight() / mBackground.getHeight());
            //得到背景图片。
            mBackground=Bitmap.createBitmap(mBackground,0,0,
                    mBackground.getWidth(),mBackground.getHeight(),_m,true);
            changeMine();
            surCreate();
        }else mIsFirstTime=false;
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.i("stqbill","surfaceDestroyed");
    }
    public void surfaceRestart(){
        //重新开始游戏，重绘界面
        createMine();
        mMineHolder.mNewGridNumber=mMineHolder.mRawGridNumber;
        mMineHolder.mNewMineNumber=mUsers.mMineNum;
        mMyView.mTVMineLeave.setText("Mine:" + mMineHolder.mNewMineNumber);
        mMyView.mSmile.setImageResource(R.drawable.common_smile_selector);
        //mMyView.mChronometer.stop();
        mMyView.mChornIsStart=false;
        mMyView.mChronometer.setBase(SystemClock.elapsedRealtime());
        surCreate();
        setOnTouchListener(mMyOnTouchListener);

    }
    public void createMine(){
        for (int i=0;i<mMyMine.length;i++){
            mMyMine[i]=new MyMine();
        }
        int _order;
        for (int i=0;i< mUsers.mMineNum;i++){
            do {
                _order=(int)(mUsers.mColNum*mUsers.mRowNum*Math.random());
            } while (mMyMine[_order].getIsMinne());
            mMyMine[_order].setIsMinne(true);
        }
        int _id;
        int _scaleY;
        int _labour;
        for (int i=0;i<mUsers.mRowNum;i++){
            _scaleY=mMineHolder.mStartY+mMineHolder.mSpace*i+mMineHolder.mPaddingMine;
            for (int j=0;j<mUsers.mColNum;j++){
                _id=i*mUsers.mColNum+j;
                mMyMine[_id].setID(_id);
                //设置格子坐标
                mMyMine[_id].setScaleX(mMineHolder.mStartX+mMineHolder.mSpace * j +
                        mMineHolder.mPaddingMine);
                mMyMine[_id].setScaleY(_scaleY);
                _labour=0;
                // 计算周围雷的数目
                if (i>0){
                    if (j>0)
                        if (mMyMine[_id-mUsers.mColNum-1].getIsMinne())
                            _labour++;
                    if (mMyMine[_id-mUsers.mColNum].getIsMinne())
                        _labour++;
                    if (j<mUsers.mColNum-1)
                        if (mMyMine[_id-mUsers.mColNum+1].getIsMinne())
                            _labour++;
                }
                if (j>0)
                    if (mMyMine[_id-1].getIsMinne())
                        _labour++;
                if (j<mUsers.mColNum-1)
                    if (mMyMine[_id+1].getIsMinne())
                        _labour++;
                if (i<mUsers.mRowNum-1){
                    if (j>0)
                        if (mMyMine[_id+mUsers.mColNum-1].getIsMinne())
                            _labour++;
                    if (mMyMine[_id+mUsers.mColNum].getIsMinne())
                        _labour++;
                    if (j<mUsers.mColNum-1)
                        if (mMyMine[_id+mUsers.mColNum+1].getIsMinne())
                            _labour++;
                }
                mMyMine[_id].setLabourNum(_labour);
            }
        }
        int[] _emptyOrder;
        //添加空白雷周围index
        for (int i=0;i<mUsers.mRowNum;i++){
            for (int j=0;j<mUsers.mColNum;j++){
                _id=i*mUsers.mColNum+j;
                if (mMyMine[_id].getLabourNum()==0) {
                    _emptyOrder = new int[8];
                    _order = 0;
                    if (i > 0) {
                        if (j > 0) {
                            _emptyOrder[_order] = _id - mUsers.mColNum - 1;
                            _order++;
                        }
                        _emptyOrder[_order] = _id - mUsers.mColNum;
                        _order++;
                        if (j < mUsers.mColNum - 1) {
                            _emptyOrder[_order] = _id - mUsers.mColNum + 1;
                            _order++;
                        }
                    }
                    if (j > 0) {
                        _emptyOrder[_order] = _id - 1;
                        _order++;
                    }
                    if (j < mUsers.mColNum - 1) {
                        _emptyOrder[_order] = _id + 1;
                        _order++;
                    }
                    if (i < mUsers.mRowNum - 1) {
                        if (j > 0) {
                            _emptyOrder[_order] = _id + mUsers.mColNum - 1;
                            _order++;
                        }
                        _emptyOrder[_order] = _id + mUsers.mColNum;
                        _order++;
                        if (j < mUsers.mColNum - 1) {
                            _emptyOrder[_order] = _id + mUsers.mColNum + 1;
                            _order++;
                        }
                    }
                    if (_order>0){
                        int[] _int = new int[_order];
                        for (int ii = 0; ii < _order; ii++) {
                            _int[ii] = _emptyOrder[ii];
                        }
                        mMyMine[_id].setEmptyLabour(_int);
                    }else {
                        //如果不是空格，则标记为-1
                        mMyMine[_id].setEmptyLabour(new int[]{-1});
                    }
                }else {
                    mMyMine[_id].setEmptyLabour(new int[]{-1});
                }

            }
        }
    }

    public void surCreate() {
        //游戏界面创建函数
        Canvas _canvas=mHolder.lockCanvas();
        try {
            _canvas.drawBitmap(mBackground,0,0,null);
            Paint _paint=new Paint();
            _paint.setColor(Color.LTGRAY);
            _canvas.drawRect(mMineHolder.mStartX, mMineHolder.mStartY,
                    mMineHolder.mStartX + mMineHolder.mPaddingMine,
                    mMineHolder.mHight, _paint);
            _canvas.drawRect(mMineHolder.mStartX,mMineHolder.mStartY,
                    mMineHolder.mWidth,
                    mMineHolder.mStartY+mMineHolder.mPaddingMine,_paint);
            _canvas.drawRect(mMineHolder.mWidth-mMineHolder.mPaddingMine
                    ,mMineHolder.mStartY,
                    mMineHolder.mWidth,
                    mMineHolder.mHight,_paint);
            _canvas.drawRect(mMineHolder.mStartX,
                    mMineHolder.mHight-mMineHolder.mPaddingMine,
                    mMineHolder.mWidth,
                    mMineHolder.mHight,_paint);
            for (int i=1;i<= mUsers.mColNum;i++){
                _canvas.drawRect(mMineHolder.mStartX-mMineHolder.mPaddingMine
                                +(mMineHolder.mPaddingMine*2+mMineHolder.mSpacingMine)*i,mMineHolder.mStartY,
                        mMineHolder.mStartX+mMineHolder.mPaddingMine
                                +(mMineHolder.mPaddingMine*2+mMineHolder.mSpacingMine)*i,
                        mMineHolder.mHight,_paint);
            }
            for (int i=1;i<= mUsers.mRowNum;i++){
                _canvas.drawRect(mMineHolder.mStartX,
                        mMineHolder.mStartY - mMineHolder.mPaddingMine
                                + (mMineHolder.mPaddingMine * 2 + mMineHolder.mSpacingMine) * i,
                        mMineHolder.mWidth,
                        mMineHolder.mStartY + mMineHolder.mPaddingMine
                                + (mMineHolder.mPaddingMine * 2 + mMineHolder.mSpacingMine) * i,_paint);
            }
            for (int i=0;i<=mMyMine.length;i++)
                drawMineState(mMyMine[i],_canvas);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mHolder.unlockCanvasAndPost(_canvas);
        }
    }

    //一个格子状态状态绘出函数
    public void drawMineState(MyMine mine,Canvas canvas){
        Rect _rect= getMineRect(mine);
        Paint _paint=new Paint();
        _paint.setMaskFilter(mEmboss);
        _paint.setStyle(Paint.Style.FILL);
        //显示状态0：覆盖，1：按压，2裸露，3：红旗，4：疑问标记,5.踩中雷，6.标记错误；
        switch (mine.getState()){
            case 0:
                canvas.drawBitmap(mBackground,_rect,_rect,_paint);
                break;
            case 1:
                _paint.setColor( getResources().getColor(R.color.minepress));
                canvas.drawRect(_rect,_paint);
                break;
            case 2:
                _paint.setColor(getResources().getColor(R.color.minenaked));
                canvas.drawRect(_rect, _paint);
                if (mine.getIsMinne()){
                    Bitmap _mine=getBitmapByID(R.drawable.mine2);
                    _paint.setStyle(Paint.Style.FILL);
                    canvas.drawBitmap(_mine,null,getMineDrawRect(mine),_paint);

                }else{
                    if (mine.getLabourNum()!=0)
                    {   _paint.setTextSize(mMineHolder.mSpacingMine * 3 / 4);
                        _paint.setStrokeWidth(3);
                        _paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        _paint.setColor(getResources().
                                getColor(getColorByLabourNum(mine.getLabourNum())));
                        _paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                        _paint.setTextAlign(Paint.Align.CENTER);
                        Paint.FontMetrics _font=_paint.getFontMetrics();
                        canvas.drawText("" + mine.getLabourNum(), mine.getScaleX() +
                                mMineHolder.mSpacingMine / 2
                                , mine.getScaleY() + mMineHolder.mSpacingMine * 7 / 8
                                - _font.descent / 2, _paint);

                    }

                }

                break;
            case 3:
                canvas.drawBitmap(mBackground, _rect, _rect,_paint);
                Bitmap _bone=getBitmapByID(R.drawable.bonewhite);
                //_paint.setStyle(Paint.Style.STROKE);
                canvas.drawBitmap(_bone, null, getMineDrawRect(mine), _paint);
                break;
            case 4:
                canvas.drawBitmap(mBackground, _rect, _rect, _paint);
                Bitmap _question=getBitmapByID(R.drawable.question);
                canvas.drawBitmap(_question, null, getMineDrawRect(mine), _paint);
                break;
            case 5:
                _paint.setColor(getResources().getColor(R.color.minenaked));
                canvas.drawRect(_rect, _paint);
                Bitmap _wrongMine=getBitmapByID(R.drawable.mine2worng);
                _paint.setStyle(Paint.Style.FILL);
                canvas.drawBitmap(_wrongMine,null,getMineDrawRect(mine),_paint);
                break;
            case 6:
                _paint.setColor(getResources().getColor(R.color.minenaked));
                canvas.drawRect(_rect, _paint);
                Bitmap _touchMine=getBitmapByID(R.drawable.mine2touch);
                _paint.setStyle(Paint.Style.FILL);
                canvas.drawBitmap(_touchMine,null,getMineDrawRect(mine),_paint);
                break;

        }
    }

    public void changeMine(){
        //若屏幕尺寸发生改变（如屏幕旋转时）重新获取各格子的坐标。
        int _id;
        int _scaleY;
        for (int i=0;i<mUsers.mRowNum;i++){
            _scaleY=mMineHolder.mStartY+mMineHolder.mSpace*i+mMineHolder.mPaddingMine;
            for (int j=0;j<mUsers.mColNum;j++){
                _id=i*mUsers.mColNum+j;;
                mMyMine[_id].setScaleX(mMineHolder.mStartX+mMineHolder.mSpace * j +
                        mMineHolder.mPaddingMine);
                mMyMine[_id].setScaleY(_scaleY);
            }
        }

    }


    private Rect getMineRect(MyMine mine){
        //得到格子矩形
        Rect _rect=new Rect(mine.getScaleX(),mine.getScaleY(),
                               mine.getScaleX()+mMineHolder.mSpacingMine,mine.getScaleY()+
                mMineHolder.mSpacingMine );
        return _rect;
    }
    private Rect getMineDrawRect(MyMine mine){
        //得到该格子的绘画的矩形
        int _padding= mMineHolder.mSpacingMine/8;
        Rect _rect=new Rect(mine.getScaleX()+_padding,mine.getScaleY()+_padding,
                mine.getScaleX()+mMineHolder.mSpacingMine-_padding,
                mine.getScaleY()+mMineHolder.mSpacingMine-_padding );
        return _rect;
    }

    public void mineOneDraw(){
        mineOneDraw(mMineHolder.mPoint);
    }
    public void mineOneDraw(int index) {
        //画出一个雷
//        Canvas _canvas = mHolder.lockCanvas(new Rect(mMyMine[index].getScaleX(),
//                mMyMine[index].getScaleY(), (mMyMine[index].getScaleX() +
//                mMineHolder.mSpacingMine), (mMyMine[index].getScaleY() +
//                mMineHolder.mSpacingMine)));
        Canvas _canvas=mHolder.lockCanvas(getMineRect(mMyMine[index]));
        try {
                drawMineState(mMyMine[index], _canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mHolder.unlockCanvasAndPost(_canvas);
        }
    }


    public int getMineByScale(float x,float y){
        //根据坐标得到雷的索引
        int _i=(int)(x-mMineHolder.mStartX)/mMineHolder.mSpace;
        int _j=(int)(y-mMineHolder.mStartY)/mMineHolder.mSpace;
        if (x>=mMineHolder.mStartX+_i*mMineHolder.mSpace+mMineHolder.mPaddingMine
                &&x<=mMineHolder.mStartX+(_i+1)*mMineHolder.mSpace-mMineHolder.mPaddingMine
            &&y>=mMineHolder.mStartY+_j*mMineHolder.mSpace+mMineHolder.mPaddingMine
                &&y<=mMineHolder.mStartY+(_j+1)*mMineHolder.mSpace-mMineHolder.mPaddingMine) {
            return (_j * mUsers.mColNum + _i);
        }
        return -1;
    }
    public void showAllMine(){
        //游戏结束时显示所有结果。
        for(int i=0;i<mMineHolder.mRawGridNumber;i++){
            switch (mMyMine[i].getState()){
                case 0:
                    mMyMine[i].setState(2);
                    mineOneDraw(i);
                    break;
                case 3:
                    if (!mMyMine[i].getIsMinne()){
                        mMyMine[i].setState(5);
                        mineOneDraw(i);
                    }
                    break;
                case 4:
                    mMyMine[i].setState(2);
                    mineOneDraw(i);
                    break;
            }
            }
    }
    public void newMineChange(boolean isAdd){
        //当格子的状态发生改变时判断结果。
        if (isAdd)
            mMineHolder.mNewMineNumber++;
        else mMineHolder.mNewMineNumber--;
        mMyView.mTVMineLeave.setText("Mine:" + mMineHolder.mNewMineNumber);
        if (mMineHolder.mNewMineNumber==0) {
            showAllMine();
            mMyView.mChronometer.stop();
            mMyView.mChornIsStart = false;
            boolean isWin = true;
            for (int i = 0; i < mMineHolder.mRawGridNumber; i++) {
                if (mMyMine[i].getState()== 5) {
                    isWin = false;
                    break;
                }
            }
            gameResult(isWin);
        }
    }
    public void gameResult(boolean isWin){
        //游戏结束，弹出对话框
        setOnTouchListener(null);
        mMyView.mChronometer.stop();
        mMyView.mChornIsStart=false;
        String _title;
        String _msg;
        if(isWin){
            _title="Success!";
            _msg="恭喜您赢了，是否重来？";
            if (mUsers.mLBestTime>SystemClock.elapsedRealtime()-mMyView.mChronometer.getBase()){
                mUsers.mLBestTime=SystemClock.elapsedRealtime()-mMyView.mChronometer.getBase();
                mUsers.mSBestTime=mMyView.mChronometer.getText().toString();
            }
            mMyView.mSmile.setImageResource(R.drawable.win_smile_selector);
        }else{
            _title="Fail!";
            _msg="您输了，是否重来？";
            mMyView.mSmile.setImageResource(R.drawable.cry_smile_selector);
        }
        showAllMine();
        Drawable _icon=getResources().getDrawable(R.drawable.mine2icon);
        try {
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        new AlertDialog.Builder(mContext)
                .setIcon(_icon)
                .setTitle(_title)
                .setMessage(_msg)
                .setPositiveButton("是", new OnMineDialogListener())
                .setNeutralButton("否", null)
                .show();
    }


    public void newGridChange() {
        mMineHolder.mNewGridNumber--;
    }

    class MyOnTouchListener implements OnTouchListener {
        private final long LONG_PRESS_TIME =250;
        private Handler mHandler=new Handler();
        private MyRunable mMyRunable;
        @Override
        //触摸事件，并实现了长按和滑动的功能。
        public boolean onTouch(View v, MotionEvent event) {
            if (mIsSurfaceChange){
                mIsSurfaceChange =false;
                surCreate();
            }
            if (event.getX() > mMineHolder.mStartX && event.getY() > mMineHolder.mStartY &&
                    event.getX() < mMineHolder.mWidth && event.getY() < mMineHolder.mHight) {
                if (!mMyView.mChornIsStart) {
                    mMyView.mChronometer.setBase(SystemClock.elapsedRealtime());
                    mMyView.mChronometer.start();
                    mMyView.mChornIsStart = true;
                }

                int _newPoint;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        mMineHolder.mPoint = getMineByScale(event.getX(), event.getY());
                        if (mMineHolder.mPoint != -1 ){
                            mMyView.mSmile.setImageResource(R.drawable.smile2);
                            mCount++;
                            if (mMyRunable!=null){
                                mHandler.removeCallbacks(mMyRunable);
                            }
                            mMyRunable=new MyRunable();
                            mHandler.postDelayed(mMyRunable, LONG_PRESS_TIME);
                            if(mMyMine[mMineHolder.mPoint].getState() == 0) {
                                mMyMine[mMineHolder.mPoint].setState(1);
                                mineOneDraw();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mMyView.mSmile.setImageResource(R.drawable.common_smile_selector);
                        if (mCount ==0) {
                            if (mMineHolder.mPoint != -1) {
                                switch (mMyMine[mMineHolder.mPoint].getState()) {
                                    case 1:
                                        mMyMine[mMineHolder.mPoint].setState(3);
                                        mineOneDraw();
                                        newMineChange(false);
                                        break;
                                    case 3:
                                        mMyMine[mMineHolder.mPoint].setState(4);
                                        mineOneDraw();
                                        newMineChange(true);
                                        break;
                                    case 4:
                                        mMyMine[mMineHolder.mPoint].setState(0);
                                        mineOneDraw();
                                        break;
                                }
                            }
                        } else if (mMineHolder.mPoint != -1
                                && (mMyMine[mMineHolder.mPoint].getState() == 1
                                || mMyMine[mMineHolder.mPoint].getState() == 4)) {

                            if (mMyMine[mMineHolder.mPoint].getIsMinne()) {
                                mMyMine[mMineHolder.mPoint].setState(6);
                                mineOneDraw();
                                gameResult(false);
                            } else {
                                if (mMyMine[mMineHolder.mPoint].getLabourNum() == 0) {
                                    mMyMine[mMineHolder.mPoint].setState(0);
                                    mineOneDraw();
                                    mMyMine[mMineHolder.mPoint].setState(2);
                                    newGridChange();
                                    newSetLabourEmptyState(mMineHolder.mPoint);
                                    surCreate();
                                }else {
                                    mMyMine[mMineHolder.mPoint].setState(2);
                                    mineOneDraw();
                                    newGridChange();
                                }
                                if (mMineHolder.mNewGridNumber == mUsers.mMineNum) {
                                    gameResult(true);
                                }
                            }

                        }

                        if (mMyRunable!=null){
                            mCount=0;
                            mHandler.removeCallbacks(mMyRunable);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        _newPoint = getMineByScale(event.getX(), event.getY());
                        if (_newPoint!=-1){
                            mMyView.mSmile.setImageResource(R.drawable.smile2);
                        }else mMyView.mSmile.setImageResource(R.drawable.common_smile_selector);
                        if (_newPoint==-1||mMineHolder.mPoint != _newPoint) {

                            if (mCount==0){
                                mCount=1;
                            }
                            if (mMyRunable!=null){
                                mHandler.removeCallbacks(mMyRunable);
                            }

                            if (mMineHolder.mPoint != -1 && mMyMine[mMineHolder.mPoint].getState() == 1) {

                                mMyMine[mMineHolder.mPoint].setState(0);
                                mineOneDraw();
                            }
                            if (_newPoint != -1 && mMyMine[_newPoint].getState() == 0) {
                                mMineHolder.mPoint = _newPoint;
                                mMyMine[mMineHolder.mPoint].setState(1);
                                mineOneDraw();
                            }
                            mMineHolder.mPoint = _newPoint;
                        }else{

                                mHandler.postDelayed(mMyRunable,LONG_PRESS_TIME);
                    }
                        break;
                }
            } else {
                mMyView.mSmile.setImageResource(R.drawable.common_smile_selector);
                if (mMineHolder.mPoint != -1 && mMyMine[mMineHolder.mPoint].getState() == 1) {
                    mMyMine[mMineHolder.mPoint].setState(0);
                    mineOneDraw();
                }
            }
            return true;
        }
        class MyRunable implements Runnable{
            @Override
            public void run() {
                mCount=0;
            }
        }
    }

//    public void setEmptyMineState(int index){
//            mMyMine[index].setState(2);
//            newGridChange();
//            if (mMyMine[index].getLabourNum()==0)
//                stLabourEmptyState(index);
//        }
//    public void stLabourEmptyState(int index){
//        int i=index/mUsers.mColNum;
//        int j=index%mUsers.mColNum;
//        if (i>0){
//            if (j>0&&mMyMine[index - mUsers.mColNum - 1].getState()==0)
//                    setEmptyMineState(index - mUsers.mColNum - 1);
//            if (mMyMine[index - mUsers.mColNum].getState()==0) {
//                setEmptyMineState(index - mUsers.mColNum);
//            }
//            if (j<mUsers.mColNum-1&&mMyMine[index - mUsers.mColNum +1].getState()==0)
//                    setEmptyMineState(index - mUsers.mColNum + 1);
//        }
//        if (j>0&&mMyMine[index- 1].getState()==0)
//           setEmptyMineState(index - 1);
//        if (j<mUsers.mColNum-1&&mMyMine[index+ 1].getState()==0)
//           setEmptyMineState(index + 1);
//        if (i<mUsers.mRowNum-1){
//            if (j>0&&mMyMine[index +mUsers.mColNum - 1].getState()==0)
//               setEmptyMineState(index + mUsers.mColNum - 1);
//            if (mMyMine[index + mUsers.mColNum].getState()==0)
//                setEmptyMineState(index + mUsers.mColNum);
//            if (j<mUsers.mColNum-1&&mMyMine[index + mUsers.mColNum + 1].getState()==0)
//                setEmptyMineState(index + mUsers.mColNum + 1);
//        }
//    }
    //利用递归的方法绘出周围空白格子。
    void newSetLabourEmptyState(int index){
        int[] _int=mMyMine[index].getEmptyLabour();
        if (_int.length>0&&_int[0]!=-1){
            for (int i=0;i<_int.length;i++){
                if (mMyMine[_int[i]].getState()==0)
                    newSetEmptyMineState(_int[i]);
            }
        }

    }
    //利用递归的方法绘出周围空白格子。
    public void newSetEmptyMineState(int index){
        mMyMine[index].setState(2);
        newGridChange();
        if (mMyMine[index].getLabourNum()==0)
            newSetLabourEmptyState(index);
    }
    class OnMineDialogListener implements Dialog.OnClickListener{
        public OnMineDialogListener(){

        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
             surfaceRestart();

        }
    }

    //得到邻居雷数的颜色
    public int getColorByLabourNum(int labourNum){
        switch (labourNum){
            case 1:
                return R.color.one;
            case 2:
                return R.color.two;
            case 3:
                return R.color.three;
            case 4:
                return R.color.four;
            case 5:
                return R.color.five;
            case 6:
                return R.color.six;
            case 7:
                return R.color.seven;
            case 8:
                return R.color.eight;
            default:
                return 0;

        }
    }
    public EmbossMaskFilter createEmboss(){
        // 设置光源的方向
        float[] direction = new float[]{ 1, 1, 1 };
        //设置环境光亮度
        float light = 0.4f;
        // 选择要应用的反射等级
        float specular = 6;
        // 向mask应用一定级别的模糊
        float blur = 3.5f;
        EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,specular,blur);
        return  emboss;
    }
}


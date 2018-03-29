package com.example.lenovo.gua_gua_le;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class XfermodeView extends View {
    private Bitmap mBgBitmap, mFgBitmap;
    private Paint mPaint;
    private Canvas mCanvas;
    private Path mPath;
   // public  Handler handler;
    //    private int newP[];
    //    private int number=0;
    private int pixels[];
    private Context context;
    public int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public XfermodeView(Context context,Handler handler) {
        super(context);
        this.context = context;
  //      this.handler=handler;
        init();
    }

    public XfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XfermodeView(Context context, AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAlpha(0);//设置透明度为0
        mPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(50);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();  //记录手指划过的路径
        mBgBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.photo);
        mFgBitmap = Bitmap.createBitmap(mBgBitmap.getWidth(),
                mBgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //      newP=new int[mBgBitmap.getWidth()*mBgBitmap.getHeight()];

        mCanvas = new Canvas(mFgBitmap);
        mCanvas.drawColor(Color.GRAY); //在外层画上一层灰色
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
        }
        mCanvas.drawPath(mPath, mPaint);

//        mFgBitmap.getPixels(newP,0,mBgBitmap.getWidth(),0,0,mBgBitmap.getWidth(),mBgBitmap.getHeight());
//
//        for (int i=0;i<=newP.length;i++)
//        if(Color.alpha(newP[i])==0) number++;
//        if((float)(number/newP.length)>=0.5){
//            Toast.makeText(context, "已经刮出了一半", Toast.LENGTH_SHORT).show();
//        }
        new MyThread().start();
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        canvas.drawBitmap(mFgBitmap, 0, 0, null);   //重新画一下灰色图片
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
               //     Toast.makeText(context, msg.obj + "", Toast.LENGTH_SHORT).show();break;
                    if ((float)msg.obj>=0.3){
                        setNumber(1);
                    }
                    Log.d("刮出来的面积：",msg.obj+"");
                default:break;
            }
        }
    };


    class MyThread extends Thread {

        public MyThread() {
        }

        @Override
        public void run() {
            super.run();
            // 取出像素点
            synchronized (mFgBitmap) {
                if (pixels == null) {
                    pixels = new int[mFgBitmap.getWidth() * mFgBitmap.getHeight()];
                }
                mFgBitmap.getPixels(pixels, 0, mFgBitmap.getWidth(), 0, 0, mFgBitmap.getWidth(),
                        mFgBitmap.getHeight());
            }
            int sum = pixels.length;
            int num = 0;
            for (int i = 0; i < sum; i++) {
                if (pixels[i] == 0) {
                    num++;
                }
            }
            Message m = new Message();
            m.what = 1;
            m.obj = (float)num /(float) sum;
            handler.sendMessage(m);
//            Toast.makeText(context, num/sum+" ", Toast.LENGTH_SHORT).show();

        }
    }

    ;

}


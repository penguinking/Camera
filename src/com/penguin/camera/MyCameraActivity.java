package com.penguin.camera;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MyCameraActivity extends Activity 
{
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private Button takePictureButton = null;
    private Camera mCamera;
    //记录保存的是第几张图片
    private int whichPicture = 0;
    
    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        takePictureButton = (Button)this.findViewById(R.id.button);
        mSurfaceView = (SurfaceView)this.findViewById(R.id.surfaceview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolderCallback());
        //设置缓冲区类型
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   
        //拍照按钮监听    
        takePictureButton.setOnClickListener(new View.OnClickListener() 
        {
            
            @Override
            public void onClick(View v) 
            {
                // TODO Auto-generated method stub
                //拍照
                mCamera.takePicture(null, null, pictureCallback);
            }
        });
         
    }
    
    private class SurfaceHolderCallback implements SurfaceHolder.Callback
    {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) 
        {
            // TODO Auto-generated method stub
            //打开摄像头
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //获取摄像头参数
            Camera.Parameters mParameters = mCamera.getParameters();
            //设置图片格式
            mParameters.setPictureFormat(PixelFormat.JPEG);

            mCamera.setParameters(mParameters); 
            //开始预览
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) 
        {
            // TODO Auto-generated method stub
            if(mCamera!=null)
            {
                //停止预览
                mCamera.stopPreview();
                //释放摄像头
                mCamera.release();
                mCamera = null;
            }
        }
        
    }
    
    //拍照回调
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback()
    {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) 
        {
            // TODO Auto-generated method stub
            //停止预览
            mCamera.stopPreview();
            Bitmap mBitmap;
            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //文件路径和文件名
            File pictureFile = new File(Environment.getExternalStorageDirectory(),"camera"+Integer.toString(whichPicture)+".jpg");    
            
            try 
            {
                FileOutputStream mFileOutputStream = new FileOutputStream(pictureFile);
                mBitmap=rotaingImageView(90,mBitmap);
                //将图像数据压入文件
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 75, mFileOutputStream);
                
                try {
                    //关闭输出流
                    mFileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } 
            catch (FileNotFoundException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }        
            displayToast("保存成功！");
            whichPicture++;
            //int degree = readPictureDegree(pictureFile.getAbsolutePath());  
           // System.out.println(degree);
            
            //开始预览
            mCamera.startPreview();
        }
        
    }; 
    
    //显示Toast函数
    private void displayToast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
    
    /**
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
		Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        		bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
    
      
}
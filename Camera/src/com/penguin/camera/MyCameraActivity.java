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
    //��¼������ǵڼ���ͼƬ
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
        //���û���������
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   
        //���հ�ť����    
        takePictureButton.setOnClickListener(new View.OnClickListener() 
        {
            
            @Override
            public void onClick(View v) 
            {
                // TODO Auto-generated method stub
                //����
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
            //������ͷ
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //��ȡ����ͷ����
            Camera.Parameters mParameters = mCamera.getParameters();
            //����ͼƬ��ʽ
            mParameters.setPictureFormat(PixelFormat.JPEG);

            mCamera.setParameters(mParameters); 
            //��ʼԤ��
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) 
        {
            // TODO Auto-generated method stub
            if(mCamera!=null)
            {
                //ֹͣԤ��
                mCamera.stopPreview();
                //�ͷ�����ͷ
                mCamera.release();
                mCamera = null;
            }
        }
        
    }
    
    //���ջص�
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback()
    {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) 
        {
            // TODO Auto-generated method stub
            //ֹͣԤ��
            mCamera.stopPreview();
            Bitmap mBitmap;
            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //�ļ�·�����ļ���
            File pictureFile = new File(Environment.getExternalStorageDirectory(),"camera"+Integer.toString(whichPicture)+".jpg");    
            
            try 
            {
                FileOutputStream mFileOutputStream = new FileOutputStream(pictureFile);
                mBitmap=rotaingImageView(90,mBitmap);
                //��ͼ������ѹ���ļ�
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 75, mFileOutputStream);
                
                try {
                    //�ر������
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
            displayToast("����ɹ���");
            whichPicture++;
            //int degree = readPictureDegree(pictureFile.getAbsolutePath());  
           // System.out.println(degree);
            
            //��ʼԤ��
            mCamera.startPreview();
        }
        
    }; 
    
    //��ʾToast����
    private void displayToast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    
	/**
	 * ��ȡͼƬ���ԣ���ת�ĽǶ�
	 * @param path ͼƬ����·��
	 * @return degree��ת�ĽǶ�
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
	 * ��תͼƬ
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //��תͼƬ ����
		Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // �����µ�ͼƬ
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        		bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
    
      
}
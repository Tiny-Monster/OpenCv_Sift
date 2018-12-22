package com.tinymonster.opencvstudy2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private Button button1;
    private ImageView imageView1;
    private ImageView imageView2;
    private static String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        initView();
    }
    private void initView(){
        button1=(Button)findViewById(R.id.button1);
        imageView1=(ImageView) findViewById(R.id.imageView1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void,Void,Bitmap>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        Mat test1=new Mat();
                        Mat test2=new Mat();
                        Mat out;
                        FeatureDetector SIFTdter = FeatureDetector.create(FeatureDetector.SIFT);//创建特征监测
                        DescriptorExtractor descriptorExtractor=DescriptorExtractor.create(DescriptorExtractor.SIFT);//描述子提取
                        DescriptorMatcher descriptorMatcher=DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);//描述子匹配 暴力匹配器
                        MatOfDMatch matchs=new MatOfDMatch();
                        Bitmap src_bitmap1= BitmapFactory.decodeResource(getResources(),R.drawable.photo7);
                        Bitmap src_bitmap2= BitmapFactory.decodeResource(getResources(),R.drawable.photo6);
                        Utils.bitmapToMat(src_bitmap1,test1);
                        Utils.bitmapToMat(src_bitmap2,test2);
                        Mat descriptors1=new Mat();
                        Mat descriptors2=new Mat();
                        MatOfKeyPoint kp1 = new MatOfKeyPoint();//特征点
                        MatOfKeyPoint kp2 = new MatOfKeyPoint();//特征点
                        SIFTdter.detect(test1,kp1);
                        SIFTdter.detect(test2,kp2);//监测特征点
                        descriptorExtractor.compute(test1,kp1,descriptors1);//计算描述子
                        descriptorExtractor.compute(test2,kp2,descriptors2);
                        descriptorMatcher.match(descriptors1,descriptors2,matchs);//进行匹配
                        out=drawMatchs(test1,kp1,test2,kp2,matchs,false);
                        Bitmap out_drawable =Bitmap.createBitmap(out.cols(),out.rows(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(out,out_drawable);
                        return out_drawable;
                    }
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        imageView1.setImageBitmap(bitmap);
                    }
                }.execute();
            }
        });
    }
    Mat drawMatchs(Mat img1,MatOfKeyPoint key1,Mat img2,MatOfKeyPoint key2,MatOfDMatch matches,boolean imageOnly){
        Mat out =new Mat();
        Mat im1 =new Mat();
        Mat im2 =new Mat();
        Imgproc.cvtColor(img1,im1,Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(img2,im2,Imgproc.COLOR_BGR2RGB);
        if(imageOnly){
            MatOfDMatch emptyMatch=new MatOfDMatch();
            MatOfKeyPoint emptyKey1=new MatOfKeyPoint();
            MatOfKeyPoint emptyKey2=new MatOfKeyPoint();
            Features2d.drawMatches(im1,emptyKey1,im2,emptyKey2,emptyMatch,out);
        }else {
            Features2d.drawMatches(im1,key1,im2,key2,matches,out);
        }
        Imgproc.cvtColor(out,out, Imgproc.COLOR_BGR2RGB);
        Core.putText(out,"src",new Point(img1.width()/2,30),Core.FONT_HERSHEY_PLAIN,2,new Scalar(0,255,255),3);
        Core.putText(out,"matched",new Point((img1.width()+img2.width())/2,30),Core.FONT_HERSHEY_PLAIN,2,new Scalar(255,0,0),3);
        return out;
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    System.loadLibrary("nonfree");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}


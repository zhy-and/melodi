package com.extraordinary.weaves.tendency.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by cjz on 2019/8/8.
 */

public class BookPageView extends FrameLayout{
    private boolean initFinished = false;
    /**本父空间宽度**/
    private int mWidth;
    /**本父空间高度**/
    private int mHeight;
    /**轮播ImageView表**/
    private List<ImageView> ivList = new ArrayList<>();
    /**上次触摸坐标，移动用**/
    private float prevX, prevY;
    /**图标最大的比例**/
    private float maxScale = 1f;
    /**图标最小的比例**/
    private float minScale = 0.5f;
    /**图标宽度**/
    private int unitWidth;
    /**图标高度**/
    private int unitHeight;

    /**11个轮播ImageView**/
    private  int count = 5;


    /**控件大小调整**/
    private float sizeRatio = 2.5f;
    /**拖动限速**/
    private float speedLimit = 600f;


    public BookPageView(Context context) {
        super(context);
    }

    public BookPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public List<ImageView> getIvList() {
        return ivList;
    }

    public interface ItemOnTopListener{
        /**传入顶部ImageView，方便用户修改图片**/
        void topItem(ImageView imageView);
    }
    private ItemOnTopListener itemOnTopListener;

    public ItemOnTopListener getItemOnTopListener() {
        return itemOnTopListener;
    }

    /**控件到达中线（顶部）时回调给同事**/
    public void setItemOnTopListener(ItemOnTopListener itemOnTopListener) {
        this.itemOnTopListener = itemOnTopListener;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!initFinished){
            int width = (int)(MeasureSpec.getSize(widthMeasureSpec) * 1f);
            int height = (int)(MeasureSpec.getSize(heightMeasureSpec) * 1f);
            mWidth = width;
            mHeight = height;
            this.setLayoutParams(new LayoutParams(width, height));
           createView(width, height);
            initFinished = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                prevX = event.getX();
                prevY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                translate(event.getX() - prevX, event.getY() - prevY);
                prevX = event.getX();
                prevY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                //根据Prevx prevy在ivList哪个控件范围内，然后将该整个对象队列反向移动，到中线居中为止
//                for(int i = 0; i < ivList.size(); i++){
//                    ImageView item = ivList.get(i);
//                    RectF rect = new RectF(item.getLeft() + (1 - item.getScaleX()) / 2 * item.getWidth(),
//                            item.getTop() + (1 - item.getScaleY() / 2 * item.getHeight()),
//                            item.getRight() + (1 - item.getScaleX()) / 2 * item.getWidth() + item.getScaleX() * item.getWidth(),
//                            item.getTop() + (1 - item.getScaleY()) / 2 * item.getTop() + item.getScaleY() * item.getHeight());
//                    if(rect.contains(prevX, prevY)){
//                        float imageViewXCenterPoint = (item.getX() + (1 - item.getScaleX()) / 2 * item.getWidth() + (item.getWidth() * item.getScaleX()) / 2);
//                        if(im){
//
//                        }
//                    }
//                }
                //自动跳转到最近中线的ImageView
                //先找到最近中线的ImageView
                float minDistance = Float.MAX_VALUE;
                ImageView minItem = null;
                boolean isOnRight = false;
                for(int i = 0; i < ivList.size(); i++){
                    ImageView imageView = ivList.get(i);
                    float imageViewXCenterPoint = (imageView.getX() + (1 - imageView.getScaleX()) / 2 * imageView.getWidth() + (imageView.getWidth() * imageView.getScaleX()) / 2);
                    float distance = imageViewXCenterPoint - mWidth / 2;
                    if(Math.abs(distance) < minDistance){
                        minDistance = Math.abs(distance);
                        minItem = imageView;
                        if(distance > 0){
                            isOnRight = true;
                        }
                    }
                }
                //逐步跳转到中线
                float imageViewXCenterPoint = 0;
                while (Math.abs(imageViewXCenterPoint - mWidth / 2) > 4){
                    if(isOnRight){
                        translate(-4, 0);
                    } else {
                        translate(4, 0);
                    }
                    imageViewXCenterPoint = (minItem.getX() + (1 - minItem.getScaleX()) / 2 * minItem.getWidth() + (minItem.getWidth() * minItem.getScaleX()) / 2);
                }
                break;
        }
        return true;
    }

    /**移动函数，@param distanceY 备用 **/
    public void translate(float distanceX, float distanceY) {
        //不让用户拖太快
        if(distanceX > speedLimit){
            distanceX = speedLimit;
        } else if(distanceX < -speedLimit){
            distanceX = -speedLimit;
        }
        float imageViewXCenterPoint;
        for (int i = 0; i <  ivList.size(); i++) {
            ImageView imageView = ivList.get(i);
            imageView.setX(imageView.getX() + distanceX);
            imageViewXCenterPoint = (imageView.getX() + (1 - imageView.getScaleX()) / 2 * imageView.getWidth() + (imageView.getWidth() * imageView.getScaleX()) / 2);
            if (imageViewXCenterPoint < mWidth / 2) { //如果控件越过中线
//                float newScale = maxScale  * imageViewXCenterPoint / (mWidth / 2);
                float newScale = minScale + (maxScale - minScale) * imageViewXCenterPoint / (mWidth / 2);
                imageView.setScaleX(newScale);
                imageView.setScaleY(newScale);
                if(itemOnTopListener != null){
                    itemOnTopListener.topItem(imageView);
                }
            } else {
//                float newScale = maxScale * (mWidth - imageViewXCenterPoint) / (mWidth / 2);
                float newScale = minScale + (maxScale - minScale) * (mWidth - imageViewXCenterPoint) / (mWidth / 2);
                imageView.setScaleX(newScale);
                imageView.setScaleY(newScale);
                if(itemOnTopListener != null){
                    itemOnTopListener.topItem(imageView);
                }
            }
            if(imageView.getScaleX() < 0.6f){
                imageView.setAlpha(0.3f);
            } else {
//                imageView.setAlpha(1f);
                imageView.setAlpha((float)Math.pow(imageView.getScaleX(), 3));
            }
        }
        //控件循环
        for (int i = 0; i <  ivList.size(); i++) {
            ImageView imageView = ivList.get(i);
            if(imageView.getX() + imageView.getWidth() * (1 - imageView.getScaleX()) > mWidth){
                imageView.setX(ivList.get(0).getX() - imageView.getWidth() / sizeRatio);
                ivList.remove(i);
                ivList.add(0, imageView);
                break;
            } else if(imageView.getX() < 0  - unitWidth / sizeRatio){
                imageView.setX(ivList.get(ivList.size() - 1).getX() + ivList.get(ivList.size() - 1).getWidth() / sizeRatio);
                ivList.remove(i);
                ivList.add(ivList.size(), imageView);
                break;
            }
        }
        reSortLayer();
    }

    /**重新排序图层**/
    private void reSortLayer(){
        List<ImageView> ivListCopy = new ArrayList<>();
        ivListCopy.addAll(ivList);
        Collections.sort(ivListCopy, new Comparator<ImageView>() {
            @Override
            public int compare(ImageView o1, ImageView o2) {
                if(o1.getScaleX() > o2.getScaleX()){
                    return  1;
                } else {
                    return -1;
                }
            }
        });
        removeAllViews();
        for(int i = 0; i < ivListCopy.size(); i++){
            addView(ivListCopy.get(i));
        }
    }

    /**创建图层**/
    private void createView(int width, int height) {
        unitWidth = (int)(width * sizeRatio / count);
        unitHeight = (int) (height * 2 * sizeRatio / count);
//        unitHeight = (int) (height  * sizeRatio / count);
        for(int i = 0; i < count; i++){
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(unitWidth, unitHeight));
            addView(imageView);
            ivList.add(imageView);
            imageView.setTag(new Integer(i));
        }
        for(int i = 0; i < ivList.size(); i++){
//            ivList.get(i).setX(((float)unitWidth / (float)mWidth) * unitWidth * i);
                ivList.get(i).setX(((float)mWidth / (float)count) * i - unitWidth * 0.3f);
            Log.i("Unit" + i, ivList.get(i).getX() + "");
//            ivList.get(i).setY(mHeight- unitHeight);
            ivList.get(i).setY(mHeight /2 - unitHeight/2);
//            ivList.get(i).setY(unitHeight / sizeRatio / 3);
//            ivList.get(i).setBackgroundColor(Color.YELLOW);
//            ivList.get(i).setImageResource(R.drawable.test1);
//            ivList.get(i).setScaleType(ImageView.ScaleType.CENTER);

            if(i < ivList.size() / 2){
                ivList.get(i).setScaleX(minScale + (maxScale - minScale) * ((i ) / (ivList.size() / 2f)) );
                ivList.get(i).setScaleY(minScale + (maxScale - minScale) * ((i ) / (ivList.size() / 2f)) );
                Log.i("比例", (minScale + maxScale - minScale) * ((i ) / (ivList.size() / 2f))  + "");
            } else {
                ivList.get(i).setScaleX(minScale + (maxScale - minScale) * ((ivList.size() - i) / (ivList.size() / 2f)) );
                ivList.get(i).setScaleY(minScale +(maxScale - minScale) *((ivList.size() - i) / (ivList.size() / 2f)) );
                Log.i("比例", minScale + (maxScale - minScale) * ((ivList.size() - i) / (ivList.size() / 2f))  + "");
            }
            if(ivList.get(i).getScaleX() < 0.6f){
//                ivList.get(i).setAlpha(1f);
                ivList.get(i).setAlpha(0.3f);
            } else {
//                ivList.get(i).setAlpha(1f);
                ivList.get(i).setAlpha((float)Math.pow(ivList.get(i).getScaleX(), 3));
            }
        }
        reSortLayer();
    }

}
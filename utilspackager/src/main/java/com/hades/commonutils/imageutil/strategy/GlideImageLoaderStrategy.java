package com.hades.commonutils.imageutil.strategy;

import android.content.Context;
import android.os.Looper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hades.commonutils.imageutil.CacheSizeUtil;
import com.hades.commonutils.imageutil.ImageParameter;
import com.hades.commonutils.imageutil.ImageLoaderStrategy;


/**
 * Created by Hades on 2017/2/16.
 */

public class GlideImageLoaderStrategy implements ImageLoaderStrategy {

    @Override
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                //MainThread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            } else {
                //非UI Thread
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }

    @Override
    public String getCacheSize(Context context) {
        try {
            return CacheSizeUtil.getFormatSize(CacheSizeUtil.getFolderSize(Glide.getPhotoCacheDir(context.getApplicationContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void loadImage(ImageParameter parameter) {
        loadNormal(parameter.getImageView().getContext(), parameter);
    }

    @Override
    public void loadImage(Context context, ImageParameter parameter) {
        loadNormal(context, parameter);
    }


    private void loadNormal(Context context,ImageParameter parameter){
        //去掉动画 解决与CircleImageView冲突的问题 这个只是其中的一个解决方案
        //使用SOURCE 图片load结束再显示而不是先显示缩略图再显示最终的图片（导致图片大小不一致变化）
        final long startTime = System.currentTimeMillis();
        Glide.with(context)
                .load(parameter.getImageUrl())
                .error(parameter.getErrorHolder())//load失敗的Drawable
                .placeholder(parameter.getPlaceHolder())//loading時候的Drawable
                .dontAnimate() //去掉淡入淡出
                // .animate()//設置load完的動畫
//                .centerCrop()//中心切圖, 會填滿
                .fitCenter()//中心fit, 以原本圖片的長寬為主
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                    })
                .into(parameter.getImageView());
    }
}

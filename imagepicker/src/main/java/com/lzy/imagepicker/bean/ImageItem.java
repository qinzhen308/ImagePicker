package com.lzy.imagepicker.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：图片信息
 * 修订历史：
 * ================================================
 */
public class ImageItem implements Serializable {

    public String name;     //图片的名字
    public String path;     //图片的路径
    public long size;     //图片的大小
    public int width;    //图片的宽度
    public int height;   //图片的高度
    public String mimeType; //图片的类型
    public long addTime;  //图片的创建时间
    public String url;      //上传后的图片路径
    public boolean SelectBoolean=false;//是否选择
    public int SelectNumber=-1;//选择的数字
    public int position=-1;//这张图片在相册的位子

    /**
     * 图片的路径和创建时间相同就认为是同一张图片
     */
    @Override
    public boolean equals(Object o) {
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path) && this.addTime == other.addTime;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}

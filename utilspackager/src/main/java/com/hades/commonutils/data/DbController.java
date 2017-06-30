package com.hades.commonutils.data;

import android.content.Context;

import com.litesuits.orm.LiteOrm;


/**
 * Created by Hades on 2016/11/25.
 * 全局单例，因此在切换平台时需要释放数据库调用releaseReference()
 * 退出登录，删除对应表或数据库
 */
public class DbController {

    public static DbController getInstance(){
        return SingleInstace.instance;
    }


    private LiteOrm mLiteOrm;
    public void initDb(Context context, String dbname){
        if (mLiteOrm == null){
            mLiteOrm = LiteOrm.newSingleInstance(context, dbname);
            mLiteOrm.setDebugged(true);
        }
    }

    public LiteOrm getmLiteOrm() {
        return mLiteOrm;
    }

    public void closeDb(){
        if (null != mLiteOrm){
            mLiteOrm.close();
            mLiteOrm = null;
        }
    }

    private static class SingleInstace{
        public static DbController instance = new DbController();
    }

    private DbController(){}

}

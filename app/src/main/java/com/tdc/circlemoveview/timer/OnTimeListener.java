package com.tdc.circlemoveview.timer;

/**
 * 描述：TODO
 * 作者：gong.xl
 * 创建日期：2018/5/23 下午3:14
 * 修改日期: 2018/5/23
 * 修改备注：
 * 邮箱：gong.xl@belle.com.cn
 */

public interface OnTimeListener {

    /**
     * 计时，每秒回调一次
     *
     * @param time 00:01
     */
    void timeProceed(String time);

    /**
     * 计时错误，计时时间小于2s会回调次方法
     */
    void timeError();


    /**
     * 计时停止，计时满60s会回调此方法
     */
    void timeStop();
}

package com.example.asus1.pm;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class BluetoothViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private BluetoothDevice mDevice;
    private Handler mHanlder;
    private TextView mDeviceName;
    private TextView mTitle;
    private static final String TAG = "BluetoothViewHolder";

    public BluetoothViewHolder(@NonNull View itemView,boolean click,Handler handler) {
        super(itemView);
        Log.d(TAG, "BluetoothViewHolder: "+click);
        mHanlder = handler;
        if(click){
            mDeviceName = itemView.findViewById(R.id.tv_bluetooth_name);
            mDeviceName.setOnClickListener(this);
        }else {
            mTitle = itemView.findViewById(R.id.tv_bluetooth);
        }
    }

    public void setData(BluetoothDevice data){
        mDevice = data;
        mDeviceName.setText(mDevice.getName());
    }

    public void setData(String text){
        mTitle.setText(text);
    }

    @Override
    public void onClick(View v) {
        mHanlder.obtainMessage(MainActivity.MSG_DEVICE, 0,0,mDevice.getAddress()).sendToTarget();
    }
}

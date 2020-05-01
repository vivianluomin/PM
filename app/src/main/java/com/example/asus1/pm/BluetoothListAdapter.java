package com.example.asus1.pm;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> mPairedDevices;
    private List<BluetoothDevice> mFindedDeviced;
    private static final String TAG = "BluetoothListAdapter";

    private int TITLE = 1;
    private int DEVICE = 2;
    private Handler mHanlder;

    public BluetoothListAdapter(Context context, List<BluetoothDevice> paireds,
                                List<BluetoothDevice> finds, Handler handler) {
        mContext = context;
        mPairedDevices = paireds;
        mFindedDeviced = finds;
        mHanlder = handler;
    }

    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        boolean click = true;
        if(i == TITLE){
            view = LayoutInflater.from(mContext).inflate(R.layout.view_bluetooth_matched,viewGroup,false);
            click = false;
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_bluetooth_item,viewGroup,false);
            click = true;
        }

        Log.d(TAG, "onCreateViewHolder: "+click+"----"+i);

        return new BluetoothViewHolder(view,click,mHanlder);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder bluetoothViewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: "+i);
        Log.d(TAG, "onBindViewHolder: "+mPairedDevices.size()+"---"+mFindedDeviced.size());
        if(i != 0 && i < mPairedDevices.size()+1){
            bluetoothViewHolder.setData(mPairedDevices.get(i-1));
        }else if(i != 0 && i == mPairedDevices.size()+1){
            bluetoothViewHolder.setData("可用设备");
        }else if(i != 0 && i< mPairedDevices.size()+2+mFindedDeviced.size()){
            bluetoothViewHolder.setData(mFindedDeviced.get(i-2-mPairedDevices.size()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || position == mPairedDevices.size()+1){
            return TITLE;
        }else {
            return DEVICE;
        }
    }

    @Override
    public int getItemCount() {
        int size = 2+mPairedDevices.size()+mFindedDeviced.size();
        Log.d(TAG, "getItemCount: "+size);
        return size;
    }
}

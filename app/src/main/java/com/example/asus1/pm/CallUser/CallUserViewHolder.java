package com.example.asus1.pm.CallUser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus1.pm.R;


public class CallUserViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private TextView mName;
    private TextView mPhone;
    private LinearLayout mRoot;
    private User mData;
    private Handler mHandler;

    public CallUserViewHolder(@NonNull View itemView, Context context,Handler handler) {
        super(itemView);
        mContext = context;
        mHandler = handler;
        mName = itemView.findViewById(R.id.tv_name);
        mPhone = itemView.findViewById(R.id.tv_phone);
        mRoot = itemView.findViewById(R.id.linear_root);
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + mData.phoone);
                intent.setData(data);
                mContext.startActivity(intent);

            }
        });
        mRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Message message = new Message();
                message.obj = mData;
                mHandler.sendMessage(message);
                return true;
            }
        });
    }

    public void setData(User data){
        mData = data;
        mName.setText(data.name);
        mPhone.setText(data.phoone);
    }
}

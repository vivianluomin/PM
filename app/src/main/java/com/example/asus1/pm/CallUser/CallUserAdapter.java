package com.example.asus1.pm.CallUser;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus1.pm.R;

import java.util.List;

public class CallUserAdapter extends RecyclerView.Adapter<CallUserViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private Handler mHandler;

    public CallUserAdapter(Context context, List<User> users, Handler handler) {
        mContext = context;
        mUsers = users;
        mHandler = handler;
    }

    @NonNull
    @Override
    public CallUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_call_user_item,viewGroup,false);
        return new CallUserViewHolder(view,mContext,mHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull CallUserViewHolder callUserViewHolder, int i) {
        callUserViewHolder.setData(mUsers.get(i));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}

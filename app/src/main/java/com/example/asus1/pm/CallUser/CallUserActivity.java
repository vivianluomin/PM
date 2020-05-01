package com.example.asus1.pm.CallUser;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus1.pm.AppSharePreferenceMrg;
import com.example.asus1.pm.BaseActivity;
import com.example.asus1.pm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallUserActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ImageView mBack;
    private TextView mAddUser;
    private CallUserAdapter mAdapter;
    private List<User> mUsersList = new ArrayList<>();
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_user);
        init();
    }

    private void init(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAddUser = findViewById(R.id.tv_add_user);
        mAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
//        mUsersList.add(new User("小明","15378149488"));
        mAdapter = new CallUserAdapter(this,mUsersList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        getUser();
    }

    private void getUser(){
        new Thread(new Runnable() {
            @Override
            public void run() {
              Map<String,String> user = (Map<String, String>) AppSharePreferenceMrg.getAll(AppSharePreferenceMrg.CALL_USER,CallUserActivity.this);
              mUsersList.clear();
                for(Map.Entry<String, String> entry : user.entrySet()){
                    String mapKey = entry.getKey();
                    String mapValue = entry.getValue();
                    mUsersList.add(new User(mapKey,mapValue));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此时已在主线程中，更新UI
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void addUser(){

        final Dialog dialog = new Dialog(this);
        mDialog = dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        //将自定义布局设置进去
        dialog.setContentView(dialogView);
        //设置指定的宽高
        WindowManager.LayoutParams lp    = new WindowManager.LayoutParams();
        Window  window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        window.setAttributes(lp);

        //设置点击其它地方不让消失弹窗
        dialog.setCancelable(false);

        TextView confirm = dialogView.findViewById(R.id.tv_confirm);
        TextView cancel = dialogView.findViewById(R.id.tv_cancel);
        final EditText nameEdit = dialogView.findViewById(R.id.edit_name);
        final EditText phoneEdit = dialogView.findViewById(R.id.edit_phone);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(nameEdit.getText().toString(),phoneEdit.getText().toString());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void updateUser(final String name, final String phone){
        if(name.equals("")||phone.equals("")){
            return;
        }

        AppSharePreferenceMrg.put(AppSharePreferenceMrg.CALL_USER,
                CallUserActivity.this,name,phone);
        mDialog.dismiss();

        mUsersList.add(new User(name,phone));
        mAdapter.notifyDataSetChanged();

    }
}

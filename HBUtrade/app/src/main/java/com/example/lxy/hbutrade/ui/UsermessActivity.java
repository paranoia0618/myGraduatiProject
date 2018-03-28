package com.example.lxy.hbutrade.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lxy.hbutrade.R;
import com.example.lxy.hbutrade.base.BaseActivity;
import com.example.lxy.hbutrade.entity.MyUser;
import com.example.lxy.hbutrade.util.ShareUtils;
import com.example.lxy.hbutrade.util.UtilTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lxy.hbutrade.R.id.btn_update_ok;
import static com.example.lxy.hbutrade.R.id.et_age;
import static com.example.lxy.hbutrade.R.id.et_desc;
import static com.example.lxy.hbutrade.R.id.et_sex;
import static com.example.lxy.hbutrade.R.id.et_username;
import static com.example.lxy.hbutrade.R.id.profile_image2;

/**
 * Created by LXY on 2018/3/21.
 */

public class UsermessActivity extends BaseActivity{
    @BindView(profile_image2)
    CircleImageView profileImage2;
    @BindView(btn_update_ok)
    Button btnUpdateOk;
    @BindView(R.id.edit_user)
    TextView editUser;
    @BindView(et_username)
    EditText etUsername;
    @BindView(et_sex)
    EditText etSex;
    @BindView(et_age)
    EditText etAge;
    @BindView(et_desc)
    EditText etDesc;


    private Boolean b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermess);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {

        //默认是不可点击的/不可输入
        setEnabled(false);

        //设置具体的值
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        etUsername.setText(userInfo.getUsername());
        etAge.setText(userInfo.getAge() + "");
        etSex.setText(userInfo.isSex() ? getString(R.string.text_boy) : getString(R.string.text_girl_f));
        etDesc.setText(userInfo.getDesc());

        b= ShareUtils.getBoolean(this,"image_modify",false);

        if(b==true){
            UtilTools.getImageToShare(this,profileImage2);
        }

    }



    private void setEnabled(boolean b) {
        etUsername.setEnabled(b);
        etSex.setEnabled(b);
        etAge.setEnabled(b);
        etDesc.setEnabled(b);
    }


    @OnClick({ R.id.edit_user, btn_update_ok,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_user:
                setEnabled(true);
                btnUpdateOk.setVisibility(View.VISIBLE);
                break;
            case btn_update_ok:
                updateUser();
                break;
        }
    }

    private void updateUser() {
        //1.拿到输入框的值
        String username = etUsername.getText().toString();
        String age = etAge.getText().toString();
        String sex = etSex.getText().toString();
        String desc = etDesc.getText().toString();

        //2.判断是否为空
        if (!TextUtils.isEmpty(username) & !TextUtils.isEmpty(age) & !TextUtils.isEmpty(sex)) {
            //3.更新属性
            MyUser user = new MyUser();
            user.setUsername(username);
            user.setAge(Integer.parseInt(age));
            //性别
            if (sex.equals(getString(R.string.text_boy))) {
                user.setSex(true);
            } else {
                user.setSex(false);
            }
            //简介
            if (!TextUtils.isEmpty(desc)) {
                user.setDesc(desc);
            } else {
                user.setDesc(getString(R.string.text_nothing));
            }
            BmobUser bmobUser = BmobUser.getCurrentUser();
            user.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //修改成功
                        setEnabled(false);
                        btnUpdateOk.setVisibility(View.GONE);
                        Toast.makeText(UsermessActivity.this, R.string.text_editor_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UsermessActivity.this, R.string.text_editor_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.text_tost_empty), Toast.LENGTH_SHORT).show();
        }

    }
}








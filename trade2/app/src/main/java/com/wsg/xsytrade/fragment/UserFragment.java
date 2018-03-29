package com.wsg.xsytrade.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wsg.xsytrade.R;
import com.wsg.xsytrade.ui.AboutActivity;
import com.wsg.xsytrade.ui.BackActivity;
import com.wsg.xsytrade.ui.LoginActivity;
import com.wsg.xsytrade.ui.MyBuyActivity;
import com.wsg.xsytrade.ui.MySellActivity;
import com.wsg.xsytrade.ui.UpdateActivity;
import com.wsg.xsytrade.ui.usermess;
import com.wsg.xsytrade.util.L;
import com.wsg.xsytrade.util.ShareUtils;
import com.wsg.xsytrade.util.UtilTools;
import com.wsg.xsytrade.view.CustomDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.wsg.xsytrade.R.id.profile_image;

/**
 * 项目名：XSYTrade
 * 包名：com.wsg.xsytrade.fragment
 * 文件名：UserFragment
 * 创建者：wsg
 * 创建时间：2017/9/16  21:36
 * 描述：个人中心
 */

public class UserFragment extends Fragment implements View.OnClickListener {
    @BindView(profile_image)
    CircleImageView profileImage;
    @BindView(R.id.edit_user)
    TextView editUser;
    @BindView(R.id.tv_sell)
    TextView tvSell;
    @BindView(R.id.tv_buy)
    TextView tvBuy;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.btn_Exit)
    TextView btnEit;
    Unbinder unbinder;



    private CustomDialog dialog;
    private Button btn_camera;
    private Button btn_picture;
    private Button btn_cancel;
    private Boolean b;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        //初始化dialog
        dialog = new CustomDialog(getActivity(), 0, 0,
                R.layout.dialog_photo, R.style.pop_anim_style, Gravity.BOTTOM, 0);
        //提示框以外点击无效
        dialog.setCancelable(false);

        btn_camera = (Button) dialog.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(this);
        btn_picture = (Button) dialog.findViewById(R.id.btn_picture);
        btn_picture.setOnClickListener(this);
        btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);


        b= ShareUtils.getBoolean(getActivity(),"image_modify",false);

        if(b==true){
            UtilTools.getImageToShare(getActivity(),profileImage);
        }

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({profile_image, R.id.edit_user, R.id.tv_sell, R.id.tv_buy, R.id.tv_back, R.id.tv_about, R.id.tv_update,R.id.btn_Exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case profile_image:
                dialog.show();
                break;
            case R.id.edit_user:
                startActivity(new Intent(getActivity(), usermess.class));
                break;
            case R.id.tv_sell:
                startActivity(new Intent(getActivity(), MySellActivity.class));
                break;
            case R.id.tv_buy:
                startActivity(new Intent(getActivity(), MyBuyActivity.class));
                break;
            case R.id.tv_back:
                startActivity(new Intent(getActivity(), BackActivity.class));
                break;
            case R.id.tv_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.tv_update:
                startActivity(new Intent(getActivity(), UpdateActivity.class));
                break;
            case R.id.btn_Exit:
                Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
            case R.id.btn_camera:
                toCamera();
                break;
            case R.id.btn_picture:
                toPicture();
                break;

        }

    }

    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private File tempFile = null;

    //跳转相机
    private void toCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        dialog.dismiss();
    }

    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        dialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //拿到图片设置
                        setImageToView(data);
                        //既然已经设置了图片，我们原先的就应该删除
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                    break;
            }
        }
    }

    //裁剪
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            L.e("uri == null");
            return;
        }




        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        //发送数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //设置图片
    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            profileImage.setImageBitmap(bitmap);
            //保存
            UtilTools.putImageToShare(getActivity(), profileImage);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}

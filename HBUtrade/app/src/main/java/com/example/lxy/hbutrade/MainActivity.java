package com.example.lxy.hbutrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.lxy.hbutrade.fragment.BuyFragment;
import com.example.lxy.hbutrade.fragment.MyMessageFragment;
import com.example.lxy.hbutrade.fragment.SellFragment;
import com.example.lxy.hbutrade.fragment.UserFragment;
import com.example.lxy.hbutrade.ui.ChatActivity;
import com.example.lxy.hbutrade.util.ShareUtils;
import com.example.lxy.hbutrade.util.StaticClass;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


//主页面

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.mTabLayout)
    TabLayout mTabLayout;
    //Title
    private List<String> mTitle;
    //Fragment
    private List<Fragment> mFragment;
    private MyMessageFragment conversationListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        ButterKnife.bind(this);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        initData();
        initViews();
    }

    private void initData() {
        ShareUtils.putBoolean(this, StaticClass.SHARE_IS_LOGIN,false);
        mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.text_sell));
        mTitle.add(getString(R.string.text_buy));
        mTitle.add(getString(R.string.text_message));
        mTitle.add(getString(R.string.text_user_info));



        conversationListFragment = new MyMessageFragment();

//
//        // run in a second
//        final long timeInterval = 1000;
//        Runnable runnable = new Runnable() {
//            public void run() {
//                while (true) {
//                    // ------- code for task to run
//                    conversationListFragment.refresh();
//                    // ------- ends here
//                    try {
//                        Thread.sleep(timeInterval);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();



        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            //            //隐藏标题栏
//            hideTitleBar();
            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
            }
        });




        mFragment = new ArrayList<>();
        mFragment.add(new SellFragment());
        mFragment.add(new BuyFragment());
        mFragment.add(conversationListFragment);
        mFragment.add(new UserFragment());

    }

    private void initViews() {
        //预加载
        mViewPager.setOffscreenPageLimit(mFragment.size());

        //mViewPager滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置适配器
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的item
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            //返回item的个数
            @Override
            public int getCount() {
                return mFragment.size();
            }

            //设置标题
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        //绑定
        mTabLayout.setupWithViewPager(mViewPager);

    }
}

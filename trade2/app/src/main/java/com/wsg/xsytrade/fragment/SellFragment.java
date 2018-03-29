package com.wsg.xsytrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.EaseConstant;
import com.wsg.xsytrade.R;
import com.wsg.xsytrade.adapter.SellAdapter;
import com.wsg.xsytrade.entity.Sell;
import com.wsg.xsytrade.ui.ChatActivity;
import com.wsg.xsytrade.ui.NewSellActivity;
import com.wsg.xsytrade.ui.SearchSellActivity;
import com.wsg.xsytrade.util.L;
import com.wsg.xsytrade.view.BannerView;
import com.wsg.xsytrade.view.UPMarqueeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * 项目名：XSYTrade
 * 包名：com.wsg.xsytrade.fragment
 * 文件名：SellFragment
 * 创建者：wsg
 * 创建时间：2017/9/16  21:37
 * 描述：闲置求购
 */

public class SellFragment extends Fragment implements SellAdapter.Callback{

    //仿淘宝首页的 淘宝头条滚动的自定义View
    private UPMarqueeView upview1;
    List<String> data = new ArrayList<>();
    List<View> views = new ArrayList<>();
    //bannerView
    private int[] imgs = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d};
    private List<View> viewList;
    BannerView bannerView;

    //刷新
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private String msearch;
    private List<Sell> mList = new ArrayList<>();
    private SellAdapter adapter;


    @BindView(R.id.sell_ed)
    EditText sellEd;
    @BindView(R.id.sell_search)
    ImageView sellSearch;
    @BindView(R.id.sell_write)
    ImageView sellWrite;
    @BindView(R.id.sell_lv)
    ListView sellLv;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, null);
        unbinder = ButterKnife.bind(this, view);

        viewList = new ArrayList<View>();
        //bannerview
        for (int i = 0; i < imgs.length; i++) {
            ImageView image = new ImageView(getActivity());
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //设置显示格式
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageResource(imgs[i]);
            viewList.add(image);
        }
        bannerView = (BannerView) view.findViewById(R.id.banner);
        bannerView.startLoop(true);

        bannerView.setViewList(viewList);

        upview1 = (UPMarqueeView) view.findViewById(R.id.upview1);
        /*initParam();*/

        initdata();

        initView();
        return view;
    }

    /**
     * 初始化界面程序
     */
    private void initView() {

        setView();
        upview1.setViews(views);
        /**
         * 设置item_view的监听
         */
        upview1.setOnItemClickListener(new UPMarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Toast.makeText(getActivity(), "你点击了第几个items" + position, Toast.LENGTH_SHORT).show();

            }
        });


        //下拉刷新
        adapter = new SellAdapter(getActivity(), mList,this);
        sellLv.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);   //设置下拉刷新进度条的颜色
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();   //进行刷新操作
                swipeRefresh.setRefreshing(false);
            }



        });




    }

    /**
     * 初始化需要循环的View
     * 为了灵活的使用滚动的View，所以把滚动的内容让用户自定义
     * 假如滚动的是三条或者一条，或者是其他，只需要把对应的布局，和这个方法稍微改改就可以了，
     */
    private void setView() {
        for (int i = 0; i < data.size(); i = i + 2) {
            final int position = i;
            //设置滚动的单个布局
            LinearLayout moreView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_view, null);
            //初始化布局的控件
            TextView tv1 = (TextView) moreView.findViewById(R.id.tv1);
            TextView tv2 = (TextView) moreView.findViewById(R.id.tv2);

            /**
             * 设置监听
             */
            moreView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), position + "你点击了" + data.get(position).toString(), Toast.LENGTH_SHORT).show();
                }
            });
            /**
             * 设置监听
             */
            moreView.findViewById(R.id.rl2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), position + "你点击了" + data.get(position + 1).toString(), Toast.LENGTH_SHORT).show();
                }
            });
            //进行对控件赋值
            tv1.setText(data.get(i).toString());
            if (data.size() > i + 1) {
                //因为淘宝那儿是两条数据，但是当数据是奇数时就不需要赋值第二个，所以加了一个判断，还应该把第二个布局给隐藏掉
                tv2.setText(data.get(i + 1).toString());
            } else {
                moreView.findViewById(R.id.rl2).setVisibility(View.GONE);
            }

            //添加到循环滚动数组里面去
            views.add(moreView);
        }
    }

    /**
     * 初始化数据
     */
    private void initdata() {
        data = new ArrayList<>();
        data.add("因为快要毕业了，书本低价出售！！！");
        data.add("iPhone8感人二手价，必须买买买买!!!!");
        data.add("简直是白菜价！王者玩家低价甩卖游戏号");
        data.add("口红低价转卖，有意者请尽快联系！！");
        data.add("二手电脑低价转卖，友情价，速联系！");
//        data.add("竟不是小米乐视！看水抢了骁龙821首发了！！！");

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.sell_search, R.id.sell_write})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sell_search:
                msearch = sellEd.getText().toString().trim();

                if (!TextUtils.isEmpty(msearch)){
                    Intent intent=new Intent(getActivity(), SearchSellActivity.class);
                    intent.putExtra("sell",msearch);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(),"亲，输入框不能为空~~~~",Toast.LENGTH_SHORT).show();
                }



                break;
            case R.id.sell_write:
                //添加的逻辑
                startActivity(new Intent(getActivity(), NewSellActivity.class));
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
         /*
        1、获取数据
        2、将数据添加到集合
        3、设置适配器
         */

        //1、获取表中存放的数据
        BmobQuery<Sell> query = new BmobQuery<Sell>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //按照时间降序
        query.order("-createdAt");
        //执行查询，第一个参数为上下文，第二个参数为查找的回调
        query.findObjects(new FindListener<Sell>() {
            @Override
            public void done(List<Sell> list, BmobException e) {
                if (e == null) {

                    //2、已经获取到集合
                    mList.clear();
                    mList.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "数据获取失败，请检查网络，亲~~~", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void click(View v) {

        int i=(Integer) v.getTag();

        String name =mList.get(i).getMessageid();


        L.d(EaseConstant.EXTRA_USER_ID);
        L.d(name);


        Intent chat = new Intent(getActivity(),ChatActivity.class);
        chat.putExtra(EaseConstant.EXTRA_USER_ID,name);  //对方账号
        chat.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE); //单聊模式


        startActivity(chat);




    }

}

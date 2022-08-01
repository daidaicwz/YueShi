package com.example.yueshi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yueshi.bean.Gift;
import com.example.yueshi.bean.MyUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class GiftActivity extends AppCompatActivity {

    private MyUser user;
    private ImageView back;
    List<Gift> myGiftList = new ArrayList<Gift>();
    private ListView list;
    private TextView myPoint;
    private SwipeRefreshLayout swiperLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        Bmob.initialize(this, "0f9fca80b537d756e72c2a70c3420f92");
        user = new MyUser();
        user = BmobUser.getCurrentUser(MyUser.class);
        getGiftData();
        back = (ImageView) findViewById(R.id.layoutgiftImageView1);
        list = (ListView) findViewById(R.id.mygiftlist);
        myPoint = (TextView) findViewById(R.id.my_point);
        swiperLayout = (SwipeRefreshLayout)findViewById(R.id.layoutmygiftSwipeRefreshLayout1);
        swiperLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                重新刷新
                getGiftData();
//                延时三秒刷新完成
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        myPoint.setText(user.getPoint().toString());

        //list下拉事件，确保swiper在没有数据/到顶部的item时候再刷新，避免在向上滑动的时候就刷新
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (list != null && list.getChildCount() > 0) {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (list != null && list.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                swiperLayout.setEnabled(enable);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getGiftData() {
        Bmob.initialize(this, "0f9fca80b537d756e72c2a70c3420f92");
        BmobQuery<Gift> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(500);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<Gift>() {
            @Override
            public void done(List<Gift> gifts, BmobException e) {
                if (e == null) {
                        myGiftList.clear();
                        for (int i = 0; i < gifts.size(); i++) {
                            myGiftList.add(gifts.get(i));
                        }
                        // 列表数据加载完成
                        list.setAdapter(new ItemListAdapter());
                    }
                }
        });
    }

    class ItemListAdapter extends BaseAdapter{
        private Gift gift;

        @Override
        public int getCount() {
            if (myGiftList.size() > 0) {
                return myGiftList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) { return myGiftList.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final GiftActivity.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new GiftActivity.ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_gift, null);
                viewHolder.giftimg = (ImageView) convertView.findViewById(R.id.iv_gift);
                viewHolder.giftname = (TextView) convertView.findViewById(R.id.tv_giftName);
                viewHolder.giftpoint = (TextView) convertView.findViewById(R.id.tv_spendPoint);
                viewHolder.btn_gift = (Button) convertView.findViewById(R.id.btn_gift);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GiftActivity.ItemListAdapter.ViewHolder) convertView.getTag();
            }

            gift = myGiftList.get(position);
            viewHolder.giftname.setText(gift.getGiftName());
            viewHolder.giftpoint.setText(gift.getSpendPoint().toString());

            Picasso.with(GiftActivity.this)
                    .load(gift.getGiftPicture().getUrl().replace("https","http"))
                    .placeholder(R.drawable.pic1)//加载过程中的图片显示
                    .error(R.drawable.pic1)//加载失败中的图片显示
                    .into(viewHolder.giftimg);

            viewHolder.btn_gift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gift mygift = new Gift();
                    mygift.setObjectId(myGiftList.get(position).getObjectId());
                    BmobRelation relation = new BmobRelation();
                    relation.add(user);
                    mygift.setGiftUser(relation);
                    mygift.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Toast.makeText(GiftActivity.this,"兑换成功",Toast.LENGTH_SHORT).show();
                            user.setPoint(user.getPoint()-gift.getSpendPoint());
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                }
                            });
                        }
                    });
                }
            });

            return convertView;
        }

        public class ViewHolder {
            public ImageView giftimg;//礼品图片
            public TextView giftname;//礼品名
            public TextView giftpoint;//礼品积分数
            public Button btn_gift;//兑换按钮
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getGiftData();

    }

}
package com.example.yueshi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yueshi.bean.Comment;
import com.example.yueshi.bean.MyUser;
import com.example.yueshi.bean.Topic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    private ImageView back;
    private Button search_btn;
    private String search_content;
    private EditText edit;
    List<Topic> mPostList = new ArrayList<>();
    private ListView list;
    private SwipeRefreshLayout swiperLayout;
    private ImageView ivEmpty;//无内容的图片
    private MyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //初始化
        Bmob.initialize(this, "0f9fca80b537d756e72c2a70c3420f92");
        user = new MyUser();
        user = BmobUser.getCurrentUser(MyUser.class);

        back = (ImageView)findViewById(R.id.iv_fanhui);
        search_btn = (Button)findViewById(R.id.bt_search1);
        edit = (EditText)findViewById(R.id.et_search);
        list = (ListView)findViewById(R.id.lv_topic_search);
        ivEmpty = (ImageView)findViewById(R.id.iv_emptyGirl1);

        //返回热门页
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //搜索
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_content = edit.getText().toString().trim();
                if(search_content.equals("")){
                    Toast.makeText(SearchActivity.this,"搜索内容为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    mPostList.clear();
                    getTopicData();
                    hintKeyBoard();
                }
            }
        });

        //刷新获取数据
        swiperLayout = (SwipeRefreshLayout) findViewById(R.id.swiperlayout_search);
        swiperLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                重新刷新
                getTopicData();
//                延时三秒刷新完成
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        //item点击事件，将帖子数据带到评论页
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private Intent intent;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = new Topic();
                topic.setObjectId(mPostList.get(position).getObjectId());
                BmobRelation relation = new BmobRelation();
                relation.add(user);
//             简化处理   浏览量+1
                topic.setView(relation);
                topic.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //浏览量+1
                        }
                    }
                });

                Bundle bundle = new Bundle();
                bundle.putString("topicAuthor", mPostList.get(position).getAuthor().getName());
                bundle.putString("topicTitle", mPostList.get(position).getTitle());
                bundle.putString("topicTextContent", mPostList.get(position).getContent());
                bundle.putString("topicAuthorSex", mPostList.get(position).getAuthor().getSex());
                bundle.putString("topicPostTime", mPostList.get(position).getCreatedAt());
                bundle.putString("topicId", mPostList.get(position).getObjectId());
                if (mPostList.get(position).getPicture() != null) {
                // 帖子有图片
                    bundle.putString("topicPicture",mPostList.get(position).getPicture().getUrl());
                }
                // 跳转至帖子详情页
                intent = new Intent(SearchActivity.this,PostDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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

    }


    //帖子适配器
    class ItemListAdapter extends BaseAdapter {
        private Topic search_topic;

        //适配器
        @Override
        public int getCount() {

            if (mPostList.size() > 0) {
                return mPostList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mPostList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_topic, null);
                viewHolder.rl=(RelativeLayout)convertView.findViewById(R.id.fragmentlist1RelativeLayout1);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.imageView);
                viewHolder.viewcount = (TextView) convertView.findViewById(R.id.listfabuTextView6);
                viewHolder.likes = (ImageView) convertView.findViewById(R.id.fragmentlist1ImageView1);
                viewHolder.commentcount = (TextView) convertView.findViewById(R.id.listfabuTextView5);
                viewHolder.likescount = (TextView) convertView.findViewById(R.id.listfabuTextView7);

                viewHolder.name = (TextView) convertView.findViewById(R.id.listfabuTextView1);
                viewHolder.topicTitle = (TextView) convertView.findViewById(R.id.listfabuTextView3);
                viewHolder.topicText = (TextView) convertView.findViewById(R.id.listfabuTextView4);
                viewHolder.time = (TextView) convertView.findViewById(R.id.listfabuTextView2);
                viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.listfabuCircleImageView1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ItemListAdapter.ViewHolder) convertView.getTag();
            }

            search_topic = mPostList.get(position);
            if(search_topic.getTitle().contains(search_content)||search_topic.getContent().contains(search_content)||search_topic.getAuthor().getUsername().contains(search_content))
            {
                if (search_topic.getPicture() == null) {
                    //帖子没有图片
                    viewHolder.img.setVisibility(View.GONE);
                } else {
                    //帖子有图片
                    viewHolder.img.setVisibility(View.VISIBLE);
                    //Picasso使用了流式接口的调用方式 毕加索图片加载框架
                    /*android:usesCleartextTraffic="true"  一定注意设置*/
                    Picasso.with(SearchActivity.this)
                            .load(search_topic.getPicture().getUrl().replace("https","http"))
                            .placeholder(R.drawable.pic1)//加载过程中的图片显示
                            .error(R.drawable.pic1)//加载失败中的图片显示
                            .into(viewHolder.img);
                }

                MyUser m = new MyUser();
                m = search_topic.getAuthor();
                if ("男".equals(m.getSex())) {
                    viewHolder.icon.setImageResource(R.drawable.man);
                } else {
                    viewHolder.icon.setImageResource(R.drawable.lady);
                }
                viewHolder.time.setText(search_topic.getCreatedAt());
                viewHolder.name.setText(search_topic.getAuthor().getName());
                viewHolder.topicTitle.setText(search_topic.getTitle());
                viewHolder.topicText.setText(search_topic.getContent());

//              获取该帖子的所有浏览量
                BmobQuery<MyUser> myUserBmobQuery = new BmobQuery<>();
                Topic post = new Topic();
                post.setObjectId(search_topic.getObjectId());
                myUserBmobQuery.addWhereRelatedTo("view", new BmobPointer(post));
                myUserBmobQuery.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> myUserList, BmobException e) {
                        if (e == null) {
                            String viewCounts = String.valueOf(myUserList.size());
                            viewHolder.viewcount.setText(viewCounts);
                        }
                    }
                });

                //获取该帖子下的点赞数
                BmobQuery<MyUser> myUserBmobQuery1 = new BmobQuery<>();
                Topic post2 = new Topic();
                post2.setObjectId(search_topic.getObjectId());
                myUserBmobQuery1.addWhereRelatedTo("likes",new BmobPointer(post2));
                myUserBmobQuery1.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        for(MyUser user1:list){
                            if(user.getObjectId().equals(user1.getObjectId())){
                                viewHolder.likes.setImageResource(R.drawable.dianzan1);
                            }
                        }
                        int a=list.size();
                        String b=String.valueOf(a);
                        viewHolder.likescount.setText(b);
                    }
                });


//            获取该话题下所有的评论
                BmobQuery<Comment> query = new BmobQuery<Comment>();
                Topic post1 = new Topic();
                post1.setObjectId(search_topic.getObjectId());
                query.addWhereEqualTo("post", new BmobPointer(post1));
//            没user
                query.include("user,post.author,zuozhe");
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.findObjects(new FindListener<Comment>() {
                    @Override
                    public void done(List<Comment> commentList, BmobException e) {
                        if (e == null) {
                            String commentCounts = String.valueOf(commentList.size());
                            viewHolder.commentcount.setText(commentCounts);
                            swiperLayout.setRefreshing(false);
                        }
                    }
                });
            }else{

                viewHolder.rl.setVisibility(View.GONE);

            }

            //点赞按钮
            viewHolder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.likes.setImageResource(R.drawable.dianzan1);

                    Topic dian =new Topic();
                    dian.setObjectId(mPostList.get(position).getObjectId());
                    BmobRelation relation = new BmobRelation();
                    relation.add(user);
                    dian.setLikes(relation);
                    dian.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Toast.makeText(SearchActivity.this,"你已成功点赞",Toast.LENGTH_SHORT).show();
                            BmobQuery<MyUser> query1 = new BmobQuery<>();
                            Topic post = new Topic();
                            post.setObjectId(mPostList.get(position).getObjectId());
                            query1.addWhereRelatedTo("likes",new BmobPointer(post));
                            query1.findObjects(new FindListener<MyUser>() {
                                @Override
                                public void done(List<MyUser> list, BmobException e) {
                                    int a=list.size();
                                    String b=String.valueOf(a);
                                    viewHolder.likescount.setText(b);
                                }
                            });
                        }
                    });
                }
            });

            return convertView;
        }

        public class ViewHolder {

            public ImageView img;//帖子图片
            public ImageView likes;//点赞图片
            public TextView viewcount;//浏览量
            public TextView commentcount;//评论量
            public TextView likescount;//点赞数
            public TextView name, topicTitle, topicText, time;//用户名、标题、帖子内容文字、发布时间
            private CircleImageView icon;//circleview圆形头像
            public RelativeLayout rl;
        }
    }

    private void getTopicData() {
        BmobQuery<Topic> query = new BmobQuery<>();

        query.order("-createdAt");//依照帖子降序时间排序
//        同时获取帖子的作者信息
        query.include("author");
        query.setLimit(500);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<Topic>() {
            @Override
            public void done(List<Topic> topics, BmobException e) {
                if (e == null) {
                    if (topics.size() == 0) {
                        //没有帖子信息
                        ivEmpty.setVisibility(View.VISIBLE);
                    }else {
                        ivEmpty.setVisibility(View.GONE);
                        mPostList.clear();
                        for (int i = 0; i < topics.size(); i++) {
                            mPostList.add(topics.get(i));
                        }
//                列表数据加载完成
                        list.setAdapter(new ItemListAdapter());
                    }
                }else {
                    ivEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive() && getCurrentFocus() != null) {
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }        }
    }



}
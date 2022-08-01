package com.example.yueshi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yueshi.bean.Comment;
import com.example.yueshi.bean.MyUser;
import com.example.yueshi.bean.Topic;
import com.example.yueshi.myview.MyListView;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

//帖子详情页
public class PostDetailActivity extends AppCompatActivity {
    private ImageView ivReturn;
    private String topicTitle, topicTextContent, topicAuthor, topicId, topicAuthorSex,
            topicPostTime, topicPicture;
    private MyUser user;
    private MyListView list;
    private SwipeRefreshLayout swiperLayout;
    List<Comment> mCmtList = new ArrayList<>();
    private TextView tvTopicAuthorName, tvTopicTime, tvTopicTitle, tvTextContent, tvTitle,
            tvCmtCounts, tvLikes, tvCollection;
    private CircleImageView circleimageview;
    private ImageView ivPostCmt,ivLikes,ivCollection;
    private EditText editContent;
    private ImageView ivEmptyCmt;
    private ImageView ivTopicPic;
    private ClipboardManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        //初始化
        Bmob.initialize(this,"0f9fca80b537d756e72c2a70c3420f92");
        user = new MyUser();
        user = BmobUser.getCurrentUser(MyUser.class);

        Bundle bundle = getIntent().getExtras();
        topicPostTime = bundle.getString("topicPostTime");
        topicTitle = bundle.getString("topicTitle");
        topicAuthor = bundle.getString("topicAuthor");
        topicTextContent = bundle.getString("topicTextContent");
        topicId = bundle.getString("topicId");
        topicAuthorSex = bundle.getString("topicAuthorSex");
        topicPicture = bundle.getString("topicPicture");

        editContent = findViewById(R.id.layoutfabuEditText1);//评论框
        tvTitle = findViewById(R.id.tv_title);//标题栏
        tvTopicAuthorName = findViewById(R.id.tv_topicAuthorName);//用户名
        tvTopicTime = findViewById(R.id.layoutfabuTextView2);//时间
        tvTopicTitle = findViewById(R.id.layoutfabuTextView3);//标题
        tvTextContent = findViewById(R.id.layoutfabuTextView4);//内容
        tvCmtCounts = findViewById(R.id.layoutfabuTextView6);//评论数
        tvLikes = findViewById(R.id.layoutfabuTextView5);//点赞数
        tvCollection = findViewById(R.id.layoutfabuTextView7);//收藏数

        circleimageview = findViewById(R.id.layoutfabuCircleImageView1);
        ivReturn = findViewById(R.id.ivReturn);//返回
        ivPostCmt = findViewById(R.id.img_main_small_logo);//发布评论
        ivEmptyCmt = findViewById(R.id.layoutfabuImageView1);//内容为空的图
        ivTopicPic = findViewById(R.id.layoutfabu1image);
        ivLikes = findViewById(R.id.btn_likes);
        ivCollection = findViewById(R.id.btn_collection);

        list = findViewById(R.id.comment_list);
        swiperLayout = findViewById(R.id.quanziSwipeRefreshLayout);
        swiperLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata();

                getcommentdata();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

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
                // 把标识传给swiperLayoutipeRefreshLayout
                swiperLayout.setEnabled(enable);
            }
        });

        ivLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivLikes.setImageResource(R.drawable.dianzan1);
                Topic dian = new Topic();
                dian.setObjectId(topicId);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                dian.setLikes(relation);
                dian.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        Toast.makeText(PostDetailActivity.this,"你已成功点赞",Toast.LENGTH_SHORT).show();
                        getLikes();
                    }
                });
            }
        });

        ivCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnCollect();
                Topic collect = new Topic();
                collect.setObjectId(topicId);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                collect.setCollection(relation);
                collect.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        getCollection();
                        ivCollection.setImageResource(R.drawable.shoucang2);
                    }
                });
            }
        });

        ivPostCmt.setOnClickListener(new View.OnClickListener() {
            private String neirong;

            @Override
            public void onClick(View v) {
                neirong = editContent.getText().toString();
                if (neirong.equals("")) {
                    Toast.makeText(PostDetailActivity.this, "评论内容为空！", Toast.LENGTH_SHORT).show();

                } else if (neirong.length() > 30) {
                    Toast.makeText(PostDetailActivity.this, "评论字数不能超过30字", Toast.LENGTH_SHORT).show();
                } else {

                    Topic post = new Topic();
                    post.setObjectId(topicId);
                    Comment cm = new Comment();
                    cm.setContent(neirong);
                    cm.setZuozhe(user);
                    cm.setPost(post);
                    cm.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                //添加数据成功
                                Toast.makeText(PostDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                editContent.setText("");
                                hintKeyBoard();
                                getcommentdata();
                            }
                        }
                    });
                }
            }
        });

        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getdata();//获取内容数据
        getLikes();//获取点赞数
        getCollection();//获取收藏数

    }

    public void getdata() {

        tvTitle.setText("遇见故事");
        tvTopicAuthorName.setText(topicAuthor);
        tvTopicTime.setText(topicPostTime);
        tvTopicTitle.setText(topicTitle);
        tvTextContent.setText(topicTextContent);
        if ("男".equals(topicAuthorSex)) {
            circleimageview.setImageResource(R.drawable.man);
        } else {
            circleimageview.setImageResource(R.drawable.lady);
        }
        if (topicPicture == null) {
            ivTopicPic.setVisibility(View.GONE);
        } else {
            Picasso.with(PostDetailActivity.this)
                    .load(topicPicture.replace("https","http"))
                    .placeholder(R.drawable.pic1)
                    .error(R.drawable.pic1)
                    .into(ivTopicPic);
        }
    }

    public void fenxiangbutton(View v) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //分享的内容
        shareIntent.putExtra(Intent.EXTRA_TEXT, "标题："+topicTitle + "\n" + "作者：" + topicAuthor + "\n" + "内容："+topicTextContent + "\n\n/********\n*分享内容来源于NCU社区APP\n*" + formatter.format(curDate) + "\n**********/");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
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
            }
        }
    }

    class ItemListAdapter extends BaseAdapter {
        private Intent intent;
        private Comment comment;

        //适配器
        @Override
        public int getCount() {
            if (mCmtList.size() > 0) {
                return mCmtList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mCmtList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_comment, null);
                viewHolder.time = (TextView) convertView.findViewById(R.id.commentlistTextView2);
                viewHolder.name = (TextView) convertView.findViewById(R.id.commentlistTextView1);
                viewHolder.neirong = (TextView) convertView.findViewById(R.id.commentlistTextView3);
                viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.commentlistCircleImageView1);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            comment = mCmtList.get(position);
            if ("男".equals(comment.getZuozhe().getSex())) {
                viewHolder.icon.setImageResource(R.drawable.man);
            } else {
                viewHolder.icon.setImageResource(R.drawable.lady);
            }


            viewHolder.time.setText(comment.getCreatedAt());
            viewHolder.name.setText(comment.getZuozhe().getName());
            viewHolder.neirong.setText(comment.getContent());


            return convertView;
        }

        public class ViewHolder {
            public TextView time, name, neirong;
            public CircleImageView icon;

        }
    }

    public void getLikes(){
        BmobQuery<MyUser> query =new BmobQuery<MyUser>();
        Topic dian =new Topic();
        dian.setObjectId(topicId);
        query.addWhereRelatedTo("likes",new BmobPointer(dian));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                for(MyUser user1:list){

                    if(user.getObjectId().equals(user1.getObjectId())){
                        ivLikes.setImageResource(R.drawable.dianzan1);
                    }
                }
                int a=list.size();
                String b=String.valueOf(a);
                tvLikes.setText(b);
            }
        });

    }

    public void getCollection(){
        BmobQuery<MyUser> query=new BmobQuery<MyUser>();
        Topic collect=new Topic();
        collect.setObjectId(topicId);
        query.addWhereRelatedTo("collection",new BmobPointer(collect));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                for(MyUser user1:list){
                    if(user.getObjectId().equals(user1.getObjectId())){
                        ivCollection.setImageResource(R.drawable.shoucang2);
                    }
                    else{
                        ivCollection.setImageResource(R.drawable.shoucang1);
                    }
                }
                int a=list.size();
                String b=String.valueOf(a);
                tvCollection.setText(b);
            }
        });
    }

    public void OnCollect(){
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        Topic coll = new Topic();
        coll.setObjectId(topicId);
        query.addWhereRelatedTo("collection",new BmobPointer(coll));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                for(MyUser user1:list){
                    if(user.getObjectId().equals(user1.getObjectId())){
                        CancelCollect();
                    }
                    else{
                        Toast.makeText(PostDetailActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //取消收藏
    public void CancelCollect(){
        Topic collect = new Topic();
        collect.setObjectId(topicId);
        BmobRelation relation = new BmobRelation();
        relation.remove(user);
        collect.setCollection(relation);
        collect.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                ivCollection.setImageResource(R.drawable.shoucang1);
                Toast.makeText(PostDetailActivity.this,"取消收藏成功",Toast.LENGTH_SHORT).show();
                getCollection();
            }
        });
    }

    public void getcommentdata() {
        Bmob.initialize(this, "0f9fca80b537d756e72c2a70c3420f92");
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        Topic post = new Topic();
        post.setObjectId(topicId);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));//此表示缓存一天
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.include("user,post.author,zuozhe");
        query.setLimit(500);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
//        查询该帖子下所有评论
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> comments, BmobException e) {
                if (e == null) {
                    if (comments.size() == 0) {
                        ivEmptyCmt.setVisibility(View.VISIBLE);
                    } else {
                        ivEmptyCmt.setVisibility(View.GONE);
                        mCmtList.clear();
                        for (int i = 0; i < comments.size(); i++) {
                            mCmtList.add(comments.get(i));
                        }
                        list.setAdapter(new ItemListAdapter());
//                    加载完成
                        String commentCount = String.valueOf(comments.size());
                        tvCmtCounts.setText(commentCount);
                        swiperLayout.setRefreshing(false);
                    }
                }else {
                    ivEmptyCmt.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getdata();
        getLikes();
        getcommentdata();
        getCollection();
    }
}

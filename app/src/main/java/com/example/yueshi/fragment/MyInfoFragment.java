package com.example.yueshi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.yueshi.AttendanceActivity;
import com.example.yueshi.FeedBackActivity;
import com.example.yueshi.GiftActivity;
import com.example.yueshi.MyCollectionActivity;
import com.example.yueshi.MyHistoryViewActivity;
import com.example.yueshi.MyPostActivity;
import com.example.yueshi.R;
import com.example.yueshi.SettingActivity;
import com.example.yueshi.UserProfileActivity;
import com.example.yueshi.bean.MyUser;
import com.example.yueshi.bean.Topic;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class MyInfoFragment extends Fragment {

    private TextView text1;
    private TextView text2;
    private MyUser user;
    private SwipeRefreshLayout sw;
    private LinearLayout layout;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private RelativeLayout layout4;
    private LinearLayout layout5;
    private LinearLayout layout6;
    private LinearLayout layout7;
    private LinearLayout layout8;
    private TextView text3, text4, text5;

    private CircleImageView circleimageview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myinfo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // TODO: Implement this method
        super.onActivityCreated(savedInstanceState);

        user = new MyUser();
        user = BmobUser.getCurrentUser(MyUser.class);

        circleimageview = (CircleImageView) getActivity().findViewById(R.id.fg2CircleImageView1);
        text1 = (TextView) getActivity().findViewById(R.id.fg2TextView1);//?????????
        text2 = (TextView) getActivity().findViewById(R.id.fg2TextView2);//??????
        text3 = (TextView) getActivity().findViewById(R.id.fg2textview);//?????????
        text4 = (TextView) getActivity().findViewById(R.id.fg2textview1);//?????????
        text5 = (TextView) getActivity().findViewById(R.id.fg2textview2);//??????

        layout = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout1);
        layout1 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout2);
        layout2 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout3);
        layout3 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout4);
        layout4 = (RelativeLayout) getActivity().findViewById(R.id.fg2relativelayout);
        layout5 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout5);
        layout6 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout6);
        layout7 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout7);
        layout8 = (LinearLayout) getActivity().findViewById(R.id.fg2LinearLayout8);
        sw = (SwipeRefreshLayout) getActivity().findViewById(R.id.fg2SwipeRefreshLayout1);

        sw.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
//        ????????????
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata();
                getfabiaocount();
                getviewcount();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sw.setRefreshing(false);
                    }
                }, 3000);
            }
        });
//      ??????????????????
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
            }
        });
//        ????????????
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttendanceActivity.class);
                startActivity(intent);
            }
        });
//        ????????????
        layout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

//        ???????????????
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

//         ???????????????
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

//      ??????????????????
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollectionActivity.class);
                startActivity(intent);
            }
        });
//      ????????????
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPostActivity.class);
                startActivity(intent);
            }
        });
//      ????????????
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyHistoryViewActivity.class);
                startActivity(intent);
            }
        });

        //      ????????????
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GiftActivity.class);
                startActivity(intent);
            }
        });

        getdata();
        getviewcount();
        getfabiaocount();
    }

//    ???????????????
    public void getviewcount() {
        BmobQuery<Topic> quary = new BmobQuery<>();
        quary.addWhereEqualTo("view", user);
        quary.count(Topic.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    if (integer == null) {
                        text4.setText(0+ "");
                    }else {
                        text4.setText(integer.intValue() + "");
                    }
                }
            }
        });
    }

//    ?????????????????????
    public void getfabiaocount() {
        BmobQuery<Topic> qury = new BmobQuery<>();
        qury.addWhereEqualTo("author", user);
        qury.count(Topic.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    if (integer == null) {
                        text3.setText(0+ "");
                    }else {
                        text3.setText(integer.intValue() + "");
                    }
                }
            }
        });

    }
//    ????????????????????????
    public void getdata() {
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("objectId", user.getObjectId());
        query.order("-createdAt");//??????????????????????????????
        query.setLimit(1);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> myUsers, BmobException e) {
                if (e == null) {
                    if (myUsers.size() == 0) {
                        Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();//?????????????????????
                    }else {
                        for (MyUser p1 : myUsers) {
                            text1.setText(p1.getName());
                            text2.setText(p1.getEmail());
                            text5.setText(p1.getPoint().toString());
                            if ("???".equals(p1.getSex())) {
                                circleimageview.setImageResource(R.drawable.man);
                            } else {
                                circleimageview.setImageResource(R.drawable.lady);
                            }
                        }
                    }
                }

            }
        });

    }
}

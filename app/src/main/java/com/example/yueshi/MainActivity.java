package com.example.yueshi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.yueshi.bean.MyUser;
import com.example.yueshi.fragment.MyInfoFragment;
import com.example.yueshi.fragment.DiscoveryFragment;
import com.example.yueshi.fragment.cmFragment;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    三个fragment
    private cmFragment cmtyFg;
    private MyInfoFragment infoFg;
    private DiscoveryFragment discFg;


    private RelativeLayout rlayoutCommunity;
    private RelativeLayout rlayoutDiscovery;
    private RelativeLayout rlayoutMyInfo;

    private ImageView ivCommunity;
    private ImageView ivDiscovery;
    private ImageView ivMyInfo;

    private MyUser user;
    private String nowTime;


    //    定义FragmentManager对象管理器
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        initView();
//        默认选中社区模块
        setChoiceItem(1);
    }



    private void setChoiceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // 清空, 重置选项, 隐藏所有Fragment
        clearChioce();
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                ivDiscovery.setImageResource(R.drawable.search);
                if (discFg == null) {
                    discFg = new DiscoveryFragment();
                    fragmentTransaction.add(R.id.content, discFg);
                } else {
                    fragmentTransaction.show(discFg);
                }
                break;
            case 1:
                ivCommunity.setImageResource(R.drawable.fx);
                if (cmtyFg == null) {
                    cmtyFg = new cmFragment();
                    fragmentTransaction.add(R.id.content, cmtyFg);
                } else {
                    fragmentTransaction.show(cmtyFg);
                }
                break;
            case 2:
                ivMyInfo.setImageResource(R.drawable.my);
                if (infoFg == null) {
                    infoFg = new MyInfoFragment();
                    fragmentTransaction.add(R.id.content, infoFg);
                } else {
                    fragmentTransaction.show(infoFg);
                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }

    private void clearChioce() {
        ivDiscovery.setImageResource(R.drawable.search);
        ivCommunity.setImageResource(R.drawable.fx1);
        ivMyInfo.setImageResource(R.drawable.my1);
    }

    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (discFg != null) {
            fragmentTransaction.hide(discFg);
        }
        if (cmtyFg != null) {
            fragmentTransaction.hide(cmtyFg);
        }
        if (infoFg != null) {
            fragmentTransaction.hide(infoFg);
        }
    }

    private void initView() {
        Bmob.initialize(this, "0f9fca80b537d756e72c2a70c3420f92");
//        初始化底部导航栏的控件
        ivDiscovery = findViewById(R.id.iv_discovery);
        ivCommunity = findViewById(R.id.iv_community);
        ivMyInfo = findViewById(R.id.iv_myInfo);
        rlayoutDiscovery = findViewById(R.id.rlayout_discovery);
        rlayoutCommunity = findViewById(R.id.rlayout_community);
        rlayoutMyInfo = findViewById(R.id.rlayout_myInfo);
//      监听fragment切换
        rlayoutDiscovery.setOnClickListener(MainActivity.this);
        rlayoutCommunity.setOnClickListener(MainActivity.this);
        rlayoutMyInfo.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout_discovery:
                setChoiceItem(0);
                break;
            case R.id.rlayout_community:
                setChoiceItem(1);
                break;
            case R.id.rlayout_myInfo:
                setChoiceItem(2);
                break;
            default:
                break;
        }
    }

    //再按一次退出
    private long exitTime=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis()-exitTime)>800)
            {
                Toast.makeText(MainActivity.this,"再返回一次退出",Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                finish();
                //关闭java虚拟机
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

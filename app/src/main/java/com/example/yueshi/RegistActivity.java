package com.example.yueshi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.yueshi.bean.MyUser;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends AppCompatActivity {

    private EditText editRegistAccount;
    private EditText editRegistPwd;
    private EditText editRegistEmail;
    private EditText editRegistNickName;

    private RadioButton rbtnMale;
    private RadioButton rbtnFemale;
    private Button btnSubmit;

    String userName;
    String nickName;
    String userPassword;
    String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        Bmob.initialize(this, "0f9fca80b537d756e72c2a70c3420f92");
        initView();
    }

    private void initView() {
        editRegistAccount = findViewById(R.id.edit_registAccount);
        editRegistPwd = findViewById(R.id.edit_registPwd);
        editRegistNickName = findViewById(R.id.edit_registNickName);
        editRegistEmail = findViewById(R.id.edit_registEmail);

        rbtnFemale = findViewById(R.id.rbtn_female);
        rbtnMale = findViewById(R.id.rbtn_male);
        btnSubmit = findViewById(R.id.btn_submitRegist);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editRegistAccount.getText().toString();
                userPassword = editRegistPwd.getText().toString();
                nickName = editRegistNickName.getText().toString();
                userEmail = editRegistEmail.getText().toString();

                Pattern pattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
                Matcher matcher = pattern.matcher(userEmail);
//                ??????
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPassword)
                        || TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(RegistActivity.this,"??????/??????/??????/????????????????????????",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (userName.length() > 15) {
                        Toast.makeText(RegistActivity.this,"??????????????????15?????????",Toast.LENGTH_SHORT).show();
                        editRegistAccount.setText("");
                        return;
                    }
                    if (userPassword.length() < 6) {
                        Toast.makeText(RegistActivity.this,"??????????????????6?????????",Toast.LENGTH_SHORT).show();
                        editRegistPwd.setText("");
                        return;
                    }
                    if(!matcher.matches()){
                        Toast.makeText(RegistActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                        editRegistEmail.setText("");
                        return;
                    }

                    MyUser user = new MyUser();
                    user.setUsername(userName);
                    user.setName(nickName);
                    user.setPassword(userPassword);
                    user.setEmail(userEmail);
                    user.setIdentity("??????");
                    user.setDays(Integer.parseInt(0+""));
                    user.setPoint(Integer.parseInt(0+""));
                    user.setView(Integer.parseInt(0+""));
                    user.setState(false);
                    if (rbtnFemale.isChecked()) {
                        user.setSex("???");
                    } else if (rbtnMale.isChecked()) {
                        user.setSex("???");
                    }
                    user.signUp(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if (e == null) {
                                Toast.makeText(RegistActivity.this, "???????????????", Toast.LENGTH_LONG).show();
//                                Snackbar.make( RegistActivity.this,"????????????", Snackbar.LENGTH_LONG).show();
                                RegistActivity.this.finish();
                            } else {
                                Toast.makeText(RegistActivity.this, "????????????????????????????????????????????????", Toast.LENGTH_LONG).show();

                            }
                        }

                    });

                }
            }
        });

    }

    public void onReturnClick(View view) {
        finish();
    }
}

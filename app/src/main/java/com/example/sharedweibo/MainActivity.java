package com.example.sharedweibo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;

public class MainActivity extends AppCompatActivity {
 private Button shared;

    private static final String APP_KY = "3982212830";
    private static final String REDIRECT_URL = "http://www.android";
    private static final String SCOPE = "email,direct_messages_read,direct_messages_write,\"\n" +
            "                    + \"friendships_groups_read,friendships_groups_write,statuses_to_me_read,\"\n" +
            "                    + \"follow_app_official_microblog,\" + \"invitation_write\"";
    private IWBAPI mWBAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared=findViewById(R.id.shared_weiBo);

        //监听
        shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享
                startAuth();
                doWeiboShare();
            }
        });
        //方法
        initSdk();
    }

    //重写方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBAPI != null) {
            mWBAPI.authorizeCallback(requestCode, resultCode, data);
            mWBAPI.doResultIntent(data, new ShareCallback());
        }
    }

    private void initSdk() {
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
    }

    private void startAuth() {
        //auth
        mWBAPI.authorize(new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(MainActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(UiError error) {
                Toast.makeText(MainActivity.this, "微博授权失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "取消微博授权", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doWeiboShare() {
        //分享文本
        WeiboMultiMessage message = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = "内容";
        message.textObject = textObject;
        mWBAPI.shareMessage(message,true);
    }

    // 接口回调
    private class ShareCallback implements WbShareCallback {
        @Override
        public void onComplete() {
            Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onError(UiError error) {
            Toast.makeText(MainActivity.this, "分享失败" + error.errorMessage, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
        }
    }
}

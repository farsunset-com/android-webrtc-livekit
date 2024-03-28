package com.example.webrtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webrtc.config.AppConfig;
import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.webrtc.WebrtcMeetingSdk;
import com.farsunset.webrtc.entity.Friend;
import com.farsunset.webrtc.model.IceServerConfig;
import com.farsunset.webrtc.model.SetupAppConfig;
import com.farsunset.webrtc.ui.FriendSelectorActivity;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * k可用2个手机 设置不同的UID 拨打测试
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.action_call_room).setOnClickListener(this);
        findViewById(R.id.action_call_p2p_video).setOnClickListener(this);
        findViewById(R.id.action_call_p2p).setOnClickListener(this);

        IceServerConfig iceServerConfig = new IceServerConfig();
        iceServerConfig.setUri("stun:stun.l.google.com:19302");

        SetupAppConfig appConfig = new SetupAppConfig();

        appConfig.addIceServer(iceServerConfig);

        appConfig.setHost(AppConfig.WEBRTC_PUSH_SERVER_URI);
        appConfig.setName(AppConfig.NAME);
        appConfig.setUid(AppConfig.UID);
        appConfig.setLogoUri(AppConfig.USER_LOGO_URI);
        appConfig.setLivekitUri(AppConfig.LIVEKIT_SERVER_URI);

        WebrtcMeetingSdk.setupAppConfig(appConfig);

        /**
         * 获取音视频权限
         */
        WebrtcMeetingSdk.requestAudioCameraPermission(this);

        /**
         * 提示开启通知栏和浮窗开关
         */
        WebrtcMeetingSdk.checkNotificationEnable(this);
        WebrtcMeetingSdk.checkFloatWindowEnable(this);

        /**
         * 构建联系人列表
         */
        String friendsJson = "[{\"id\":10060,\"name\":\"女娲\"},{\"id\":10001,\"name\":\"老夫子\"},{\"id\":10031,\"name\":\"关羽\"},{\"id\":10042,\"name\":\"高渐离\"},{\"id\":10016,\"name\":\"鬼谷子\"},{\"id\":10073,\"name\":\"公孙离\"},{\"id\":10028,\"name\":\"花木兰\"},{\"id\":10023,\"name\":\"韩信\"}]\n";

        WebrtcMeetingSdk.setupContactList(parseList(friendsJson, Friend.class));


        /**
         * demo 里面接入了cim ，可以替换为自己的消息通道，接收到消息
         * 调研CIMPushMessageReceiver .onMessageReceived()
         */
        CIMPushManager.connect(this,AppConfig.WEBRTC_PUSH_SERVER_HOST,34567);
    }

    public  <T> List<T> parseList(String data, Class<T> tClass) {
        ParameterizedType type = $Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, tClass);
        return new Gson().fromJson(data, type);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.action_call_p2p) {
            Intent intent = new Intent(this, FriendSelectorActivity.class);
            startActivityForResult(intent,123);
        }

        if (id == R.id.action_call_p2p_video) {
            Intent intent = new Intent(this, FriendSelectorActivity.class);
            startActivityForResult(intent,456);
        }


        if (id == R.id.action_call_room) {
            WebrtcMeetingSdk.onCreateMeeting(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK){
            return;
        }

        Friend friend = (Friend) data.getSerializableExtra("ATTR_FRIEND");
        if (friend == null){
            return;
        }

        if (requestCode == 123) {
            WebrtcMeetingSdk.callSingleVoice(friend.id);
        }

        if (requestCode == 456) {
            WebrtcMeetingSdk.callSingleVideo(friend.id);
        }
    }
}
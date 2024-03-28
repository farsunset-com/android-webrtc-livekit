### 项目介绍
该项目为原生Android提供开箱即用的单人、多人音视频通话和共享桌面插件。
目前仅仅提供了安装包。

需要源码请访问 https://farsunset.com/about


| 目录                 | 说明                      |
|--------------------|-------------------------|
| livekit-meeting-android-2024.03.28.aar            | 为原生Android应用提供的可开箱使用插件包 |
| meeting-plugin-demo                | 插件使用demo                |
| webrtc-boot-server-1.0.0.jar | 提供信令和通话指令推送服务安装包        |

<div align="center">
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/group_video_calling.jpg" width="48%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/call_video_incoming.jpg" width="48%"  />
</div>

### 服务端安装

webrtc-boot-server依赖组件

JDK 1.8+

Redis 6.0+

Livekit 1.5.3+

在webrtc-boot-server-1.0.0.jar 同目录创建 ./config/application.properties 文件

```
├─webrtc-boot-server-1.0.0.jar
├─config
│  └─application.properties
```
可覆盖jar里面的默认参数配置


#### 配置Redis
修改 ./config/application.properties

```
spring.redis.host=127.0.0.1
spring.redis.port=6379
#spring.redis.password=
```

#### Livekit服务器地址

安装文档 https://www.yuque.com/yuanfangxiyang/hzema9/mpr8zlo99idggx28
多人会议(SFU)使用了livekit开源的框架，请自己安装搭建
完成后配置livekit服务地址和appid、secret

修改 ./config/application.properties

```
livekit.uri=https://livekit.yourdomain.com
livekit.app-id=XXXXXXXXXX
livekit.secret=XXXXXXXXXXX
```

#### 使用CIM为消息推送通道

默认端口需要开启34567 和 45678(websocket)的socket端口,
客户端需要接入cim的客户端sdk
https://gitee.com/farsunset/cim


#### 使用自有的消息推送通道
关闭cim的socket服务
在webrtc-boot-server-1.0.0.jar 同目录创建 ./config/application.properties 文件

```
# 关闭socket服务
cim.app.enable=false
cim.websocket.enable=false

```

```
#配置自己系统的消息推送webhook
webrtc.message.webhook=http://192.168.1.100:8081/message/push

```
当产生业务消息，将会调用该webhook进行消息推送，
请求方式：POST
请求类型: application/json
请求体:

```
{
    "id": 362433383034392576,
    "sender": 0,
    "receiver":"10000,10001,10002",
    "action": "2",
    "content": "系统通知",
    "extra": null,
    "format": 0,
    "timestamp": 1601024512030
  } 

```
receiver 就是需要接受消息的用户ID ，可能多个英文逗号分割

客户端收到消息后按照插件接口传入即可


#### 启动服务
运行 run.sh
```
#! /bin/bash  
java -Dcom.sun.akuma.Daemon=daemonized -Dspring.profiles.active=pro -jar ./webrtc-boot-server-1.0.0.jar &
```

运行 run.bat（ windows）
```
java -Dcom.sun.akuma.Daemon=daemonized -Dspring.profiles.active=pro -jar ./webrtc-boot-server-1.0.0.jar
```


### Android插件接口文档


#### 1.初始化

依赖的组件
```
    implementation 'org.webrtc:google-webrtc:1.0.32006'
    implementation 'androidx.palette:palette:1.0.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    implementation "com.github.bumptech.glide:okhttp3-integration:4.16.0"
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
    implementation 'jp.wasabeef:glide-transformations:4.3.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.belerweb:pinyin4j:2.5.1'
    implementation 'io.livekit:livekit-android:2.1.1'
    implementation 'com.google.protobuf:protobuf-javalite:3.23.0'
    
    //可选
    implementation "com.farsunset:cim-android-sdk:4.2.13"
```


在应用的Application.create()里调用
```
WebrtcMeetingSdk.install(this);        
```





#### 2.设置配置信息

| 字段名        | 必须的    | 含义                                  |
|------------|--------|-------------------------------------|
| uid        | 是  | 当前用户ID,可使用多个手机，设置不同UID 测试           |
| name       | 是  | 当前用户名称                              |
| logoUri    | 是  | 用户LOGO头像规则地址，根据占位符{uid}可动态获取头像地址 |
| host       | 是  | 服务端地址  webrtc-boot-server部署访问地址     |
| token      | 否  | 当前用户token，当前demo 可不传，服务端实现登录接口后可再传  |
| iceservers | 是  | turn或者sutn服务配置                      |

```
SetupAppConfig config = new SetupAppConfig();

config.setHost("http://39.99.150.41:8080");

/* 设置当前用户信息 */
config.setUid(10000);
config.setName("张三");
config.setLogoUri("http://api-hoxin.farsunset.com/file/user-icon/{uid}");

/* 设置ice服务 */
config.addIceServer(new IceServerConfig("stun:stun.l.google.com:19302"))

/* livekit 服务地址 */
config.setLivekitUri("wss://livekit.farsunset.com");

WebrtcMeetingSdk.setupAppConfig(config);

```


#### 3.更新通讯录列表
同步通讯录。用于选择联系人，不包含当前用户
```

List<Friend> friends = new ArrayList<Friend>();

Friend friend = new Friend();
friend.id = 10001;
friend.name = "李四";

friends.add(friend);
WebrtcMeetingSdk.setupContactList(friends);
```

#### 4.拨打单人语音通话
id:被叫UID

```
WebrtcMeetingSdk.callSingleVoice(10000);
```
#### 5.拨打单人视频通话
id:被叫UID

```
WebrtcMeetingSdk.callSingleVideo(10000);
```

#### 6.发起音视频会议
true:从群成员中选择联系人 先调用setupGroupMemberList()
false:从好友列表选择
```
WebrtcMeetingSdk.onCreateMeeting(false);
```

#### 7.收到推送信令消息
收到服务端推送的通话相关消息，如收到单人来电、会议邀请、ice同步等，都传递给插件去执行即可。
消息来源参照服务端，使用CIM为消息推送通道或者使用自有的消息推送通道
```
WebrtcMeetingSdk.onMessageReceived(message);
```


#### 8.  新增联系人
通讯录增增联系人
```
Friend friend = new Friend();
friend.id = 10002;
friend.name = "王五";

WebrtcMeetingSdk.addContact(friend);
```

#### 9.  删除联系人
通讯录删除联系人
id:用户UID
```
WebrtcMeetingSdk.removeContact(10002);
```


#### 本地广播事件通知
当通话事件产生。通过发送本地广播的方式来通知上层应用，自行监听本地广播获取事件信息，可记录通话，会议记录
示例代码如下
```
public class WebrtcCallEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
          
        }

        IntentFilter getIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(" com.farsunset.meeting.plugin.MEETING_PLUGIN_EVENT");
            return filter;
        }
}

BroadcastReceiver  eventReceiver = new WebrtcCallEventReceiver();
LocalBroadcastManager.getInstance(this).registerLocalReceiver(eventReceiver, eventReceiver.getIntentFilter());
```


##### 1.单人通话完结通知

 所有单人通话事件均放在这个事件当中，根据状态来进行消息记录显示处理

| 字段名           | 含义                                            |
|---------------|-----------------------------------------------|
| key           | ACTION_CALL_FINISHED                          |
| data.uid      | 对方UID                                         |
| data.state    | 通话状态 0:已经接通 1:已经取消 2:已拒绝 3:设备正忙 4:响应超时 5:忽略来电 |
| data.duration | 通话时长(毫秒)                                      |
| data.role     | 通话角色 0:主叫 1:被叫                                |
| data.type     | 通话类型 0:语音 1:视频                                |
```
public class WebrtcCallEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
             String key = intent.getStringExtra("type");
             ChatCall call = (ChatCall)intent.getSerializableExtra("data");
        }
       
}
```

##### 2.发起单人通话

发起单人通话时立即通知

| 字段名            | 含义                      |
|----------------|-------------------------|
| key            | ACTION_START_CALLING |
```
public class WebrtcCallEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
             String key = intent.getStringExtra("type");
        }
       
}
```

##### 3.收到会议邀请

收到会议邀请

| 字段名            | 含义                      |
|----------------|-------------------------|
| key            | ACTION_MEETING_RING |
| data.tag       | 房间号                     |
| data.title  | 会议主题                    |
| data.description | 会议描述                    |
| data.createAt  | 会议创建时间戳(13位)            |
| data.uid       | 通话发起人UID                |
| data.name      | 发起人名称                   |
| data.dueTime   | 会议预约时间                  |
```
public class WebrtcCallEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
             String key = intent.getStringExtra("type");
             String data = intent.getStringExtra("data");
        }
       
}
```


##### 4.会议结束结通知

 所有多人通话事件均放在这个事件当中，根据状态来进行消息记录显示处理

| 字段名            | 含义                      |
|----------------|-------------------------|
| key            | ACTION_MEETING_FINISHED |
| data.tag       | 房间号                     |
| data.duration  | 通话时长(毫秒)                |
| data.timestamp | 会议时间 房间创建时间戳(13位)       |
| data.joinedAt  | 进入房间时间戳(13位)            |
| data.uid       | 通话发起人UID                |
| data.name      | 发起人名称                   |
| data.members   | 参会人员信息map               |
```
public class WebrtcCallEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
             String key = intent.getStringExtra("type");
             String data = intent.getStringExtra("data");
        }
       
}
```

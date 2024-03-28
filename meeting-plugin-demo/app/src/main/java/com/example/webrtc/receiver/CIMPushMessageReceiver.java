/*
 * Copyright 2019-2023 Xia Jun(3979434@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **************************************************************************************
 *
 *                         Website : http://www.farsunset.com                           *
 *
 * **************************************************************************************
 */
package com.example.webrtc.receiver;

import android.content.Intent;
import android.os.Bundle;


import com.example.webrtc.config.AppConfig;
import com.farsunset.cim.sdk.android.CIMEventBroadcastReceiver;
import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;
import com.farsunset.webrtc.WebrtcMeetingSdk;
import com.farsunset.webrtc.entity.Message;

/**
 * 消息入口，所有消息都会经过这里
 */
public final class CIMPushMessageReceiver extends CIMEventBroadcastReceiver {

    /**
     * 当收到消息时调用此方法
     */
    @Override
    public void onMessageReceived(com.farsunset.cim.sdk.android.model.Message cimMessage, Intent intent) {

        Message sdkMessage = new Message();

        sdkMessage.sender = Long.parseLong(cimMessage.getSender());
        sdkMessage.action = cimMessage.getAction();
        sdkMessage.content = cimMessage.getContent();
        sdkMessage.createTime = cimMessage.getTimestamp();

        WebrtcMeetingSdk.onMessageReceived(sdkMessage);

    }


    @Override
    public void onConnectFinished(boolean hasAutoBind) {
        if (!hasAutoBind) {
            CIMPushManager.bind(this.context, AppConfig.UID);
        }
    }

    @Override
    public void onReplyReceived(ReplyBody body) {

    }

    @Override
    public void onNetworkChanged() {
    }

    @Override
    public void onConnectFailed() {
    }

    @Override
    public void onConnectionClosed() {
    }

    @Override
    public void onSentSucceed(SentBody body) {
    }

}

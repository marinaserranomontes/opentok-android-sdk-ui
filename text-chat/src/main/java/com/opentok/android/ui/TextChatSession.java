package com.opentok.android.ui;

import android.content.Context;

import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.SubscriberKit;

import java.io.Serializable;


public class TextChatSession extends Session implements Serializable{

    public TextChatSession(Context context, String apiKey, String sessionId) {
        super(context, apiKey, sessionId);
    }
}

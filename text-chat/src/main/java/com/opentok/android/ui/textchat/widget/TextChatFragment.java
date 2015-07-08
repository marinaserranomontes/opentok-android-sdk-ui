package com.opentok.android.ui.textchat.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.opentok.android.ui.textchat.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
* A Fragment for adding and controling text chat user interface.
*/
public class TextChatFragment extends Fragment {

    private final static String LOG_TAG = "TextChatFragment";

    private Context mContext;
    Handler mHandler;

    private ArrayList<ChatMessage> mMsgsList = new ArrayList<ChatMessage>();
    private MessageAdapter mMessageAdapter;

    private ListView mListView;
    private Button mSendButton;
    private EditText mMsgEditText;
    private TextView mMsgCharsView;
    private TextView mMsgNotificationView;
    private View mMsgDividerView;

    private int maxTextLength = 1000; // by default the maximum length is 1000.

    private String senderId;
    private String senderAlias;

    public TextChatFragment() {
        //Init the sender information for the output messages
        this.senderId = UUID.randomUUID().toString();
        this.senderAlias = "me";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.textchat_fragment_layout,
                container, false);

        mListView = (ListView) rootView.findViewById(R.id.msgs_list);
        mSendButton = (Button) rootView.findViewById(R.id.send_button);
        mMsgCharsView = (TextView) rootView.findViewById(R.id.characteres_msg);
        mMsgCharsView.setText(String.valueOf(maxTextLength));
        mMsgNotificationView = (TextView) rootView.findViewById(R.id.new_msg_notification);
        mMsgEditText = (EditText) rootView.findViewById(R.id.edit_msg);
        mMsgDividerView = (View) rootView.findViewById(R.id.divider_notification);
        mMsgEditText.addTextChangedListener(mTextEditorWatcher);

        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mMessageAdapter = new MessageAdapter(getActivity(), R.layout.sent_msg_row_layout, mMsgsList);

        mListView.setAdapter(mMessageAdapter);
        mMsgNotificationView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showMsgNotification(false);
                mListView.smoothScrollToPosition(mMessageAdapter.getMessagesList().size() - 1);
                return false;
            }
        });

        return rootView;
    }

    /**
     * An interface for receiving events when a text chat message is ready to send.
     */
    public interface TextChatListener {
        /**
         * Called when a message in the TextChatFragment is ready to send. A message is
         * ready to send when the user clicks the Send button in the TextChatFragment
         * user interface.
         */
        public boolean onMessageReadyToSend(ChatMessage msg);
    }

    private TextChatListener textChatListener;

    /**
     * Set the object that receives events for this TextChatListener.
     */
    public void setTextChatListener(TextChatListener textChatListener) {
        this.textChatListener = textChatListener;
    }

    /**
     * Set the maximum length of a text chat message.
     */
    public void setMaxTextLength(int length) {
        maxTextLength = length;
    }

    /**
     * Set the sender alias and the sender ID of the output messages.
     */
    public void setSenderInfo(String senderId, String senderAlias) {
        if ( senderAlias == null || senderId == null ) {
            throw new IllegalArgumentException("The sender alias and the sender id cannot be null");
        }
        this.senderAlias = senderAlias;
        this.senderId = senderId;
    }
    /**
     * Add a message to the TextChatListener received message list.
     */
    public void addMessage(ChatMessage msg) {
        Log.i(LOG_TAG, "New message " + msg.getText() + " is ready to be added.");
        boolean messageGroup = false;

        if (msg != null) {

            boolean visible = isNewMessageVisible();
            mMsgNotificationView.setTextColor(getResources().getColor(R.color.text));
            mMsgNotificationView.setText("New messages");
            showMsgNotification(visible);

            //generate message timestamp
            Date date = new Date();
            if (msg.getTimestamp() == 0) {
                msg.setTimestamp(date.getTime());
            }

            mMessageAdapter.add(msg);
        }
    }

    // Called when the user clicks the send button.
    private void sendMessage() {
        //checkMessage
        mMsgEditText.setEnabled(false);
        String msgStr = mMsgEditText.getText().toString();
        if (!msgStr.isEmpty()) {

            if ( msgStr.length() > maxTextLength ) {
                showError();
            }
            else {
                ChatMessage myMsg = new ChatMessage(senderId, senderAlias, msgStr, ChatMessage.MessageStatus.SENT_MESSAGE);
                boolean msgError = onMessageReadyToSend(myMsg);

                if (msgError) {
                    Log.d(LOG_TAG, "Error to send the message");
                    showError();

                } else {
                    mMsgEditText.setEnabled(true);
                    mMsgEditText.setFocusable(true);
                    mMsgEditText.setText("");
                    mMsgCharsView.setTextColor(getResources().getColor(R.color.info));
                    mListView.smoothScrollToPosition(mMessageAdapter.getCount());

                    //add the message to the component
                    addMessage(myMsg);
                }

            }

        }
        else{
            mMsgEditText.setEnabled(true);
        }
    }
    // Add a notification about a new message
    private void showMsgNotification(boolean visible) {
        if (visible) {
            mMsgDividerView.setVisibility(View.VISIBLE);
            mMsgNotificationView.setVisibility(View.VISIBLE);
        } else {
            mMsgNotificationView.setVisibility(View.GONE);
            mMsgDividerView.setVisibility(View.GONE);
        }
    }

    // To check if the next item is visible in the list
    private boolean isNewMessageVisible() {
        int last = mListView.getLastVisiblePosition();
        int transpose = 0;
        View currentBottomView;
        currentBottomView = mListView.getChildAt(last);

        if (mListView.getCount() > 1) {
            while (currentBottomView == null) {
                transpose++;
                currentBottomView = mListView.getChildAt(last - transpose);
            }
            if (last == mListView.getCount() - 1 && currentBottomView.getBottom() <= mListView.getHeight()) {
                mListView.setScrollContainer(false);
                return false;
            } else {
                mListView.setScrollContainer(true);
                return true;
            }
        }
        return false;
    }


    private void showError() {
        mMsgEditText.setEnabled(true);
        mMsgEditText.setFocusable(true);
        mMsgNotificationView.setText("Unable to send message. Retry");
        mMsgNotificationView.setTextColor(Color.RED);
        showMsgNotification(true);
    }

    /**
     * Called when a message in the TextChatFragment is ready to send. A message is
     * ready to send when the user clicks the Send button in the TextChatFragment
     * user interface.
     * 
     * If you subclass the TextChatFragment class and implement this method,
     * you do not need to set a TextChatListener.
     */
    protected boolean onMessageReadyToSend(ChatMessage msg) {
        if (this.textChatListener != null) {
            Log.d(LOG_TAG, "onMessageReadyToSend");
            return this.textChatListener.onMessageReadyToSend(msg);
        }
        return false;
    }

    // Count down the characters left.
    private TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int chars_left = maxTextLength - s.length();

            mMsgCharsView.setText(String.valueOf((maxTextLength - s.length())));
            if (chars_left < 10) {
                mMsgCharsView.setTextColor(Color.RED);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}

package com.opentok.android.ui.textchat.widget;

import java.util.UUID;

/**
* Defines the chat message object that you pass into the
* {@link com.opentok.android.ui.textchat.widget.TextChatFragment#addMessage(ChatMessage msg)}
* method.
*/
public class ChatMessage {

    /**
     * Defines the status of the message (whether it was a sent or received message).
     */
    public static enum MessageStatus {
        /**
         * The status for a sent message.
         */
        SENT_MESSAGE,
        /**
         * The status for a received message.
         */
        RECEIVED_MESSAGE
    }

    protected String senderId;
    protected String sender;
    protected String text;
    protected long timestamp;
    protected MessageStatus status;
    private UUID id;

    /**
    * Construct a chat message that includes a message string, a sender identifier, and a sender
    * alias.
    *
    * @param senderId The unique ID string for the sender of the of the message. The
    * TextChatFragment uses this ID to group messages from the same sender in the user interface.
    *
    * @param sender The string (alias) identifying the sender of the message.
    *
    * @param text The text of the message.
    */
    public ChatMessage(String senderId, String sender, String text) {
        if ( sender == null || senderId == null ) {
            throw new IllegalArgumentException("The senderId and sender values cannot be null.");
        }
        this.senderId = senderId;
        this.sender = sender;
        this.text = text;
        this.id = UUID.randomUUID();
    }

    /**
    * Construct a chat message that includes a message string, a sender identifier, a sender alias
    * and the sent/recieved status.
    *
    * @param senderId The unique ID string for the sender of the of the message. The
    * TextChatFragment uses this ID to group messages from the same sender in the user interface.
    *
    * @param sender The string (alias) identifying the sender of the message.
    *
    * @param text The text of the message.
    *
    * @param status Whether the message was sent or received.
    */
    public ChatMessage(String senderId, String sender, String text, MessageStatus status) {
        if ( sender == null || senderId == null) {
            throw new IllegalArgumentException("The senderId and sender values cannot be null.");
        }
        this.senderId = senderId;
        this.sender = sender;
        this.text = text;
        this.id= UUID.randomUUID();
        this.status = status;
    }

    /**
     * Returns the unique ID of the sender.
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the unique ID of the sender.
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the sender alias for the message.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender alias for the message.
     */
    public void setSender(String sender) {
        if ( sender == null ) {
            throw new IllegalArgumentException("The sender value cannot be null.");
        }
        this.sender = sender;
    }

    /**
     * Returns the text of the message.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the message.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the sent/received status of the message.
     */
    public MessageStatus getStatus() {
        return status;
    }

    /**
     * Sets the sent/received status of the message.
     */
    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    /**
     * Sets the UNIX timestamp for the message.
     */
    public void setTimestamp(long time) {
        timestamp = time;
    }

    /**
     * Returns the UNIX timestamp for the message.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the unique identifier for the message. (This is a unique identifier for
     * the message, not the sender.)
     */
    public UUID getId() {
        return id;
    }

}

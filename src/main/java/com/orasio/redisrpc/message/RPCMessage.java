package com.orasio.redisrpc.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Message to be sent to the client
 */
public class RPCMessage implements Serializable {

    private String internalMessageId;
    private Map<String, String> headers;
    private String replyTo;
    private String sendTo;
    private Object payload;

    public RPCMessage() {
        this.internalMessageId = UUID.randomUUID().toString();
        this.headers = new HashMap<>();
    }

    public RPCMessage(Object payload) {
        this();
        this.payload = payload;
    }

    public RPCMessage(Object payload, Map<String, String> headers, String replyTo ) {
        this();
        this.replyTo = replyTo;
        this.headers.putAll(headers);
        this.payload = payload;
    }

    public RPCMessage(Object payload, Map<String, String> headers) {
        this();
        this.headers.putAll(headers);
        this.payload = payload;
    }


    public void addHeader(String key, String value){
        this.headers.put(key, value);
    }


    public String getInternalMessageId() {
        return internalMessageId;
    }

    public void setInternalMessageId(String internalMessageId) {
        this.internalMessageId = internalMessageId;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RPCMessage that = (RPCMessage) o;

        return getInternalMessageId().equals(that.getInternalMessageId());
    }

    @Override
    public int hashCode() {
        return getInternalMessageId().hashCode();
    }

    @Override
    public String toString() {
        return "RPCMessage{" +
                "internalMessageId='" + internalMessageId + '\'' +
                ", headers=" + headers +
                ", replyTo='" + replyTo + '\'' +
                ", payload=" + payload +
                '}';
    }
}

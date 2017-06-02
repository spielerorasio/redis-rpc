package com.orasio.redisrpc.message;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by spielerl on 02/06/2017.
 */
public class RPCResponse implements Serializable {
    private String internalMessageId;
    private Map<String, String> headers;
    private Object payload;

    public RPCResponse() {    }

    public RPCResponse(Map<String, String> headers, Object payload) {
        this.headers = headers;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RPCResponse that = (RPCResponse) o;

        if (getInternalMessageId() != null ? !getInternalMessageId().equals(that.getInternalMessageId()) : that.getInternalMessageId() != null)
            return false;
        if (getHeaders() != null ? !getHeaders().equals(that.getHeaders()) : that.getHeaders() != null) return false;
        return getPayload() != null ? getPayload().equals(that.getPayload()) : that.getPayload() == null;
    }

    @Override
    public int hashCode() {
        int result = getInternalMessageId() != null ? getInternalMessageId().hashCode() : 0;
        result = 31 * result + (getHeaders() != null ? getHeaders().hashCode() : 0);
        result = 31 * result + (getPayload() != null ? getPayload().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RPCResponse{" +
                "internalMessageId='" + internalMessageId + '\'' +
                ", headers=" + headers +
                ", payload=" + payload +
                '}';
    }
}

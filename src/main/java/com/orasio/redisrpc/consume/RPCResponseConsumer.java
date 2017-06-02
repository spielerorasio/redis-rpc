package com.orasio.redisrpc.consume;

import com.orasio.redisrpc.message.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by spielerl on 26/05/2017.
 */
public class RPCResponseConsumer implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPCResponseConsumer.class);

    private RedisSerializer responseSerializer;

    private Map<String, RPCResponseListener> replyToListeners = new ConcurrentHashMap<>();

    public void addRPCResponseListener(String replyTo, RPCResponseListener rpcResponseListener){
        replyToListeners.put(replyTo, rpcResponseListener );
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        RPCResponse deserializeMSG = (RPCResponse)responseSerializer.deserialize(message.getBody());
        LOGGER.info("Got response {}",deserializeMSG);
        CompletableFuture<RPCResponse> rpcMessage = CompletableFuture.completedFuture(deserializeMSG);
        replyToListeners.remove(deserializeMSG.getInternalMessageId()).onMessage(rpcMessage);
    }

    public void setRedisSerializer(RedisSerializer responseSerializer) {
        this.responseSerializer = responseSerializer;
    }
}
package com.orasio.redisrpc.producer;

import com.orasio.redisrpc.consume.RPCResponseConsumer;
import com.orasio.redisrpc.exception.RedisRPCException;
import com.orasio.redisrpc.message.RPCMessage;
import com.orasio.redisrpc.message.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by spielerl on 26/05/2017.
 */
@Service
public class RPCProducerImpl implements RPCProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPCProducerImpl.class);

    @Autowired
    private RedisTemplate<String, RPCMessage> redisTemplate;
    @Autowired
    private ChannelTopic topic;
    @Autowired
    private RPCResponseConsumer rpcResponseConsumer;

    @Override
    public void publish(String sendTo, RPCMessage message) {
        _publish(sendTo, message);
    }

    @Override
    public void publish(String sendTo, RPCResponse message) {
        _publish(sendTo, message);
    }

    @Override
    public void publish(RPCMessage message) {
        _publish(topic.getTopic(), message);
    }

    private void _publish(String sendTo, Object message) {
        LOGGER.info("publishing message {} to {}", message, sendTo);
        redisTemplate.convertAndSend(sendTo, message);
    }






    @Override
    @Async
    public CompletableFuture<RPCResponse> rpc(RPCMessage message)   {
        try {
            return _rpc(message);
        } catch (InterruptedException e) {
            throw new RedisRPCException(e);
        }
    }

    private CompletableFuture<RPCResponse> _rpc(RPCMessage message) throws InterruptedException {
        LOGGER.info("calling RPC message {} to {}", message, message.getSendTo());

        AtomicReference<CompletableFuture<RPCResponse>> atomicReference = new AtomicReference<>();


        final Semaphore available = new Semaphore(1, true);
        try{
            available.acquire();
            rpcResponseConsumer.addRPCResponseListener(message.getInternalMessageId(),
                    (completableFuture)-> {
                        atomicReference.set(completableFuture);
                        available.release();
                    });
            redisTemplate.convertAndSend(message.getSendTo(), message); //send to channel =  connector-id-rpc

            available.acquire();
            return atomicReference.get();
        } finally {
            available.release();
        }
    }


}

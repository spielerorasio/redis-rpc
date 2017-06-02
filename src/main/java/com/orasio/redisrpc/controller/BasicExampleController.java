package com.orasio.redisrpc.controller;

import com.orasio.redisrpc.exception.RedisRPCException;
import com.orasio.redisrpc.message.RPCMessage;
import com.orasio.redisrpc.message.RPCResponse;
import com.orasio.redisrpc.producer.RPCProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by spielerl on 26/05/2017.
 */
@RestController
public class BasicExampleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicExampleController.class);

    private static final String KEY = "testKey";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RPCProducer rpcProducer;
    @Autowired
    private ChannelTopic topic;
    @Autowired
    private ChannelTopic topicResponse;



    //    @RequestMapping(value = "/testRedis", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/testRedis", method = RequestMethod.GET)
    @ResponseBody
    public boolean testRedis() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String uuid = UUID.randomUUID().toString();
        stringStringValueOperations.set(KEY,uuid);
        String returnedValue = stringStringValueOperations.get(KEY);
        boolean equals = uuid.equals(returnedValue);
        Assert.isTrue(equals, "testing that Redis works  failed! ");
        LOGGER.info("testRedis was a success");
        return equals;
    }


    @RequestMapping(value = "/testPubSub", method = RequestMethod.GET)
    @ResponseBody
    public String testRedisPubSubWorks() {
        RPCMessage rpcMessage = new RPCMessage("Hello PubSub");
        rpcMessage.addHeader("header name","header value");
        rpcProducer.publish(rpcMessage);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @RequestMapping(value = "/testRPC", method = RequestMethod.GET)
    @ResponseBody
    public String testRedisRPCWorks() {
        RPCMessage rpcMessage = new RPCMessage("Hello RPC"); //creates internalMessageId
        rpcMessage.addHeader("header name rpc","header value rpc");
        rpcMessage.setSendTo(topic.getTopic());
        rpcMessage.setReplyTo(topicResponse.getTopic());

        CompletableFuture<RPCResponse> rpcResponse = rpcProducer.rpc(rpcMessage);
        try {
            return rpcResponse.get(3, TimeUnit.SECONDS).toString();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RedisRPCException(e);
        }
    }


}

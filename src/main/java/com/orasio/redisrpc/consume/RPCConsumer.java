package com.orasio.redisrpc.consume;

import com.orasio.redisrpc.message.RPCMessage;
import com.orasio.redisrpc.message.RPCResponse;
import com.orasio.redisrpc.producer.RPCProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Created by spielerl on 23/05/2017.
 */
public class RPCConsumer implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPCConsumer.class);

    private RedisSerializer serializer;
    private RPCProducer rpcProducer;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        RPCMessage deserializeMSG = (RPCMessage)serializer.deserialize(message.getBody());
        if(deserializeMSG.getReplyTo()!=null){
            RPCResponse rpcResponse = new RPCResponse();
            rpcResponse.setHeaders(deserializeMSG.getHeaders());
            rpcResponse.setInternalMessageId(deserializeMSG.getInternalMessageId());
            rpcResponse.setPayload(deserializeMSG.getPayload()+" Response");
            rpcProducer.publish(deserializeMSG.getReplyTo() , rpcResponse);
        }
        LOGGER.info("Got message {}",deserializeMSG);
    }

    public void setSerializer(RedisSerializer serializer) {
        this.serializer = serializer;
    }

    public void setRpcProducer(RPCProducer rpcProducer) {
        this.rpcProducer = rpcProducer;
    }
}

package com.orasio.redisrpc.producer;

import com.orasio.redisrpc.message.RPCMessage;
import com.orasio.redisrpc.message.RPCResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Created by spielerl on 26/05/2017.
 */
public interface RPCProducer {
    void publish(RPCMessage message);
    void publish(String sendTo, RPCMessage message);
    void publish(String sendTo, RPCResponse message);
    CompletableFuture<RPCResponse> rpc(RPCMessage message) ;
}

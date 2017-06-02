package com.orasio.redisrpc.consume;

import com.orasio.redisrpc.message.RPCResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Created by spielerl on 29/05/2017.
 */
@FunctionalInterface
public interface RPCResponseListener {
    void onMessage(CompletableFuture<RPCResponse> completableFuture);
}

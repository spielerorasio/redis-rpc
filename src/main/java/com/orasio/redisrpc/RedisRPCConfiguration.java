package com.orasio.redisrpc;

import com.orasio.redisrpc.consume.RPCConsumer;
import com.orasio.redisrpc.consume.RPCResponseConsumer;
import com.orasio.redisrpc.message.RPCMessage;
import com.orasio.redisrpc.message.RPCResponse;
import com.orasio.redisrpc.producer.RPCProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Created by spielerl on 26/05/2017.
 */
@Configuration
public class RedisRPCConfiguration {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private RPCProducer rpcProducer;

    @Value("${rpc.topic.name}")
    private String rpcTopicName;
    @Value("${service.instance.name}")
    private String responseConsumerTopicName;


    @Bean
    RPCConsumer rpcConsumer() {
        RPCConsumer rpcConsumer = new RPCConsumer();
        rpcConsumer.setRpcProducer(rpcProducer);
        rpcConsumer.setSerializer(serializer());
        return rpcConsumer;
    }
    @Bean
    RPCResponseConsumer rpcResponseConsumer() {
        RPCResponseConsumer rpcConsumer = new RPCResponseConsumer();
        rpcConsumer.setRedisSerializer(responseSerializer());
        return rpcConsumer;
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(rpcTopicName);
    }
    @Bean
    ChannelTopic topicResponse() {
        return new ChannelTopic(responseConsumerTopicName);
    }

    @Bean
    RedisTemplate<String, RPCMessage> redisTemplate() {
        final RedisTemplate<String, RPCMessage> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(serializer());
        return redisTemplate;
    }

    @Bean
    Jackson2JsonRedisSerializer serializer(){
        return new Jackson2JsonRedisSerializer<>(RPCMessage.class);
    }

    @Bean
    RedisTemplate<String, RPCResponse> redisResponseTemplate() {
        final RedisTemplate<String, RPCResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(responseSerializer());
        return redisTemplate;
    }


    @Bean
    Jackson2JsonRedisSerializer responseSerializer(){
        return new Jackson2JsonRedisSerializer<>(RPCResponse.class);
    }
    @Bean
    MessageListenerAdapter messageListener() {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(rpcConsumer());
        messageListenerAdapter.setSerializer(serializer());
        return messageListenerAdapter;
    }
    @Bean
    MessageListenerAdapter responseMessageListener() {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(rpcResponseConsumer());
        messageListenerAdapter.setSerializer(responseSerializer());
        return messageListenerAdapter;
    }

    @Bean
    RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener() , topic());
        container.addMessageListener(responseMessageListener() , topicResponse());
        return container;
    }


}

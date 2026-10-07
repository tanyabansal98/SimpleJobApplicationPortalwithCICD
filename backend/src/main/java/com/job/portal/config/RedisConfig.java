package com.job.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration // Tells Spring that this class holds the instructions for making objects that
               // you should manage
@EnableRedisHttpSession // HTTP sessions are stored in Redis instead of server memory
public class RedisConfig {

    // A default constructor is added by the compiler since no constructor is
    // declared here

    @Bean // Bean is just an object that Spring creates and looks after for a user
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>(); // Declaring a local variable to store Key and
                                                                        // Value in Redis
        template.setConnectionFactory(connectionFactory); // Each time an operation is run, this asks Factory for a
                                                          // connection, it runs the operation and releases the
                                                          // connection
        template.setKeySerializer(new StringRedisSerializer()); // a Serializer converts the Java Object - Keys into
                                                                // Bytes to store in Redis
        template.setValueSerializer(new StringRedisSerializer()); // converts the Java Object - Values into Bytes to
                                                                  // store in Redis
        return template; // Returns template object to Spring which configures it as SINGLETON with Bean
                         // Name RedisTemplate
    }

    // Creates a Jackson ObjectMapper (converts Java objects to JSON and back), adds
    // support for Java date/time types like LocalDateTime to it
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}

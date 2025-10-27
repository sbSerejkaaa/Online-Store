package com.example.payment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonConverter {

    private final ObjectMapper mapper = new ObjectMapper();

    // Маппим из Json в Объект, при этом благодаря Дженерикам работаем с любым классом
    public <T> T toObject(String json, Class<T> clazz) {
        try {
            // автоматом парсим из jsona в объект
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Json deserializing exception: {} ", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String toJson(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}


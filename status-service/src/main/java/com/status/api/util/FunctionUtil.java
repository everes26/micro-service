package com.status.api.util;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <S, D> List<D> mapList(List<S> source, Class<D> destinationClass) {

        return source.stream()
                .map(element -> modelMapper.map(element, destinationClass))
                .collect(Collectors.toList());
    }

    public static <T, R> R convertToEntity(T response, Class<R> entityClass) {
        return modelMapper.map(response, entityClass);
    }


    public static void sleep(int millis) throws InterruptedException {
        Thread.sleep(millis * 25L);
    }

    public static HttpHeaders getHttpHeaders(String headerValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Status-Service", headerValue);
        return headers;
    }
}

package ru.yandex.practicum.filmorate.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@Slf4j
public class DefaultLogger {
    public void logRequestMethod(RequestMethod requestMethod, String path) {
        log.debug("Получен запрос {} по адресу: {}", requestMethod, path);
    }
}
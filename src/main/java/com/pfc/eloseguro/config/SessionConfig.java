package com.pfc.eloseguro.config;

import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {
}

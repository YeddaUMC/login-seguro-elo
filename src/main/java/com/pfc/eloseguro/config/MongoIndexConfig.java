package com.pfc.eloseguro.config;

import com.pfc.eloseguro.entity.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexConfig implements CommandLineRunner {
    private final MongoTemplate mongoTemplate;

    public MongoIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        mongoTemplate.indexOps(Usuario.class)
                .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }
}

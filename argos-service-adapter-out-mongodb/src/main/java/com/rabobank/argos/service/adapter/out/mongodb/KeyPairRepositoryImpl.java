package com.rabobank.argos.service.adapter.out.mongodb;

import com.rabobank.argos.domain.model.KeyPair;
import com.rabobank.argos.service.domain.repository.KeyPairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class KeyPairRepositoryImpl implements KeyPairRepository {

    private static final String COLLECTION = "keyPairs";
    private static final String KEY_ID = "keyId";
    private final MongoTemplate template;

    @PostConstruct
    public void postConstruct() {
        createIndex(new Index(KEY_ID, Sort.Direction.ASC).unique());
    }

    @Override
    public void save(KeyPair keyPair) {
        template.save(keyPair, COLLECTION);
    }

    @Override
    public Optional<KeyPair> findByKeyId(String keyId) {
        return Optional.ofNullable(template.findOne(getPrimaryKeyQuery(keyId), KeyPair.class, COLLECTION));
    }

    @Override
    public boolean exists(String keyId) {
        return template.exists(getPrimaryKeyQuery(keyId), KeyPair.class, COLLECTION);
    }

    private Query getPrimaryKeyQuery(String keyId) {
        return new Query(where(KEY_ID).is(keyId));
    }


    private void createIndex(IndexDefinition indexDefinition) {
        template.indexOps(COLLECTION).ensureIndex(indexDefinition);
    }
}

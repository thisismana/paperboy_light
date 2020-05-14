package de.stroeer.paperboy_light.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ArticleRepo {

    private static final Logger logger = LoggerFactory.getLogger(ArticleRepo.class);

    private final String tableName;
    private final DynamoDbAsyncClient client;

    @Inject
    public ArticleRepo(final DynamoDbAsyncClient client, @Named("table_name") final String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public CompletableFuture<GetItemResponse> byId(final String id) {
        logger.info("byId({})", id);
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();
        return client.getItem(request);
    }
}

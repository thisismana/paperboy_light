package de.stroeer.paperboy_light.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

public class PaperboyModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
    }

    @Provides
    @Named("table_name")
    public String tableName() {
        return "content";
    }

    @Provides
    @Named("grpc_port")
    public int grpc_port() {
        return 8080;
    }

    @Provides
    @Named("http_port")
    public int http_port() {
        return 8081;
    }

    @Provides
    public DynamoDbAsyncClient dynamo() {
        return DynamoDbAsyncClient.builder().region(Region.EU_WEST_1).build();
    }
}

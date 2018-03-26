package com.amazon.asksdk.retromaster.storage;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * Client for DynamoDB persistance layer for the Retro Master skill.
 */
public class RetroMasterDynamoDbClient {
    private final AmazonDynamoDBClient dynamoDBClient;

    public RetroMasterDynamoDbClient(final AmazonDynamoDBClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    /**
     * Loads an item from DynamoDB by primary Hash Key. Callers of this method should pass in an
     * object which represents an item in the DynamoDB table item with the primary key populated.
     *
     * @param tableItem
     * @return
     */
    public RetroMasterUserDataItem loadItem(final RetroMasterUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        RetroMasterUserDataItem item = mapper.load(tableItem);
        return item;
    }

    /**
     * Stores an item to DynamoDB.
     *
     * @param tableItem
     */
    public void saveItem(final RetroMasterUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        mapper.save(tableItem);
    }

    /**
     * Creates a {@link DynamoDBMapper} using the default configurations.
     *
     * @return
     */
    private DynamoDBMapper createDynamoDBMapper() {
        return new DynamoDBMapper(dynamoDBClient);
    }
}

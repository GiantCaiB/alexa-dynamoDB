package com.amazon.asksdk.retromaster.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Model representing an item of the RetroMasterUserData table in DynamoDB for the RetroMaster
 * skill.
 */
@DynamoDBTable(tableName = "RetroMasterUserData")
public class RetroMasterUserDataItem {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String customerId;

    private RetroMasterMeetingData meetingData;

    @DynamoDBHashKey(attributeName = "CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = RetroMasterMeetingDataMarshaller.class)
    public RetroMasterMeetingData getMeetingData() {
        return meetingData;
    }

    public void setMeetingData(RetroMasterMeetingData meetingData) {
        this.meetingData = meetingData;
    }

    /**
     * A {@link DynamoDBMarshaller} that provides marshalling and unmarshalling logic for
     * {@link RetroMasterMeetingData} values so that they can be persisted in the database as String.
     */
    public static class RetroMasterMeetingDataMarshaller implements
            DynamoDBMarshaller<RetroMasterMeetingData> {

        @Override
        public String marshall(RetroMasterMeetingData meetingData) {
            try {
                return OBJECT_MAPPER.writeValueAsString(meetingData);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Unable to marshall meeting data", e);
            }
        }

        @Override
        public RetroMasterMeetingData unmarshall(Class<RetroMasterMeetingData> clazz, String value) {
            try {
                return OBJECT_MAPPER.readValue(value, new TypeReference<RetroMasterMeetingData>() {
                });
            } catch (Exception e) {
                throw new IllegalStateException("Unable to unmarshall meeting data value", e);
            }
        }
    }
}

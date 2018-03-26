package com.amazon.asksdk.retromaster.storage;

import com.amazon.speech.speechlet.Session;

/**
 * Contains the methods to interact with the persistence layer for RetroMaster in DynamoDB.
 */
public class RetroMasterDao {
    private final RetroMasterDynamoDbClient dynamoDbClient;

    public RetroMasterDao(RetroMasterDynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Reads and returns the {@link RetroMasterMeeting} using user information from the session.
     * <p>
     * Returns null if the item could not be found in the database.
     *
     * @param session
     * @return
     */
    public RetroMasterMeeting getRetroMasterMeeting(Session session) {
        RetroMasterUserDataItem item = new RetroMasterUserDataItem();
        item.setCustomerId(session.getUser().getUserId());

        item = dynamoDbClient.loadItem(item);

        if (item == null) {
            return null;
        }

        return RetroMasterMeeting.newInstance(session, item.getMeetingData());
    }

    /**
     * Saves the {@link RetroMasterMeeting} into the database.
     *
     * @param meeting
     */
    public void saveRetroMasterMeeting(RetroMasterMeeting meeting) {
        RetroMasterUserDataItem item = new RetroMasterUserDataItem();
        item.setCustomerId(meeting.getSession().getUser().getUserId());
        item.setMeetingData(meeting.getMeetingData());

        dynamoDbClient.saveItem(item);
    }
}

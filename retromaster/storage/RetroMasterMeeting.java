package com.amazon.asksdk.retromaster.storage;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.amazon.speech.speechlet.Session;

/**
 * Represents a score keeper game.
 */
public final class RetroMasterMeeting {
    private Session session;
    private RetroMasterMeetingData meetingData;

    private RetroMasterMeeting() {
    }

    /**
     * Creates a new instance of {@link RetroMasterMeeting} with the provided {@link Session} and
     * {@link RetroMasterMeetingData}.
     * <p>
     * To create a new instance of {@link RetroMasterMeetingData}, see
     * {@link RetroMasterMeetingData#newInstance()}
     *
     * @param session
     * @param meetingData
     * @return
     * @see RetroMasterMeetingData#newInstance()
     */
    public static RetroMasterMeeting newInstance(Session session, RetroMasterMeetingData meetingData) {
        RetroMasterMeeting meeting = new RetroMasterMeeting();
        meeting.setSession(session);
        meeting.setMeetingData(meetingData);
        return meeting;
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    protected Session getSession() {
        return session;
    }

    protected RetroMasterMeetingData getMeetingData() {
        return meetingData;
    }

    protected void setMeetingData(RetroMasterMeetingData meetingData) {
        this.meetingData = meetingData;
    }

    /**
     * Add a thing to 3 lists.
\    *
     */
    public void addWhatWentWell(String something) {
        meetingData.getWhatWentWell().add(something);
    }
    public void addWhatWentNotWell(String something) {
        meetingData.getWhatWentNotWell().add(something);
    }
    public void addWhatNeedToChange(String something) {
        meetingData.getWhatNeedToChange().add(something);
    }

}

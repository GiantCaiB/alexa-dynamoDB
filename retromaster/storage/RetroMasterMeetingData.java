package com.amazon.asksdk.retromaster.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains 3 lists data to represent a retro master meeting.
 */
public class RetroMasterMeetingData {
    private List<String> whatWentWell;
    private List<String> whatWentNotWell;
    private List<String> whatNeedToChange;

    public RetroMasterMeetingData() {
        // public no-arg constructor required for DynamoDBMapper marshalling
    }

    /**
     * Creates a new instance of {@link RetroMasterMeetingData} with initialized but empty lists information.
     *
     * @return
     */
    public static RetroMasterMeetingData newInstance() {
        RetroMasterMeetingData newInstance = new RetroMasterMeetingData();
        newInstance.setWhatWentWell(new ArrayList<String>());
        newInstance.setWhatWentNotWell(new ArrayList<String>());
        newInstance.setWhatNeedToChange(new ArrayList<String>());

        return newInstance;
    }

    public List<String> getWhatWentWell() {
        return whatWentWell;
    }

    public void setWhatWentWell(List<String> whatWentWell) {
        this.whatWentWell = whatWentWell;
    }

    public List<String> getWhatWentNotWell() {
        return whatWentNotWell;
    }

    public void setWhatWentNotWell(List<String> whatWentNotWell) {
        this.whatWentNotWell = whatWentNotWell;
    }

    public List<String> getWhatNeedToChange() {
        return whatNeedToChange;
    }

    public void setWhatNeedToChange(List<String> whatNeedToChange) {
        this.whatNeedToChange = whatNeedToChange;
    }


    @Override
    public String toString() {
        return "[RetroMasterMeetingData what_Went_Well: " + whatWentWell + "] what_Went_Not_Well: " + whatWentNotWell + "] what_Need_To_Change: " + whatNeedToChange + "] ";
    }
}

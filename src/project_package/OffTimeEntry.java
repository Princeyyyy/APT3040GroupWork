package project_package;

// OffTimeEntry class to represent each off time entry
class OffTimeEntry {

    private String startTimeString;
    private String endTimeString;

    public OffTimeEntry(String startTimeString, String endTimeString) {
        this.startTimeString = startTimeString;
        this.endTimeString = endTimeString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }
}

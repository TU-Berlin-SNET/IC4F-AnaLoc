package util;

/**
 * Created by felixboenke on 19.07.17.
 */
public enum TimeFormat {
    MINUTES("minutes"),
    HOURS("hours"),
    DAYS("days"),
    SECONDS("seconds");

    private String text;

    TimeFormat(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }


}

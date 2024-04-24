package strategies.sideways.options;

public class TrendStateMap {
    private String mostRecentTrendState;

    public TrendStateMap(String initialTrendState) {
        this.mostRecentTrendState = initialTrendState;
    }

    public String getMostRecentTrendState() {
        return mostRecentTrendState;
    }

    public void updateTrendState(String newTrendState) {
        this.mostRecentTrendState = newTrendState;
    }
}

package service;

import java.time.LocalDateTime;
import java.util.Comparator;

class StartTimeComparator implements Comparator<LocalDateTime> {
    @Override
    public int compare(LocalDateTime time1, LocalDateTime time2) {
        if (time1 == null || time2 == null) {
            throw new IllegalArgumentException("Времена не могут быть null");
        }
        return time1.compareTo(time2);

    }
}

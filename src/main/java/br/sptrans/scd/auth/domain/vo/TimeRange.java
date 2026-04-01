package br.sptrans.scd.auth.domain.vo;

import java.time.LocalTime;

import lombok.Value;



@Value
public class TimeRange {

    private final LocalTime start;
    private final LocalTime end;

    public boolean contains(LocalTime time) {
        if (start == null || end == null) {
            return true;
        }
        return !time.isBefore(start) && !time.isAfter(end);
    }

}

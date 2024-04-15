package com.pricemerger.model;

import java.util.Date;

public class Period {
    private Date begin;
    private Date end;

    public Period(Date begin, Date end) {
        this.begin = begin;
        this.end = end;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public boolean containsPeriod(Period period){
        return this.begin.before(period.getBegin()) && this.end.after(period.getEnd());
    }

    public boolean containsDate(Date date){
        return this.begin.before(date) && this.end.after(date);
    }

    public int hasConflictWith(Period period){
        int result = 0;

        if(containsDate(period.getBegin())){
            result += 1;
        }

        if(containsDate(period.getEnd())){
            result += 2;
        }

        return result;
    }
}
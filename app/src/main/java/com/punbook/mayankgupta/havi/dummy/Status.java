package com.punbook.mayankgupta.havi.dummy;

import java.util.IllegalFormatException;

/**
 * Created by mayankgupta on 19/03/17.
 */

public enum Status {

    ACTIVE,
    EXPIRED,
    PAID,
    SUBMITTED;

    public static Status parse(String temp){

        if(org.apache.commons.lang3.StringUtils.isBlank(temp)){
            throw new IllegalArgumentException();
        }

        switch (temp.toLowerCase()){

            case "active" : return ACTIVE;
            case "expired" : return EXPIRED;
            case "submitted" : return SUBMITTED;
            case "paid" : return PAID;
            default: return ACTIVE;
        }

    }


}

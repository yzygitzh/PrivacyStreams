package com.github.privacystreams.motion;

import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.providers.MultiItemStreamProvider;

/**
 * Created by lenovo on 2017/3/6.
 */

public class PhysicalActivity extends Item{
    // type: Long
    private static final String TIMESTAMP = "timestamp";
    // type: String
    private static final String MOTIONTYPE ="motiontype";

    PhysicalActivity(long timestamp,String motionType){
    this.setFieldValue(TIMESTAMP,timestamp);                        //Assign value to each of the member variable
    this.setFieldValue(MOTIONTYPE,motionType);
    }
    public static MultiItemStreamProvider asUpdates() {
        return new PhysicalMotionUpdatesProvider();
    }
}
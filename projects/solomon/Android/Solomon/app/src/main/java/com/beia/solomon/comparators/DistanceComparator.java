package com.beia.solomon.comparators;

import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import java.util.Comparator;

public class DistanceComparator implements Comparator<IBeaconDevice>
{
    @Override
    public int compare(IBeaconDevice o1, IBeaconDevice o2)
    {
        if(o2.getDistance() < o1.getDistance())
            return 1;
        if(o2.getDistance() < o1.getDistance())
            return -1;
        return 0;
    }
}

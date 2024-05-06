/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems.api;

import oems.dto.CloseOrderAll;
import oems.dto.NewOrderSingle;

public interface OMSIn {

    // Map methods
    void addUpdateNOS(CharSequence setKey, NewOrderSingle nos);
    NewOrderSingle getNOS(CharSequence getKey, NewOrderSingle nos);

    void addUpdateCOA(CharSequence setKey, CloseOrderAll coa);
    CloseOrderAll getCOA(CharSequence getKey, CloseOrderAll coa);

    void addUpdateNOSArray(CharSequence setKey, double[] nosArray);
    double[] getNOSArray(CharSequence getKey, double[] nosArray);

    void addUpdateCOAArray(CharSequence setKey, double[] coaArray);
    double[] getCOAArray(CharSequence getKey, double[] coaArray);

    // Order methods
    void newOrderSingle(NewOrderSingle nos);
    void closeOrderAll(CloseOrderAll coa);
    void updateSLTP(NewOrderSingle nos);
}

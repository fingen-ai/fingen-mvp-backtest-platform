/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems.api;

import oems.dto.CloseOrderAll;
import oems.dto.NewOrderSingle;

public interface OMSIn {

    void newOrderSingle(NewOrderSingle nos);

    void closeOrderAll(CloseOrderAll coa);

    void updateSLTP(NewOrderSingle nos);
}

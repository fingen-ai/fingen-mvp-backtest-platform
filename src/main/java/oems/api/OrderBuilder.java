package oems.api;

import core.service.oems.OEMSData;
import oems.dto.CloseOrderAll;
import oems.dto.NewOrderSingle;

public interface OrderBuilder {

    NewOrderSingle buildNOS(OEMSData oems);

    CloseOrderAll buildCOA(OEMSData oems);
}

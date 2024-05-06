package oems;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.map.ChronicleMap;
import oems.api.OMSIn;
import oems.dto.CloseOrderAll;
import oems.dto.NewOrderSingle;

import java.io.File;
import java.io.IOException;

public class OMSImpl implements OMSIn {

    // NOS map inits
    File nosMap = new File(OS.USER_HOME + "/FinGen/Maps/OMS/nosMap");
    static ChronicleMap<CharSequence, NewOrderSingle> NOS;
    static NewOrderSingle nos = new NewOrderSingle();

    // COA map inits
    File coaMap = new File(OS.USER_HOME + "/FinGen/Maps/OMS/coaMap");
    static ChronicleMap<CharSequence, CloseOrderAll> COA;
    static CloseOrderAll coa = new CloseOrderAll();

    public OMSImpl() throws IOException {

        // NOS map
        if (!nosMap.exists()) {
            NOS = ChronicleMap
                    .of(CharSequence.class, NewOrderSingle.class)
                    .name("nos.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(nos)
                    .createPersistedTo(nosMap);
        } else {
            NOS = ChronicleMap
                    .of(CharSequence.class, NewOrderSingle.class)
                    .name("nos.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(nos)
                    .recoverPersistedTo(nosMap, false);
        }

        // COA map
        if (!coaMap.exists()) {
            COA = ChronicleMap
                    .of(CharSequence.class, CloseOrderAll.class)
                    .name("coa.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(coa)
                    .createPersistedTo(coaMap);
        } else {
            COA = ChronicleMap
                    .of(CharSequence.class, CloseOrderAll.class)
                    .name("coa.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(coa)
                    .recoverPersistedTo(coaMap, false);
        }
    }

    // NOS map methods
    public void addUpdateNOS(CharSequence setKey, NewOrderSingle nos) {
        NOS.put(setKey, nos);
    }
    public NewOrderSingle getNOS(CharSequence getKey, NewOrderSingle nos) {
        return NOS.getUsing(getKey, nos);
    }

    // COA map methods
    public void addUpdateCOA(CharSequence setKey, CloseOrderAll coa) {
        COA.put(setKey, coa);
    }
    public CloseOrderAll getCOA(CharSequence getKey, CloseOrderAll coa) {
        return COA.getUsing(getKey, coa);
    }

    // All order map method calls
    @Override
    public void newOrderSingle(NewOrderSingle nos) {
        addUpdateNOS(nos.clOrdID(), nos);
    }

    @Override
    public void closeOrderAll(CloseOrderAll coa) {
        addUpdateCOA(coa.clOrdID(), coa);
    }
}

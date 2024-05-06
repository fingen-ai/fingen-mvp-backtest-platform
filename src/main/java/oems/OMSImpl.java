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

    // NOS Array map inits
    File nosArrayMap = new File(OS.USER_HOME + "/FinGen/Maps/OMS/nosArrayMap");
    static ChronicleMap<CharSequence, double[]> NOSArrayMap;

    // COA Array map inits
    File coaArrayMap = new File(OS.USER_HOME + "/FinGen/Maps/OMS/coaArrayMap");
    static ChronicleMap<CharSequence, double[]> COAArrayMap;

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

        // NOS Array map
        if(!nosArrayMap.exists()) {
            NOSArrayMap = ChronicleMap
                    .of(CharSequence.class, double[].class)
                    .name("nosArray.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(new double[50])
                    .createPersistedTo(nosArrayMap);
        } else {
            NOSArrayMap = ChronicleMap
                    .of(CharSequence.class, double[].class)
                    .name("nosArray.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(new double[144])
                    .recoverPersistedTo(nosArrayMap, false);
        }

        // COA Array map
        if(!coaArrayMap.exists()) {
            COAArrayMap = ChronicleMap
                    .of(CharSequence.class, double[].class)
                    .name("coaArray.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(new double[1_000])
                    .createPersistedTo(coaArrayMap);
        } else {
            COAArrayMap = ChronicleMap
                    .of(CharSequence.class, double[].class)
                    .name("coaArray.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(new double[1_000])
                    .recoverPersistedTo(coaArrayMap, false);
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

    // NOS ARRAY map methods
    public void addUpdateNOSArray(CharSequence setKey, double[] nosArray) {
        NOSArrayMap.put(setKey, nosArray);
    }
    public double[] getNOSArray(CharSequence getKey, double[] nosArray) {
        return NOSArrayMap.getUsing(getKey, nosArray);
    }

    // COA ARRAY map methods
    public void addUpdateCOAArray(CharSequence setKey, double[] coaArray) {
        COAArrayMap.put(setKey, coaArray);
    }
    public double[] getCOAArray(CharSequence getKey, double[] coaArray) {
        return COAArrayMap.getUsing(getKey, coaArray);
    }

    // All order map method calls
    @Override
    public void newOrderSingle(NewOrderSingle nos) {
        // update map
        addUpdateNOS(nos.clOrdID(), nos);
        // update array map
    }

    @Override
    public void closeOrderAll(CloseOrderAll coa) {
        // update map
        addUpdateCOA(coa.clOrdID(), coa);
        // update array map
    }

    @Override
    public void updateSLTP(NewOrderSingle nos) {
        // update map
        addUpdateNOS(nos.clOrdID(), nos);
    }

    @Override
    public void updateNOSArray(NewOrderSingle nos, double[] nosArray) {
        addUpdateNOSArray(nos.clOrdID(), nosArray);
    }

    @Override
    public void updateCOAArray(CloseOrderAll coa, double[] coaArray) {
        addUpdateCOAArray(nos.clOrdID(), coaArray);
    }
}

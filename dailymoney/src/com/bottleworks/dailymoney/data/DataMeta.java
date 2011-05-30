package com.bottleworks.dailymoney.data;

/**
 * 
 * @author dennis
 * 
 */
public class DataMeta {

    public static final String TB_ACC = "dm_acc";

    public static final String COL_ACC_ID = "id_";
    public static final String COL_ACC_NAME = "nm_";
    public static final String COL_ACC_TYPE = "tp_";
    public static final String COL_ACC_CASHACCOUNT = "ca_";
    public static final String COL_ACC_INITVAL = "iv_";

    public static final String[] COL_ACC_ALL = new String[] { COL_ACC_ID, COL_ACC_NAME, COL_ACC_TYPE, COL_ACC_CASHACCOUNT,COL_ACC_INITVAL };

    public static final String TB_DET = "dm_det";

    public static final String COL_DET_ID = "id_";
    public static final String COL_DET_FROM = "fr_";
    public static final String COL_DET_TO = "to_";
    public static final String COL_DET_FROM_TYPE= "frt_";
    public static final String COL_DET_TO_TYPE = "tot_";
    public static final String COL_DET_DATE = "dt_";
    public static final String COL_DET_MONEY = "mn_";
    public static final String COL_DET_NOTE = "nt_";
    public static final String COL_DET_ARCHIVED = "ar_";

    public static final String[] COL_DET_ALL = new String[] { COL_DET_ID, COL_DET_FROM, COL_DET_FROM_TYPE,
            COL_DET_TO, COL_DET_TO_TYPE, COL_DET_DATE, COL_DET_MONEY, COL_DET_NOTE, COL_DET_ARCHIVED };
}

package com.bottleworks.dailymoney.data;
/**
 * 
 * @author dennis
 *
 */
public class SQLiteMeta {

    public static final String TB_ACC = "dm_account";
    
    public static final String COL_ACC_ID = "id";
    public static final String COL_ACC_NAME = "name";
    public static final String COL_ACC_TYPE = "type";
    public static final String COL_ACC_INITVAL = "initval";
    
    public static final String[] COL_ACC_ALL = new String[]{COL_ACC_ID,COL_ACC_NAME,COL_ACC_TYPE,COL_ACC_INITVAL};
}

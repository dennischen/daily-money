package com.bottleworks.dailymoney.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.bottleworks.commons.util.Files;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
/**
 * 
 * @author dennis
 *
 */
public class DataMaintenanceActivity extends ContextsActivity implements OnClickListener {

    String csvEncoding;
    
    String workingFolder;
    
    boolean backupcsv = false;
    
    static final String APPVER = "appver:";
    
    DateFormat backupformat = new SimpleDateFormat("yyyyMMdd-HHmmss");
    
    int vercode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamain);
        workingFolder = getContexts().getPrefWorkingFolder();
        backupcsv = getContexts().isPrefBackupCSV();
        
        vercode = getContexts().getApplicationVersionCode();
        csvEncoding = getContexts().getPrefCSVEncoding();
        initialListener();

    }

    private void initialListener() {
        findViewById(R.id.datamain_import_csv).setOnClickListener(this);
        findViewById(R.id.datamain_export_csv).setOnClickListener(this);
        findViewById(R.id.datamain_share_csv).setOnClickListener(this);
        findViewById(R.id.datamain_reset).setOnClickListener(this);
        findViewById(R.id.datamain_create_default).setOnClickListener(this);
        findViewById(R.id.datamain_clear_folder).setOnClickListener(this);
        findViewById(R.id.datamain_backup_db_to_sd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.datamain_import_csv) {
            doImportCSV();
        } else if (v.getId() == R.id.datamain_export_csv) {
            doExportCSV();
        } else if (v.getId() == R.id.datamain_share_csv) {
            doShareCSV();
        } else if (v.getId() == R.id.datamain_reset) {
            doReset();
        } else if (v.getId() == R.id.datamain_create_default) {
            doCreateDefault();
        } else if (v.getId() == R.id.datamain_clear_folder) {
            doClearFolder();
        } else if (v.getId() == R.id.datamain_backup_db_to_sd) {
            doBackupDbToSD();
        }
    }

    private void doBackupDbToSD() {
        final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
            int count = -1;
            Calendar now = Calendar.getInstance();

            public void onBusyError(Throwable t) {
                GUIs.error(DataMaintenanceActivity.this, t);
            }

            public void onBusyFinish() {
                if (count > 0) {
                    String msg = i18n.string(R.string.msg_db_backuped, Integer.toString(count), workingFolder);
                    GUIs.alert(DataMaintenanceActivity.this, msg);
                    getContexts().setLastBackup(now.getTime());
                } else {
                    GUIs.alert(DataMaintenanceActivity.this, R.string.msg_no_db);
                }
            }

            @Override
            public void run() {
                try {
                    count = Files.copyDatabases(getContexts().getDbFolder(), getContexts().getSdFolder(), now.getTime());
                    Files.copyPrefFile(getContexts().getPrefFolder(), getContexts().getSdFolder(), now.getTime());
                    count++;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        };
        GUIs.doBusy(DataMaintenanceActivity.this, job);
    }

    private void doClearFolder() {
        final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.alert(DataMaintenanceActivity.this, i18n.string(R.string.msg_folder_cleared,workingFolder));
            }

            @Override
            public void run() {
                File sd = Environment.getExternalStorageDirectory();
                File folder = new File(sd, workingFolder);
                if (!folder.exists()) {
                    return;
                }
                for(File f: folder.listFiles()){
                   if(f.isFile() && f.getName().toLowerCase().endsWith(".csv")){
                       f.delete();
                   }
                }
            }
        };

        GUIs.confirm(this, i18n.string(R.string.qmsg_clear_folder,workingFolder), new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if (((Integer) data).intValue() == GUIs.OK_BUTTON) {
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
        
    }

    private void doCreateDefault() {

        final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.alert(DataMaintenanceActivity.this, R.string.msg_default_created);
            }

            @Override
            public void run() {
                IDataProvider idp = getContexts().getDataProvider();
                new DataCreator(idp, i18n).createDefaultAccount();
            }
        };

        GUIs.confirm(this, i18n.string(R.string.qmsg_create_default), new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if (((Integer) data).intValue() == GUIs.OK_BUTTON) {
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
    }

    private void doReset() {

        
        
        new AlertDialog.Builder(this).setTitle(i18n.string(R.string.qmsg_reset))
        .setItems(R.array.csv_type_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
                    public void onBusyError(Throwable t) {
                        GUIs.error(DataMaintenanceActivity.this, t);
                    }
                    @Override
                    public void run() {
                        try {
                            _resetDate(which);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(),e);
                        }
                    }
                };
                GUIs.doBusy(DataMaintenanceActivity.this, job);
            }
        }).show();
    }

    private void doExportCSV() {
        final int workingBookId = getContexts().getWorkingBookId(); 
        new AlertDialog.Builder(this).setTitle(i18n.string(R.string.qmsg_export_csv))
                .setItems(R.array.csv_type_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
                            int count = -1;
                            public void onBusyError(Throwable t) {
                                GUIs.error(DataMaintenanceActivity.this, t);
                            }
                            public void onBusyFinish() {
                                if(count>=0){
                                    String msg = i18n.string(R.string.msg_csv_exported,Integer.toString(count),workingFolder);
                                    GUIs.alert(DataMaintenanceActivity.this,msg);
                                }else{
                                    GUIs.alert(DataMaintenanceActivity.this,R.string.msg_no_csv);
                                }
                            }
                            @Override
                            public void run() {
                                try {
                                    count = _exportToCSV(which,workingBookId);
                                } catch (Exception e) {
                                    throw new RuntimeException(e.getMessage(),e);
                                }
                            }
                        };
                        GUIs.doBusy(DataMaintenanceActivity.this, job);
                    }
                }).show();
    }

    private void doImportCSV() {
        final int workingBookId = getContexts().getWorkingBookId(); 
        new AlertDialog.Builder(this).setTitle(i18n.string(R.string.qmsg_import_csv))
                .setItems(R.array.csv_type_import_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
                            int count = -1;
                            public void onBusyError(Throwable t) {
                                GUIs.error(DataMaintenanceActivity.this, t);
                            }
                            public void onBusyFinish() {
                                if(count>=0){
                                    String msg = i18n.string(R.string.msg_csv_imported,Integer.toString(count),workingFolder);
                                    GUIs.alert(DataMaintenanceActivity.this,msg);
                                }else{
                                    GUIs.alert(DataMaintenanceActivity.this,R.string.msg_no_csv);
                                }
                            }
                            @Override
                            public void run() {
                                try {
                                    count = _importFromCSV(which,workingBookId);
                                } catch (Exception e) {
                                    throw new RuntimeException(e.getMessage(),e);
                                }
                            }
                        };
                        GUIs.doBusy(DataMaintenanceActivity.this, job);
                    }
                }).show();
    }
    
    private void doShareCSV() {        
        new AlertDialog.Builder(this).setTitle(i18n.string(R.string.qmsg_share_csv))
                .setItems(R.array.csv_type_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        final GUIs.IBusyRunnable job = new GUIs.BusyAdapter() {
                            int count = -1;
                            public void onBusyError(Throwable t) {
                                GUIs.error(DataMaintenanceActivity.this, t);
                            }
                            public void onBusyFinish() {
                                if(count<0){
                                    GUIs.alert(DataMaintenanceActivity.this,R.string.msg_no_csv);
                                }
                            }
                            @Override
                            public void run() {
                                try {
                                    count = _shareCSV(which);
                                } catch (Exception e) {
                                    throw new RuntimeException(e.getMessage(),e);
                                }
                            }
                        };
                        GUIs.doBusy(DataMaintenanceActivity.this, job);
                    }
                }).show();
    }
    
    

    private File getWorkingFile(String name) throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        File folder = new File(sd, workingFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, name);
        return file;
    }
    
    
    private void _resetDate(int mode){
        if(Contexts.DEBUG){
            Logger.d("reset date"+mode);
        }
        boolean account = false;
        boolean detail = false;
        switch(mode){
            case 0:
                account = detail = true;
                break;
            case 1:
                account = true;
                break;
            case 2:
                detail = true;
                break;
        }
        IDataProvider idp = getContexts().getDataProvider();
        if(account && detail){
            idp.reset();
        }else if(account){
            idp.deleteAllAccount();
        }else if(detail){
            idp.deleteAllDetail();
        }
        
    }

    /** running in thread **/
    private int _exportToCSV(int mode, int workingBookId) throws IOException {
        if(Contexts.DEBUG){
            Logger.d("export to csv "+mode);
        }
        boolean account = false;
        boolean detail = false;
        switch(mode){
            case 0:
                account = detail = true;
                break;
            case 1:
                account = true;
                break;
            case 2:
                detail = true;
                break;
            default :return -1;
        }
        IDataProvider idp = getContexts().getDataProvider();
        StringWriter sw;
        CsvWriter csvw;
        int count = 0;
        String backupstamp = backupformat.format(new Date());
        if(detail){
            sw = new StringWriter();
            csvw = new CsvWriter(sw, ',');
            csvw.writeRecord(new String[]{"id","from","to","date","value","note","archived",APPVER+vercode});
            for (Detail d : idp.listAllDetail()) {
                count++;
                csvw.writeRecord(new String[] { Integer.toString(d.getId()), d.getFrom(), d.getTo(),
                        Formats.normalizeDate2String(d.getDate()), Formats.normalizeDouble2String(d.getMoney()),
                        d.getNote(),d.isArchived()?"1":"0"});
            }
            csvw.close();
            String csv = sw.toString();
            File file0 = getWorkingFile("details.csv");
            File file1 = getWorkingFile("details-"+workingBookId+".csv");
            
            saveFile(file0,csv,backupstamp);
            saveFile(file1,csv,backupstamp);
        }

        if(account){
            sw = new StringWriter();
            csvw = new CsvWriter(sw, ',');
            csvw.writeRecord(new String[]{"id","type","name","init","cash",APPVER+vercode});
            for (Account a : idp.listAccount(null)) {
                count++;
                csvw.writeRecord(new String[]{a.getId(),a.getType(),a.getName(),Formats.normalizeDouble2String(a.getInitialValue()),a.isCashAccount()?"1":"0"});
            }
            csvw.close();
            String csv = sw.toString();
            File file0 = getWorkingFile("accounts.csv");
            File file1 = getWorkingFile("accounts-"+workingBookId+".csv");
            
            saveFile(file0,csv,backupstamp);
            saveFile(file1,csv,backupstamp);
        }
        
        return count;
    }
    
    private void saveFile(File file0, String csv, String backupstamp) throws IOException {
        if(file0.exists()){
            if(backupcsv){
                String fn = file0.getName();
                String ext = Files.getExtension(fn);
                String main = Files.getMain(fn);
                Files.copyFileTo(file0,new File(file0.getParentFile(),main+"."+backupstamp+"."+ext));
            }
        }else{
            file0.createNewFile();
        }
            
        Files.saveString(csv, file0, csvEncoding);
        if(Contexts.DEBUG){
            Logger.d("export to "+file0.toString());
        }
    }

    private int getAppver(String str){
        if(str!=null && str.startsWith(APPVER)){
            try{
                return Integer.parseInt(str.substring(APPVER.length()));
            }catch(Exception x){
                if(Contexts.DEBUG){
                    Logger.d(x.getMessage());
                }
            }
        }
        return 0;
    }
    
    /** running in thread 
     * @param workingBookId **/
    private int _importFromCSV(int mode, int workingBookId) throws Exception{
        if(Contexts.DEBUG){
            Logger.d("import from csv "+mode);
        }
        boolean account = false;
        boolean detail = false;
        boolean shared = mode>=3;
        if(shared) mode = mode-3;
        switch(mode){
            case 0:
                account = detail = true;
                break;
            case 1:
                account = true;
                break;
            case 2:
                detail = true;
                break; 
            default :return -1;
        }
        
        IDataProvider idp = getContexts().getDataProvider();
        File details = getWorkingFile(shared?"details.csv":"details-"+workingBookId+".csv");
        File accounts = getWorkingFile(shared?"accounts.csv":"accounts-"+workingBookId+".csv");
        
        if((detail && (!details.exists() || !details.canRead())) || 
                (account && (!accounts.exists() || !accounts.canRead()))){
            return -1;
        }
        
        CsvReader accountReader=null;
        CsvReader detailReader=null;
        try{
            int count = 0;
            if(account){
                accountReader = new CsvReader(new InputStreamReader(new FileInputStream(accounts),csvEncoding));
            }
            if(detail){
                detailReader = new CsvReader(new InputStreamReader(new FileInputStream(details),csvEncoding));
            }
            
            if((accountReader!=null && !accountReader.readHeaders())){
                return -1;
            }
            
            //don't combine with account checker
            if((detailReader!=null && !detailReader.readHeaders())){
                return -1;
            }
            
            if(detail){
                detailReader.setTrimWhitespace(true);
                int appver = getAppver(detailReader.getHeaders()[detailReader.getHeaderCount()-1]);
                
                idp.deleteAllDetail();
                while(detailReader.readRecord()){
                    Detail det = new Detail(detailReader.get("from"),detailReader.get("to"),Formats.normalizeString2Date(detailReader.get("date")),Formats.normalizeString2Double(detailReader.get("value")),detailReader.get("note"));
                    String archived = detailReader.get("archived");
                    if("1".equals(archived)){
                        det.setArchived(true);
                    }else if("0".equals(archived)){
                        det.setArchived(false);
                    }else{
                        det.setArchived(Boolean.parseBoolean(archived));
                    }
                    
                    idp.newDetailNoCheck(Integer.parseInt(detailReader.get("id")),det);
                    count ++;
                }
                detailReader.close();
                detailReader = null;
                if(Contexts.DEBUG){
                    Logger.d("import from "+details+" ver:"+appver);
                }
            }
            
            if(account){
                accountReader.setTrimWhitespace(true);
                int appver = getAppver(accountReader.getHeaders()[accountReader.getHeaderCount()-1]);
                idp.deleteAllAccount();
                while(accountReader.readRecord()){
                    Account acc = new Account(accountReader.get("type"),accountReader.get("name"),Formats.normalizeString2Double(accountReader.get("init")));
                    String cash = accountReader.get("cash");
                    acc.setCashAccount("1".equals(cash)?true:false);
                    
                    idp.newAccountNoCheck(accountReader.get("id"),acc);
                    count ++;
                }
                accountReader.close();
                accountReader = null;
                if(Contexts.DEBUG){
                    Logger.d("import from "+accounts+" ver:"+appver);
                }
            }
            return count;
        }finally{
            if(accountReader!=null){
                accountReader.close();
            }
            if(detailReader!=null){
                detailReader.close();
            }
        }
    }
    
    
    /** running in thread **/
    private int _shareCSV(int mode) throws Exception{
        if(Contexts.DEBUG){
            Logger.d("share csv "+mode);
        }
        boolean account = false;
        boolean detail = false;
        switch(mode){
            case 0:
                account = detail = true;
                break;
            case 1:
                account = true;
                break;
            case 2:
                detail = true;
                break;
            default :return -1;
        }
        
        File details = getWorkingFile("details.csv");
        File accounts = getWorkingFile("accounts.csv");
        
        if((detail && (!details.exists() || !details.canRead())) || 
                (account && (!accounts.exists() || !accounts.canRead())) ){
            return -1;
        }
        
        int count = 0;

        List<File> files = new ArrayList<File>();
        
        if (detail) {
            files.add(details);
            count++;
        }

        if (account) {
            files.add(accounts);
            count++;
        }
        
        if(count>0){
            DateFormat df = getContexts().getDateFormat();
            getContexts().shareTextContent(i18n.string(R.string.msg_share_csv_title,df.format(new Date())),i18n.string(R.string.msg_share_csv_content),files);
        }
        return count;
            
    }
}

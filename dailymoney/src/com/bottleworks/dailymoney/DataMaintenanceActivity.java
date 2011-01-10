package com.bottleworks.dailymoney;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.bottleworks.commons.util.Files;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class DataMaintenanceActivity extends ContextsActivity implements OnClickListener {

    String CSV_ENCODEING = "utf8";
    
    String workingFolder;
    
    boolean exportBackup = false;
    
    DateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamain);
        workingFolder = Contexts.instance().getPrefWorkingFolder();
        exportBackup = Contexts.instance().isPrefExportBackup();
        initialListener();

    }

    private void initialListener() {
        findViewById(R.id.datamain_import_csv).setOnClickListener(this);
        findViewById(R.id.datamain_export_csv).setOnClickListener(this);
        findViewById(R.id.datamain_reset).setOnClickListener(this);
        findViewById(R.id.datamain_create_default).setOnClickListener(this);
        findViewById(R.id.datamain_clear_folder).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.datamain_import_csv:
            doImportCSV();
            break;
        case R.id.datamain_export_csv:
            doExportCSV();
            break;
        case R.id.datamain_reset:
            doReset();
            break;
        case R.id.datamain_create_default:
            doCreateDefault();
            break;
        case R.id.datamain_clear_folder:
            doClearFolder();
            break;
        }
    }

    private void doClearFolder() {
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
        GUIs.alert(DataMaintenanceActivity.this, i18n.string(R.string.msg_folder_cleared,workingFolder));
    }

    private void doCreateDefault() {

        final GUIs.IBusyListener job = new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.alert(DataMaintenanceActivity.this, R.string.msg_default_created);
            }

            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
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

        final GUIs.IBusyListener job = new GUIs.BusyAdapter() {
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                idp.reset();
            }
        };

        GUIs.confirm(this, i18n.string(R.string.qmsg_reset), new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if (((Integer) data).intValue() == GUIs.OK_BUTTON) {
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
    }

    private void doExportCSV() {
        final GUIs.IBusyListener job = new GUIs.BusyAdapter() {
            
            public void onBusyError(Throwable t) {
                GUIs.error(DataMaintenanceActivity.this, t);
            }
            @Override
            public void run() {
                try {
                    _exportToCSV();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }
        };

        GUIs.confirm(this, i18n.string(R.string.qmsg_export_csv), new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if (((Integer) data).intValue() == GUIs.OK_BUTTON) {
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
    }

    private void doImportCSV() {
        final GUIs.IBusyListener job = new GUIs.BusyAdapter() {
            public void onBusyError(Throwable t) {
                GUIs.error(DataMaintenanceActivity.this, t);
            }

            @Override
            public void run() {
                try {
                    _importFromCSV();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }
        };

        GUIs.confirm(this, i18n.string(R.string.qmsg_import_csv), new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if (((Integer) data).intValue() == GUIs.OK_BUTTON) {
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
    }
    
    

    private File getWorkingFile(String name, boolean create) throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        File folder = new File(sd, workingFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, name);
        if (create && !file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /** running in thread **/
    private void _exportToCSV() throws IOException {
        IDataProvider idp = Contexts.instance().getDataProvider();
        StringWriter sw = new StringWriter();
        int count = 0;
        CsvWriter csvw = new CsvWriter(sw, ',');
        csvw.writeRecord(new String[]{"id","from","to","date","value","note","archived"});
        for (Detail d : idp.listAllDetail()) {
            count++;
            csvw.writeRecord(new String[] { Integer.toString(d.getId()), d.getFrom(), d.getTo(),
                    Formats.normalizeDate2String(d.getDate()), Formats.normalizeDouble2String(d.getMoney()),
                    d.getNote(),Boolean.toString(d.isArchived())});
        }
        csvw.close();
        String csv = sw.toString();
        File file = getWorkingFile("details.csv", true);
        Files.saveString(csv, file, CSV_ENCODEING);
        if(exportBackup){
            file = getWorkingFile("details."+format.format(new Date())+".csv", true);
            Files.saveString(csv, file, CSV_ENCODEING);
        }

        sw = new StringWriter();
        csvw = new CsvWriter(sw, ',');
        csvw.writeRecord(new String[]{"id","type","name","init"});
        for (Account a : idp.listAccount(null)) {
            count++;
            csvw.writeRecord(new String[]{a.getId(),a.getType(),a.getName(),Formats.normalizeDouble2String(a.getInitialValue())});
        }
        csvw.close();
        csv = sw.toString();
        file = getWorkingFile("accounts.csv", true);
        Files.saveString(csv, file, CSV_ENCODEING);
        if(exportBackup){
            file = getWorkingFile("accounts."+format.format(new Date())+".csv", true);
            Files.saveString(csv, file, CSV_ENCODEING);
        }
        
        final String msg = i18n.string(R.string.msg_csv_exported,Integer.toString(count),workingFolder);
        GUIs.post(new Runnable(){
            @Override
            public void run() {
                GUIs.alert(DataMaintenanceActivity.this,msg);                
            }});
        

    }
    
    /** running in thread **/
    private void _importFromCSV() throws Exception{
        IDataProvider idp = Contexts.instance().getDataProvider();
        Runnable nocsv = new Runnable(){
            @Override
            public void run() {
                GUIs.alert(DataMaintenanceActivity.this,R.string.msg_no_csv);                
            }};
        File details = getWorkingFile("details.csv", false);
        File accounts = getWorkingFile("accounts.csv", false);
        if(!details.exists() || !details.canRead() || !accounts.exists() || !accounts.canRead()){
            GUIs.post(nocsv);
            return;
        }
        
        StringReader sw = new StringReader(Files.loadString(details,CSV_ENCODEING));
        int count = 0;
        CsvReader reader = new CsvReader(sw);
        if(!reader.readHeaders()){
            GUIs.post(nocsv);
            return;
        }
        //reset all
        idp.reset();
        while(reader.readRecord()){
            Detail det = new Detail(reader.get("from"),reader.get("to"),Formats.normalizeString2Date(reader.get("date")),Formats.normalizeString2Double(reader.get("value")),reader.get("note"));
            det.setArchived(Boolean.parseBoolean(reader.get("archived")));
            idp.newDetailNoCheck(Integer.parseInt(reader.get("id")),det);
            count ++;
        }
        reader.close();
        sw.close();
        
        sw = new StringReader(Files.loadString(accounts,CSV_ENCODEING));
        reader = new CsvReader(sw);
        reader.readHeaders();
        while(reader.readRecord()){
            Account acc = new Account(reader.get("type"),reader.get("name"),Formats.normalizeString2Double(reader.get("init")));
            idp.newAccountNoCheck(reader.get("id"),acc);
            count ++;
        }
        reader.close();
        sw.close();
        
        final String msg = i18n.string(R.string.msg_csv_imported,Integer.toString(count),workingFolder);
        GUIs.post(new Runnable(){
            @Override
            public void run() {
                GUIs.alert(DataMaintenanceActivity.this,msg);                
            }});
        

    }
}

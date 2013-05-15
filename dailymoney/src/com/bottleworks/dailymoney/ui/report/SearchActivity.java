package com.bottleworks.dailymoney.ui.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.DetailListHelper;

/**
 * Edit or create a detail
 * 
 * @author Lancelot
 * 
 */
public class SearchActivity extends ContextsActivity implements android.view.View.OnClickListener {

    private DateFormat format;
    private Date today;
    private DetailListHelper detailListHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        format = getContexts().getDateFormat();
        initIntent();
        initialCriteria();
        initialContent();
    }

    private void initIntent() {
        setTitle(R.string.dtitem_report_search);
    }

    private void initialContent() {
        detailListHelper = new DetailListHelper(this, i18n, calHelper, false, new DetailListHelper.OnDetailListener() {
            @Override
            public void onDetailDeleted(Detail detail) {
                GUIs.shortToast(SearchActivity.this, i18n.string(R.string.msg_detail_deleted));
                doOk();
            }
        });
        ListView listView = (ListView) findViewById(R.id.searchResult_list);
        detailListHelper.setup(listView);
        registerForContextMenu(listView);
    }

    EditText dateFromEditor;
    EditText dateToEditor;
    EditText noteEditor;

    Button okBtn;
    Button resetBtn;
    Button closeBtn;

    private void initialCriteria() {

        today = new Date();
        dateFromEditor = (EditText) findViewById(R.id.search_from_date);
        dateFromEditor.setText(format.format(today));

        dateToEditor = (EditText) findViewById(R.id.search_to_date);
        dateToEditor.setText(format.format(today));

        noteEditor = (EditText) findViewById(R.id.deteditor_note);
        noteEditor.setText("");

        findViewById(R.id.search_from_prev).setOnClickListener(this);
        findViewById(R.id.search_from_next).setOnClickListener(this);
        findViewById(R.id.search_from_today).setOnClickListener(this);
        findViewById(R.id.search_from_datepicker).setOnClickListener(this);
        findViewById(R.id.search_to_prev).setOnClickListener(this);
        findViewById(R.id.search_to_next).setOnClickListener(this);
        findViewById(R.id.search_to_today).setOnClickListener(this);
        findViewById(R.id.search_to_datepicker).setOnClickListener(this);

        okBtn = (Button) findViewById(R.id.search_ok);
        okBtn.setOnClickListener(this);

        resetBtn = (Button) findViewById(R.id.search_reset);
        closeBtn = (Button) findViewById(R.id.search_close);

        resetBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CalendarHelper cal = getContexts().getCalendarHelper();
        if (v.getId() == R.id.search_ok) {
            doOk();
        } else if (v.getId() == R.id.search_reset) {
            doReset();
        } else if (v.getId() == R.id.search_close) {
            doClose();
        } else if (v.getId() == R.id.search_from_prev) {
            try {
                Date d = format.parse(dateFromEditor.getText().toString());
                dateFromEditor.setText(format.format(cal.yesterday(d)));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
        } else if (v.getId() == R.id.search_from_next) {
            try {
                Date d = format.parse(dateFromEditor.getText().toString());
                dateFromEditor.setText(format.format(cal.tomorrow(d)));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
        } else if (v.getId() == R.id.search_from_today) {
            dateFromEditor.setText(format.format(cal.today()));
        } else if (v.getId() == R.id.search_from_datepicker) {
            try {
                Date d = format.parse(dateFromEditor.getText().toString());
                GUIs.openDatePicker(this, d, new GUIs.OnFinishListener() {
                    @Override
                    public boolean onFinish(Object data) {
                        dateFromEditor.setText(format.format((Date) data));
                        return true;
                    }
                });
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
        } else if (v.getId() == R.id.search_to_prev) {
            try {
                Date d = format.parse(dateToEditor.getText().toString());
                dateToEditor.setText(format.format(cal.yesterday(d)));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
        } else if (v.getId() == R.id.search_to_next) {
            try {
                Date d = format.parse(dateToEditor.getText().toString());
                dateToEditor.setText(format.format(cal.tomorrow(d)));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
        } else if (v.getId() == R.id.search_to_today) {
            dateToEditor.setText(format.format(cal.today()));
        } else if (v.getId() == R.id.search_to_datepicker) {
            try {
                Date d = format.parse(dateToEditor.getText().toString());
                GUIs.openDatePicker(this, d, new GUIs.OnFinishListener() {
                    @Override
                    public boolean onFinish(Object data) {
                        dateToEditor.setText(format.format((Date) data));
                        return true;
                    }
                });
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
        }
    }

    private void doOk() {
        // verify
        String dateFromStr = dateFromEditor.getText().toString().trim();
        if ("".equals(dateFromStr)) {
            dateFromEditor.requestFocus();
            GUIs.alert(this, i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_date)));
            return;
        }

        Date dateFrom = null;
        try {
            dateFrom = getContexts().getDateFormat().parse(dateFromStr);
        } catch (ParseException e) {
            Logger.e(e.getMessage(), e);
            GUIs.errorToast(this, e);
            return;
        }

        String dateToStr = dateToEditor.getText().toString().trim();
        if ("".equals(dateToStr)) {
            dateToEditor.requestFocus();
            GUIs.alert(this, i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_date)));
            return;
        }

        Date dateTo = null;
        try {
            dateTo = getContexts().getDateFormat().parse(dateToStr);
        } catch (ParseException e) {
            Logger.e(e.getMessage(), e);
            GUIs.errorToast(this, e);
            return;
        }

        String note = noteEditor.getText().toString();

        reloadData(dateFrom, dateTo, note);
    }

    private void doReset() {
        dateFromEditor.setText(format.format(today));
        dateToEditor.setText(format.format(today));
        noteEditor.setText("");
    }

    private void doClose() {
        setResult(RESULT_OK);
        finish();
    }

    private void reloadData(Date inputStart, Date inputEnd, final String inputNote) {
        final CalendarHelper cal = getContexts().getCalendarHelper();
        final Date start;
        final Date end;
        start = cal.toDayStart(inputStart);
        end = cal.toDayEnd(inputEnd);
        final IDataProvider idp = getContexts().getDataProvider();
        GUIs.doBusy(this, new GUIs.BusyAdapter() {
            List<Detail> data = null;

            @Override
            public void run() {
                data = idp.listDetail(start, end, inputNote, getContexts().getPrefMaxRecords());
            }

            @Override
            public void onBusyFinish() {
                detailListHelper.reloadData(data);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.searchResult_list) {
            getMenuInflater().inflate(R.menu.detlist_ctxmenu, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.detlist_menu_edit) {
            detailListHelper.doEditDetail(info.position);
            return true;
        } else if (item.getItemId() == R.id.detlist_menu_delete) {
            detailListHelper.doDeleteDetail(info.position);
            return true;
        } else if (item.getItemId() == R.id.detlist_menu_copy) {
            detailListHelper.doCopyDetail(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

}

package com.bottleworks.dailymoney.ui;

import android.content.Context;
import android.content.Intent;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Book;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.SymbolPosition;

import java.util.Date;

/**
 * @author dennis
 */
public class TestsDesktop extends AbstractDesktop {

    public TestsDesktop() {
        label = i18n.string(R.string.dt_tests);
        icon = R.drawable.tab_tests;
    }

    @Override
    public boolean isAvailable() {
        return Contexts.instance().isPrefOpenTestsDesktop();
    }

    @Override
    protected void init(final Context context) {
        DesktopItem dt = new DesktopItem(new Runnable() {
            public void run() {
                Contexts ctx = Contexts.instance();
                ctx.getMasterDataProvider().reset();
            }
        }, "Reset Master DataProvider", R.drawable.dtitem_test);

        addItem(dt);

        dt = new DesktopItem(new Runnable() {
            public void run() {
                Intent intent = new Intent(context, BookMgntActivity.class);
                TestsDesktop.this.getActivity().startActivityForResult(intent, 0);
            }
        }, "Book Management", R.drawable.dtitem_test);

        addItem(dt);


        dt = new DesktopItem(new Runnable() {
            public void run() {
                Contexts ctx = Contexts.instance();
                Book book = ctx.getMasterDataProvider().findBook(ctx.getWorkingBookId());

                Intent intent = new Intent(context, BookEditorActivity.class);
                intent.putExtra(BookEditorActivity.INTENT_MODE_CREATE, false);
                intent.putExtra(BookEditorActivity.INTENT_BOOK, book);
                TestsDesktop.this.getActivity().startActivityForResult(intent, Constants.REQUEST_BOOK_EDITOR_CODE);
            }
        }, "Edit selected book", R.drawable.dtitem_test);

        addItem(dt);

        dt = new DesktopItem(new Runnable() {
            public void run() {
                Book book = new Book("test", "$", SymbolPosition.AFTER, "");
                Intent intent = new Intent(context, BookEditorActivity.class);
                intent.putExtra(BookEditorActivity.INTENT_MODE_CREATE, true);
                intent.putExtra(BookEditorActivity.INTENT_BOOK, book);
                TestsDesktop.this.getActivity().startActivityForResult(intent, Constants.REQUEST_BOOK_EDITOR_CODE);
            }
        }, "Add book", R.drawable.dtitem_test);

        addItem(dt);


        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                Contexts.instance().getDataProvider().reset();
                GUIs.shortToast(context, "reset data provider");
            }
        }, "rest data provider", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testFirstDayOfWeek();
            }
        }, "first day of week", R.drawable.dtitem_test) {
        });
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testBusy(200, null);
            }
        }, "Busy 200ms", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testBusy(200, "error short");
            }
        }, "Busy 200ms Error", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testBusy(5000, null);
            }
        }, "Busy 5s", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testBusy(5000, "error long");
            }
        }, "Busy 5s Error", R.drawable.dtitem_test));


        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testCreateTestData(25);
            }
        }, "test data25", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testCreateTestData(50);
            }
        }, "test data50", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testCreateTestData(100);
            }
        }, "test data100", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testCreateTestData(200);
            }
        }, "test data200", R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable() {
            @Override
            public void run() {
                testJust();
            }
        }, "just test", R.drawable.dtitem_test));

        DesktopItem padding = new DesktopItem(new Runnable() {
            @Override
            public void run() {

            }
        }, "padding", R.drawable.dtitem_test);

        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
    }

    protected void testBusy(final long i, final String error) {
        GUIs.doBusy(this.getContext(), new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(TestsDesktop.this.getContext(), "task finished");
            }

            public void onBusyError(Throwable x) {
                GUIs.shortToast(TestsDesktop.this.getContext(), "Error " + x.getMessage());
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (error != null) {
                    throw new RuntimeException(error);
                }
            }
        });
    }

    protected void testFirstDayOfWeek() {
        CalendarHelper calHelper = Contexts.instance().getCalendarHelper();
        for (int i = 0; i < 100; i++) {
            Date now = new Date();
            Date start = calHelper.weekStartDate(now);
//            Date end = calHelper.weekEndDate(now);
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
//            System.out.println("1>>>>>>>>>>> "+now);
            System.out.println("2>>>>>>>>>>> " + start);
//            System.out.println("3>>>>>>>>>>> "+end);
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void testCreateTestData(final int loop) {
        GUIs.doBusy(this.getContext(), new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(TestsDesktop.this.getContext(), "create test data");
            }

            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DataCreator(idp, i18n).createTestData(loop);
            }
        });

    }


    protected void testJust() {
        CalendarHelper calHelper = Contexts.instance().getCalendarHelper();
        Date now = new Date();
        Date start = calHelper.weekStartDate(now);
        Date end = calHelper.weekEndDate(now);
        System.out.println(">>>>>>>>>>>>>>> " + now);
        System.out.println("1>>>>>>>>>>> " + now);
        System.out.println("2>>>>>>>>>>> " + start);
        System.out.println("3>>>>>>>>>>> " + end);
        System.out.println(">>>>>>>>>>>>>> " + now);

    }

}

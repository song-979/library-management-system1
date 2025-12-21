package org.example.ui.reader;

import org.example.model.BorrowRecord;
import org.example.model.Reader;
import org.example.service.ReaderService;
import org.example.service.BorrowService;
import org.example.service.impl.ReaderServiceImpl;
import org.example.service.impl.BorrowServiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.List;

public class ReaderBorrowedWindow {
    private final Display display;
    private final Shell shell;
    private final ReaderService readerService = new ReaderServiceImpl();
    private final BorrowService borrowService = new BorrowServiceImpl();
    private Combo cmbReader;
    private Table table;
    private List<Reader> readers;
    private Integer initialReaderId = null;
    private Runnable onRefreshMain = null;

    public ReaderBorrowedWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display);
        shell.setText("已借阅");
        shell.setSize(800, 600);
        shell.setLayout(new GridLayout(1, false));
    }

    public ReaderBorrowedWindow(Display display, int initialReaderId) {
        this(display);
        this.initialReaderId = initialReaderId;
    }

    public ReaderBorrowedWindow(Display display, int initialReaderId, Runnable onRefreshMain) {
        this(display, initialReaderId);
        this.onRefreshMain = onRefreshMain;
    }

    public void open() {
        createControls();
        refresh();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createControls() {
        Composite top = new Composite(shell, SWT.NONE);
        top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        top.setLayout(new GridLayout(3, false));

        new Label(top, SWT.NONE).setText("读者");
        cmbReader = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
        cmbReader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        readers = readerService.getAllReaders();
        for (Reader r : readers) {
            cmbReader.add(r.getName() + " (" + r.getCode() + ")");
        }
        if (cmbReader.getItemCount() > 0) {
            if (initialReaderId != null) {
                int idx = 0;
                for (int i = 0; i < readers.size(); i++) {
                    if (readers.get(i).getId() == initialReaderId) { idx = i; break; }
                }
                cmbReader.select(idx);
            } else {
                cmbReader.select(0);
            }
        }

        Button btnRefresh = new Button(top, SWT.PUSH);
        btnRefresh.setText("刷新");
        btnRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) { refresh(); }
        });

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        String[] cols = {"记录ID", "书名", "数量", "借阅时间"};
        for (String c : cols) {
            new TableColumn(table, SWT.NONE).setText(c);
        }
        table.addListener(SWT.Resize, e -> {
            TableColumn[] cs = table.getColumns();
            int w = table.getClientArea().width;
            for (TableColumn c : cs) c.setWidth(w / cs.length);
        });

        Composite bottom = new Composite(shell, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(1, false));
        Button btnReturn = new Button(bottom, SWT.PUSH);
        btnReturn.setText("归还所选");
        btnReturn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] sel = table.getSelection();
                if (sel.length == 1) {
                    BorrowRecord br = (BorrowRecord) sel[0].getData();
                    boolean ok = borrowService.returnByRecordId(br.getId());
                    showMsg(ok ? "归还成功" : "归还失败");
                    if (ok && onRefreshMain != null) onRefreshMain.run();
                    refresh();
                } else {
                    showMsg("请选择一条记录");
                }
            }
        });
    }

    private void refresh() {
        table.removeAll();
        if (cmbReader.getSelectionIndex() < 0) return;
        int readerId = readers.get(cmbReader.getSelectionIndex()).getId();
        List<BorrowRecord> list = borrowService.getActiveBorrows(readerId);
        for (BorrowRecord br : list) {
            TableItem it = new TableItem(table, SWT.NONE);
            it.setText(new String[]{
                    String.valueOf(br.getId()),
                    br.getBookTitle(),
                    String.valueOf(br.getQuantity()),
                    br.getBorrowDate()
            });
            it.setData(br);
        }
        table.notifyListeners(SWT.Resize, new Event());
    }

    private void showMsg(String msg) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION);
        mb.setMessage(msg);
        mb.open();
    }
}

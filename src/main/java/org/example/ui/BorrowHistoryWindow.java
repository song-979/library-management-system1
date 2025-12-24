package org.example.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.example.model.BorrowRecord;
import org.example.service.BorrowService;
import org.example.service.impl.BorrowServiceImpl;

import java.util.List;

public class BorrowHistoryWindow {
    private final Display display;
    private final Shell shell;
    private final BorrowService borrowService = new BorrowServiceImpl();
    private Table table;

    public BorrowHistoryWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("借阅历史管理");
        shell.setSize(900, 600);
        shell.setLayout(new GridLayout(1, false));
    }

    public void open() {
        createControls();
        refresh();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    private void createControls() {
        Composite top = new Composite(shell, SWT.NONE);
        top.setLayout(new GridLayout(5, false));
        top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button btnAdd = new Button(top, SWT.PUSH);
        btnAdd.setText("新增");
        btnAdd.addSelectionListener(new SelectionAdapter() { @Override public void widgetSelected(SelectionEvent e) { openEditDialog(null); } });

        Button btnEdit = new Button(top, SWT.PUSH);
        btnEdit.setText("编辑");
        btnEdit.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) {
                TableItem[] sel = table.getSelection();
                if (sel.length == 1) openEditDialog((BorrowRecord) sel[0].getData()); else showMsg("请选择一行");
            }
        });

        Button btnDel = new Button(top, SWT.PUSH);
        btnDel.setText("删除");
        btnDel.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) {
                TableItem[] sel = table.getSelection();
                if (sel.length == 1) {
                    BorrowRecord r = (BorrowRecord) sel[0].getData();
                    if (borrowService.deleteHistory(r.getId())) { showMsg("删除成功"); refresh(); } else { showMsg("删除失败"); }
                } else showMsg("请选择一行");
            }
        });

        Button btnRefresh = new Button(top, SWT.PUSH);
        btnRefresh.setText("刷新");
        btnRefresh.addSelectionListener(new SelectionAdapter() { @Override public void widgetSelected(SelectionEvent e) { refresh(); } });

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setHeaderVisible(true); table.setLinesVisible(true);
        String[] cols = {"ID", "读者ID", "书名", "数量", "状态", "借阅时间", "归还时间"};
        for (String c : cols) new TableColumn(table, SWT.NONE).setText(c);
        table.addListener(SWT.Resize, e -> {
            TableColumn[] cs = table.getColumns(); int w = table.getClientArea().width; for (TableColumn c : cs) c.setWidth(w / cs.length);
        });
    }

    private void refresh() {
        table.removeAll();
        List<BorrowRecord> list = borrowService.getAllHistory();
        for (BorrowRecord r : list) {
            TableItem it = new TableItem(table, SWT.NONE);
            it.setText(new String[]{ String.valueOf(r.getId()), String.valueOf(r.getReaderId()), r.getBookTitle(), String.valueOf(r.getQuantity()), r.getStatus(), r.getBorrowDate(), r.getReturnDate() });
            it.setData(r);
        }
        table.notifyListeners(SWT.Resize, new Event());
    }

    private void openEditDialog(BorrowRecord record) {
        Shell d = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        d.setText(record == null ? "新增历史" : "编辑历史");
        d.setLayout(new GridLayout(2, false));
        d.setSize(420, 320);

        new Label(d, SWT.NONE).setText("读者ID");
        Text tReader = new Text(d, SWT.BORDER); tReader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(d, SWT.NONE).setText("图书ID");
        Text tBook = new Text(d, SWT.BORDER); tBook.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(d, SWT.NONE).setText("数量");
        Text tQty = new Text(d, SWT.BORDER); tQty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(d, SWT.NONE).setText("状态");
        Combo cmbStatus = new Combo(d, SWT.DROP_DOWN | SWT.READ_ONLY); cmbStatus.setItems(new String[]{"borrowed","returned"});
        cmbStatus.select(0);
        new Label(d, SWT.NONE).setText("借阅时间(yyyy-MM-dd HH:mm:ss)");
        Text tBorrowAt = new Text(d, SWT.BORDER); tBorrowAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(d, SWT.NONE).setText("归还时间(yyyy-MM-dd HH:mm:ss)");
        Text tReturnAt = new Text(d, SWT.BORDER); tReturnAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        if (record != null) {
            tReader.setText(String.valueOf(record.getReaderId()));
            tBook.setText(String.valueOf(record.getBookId()));
            tQty.setText(String.valueOf(record.getQuantity()));
            int idx = "returned".equals(record.getStatus()) ? 1 : 0; cmbStatus.select(idx);
            tBorrowAt.setText(record.getBorrowDate() == null ? "" : record.getBorrowDate());
            tReturnAt.setText(record.getReturnDate() == null ? "" : record.getReturnDate());
        }

        Button btnSave = new Button(d, SWT.PUSH); btnSave.setText("保存");
        btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
        btnSave.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) {
                try {
                    int readerId = Integer.parseInt(tReader.getText().trim());
                    int bookId = Integer.parseInt(tBook.getText().trim());
                    int qty = Integer.parseInt(tQty.getText().trim());
                    String status = cmbStatus.getText();
                    String borrowAt = tBorrowAt.getText().trim();
                    String returnAt = tReturnAt.getText().trim();
                    boolean ok;
                    if (record == null) {
                        ok = borrowService.createHistory(readerId, bookId, qty, status, borrowAt.isEmpty()?null:borrowAt, returnAt.isEmpty()?null:returnAt);
                    } else {
                        ok = borrowService.updateHistory(record.getId(), readerId, bookId, qty, status, borrowAt.isEmpty()?null:borrowAt, returnAt.isEmpty()?null:returnAt);
                    }
                    if (ok) { showMsg("保存成功"); d.dispose(); refresh(); } else { showMsg("保存失败"); }
                } catch (NumberFormatException ex) {
                    showMsg("请输入有效数字");
                }
            }
        });

        d.open();
        while (!d.isDisposed()) { if (!display.readAndDispatch()) display.sleep(); }
    }

    private void showMsg(String m) { MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION); mb.setMessage(m); mb.open(); }
}

package org.example.ui;

import org.example.model.Reader;
import org.example.service.ReaderService;
import org.example.service.impl.ReaderServiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import java.util.List;

public class ReaderWindow {
    private final Display display;
    private final Shell shell;
    private final ReaderService readerService = new ReaderServiceImpl();
    private Table table;

    public ReaderWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display);
        shell.setText("读者管理");
        shell.setSize(800, 600);
        shell.setLayout(new GridLayout(1, false));
    }

    public void open() {
        createControls();
        refreshReaderList();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createControls() {
        Composite topComp = new Composite(shell, SWT.NONE);
        topComp.setLayout(new GridLayout(3, false));
        topComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button btnAdd = new Button(topComp, SWT.PUSH);
        btnAdd.setText("新增读者");
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openReaderDialog(null);
            }
        });

        Button btnEdit = new Button(topComp, SWT.PUSH);
        btnEdit.setText("编辑选中");
        btnEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selected = table.getSelection();
                if (selected.length == 1) {
                    openReaderDialog((Reader) selected[0].getData());
                } else {
                    showMsg("请选择一行编辑");
                }
            }
        });

        Button btnDel = new Button(topComp, SWT.PUSH);
        btnDel.setText("删除选中");
        btnDel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selected = table.getSelection();
                if (selected.length == 1) {
                    Reader reader = (Reader) selected[0].getData();
                    if (readerService.deleteReader(reader.getId())) {
                        showMsg("删除成功");
                        refreshReaderList();
                    } else {
                        showMsg("删除失败");
                    }
                } else {
                    showMsg("请选择一行删除");
                }
            }
        });

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        String[] cols = {"ID", "姓名", "学号/工号", "电话", "最大可借", "状态", "创建时间", "更新时间"};
        for (String col : cols) {
            new TableColumn(table, SWT.NONE).setText(col);
        }

        table.addListener(SWT.Resize, e -> {
            TableColumn[] columns = table.getColumns();
            int width = table.getClientArea().width;
            for (TableColumn col : columns) {
                col.setWidth(width / columns.length);
            }
        });
    }

    private void refreshReaderList() {
        table.removeAll();
        List<Reader> readers = readerService.getAllReaders();
        for (Reader r : readers) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[]{
                    String.valueOf(r.getId()),
                    r.getName(),
                    r.getCode(),
                    r.getPhone(),
                    String.valueOf(r.getMaxBorrow()),
                    r.getStatus(),
                    r.getCreatedTime(),
                    r.getUpdatedTime()
            });
            item.setData(r);
        }
        table.notifyListeners(SWT.Resize, new Event());
    }

    private void openReaderDialog(Reader readerToEdit) {
        Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText(readerToEdit == null ? "新增读者" : "编辑读者");
        dialog.setLayout(new GridLayout(2, false));
        dialog.setSize(420, 320);

        new Label(dialog, SWT.NONE).setText("姓名：");
        Text txtName = new Text(dialog, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("学号/工号：");
        Text txtCode = new Text(dialog, SWT.BORDER);
        txtCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("电话：");
        Text txtPhone = new Text(dialog, SWT.BORDER);
        txtPhone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("最大可借：");
        Text txtMaxBorrow = new Text(dialog, SWT.BORDER);
        txtMaxBorrow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("状态：");
        Combo cmbStatus = new Combo(dialog, SWT.DROP_DOWN | SWT.READ_ONLY);
        cmbStatus.setItems(new String[]{"active", "disabled"});
        cmbStatus.select(0);
        cmbStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        if (readerToEdit != null) {
            txtName.setText(readerToEdit.getName());
            txtCode.setText(readerToEdit.getCode());
            txtPhone.setText(readerToEdit.getPhone());
            txtMaxBorrow.setText(String.valueOf(readerToEdit.getMaxBorrow()));
            int idx = readerToEdit.getStatus() != null && readerToEdit.getStatus().equals("disabled") ? 1 : 0;
            cmbStatus.select(idx);
        }

        Button btnSave = new Button(dialog, SWT.PUSH);
        btnSave.setText("保存");
        btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
        btnSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String name = txtName.getText().trim();
                    String code = txtCode.getText().trim();
                    String phone = txtPhone.getText().trim();
                    int maxBorrow = Integer.parseInt(txtMaxBorrow.getText().trim());
                    String status = cmbStatus.getText();

                    Reader r = new Reader(name, code, phone, maxBorrow, status);
                    boolean success;
                    if (readerToEdit == null) {
                        success = readerService.addReader(r);
                    } else {
                        r.setId(readerToEdit.getId());
                        success = readerService.updateReader(r);
                    }

                    if (success) {
                        showMsg("操作成功");
                        dialog.dispose();
                        refreshReaderList();
                    } else {
                        showMsg("操作失败");
                    }
                } catch (NumberFormatException ex) {
                    showMsg("最大可借请输入数字");
                }
            }
        });

        dialog.open();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void showMsg(String msg) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION);
        mb.setMessage(msg);
        mb.open();
    }
}

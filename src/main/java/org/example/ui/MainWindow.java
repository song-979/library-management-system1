package org.example.ui;

import org.example.model.Book;
import org.example.service.BookService;
import org.example.service.impl.BookServiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.example.ui.reader.ReaderBorrowWindow;
import org.example.ui.reader.ReaderBorrowedWindow;
import java.util.List;

public class MainWindow {
    private final Display display;
    private final Shell shell;
    private final BookService bookService = new BookServiceImpl();
    private Table table;
    private Text txtSearch;
    private Combo cmbCategory;
    private Text txtMinAvail;

    public MainWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display);
        shell.setText("图书管理系统");
        shell.setSize(800, 600);
        shell.setLayout(new GridLayout(1, false));
    }

    public void open() {
        createControls();
        refreshBookList();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createControls() {
        // 顶部按钮区
        Composite topComp = new Composite(shell, SWT.NONE);
        topComp.setLayout(new GridLayout(12, false));
        topComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button btnAdd = new Button(topComp, SWT.PUSH);
        btnAdd.setText("新增图书");
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openBookDialog(null);
            }
        });

        Button btnEdit = new Button(topComp, SWT.PUSH);
        btnEdit.setText("编辑选中");
        btnEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selected = table.getSelection();
                if (selected.length == 1) {
                    openBookDialog((Book) selected[0].getData());
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
                    Book book = (Book) selected[0].getData();
                    if (bookService.deleteBook(book.getId())) {
                        showMsg("删除成功");
                        refreshBookList();
                    } else {
                        showMsg("删除失败");
                    }
                } else {
                    showMsg("请选择一行删除");
                }
            }
        });

        new Label(topComp, SWT.NONE).setText("书名");
        txtSearch = new Text(topComp, SWT.BORDER); txtSearch.setLayoutData(new GridData(120, SWT.DEFAULT));
        new Label(topComp, SWT.NONE).setText("分类");
        cmbCategory = new Combo(topComp, SWT.DROP_DOWN | SWT.READ_ONLY); cmbCategory.setLayoutData(new GridData(120, SWT.DEFAULT));
        loadCategories();
        cmbCategory.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                refreshBookList();
            }
        });

        new Label(topComp, SWT.NONE).setText("可借≥");
        txtMinAvail = new Text(topComp, SWT.BORDER); txtMinAvail.setLayoutData(new GridData(60, SWT.DEFAULT));

        Button btnSearch = new Button(topComp, SWT.PUSH);
        btnSearch.setText("查询");
        btnSearch.addSelectionListener(new SelectionAdapter() { @Override public void widgetSelected(SelectionEvent e) { refreshBookList(); } });

        Button btnExport = new Button(topComp, SWT.PUSH);
        btnExport.setText("导出CSV");
        btnExport.addSelectionListener(new SelectionAdapter() { @Override public void widgetSelected(SelectionEvent e) { exportBooksCsv(); } });

        Button btnRefresh = new Button(topComp, SWT.PUSH);
        btnRefresh.setText("刷新");
        btnRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                refreshBookList();
            }
        });

        // 导航按钮已移动到主目录界面 (MenuWindow)
        
        // 图书列表
        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        String[] cols = {"ID", "书名", "分类", "总册数", "可借数", "备注", "创建时间", "更新时间"};
        for (String col : cols) {
            new TableColumn(table, SWT.NONE).setText(col);
        }

        // 自适应列宽
        table.addListener(SWT.Resize, e -> {
            TableColumn[] columns = table.getColumns();
            int width = table.getClientArea().width;
            for (TableColumn col : columns) {
                col.setWidth(width / columns.length);
            }
        });
    }

    private void refreshBookList() {
        // 保存当前选中的分类
        String currentCat = cmbCategory.getText();
        loadCategories();
        // 尝试恢复选中
        if (currentCat != null && !currentCat.isEmpty()) {
            for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                if (cmbCategory.getItem(i).equals(currentCat)) {
                    cmbCategory.select(i);
                    break;
                }
            }
        }
        if (cmbCategory.getSelectionIndex() < 0) cmbCategory.select(0); // 默认选中"全部"

        table.removeAll();
        String kw = txtSearch == null ? null : txtSearch.getText().trim();
        String cat = cmbCategory == null ? null : (cmbCategory.getSelectionIndex() > 0 ? cmbCategory.getText() : null); // index 0 is "全部", so ignore it
        Integer minA = null; try { if (txtMinAvail != null && !txtMinAvail.getText().trim().isEmpty()) minA = Integer.parseInt(txtMinAvail.getText().trim()); } catch (Exception ignore) {}
        
        List<Book> books = bookService.searchBooks(kw, cat, minA);
        for (Book book : books) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[]{
                    String.valueOf(book.getId()),
                    book.getTitle(),
                    book.getCategory(),
                    String.valueOf(book.getTotalCopies()),
                    String.valueOf(book.getAvailableCopies()),
                    book.getRemarks(),
                    book.getCreatedTime(),
                    book.getUpdatedTime()
            });
            item.setData(book);
            if (book.getAvailableCopies() == 0) item.setBackground(display.getSystemColor(SWT.COLOR_RED));
        }
        table.notifyListeners(SWT.Resize, new Event());
    }

    private void loadCategories() {
        cmbCategory.removeAll();
        cmbCategory.add("全部");
        try {
            List<String> cats = bookService.getCategories();
            for (String c : cats) {
                if (c != null && !c.trim().isEmpty()) {
                    cmbCategory.add(c);
                }
            }
        } catch (Exception ignore) {}
        if (cmbCategory.getItemCount() > 0) cmbCategory.select(0);
    }

    private void exportBooksCsv() {
        FileDialog fd = new FileDialog(shell, SWT.SAVE);
        fd.setFilterExtensions(new String[]{"*.csv"});
        fd.setFileName("books.csv");
        String path = fd.open();
        if (path == null) return;
        StringBuilder sb = new StringBuilder();
        TableColumn[] cols = table.getColumns();
        for (int i = 0; i < cols.length; i++) { sb.append(csv(cols[i].getText())); if (i < cols.length - 1) sb.append(","); }
        sb.append("\r\n");
        for (TableItem it : table.getItems()) {
            for (int i = 0; i < cols.length; i++) { sb.append(csv(it.getText(i))); if (i < cols.length - 1) sb.append(","); }
            sb.append("\r\n");
        }
        try {
            java.io.OutputStream os = java.nio.file.Files.newOutputStream(java.nio.file.Paths.get(path));
            os.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
            java.io.OutputStreamWriter w = new java.io.OutputStreamWriter(os, java.nio.charset.StandardCharsets.UTF_8);
            w.write(sb.toString());
            w.flush();
            w.close();
            showMsg("已导出: " + path);
        } catch (Exception ex) { showMsg("导出失败"); }
    }

    private String csv(String s) { if (s == null) return "\"\""; String t = s.replace("\"","\"\""); return "\"" + t + "\""; }

    private void openBookDialog(Book bookToEdit) {
        Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText(bookToEdit == null ? "新增图书" : "编辑图书");
        dialog.setLayout(new GridLayout(2, false));
        dialog.setSize(400, 300);

        // 控件
        new Label(dialog, SWT.NONE).setText("书名：");
        Text txtTitle = new Text(dialog, SWT.BORDER);
        txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("分类：");
        Text txtCategory = new Text(dialog, SWT.BORDER);
        txtCategory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("总册数：");
        Text txtTotal = new Text(dialog, SWT.BORDER);
        txtTotal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("可借数：");
        Text txtAvailable = new Text(dialog, SWT.BORDER);
        txtAvailable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("备注：");
        Text txtRemarks = new Text(dialog, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        txtRemarks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 3));

        // 填充编辑数据
        if (bookToEdit != null) {
            txtTitle.setText(bookToEdit.getTitle());
            txtCategory.setText(bookToEdit.getCategory());
            txtTotal.setText(String.valueOf(bookToEdit.getTotalCopies()));
            txtAvailable.setText(String.valueOf(bookToEdit.getAvailableCopies()));
            txtRemarks.setText(bookToEdit.getRemarks());
        }

        // 保存按钮
        Button btnSave = new Button(dialog, SWT.PUSH);
        btnSave.setText("保存");
        btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));

        btnSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String title = txtTitle.getText().trim();
                    String category = txtCategory.getText().trim();
                    int total = Integer.parseInt(txtTotal.getText().trim());
                    int available = Integer.parseInt(txtAvailable.getText().trim());
                    String remarks = txtRemarks.getText().trim();

                    Book book = new Book(title, category, total, available, remarks);
                    boolean success;
                    if (bookToEdit == null) {
                        success = bookService.addBook(book);
                    } else {
                        book.setId(bookToEdit.getId());
                        success = bookService.updateBook(book);
                    }

                    if (success) {
                        showMsg("操作成功");
                        dialog.dispose();
                        refreshBookList();
                    } else {
                        showMsg("操作失败");
                    }
                } catch (NumberFormatException ex) {
                    showMsg("册数请输入数字");
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

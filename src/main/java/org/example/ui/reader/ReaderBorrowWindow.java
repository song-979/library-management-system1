package org.example.ui.reader;

import org.example.model.Book;
import org.example.model.Reader;
import org.example.service.BookService;
import org.example.service.ReaderService;
import org.example.service.BorrowService;
import org.example.service.impl.BookServiceImpl;
import org.example.service.impl.ReaderServiceImpl;
import org.example.service.impl.BorrowServiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.List;

public class ReaderBorrowWindow {
    private final Display display;
    private final Shell shell;
    private final BookService bookService = new BookServiceImpl();
    private final ReaderService readerService = new ReaderServiceImpl();
    private final BorrowService borrowService = new BorrowServiceImpl();
    private Table table;
    private Combo cmbReader;
    private Spinner spQty;
    private List<Reader> readers;
    private Runnable onRefreshMain;

    public ReaderBorrowWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display);
        shell.setText("读者借阅");
        shell.setSize(900, 600);
        shell.setLayout(new GridLayout(1, false));
    }

    public ReaderBorrowWindow(Display display, Runnable onRefreshMain) {
        this(display);
        this.onRefreshMain = onRefreshMain;
    }

    public void open() {
        createControls();
        refreshBooks();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createControls() {
        Composite top = new Composite(shell, SWT.NONE);
        top.setLayout(new GridLayout(6, false));
        top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(top, SWT.NONE).setText("读者");
        cmbReader = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
        cmbReader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        readers = readerService.getAllReaders();
        for (Reader r : readers) {
            cmbReader.add(r.getName() + " (" + r.getCode() + ")");
        }
        if (cmbReader.getItemCount() > 0) cmbReader.select(0);

        new Label(top, SWT.NONE).setText("数量");
        spQty = new Spinner(top, SWT.BORDER);
        spQty.setMinimum(1);
        spQty.setMaximum(100);
        spQty.setSelection(1);
        spQty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Button btnBorrow = new Button(top, SWT.PUSH);
        btnBorrow.setText("借阅");
        btnBorrow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] sel = table.getSelection();
                if (sel.length == 1 && cmbReader.getSelectionIndex() >= 0) {
                    Book b = (Book) sel[0].getData();
                    int qty = spQty.getSelection();
                    int readerId = readers.get(cmbReader.getSelectionIndex()).getId();
                    String error = borrowService.borrow(readerId, b.getId(), qty);
                    if (error == null) {
                        showMsg("借阅成功");
                        if (onRefreshMain != null) onRefreshMain.run();
                        shell.dispose();
                        new ReaderBorrowedWindow(display, readerId, onRefreshMain).open();
                    } else {
                        showMsg("借阅失败: " + error);
                        refreshBooks();
                    }
                } else {
                    showMsg("请选择读者与图书");
                }
            }
        });

        Button btnBorrowed = new Button(top, SWT.PUSH);
        btnBorrowed.setText("已借阅");
        btnBorrowed.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (cmbReader.getSelectionIndex() >= 0) {
                    int readerId = readers.get(cmbReader.getSelectionIndex()).getId();
                    new ReaderBorrowedWindow(display, readerId, onRefreshMain).open();
                } else {
                    new ReaderBorrowedWindow(display).open();
                }
            }
        });

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        String[] cols = {"ID", "书名", "分类", "总册数", "可借数", "备注", "创建时间", "更新时间"};
        for (String c : cols) {
            new TableColumn(table, SWT.NONE).setText(c);
        }
        table.addListener(SWT.Resize, e -> {
            TableColumn[] cs = table.getColumns();
            int w = table.getClientArea().width;
            for (TableColumn c : cs) c.setWidth(w / cs.length);
        });
    }

    private void refreshBooks() {
        table.removeAll();
        List<Book> books = bookService.getAllBooks();
        for (Book b : books) {
            TableItem it = new TableItem(table, SWT.NONE);
            it.setText(new String[]{
                    String.valueOf(b.getId()),
                    b.getTitle(),
                    b.getCategory(),
                    String.valueOf(b.getTotalCopies()),
                    String.valueOf(b.getAvailableCopies()),
                    b.getRemarks(),
                    b.getCreatedTime(),
                    b.getUpdatedTime()
            });
            it.setData(b);
        }
        table.notifyListeners(SWT.Resize, new Event());
    }

    private void showMsg(String msg) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION);
        mb.setMessage(msg);
        mb.open();
    }
}

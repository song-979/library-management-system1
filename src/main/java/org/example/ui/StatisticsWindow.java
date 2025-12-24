package org.example.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.example.model.Statistics;
import org.example.service.StatsService;
import org.example.service.impl.StatsServiceImpl;

public class StatisticsWindow {
    private final Display display;
    private final Shell shell;
    private final StatsService statsService = new StatsServiceImpl();

    public StatisticsWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("统计概览");
        shell.setSize(900, 700);
        shell.setLayout(new GridLayout(2, false));
    }

    public void open() {
        Statistics s = statsService.getStatistics();
        createRow("图书种类", String.valueOf(s.getTotalBooks()));
        createRow("总册数", String.valueOf(s.getTotalCopies()));
        createRow("可借册数", String.valueOf(s.getAvailableCopies()));
        createRow("借阅中", String.valueOf(s.getActiveBorrowCount()));
        createRow("已归还", String.valueOf(s.getReturnedCount()));
        createRow("读者人数", String.valueOf(s.getReaderCount()));

        Label t1 = new Label(shell, SWT.NONE); t1.setText("热门图书TOP10");
        Table tbTop = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        tbTop.setHeaderVisible(true); tbTop.setLinesVisible(true);
        tbTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        new TableColumn(tbTop, SWT.NONE).setText("书名");
        new TableColumn(tbTop, SWT.NONE).setText("分类");
        java.util.List<org.example.model.Book> top = new org.example.dao.StatsDAO().topBorrowedBooks(10);
        for (org.example.model.Book b : top) { TableItem it = new TableItem(tbTop, SWT.NONE); it.setText(new String[]{b.getTitle(), b.getCategory()}); }
        tbTop.addListener(SWT.Resize, e -> { TableColumn[] cs = tbTop.getColumns(); int w = tbTop.getClientArea().width; for (TableColumn c : cs) c.setWidth(w/cs.length); });

        Label t2 = new Label(shell, SWT.NONE); t2.setText("分类汇总");
        Table tbCat = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        tbCat.setHeaderVisible(true); tbCat.setLinesVisible(true);
        tbCat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        String[] cs1 = {"分类","标题数","总册数","可借数","借阅次数"}; for(String c:cs1) new TableColumn(tbCat, SWT.NONE).setText(c);
        java.util.List<org.example.model.CategoryReport> cats = new org.example.dao.StatsDAO().categoryReport();
        for (org.example.model.CategoryReport c : cats) { TableItem it = new TableItem(tbCat, SWT.NONE); it.setText(new String[]{ c.getCategory(), String.valueOf(c.getTitles()), String.valueOf(c.getTotalCopies()), String.valueOf(c.getAvailableCopies()), String.valueOf(c.getBorrowCount()) }); }
        tbCat.addListener(SWT.Resize, e -> { TableColumn[] cs = tbCat.getColumns(); int w = tbCat.getClientArea().width; for (TableColumn c : cs) c.setWidth(w/cs.length); });

        Label t3 = new Label(shell, SWT.NONE); t3.setText("逾期记录(>30天)");
        Table tbOver = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        tbOver.setHeaderVisible(true); tbOver.setLinesVisible(true);
        tbOver.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        String[] cs2 = {"ID","读者ID","书名","数量","借阅时间"}; for(String c:cs2) new TableColumn(tbOver, SWT.NONE).setText(c);
        java.util.List<org.example.model.BorrowRecord> overs = new org.example.dao.StatsDAO().overdueRecords(30);
        for (org.example.model.BorrowRecord r : overs) { TableItem it = new TableItem(tbOver, SWT.NONE); it.setText(new String[]{ String.valueOf(r.getId()), String.valueOf(r.getReaderId()), r.getBookTitle(), String.valueOf(r.getQuantity()), r.getBorrowDate() }); }
        tbOver.addListener(SWT.Resize, e -> { TableColumn[] cs = tbOver.getColumns(); int w = tbOver.getClientArea().width; for (TableColumn c : cs) c.setWidth(w/cs.length); });

        Composite actions = new Composite(shell, SWT.NONE); actions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1)); actions.setLayout(new RowLayout());
        Button btnExportCat = new Button(actions, SWT.PUSH); btnExportCat.setText("导出分类CSV");
        btnExportCat.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){ public void widgetSelected(org.eclipse.swt.events.SelectionEvent e){ exportCsv(tbCat, "category_report.csv"); }});
        Button btnExportOver = new Button(actions, SWT.PUSH); btnExportOver.setText("导出逾期CSV");
        btnExportOver.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){ public void widgetSelected(org.eclipse.swt.events.SelectionEvent e){ exportCsv(tbOver, "overdue_report.csv"); }});
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    private void createRow(String label, String value) {
        Label k = new Label(shell, SWT.NONE);
        k.setText(label);
        Label v = new Label(shell, SWT.NONE);
        v.setText(value);
        v.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    private void exportCsv(Table table, String defaultName) {
        org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(shell, SWT.SAVE); fd.setFilterExtensions(new String[]{"*.csv"}); fd.setFileName(defaultName); String path = fd.open(); if (path == null) return;
        StringBuilder sb = new StringBuilder(); TableColumn[] cols = table.getColumns(); for (int i=0;i<cols.length;i++){ sb.append(csv(cols[i].getText())); if(i<cols.length-1) sb.append(","); } sb.append("\r\n");
        for (TableItem it : table.getItems()) { for (int i=0;i<cols.length;i++){ sb.append(csv(it.getText(i))); if(i<cols.length-1) sb.append(","); } sb.append("\r\n"); }
        try { java.io.OutputStream os = java.nio.file.Files.newOutputStream(java.nio.file.Paths.get(path)); os.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF}); java.io.OutputStreamWriter w = new java.io.OutputStreamWriter(os, java.nio.charset.StandardCharsets.UTF_8); w.write(sb.toString()); w.flush(); w.close(); } catch (Exception ex) { /* ignore */ }
    }

    private String csv(String s){ if(s==null) return "\"\""; String t=s.replace("\"","\"\""); return "\""+t+"\""; }
}

package org.example.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
        shell.setSize(420, 300);
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
}

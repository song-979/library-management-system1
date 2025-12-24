package org.example.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.example.ui.reader.ReaderBorrowWindow;

public class MenuWindow {
    private final Display display;
    private final Shell shell;
    private Font btnFont;

    public MenuWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
        shell.setText("图书馆管理系统 - 目录");
        shell.setSize(800, 600);
        
        // Center the shell
        Monitor primary = display.getPrimaryMonitor();
        org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();
        org.eclipse.swt.graphics.Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);

        shell.setLayout(new GridLayout(1, false));
    }

    public void open() {
        createControls();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        if (btnFont != null && !btnFont.isDisposed()) {
            btnFont.dispose();
        }
    }

    private void createControls() {
        Composite container = new Composite(shell, SWT.NONE);
        GridData gdContainer = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        container.setLayoutData(gdContainer);
        
        GridLayout layout = new GridLayout(2, true);
        layout.horizontalSpacing = 40;
        layout.verticalSpacing = 40;
        container.setLayout(layout);

        btnFont = new Font(display, new FontData("Microsoft YaHei", 16, SWT.BOLD));

        createMenuButton(container, "图书管理", () -> new MainWindow(display).open());
        createMenuButton(container, "读者管理", () -> new ReaderWindow(display).open());
        createMenuButton(container, "读者借阅", () -> new ReaderBorrowWindow(display, null).open());
        createMenuButton(container, "借阅历史", () -> new BorrowHistoryWindow(display).open());
        createMenuButton(container, "数据统计", () -> new StatisticsWindow(display).open());
        
        // Add an exit button
        createMenuButton(container, "退出系统", () -> shell.dispose());
    }

    private void createMenuButton(Composite parent, String text, Runnable action) {
        Button btn = new Button(parent, SWT.PUSH);
        btn.setText(text);
        btn.setFont(btnFont);
        
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 200;
        gd.heightHint = 100;
        btn.setLayoutData(gd);

        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Minimize menu or keep it open? 
                // Usually keeping it open is better so user can come back.
                // We just run the action (which opens a modal-like window loop).
                shell.setVisible(false); // Hide menu while in sub-module
                try {
                    action.run();
                } finally {
                    if (!shell.isDisposed()) {
                        shell.setVisible(true); // Show menu when sub-module closes
                        shell.setActive();
                    }
                }
            }
        });
    }
}

package org.example.ui;

import org.example.service.AdminService;
import org.example.service.impl.AdminServiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class LoginWindow {
    private final Display display;
    private final Shell shell;
    private final AdminService adminService = new AdminServiceImpl();
    private Text txtUsername;
    private Text txtPassword;

    public LoginWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
        shell.setText("管理员登录");
        shell.setSize(300, 200);
        shell.setLayout(new GridLayout(2, false));
    }

    public void open() {
        createControls();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createControls() {
        new Label(shell, SWT.NONE).setText("用户名：");
        txtUsername = new Text(shell, SWT.BORDER);
        txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtUsername.setText("admin");

        new Label(shell, SWT.NONE).setText("密码：");
        txtPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtPassword.setText("admin");

        Button btnLogin = new Button(shell, SWT.PUSH);
        btnLogin.setText("登录");
        btnLogin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));

        btnLogin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String username = txtUsername.getText().trim();
                String password = txtPassword.getText().trim();
                if (adminService.login(username, password)) {
                    shell.dispose();
                    new MainWindow(display).open();
                } else {
                    MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
                    mb.setMessage("用户名或密码错误！");
                    mb.open();
                }
            }
        });
    }
}
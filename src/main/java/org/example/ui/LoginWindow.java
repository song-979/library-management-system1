package org.example.ui;

import org.example.service.AdminService;
import org.example.service.impl.AdminServiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
 
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
 

public class LoginWindow {
    private final Display display;
    private final Shell shell;
    private final AdminService adminService = new AdminServiceImpl();
    private Text txtUsername;
    private Text txtPassword;
    private Color shellBg;
    private Color cardBg;
    private Color titleColor;
    private Font titleFont;
    private Font subtitleFont;
    private Font btnFont;
    private Composite card;

    public LoginWindow(Display display) {
        this.display = display;
        this.shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
        shell.setText("管理员登录");
        shell.setSize(1024, 640);
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        shell.setLayout(gl);
        shellBg = new Color(display, 246, 247, 250);
        shell.setBackground(shellBg);
        shell.addListener(SWT.Dispose, e -> {
            if (titleFont != null) titleFont.dispose();
            if (subtitleFont != null) subtitleFont.dispose();
            if (btnFont != null) btnFont.dispose();
            if (cardBg != null) cardBg.dispose();
            if (titleColor != null) titleColor.dispose();
            if (shellBg != null) shellBg.dispose();
        });
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
        card = new Composite(shell, SWT.NONE);
        cardBg = new Color(display, 255, 255, 255);
        card.setBackground(cardBg);
        GridData cardGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        card.setLayoutData(cardGD);
        GridLayout cl = new GridLayout(1, false);
        cl.marginWidth = 48;
        cl.marginHeight = 48;
        cl.verticalSpacing = 20;
        card.setLayout(cl);

        Label formTitle = new Label(card, SWT.NONE);
        formTitle.setText("管理员登录");
        titleFont = new Font(display, new FontData("Segoe UI", 20, SWT.BOLD));
        formTitle.setFont(titleFont);
        titleColor = new Color(display, 33, 37, 41);
        formTitle.setForeground(titleColor);

        Label lblUser = new Label(card, SWT.NONE);
        lblUser.setText("用户名");
        txtUsername = new Text(card, SWT.BORDER);
        GridData userGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        userGD.heightHint = 36;
        txtUsername.setLayoutData(userGD);
        txtUsername.setMessage("请输入用户名");

        Label lblPass = new Label(card, SWT.NONE);
        lblPass.setText("密码");
        txtPassword = new Text(card, SWT.BORDER | SWT.PASSWORD);
        GridData passGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        passGD.heightHint = 36;
        txtPassword.setLayoutData(passGD);
        txtPassword.setMessage("请输入密码");

        Button btnLogin = new Button(card, SWT.PUSH);
        btnLogin.setText("登录");
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 40;
        btnLogin.setLayoutData(gd);
        btnFont = new Font(display, new FontData("Segoe UI", 12, SWT.BOLD));
        btnLogin.setFont(btnFont);
        shell.setDefaultButton(btnLogin);

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
        // 填充布局，无需额外自适应回调
    }

}

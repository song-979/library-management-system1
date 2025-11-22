package org.example;

import org.example.ui.LoginWindow;
import org.eclipse.swt.widgets.Display;

public class App {
    public static void main(String[] args) {
        Display display = new Display();
        new LoginWindow(display).open();
        display.dispose();
    }
}
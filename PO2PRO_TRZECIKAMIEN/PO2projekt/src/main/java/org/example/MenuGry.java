package org.example;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MenuGry extends JFrame {
    public MenuGry() {
        this.setTitle("Menu Gry");
        this.setLayout(new BorderLayout());
        JButton przyciskStart = new JButton("Rozpocznij grę");
        przyciskStart.setPreferredSize(new Dimension(200, 200));
        przyciskStart.addActionListener((var1) -> {
            this.rozpocznijGre();
        });
        JButton przyciskZakoncz = new JButton("Zakończ");
        przyciskZakoncz.addActionListener((var0) -> {
            System.exit(0);
        });
        this.add(przyciskStart, "North");
        this.add(przyciskZakoncz, "South");
        JPanel panelPrzyciskow = new JPanel(new GridLayout(2, 1, 10, 10));
        panelPrzyciskow.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panelPrzyciskow.add(przyciskStart);
        panelPrzyciskow.add(przyciskZakoncz);
        this.add(panelPrzyciskow, "Center");
        this.setDefaultCloseOperation(3);
        this.setSize(400, 400);
        this.setLocationRelativeTo((Component)null);
        this.setVisible(true);
    }

    private void rozpocznijGre() {
        (new Thread(() -> {
            try {
                Serwer.main(new String[0]);
            } catch (Exception var1) {
                Exception e = var1;
                e.printStackTrace();
            }

        })).start();
        this.uruchomKlientow();
    }

    private void restartujGre() {
        this.rozpocznijGre();
    }

    private void uruchomKlientow() {
        (new Thread(() -> {
            try {
                Klient.main(new String[0]);
            } catch (Exception var1) {
                Exception e = var1;
                e.printStackTrace();
            }

        })).start();
        (new Thread(() -> {
            try {
                Thread.sleep(500L);
                Klient2.main(new String[0]);
            } catch (Exception var1) {
                Exception e = var1;
                e.printStackTrace();
            }

        })).start();
    }

    public static void main(String[] args) {
        new MenuGry();
    }
}

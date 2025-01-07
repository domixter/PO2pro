
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuGry extends JFrame {
    private static final int PORT_SERWERA = 12345;

    public MenuGry() {
        setTitle("Menu Gry");
        setLayout(new BorderLayout());


        JButton przyciskStart = new JButton("Rozpocznij grę");
        przyciskStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rozpocznijGre();
            }
        });


        JButton przyciskZakoncz = new JButton("Zakończ");
        przyciskZakoncz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.add(przyciskStart);
        panelPrzyciskow.add(przyciskZakoncz);

        add(panelPrzyciskow, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 100);
        setVisible(true);
    }

    private void rozpocznijGre() {

        new Thread(() -> {
            try {
                Serwer.main(new String[]{});  // Uruchamia serwer
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        uruchomKlientow();
    }

    private void restartujGre() {

        rozpocznijGre();
    }

    private void uruchomKlientow() {
        new Thread(() -> {
            try {
                Klient.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(500);
                Klient2.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new MenuGry();
    }
}

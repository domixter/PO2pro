
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Klient2 extends JFrame {
    private static final String ADRES_SERWERA = "127.0.0.1";
    private static final int PORT_SERWERA = 12345;
    private JButton[][] przyciski = new JButton[3][3];
    private Character[][] plansza = new Character[3][3]; // Plansza gry
    private Socket socket;
    private PrintWriter wyjscie;
    private BufferedReader wejsciowe;
    private boolean mojaTura = false;
    private char mojSymbol = 'X';
    private JLabel etykietaStatusu = new JLabel("Czekaj na przeciwnika...");

    public Klient2() {
        try {
            polaczZSerwerem();
            setTitle("Kółko i Krzyżyk");
            setLayout(new BorderLayout());

            JPanel panelPlanszy = new JPanel(new GridLayout(3, 3));
            inicjalizujPrzyciski(panelPlanszy);
            add(panelPlanszy, BorderLayout.CENTER);

            etykietaStatusu.setHorizontalAlignment(SwingConstants.CENTER);
            add(etykietaStatusu, BorderLayout.SOUTH);

            new Thread(this::sluchajAktualizacjiPlanszy).start();

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(400, 400);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void polaczZSerwerem() throws IOException {
        socket = new Socket(ADRES_SERWERA, PORT_SERWERA);
        wyjscie = new PrintWriter(socket.getOutputStream(), true);
        wejsciowe = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                plansza[i][j] = null;
            }
        }


        String statusGracza = wejsciowe.readLine();
        if (statusGracza != null) {
            JOptionPane.showMessageDialog(this, statusGracza);
            mojaTura = statusGracza.contains("X");
            mojSymbol = statusGracza.contains("X") ? 'X' : 'O';
        }
    }

    private void inicjalizujPrzyciski(JPanel panelPlanszy) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton przycisk = new JButton("");
                przycisk.setFont(new Font("Arial", Font.PLAIN, 60));
                przycisk.addActionListener(new NasluchujPrzycisku(i, j));
                przyciski[i][j] = przycisk;
                panelPlanszy.add(przycisk);
            }
        }
    }

    private void sluchajAktualizacjiPlanszy() {
        try {
            while (true) {
                String komunikat = wejsciowe.readLine();
                if (komunikat.startsWith("PLANSZA:")) {
                    String danePlanszy = komunikat.substring(8);
                    String[] komorki = danePlanszy.split(",");
                    int index = 0;

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (index < komorki.length && !komorki[index].isEmpty()) {
                                plansza[i][j] = komorki[index].charAt(0);
                            } else {
                                plansza[i][j] = null;
                            }
                            index++;
                        }
                    }
                    przerysujPlansze();
                } else if (komunikat.startsWith("TURA:")) {
                    char tura = komunikat.charAt(5);
                    mojaTura = (tura == mojSymbol);
                    etykietaStatusu.setText(mojaTura ? "Twoja tura!" : "Czekaj na przeciwnika...");
                } else if (komunikat.startsWith("KONIEC_GRy:")) {
                    String wynik = komunikat.substring(12);
                    etykietaStatusu.setText("Koniec gry: " + wynik);
                    JOptionPane.showMessageDialog(this, "Koniec gry: " + wynik);

                    // Po zakończeniu gry uruchamiamy ponownie połączenie
                    int opcja = JOptionPane.showConfirmDialog(this, "Czy chcesz zagrać ponownie?", "Restart gry", JOptionPane.YES_NO_OPTION);
                    if (opcja == JOptionPane.YES_OPTION) {
                        restartujGre();
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void przerysujPlansze() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (plansza[i][j] != null) {
                    przyciski[i][j].setText(plansza[i][j].toString());
                } else {
                    przyciski[i][j].setText("");
                }
            }
        }
    }

    private class NasluchujPrzycisku implements ActionListener {
        private final int wiersz, kolumna;

        public NasluchujPrzycisku(int wiersz, int kolumna) {
            this.wiersz = wiersz;
            this.kolumna = kolumna;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mojaTura && przyciski[wiersz][kolumna].getText().equals("")) {
                przyciski[wiersz][kolumna].setText(String.valueOf(mojSymbol));
                wyjscie.println("RUCH," + wiersz + "," + kolumna);
                mojaTura = false;
                etykietaStatusu.setText("Czekaj na przeciwnika...");
            }
        }
    }

    private void restartujGre() {
        try {
            socket.close();

            new Klient2();
            dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Klient2();
    }
}

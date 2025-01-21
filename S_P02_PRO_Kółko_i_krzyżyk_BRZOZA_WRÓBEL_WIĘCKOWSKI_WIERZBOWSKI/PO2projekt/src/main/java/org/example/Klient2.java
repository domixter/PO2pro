package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Klient2 extends JFrame {
    private static final String ADRES_SERWERA = "127.0.0.1";
    private static final int PORT_SERWERA = 1235;
    private final JButton[][] przyciski = new JButton[3][3];
    private final Character[][] plansza = new Character[3][3]; // Plansza gry
    private Socket socket;
    private PrintWriter wyjscie;
    private BufferedReader wejsciowe;
    private boolean mojaTura = false;
    private char mojSymbol = 'X';
    private final JLabel etykietaStatusu = new JLabel("Czekasz na gracza 1 -> X:)");
    private final Color kolorX;  // Color for X
    private final Color kolorO;  // Color for O

    public Klient2() {
        this.kolorX = Color.RED;
        this.kolorO = Color.GREEN;

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
            this.setLocation(1140, 0);

            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void polaczZSerwerem() throws IOException {
        socket = new Socket(ADRES_SERWERA, PORT_SERWERA);
        wyjscie = new PrintWriter(socket.getOutputStream(), true);
        wejsciowe = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Initialize the game board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                plansza[i][j] = null;
            }
        }

        // Read the player status
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
                przycisk.setFont(new Font("Arial", Font.BOLD, 60));
                przycisk.addActionListener(new NasluchujPrzycisku(i, j));
                przyciski[i][j] = przycisk;

                przycisk.setBackground(Color.BLACK); // Kolor tła pustych przycisków

                panelPlanszy.add(przycisk);
            }
        }
    }

    private void sluchajAktualizacjiPlanszy() {
        while (true) {
            try {
                String komunikat = wejsciowe.readLine();
                String wynik;
                if (komunikat.startsWith("PLANSZA:")) {
                    wynik = komunikat.substring(8);
                    String[] komorki = wynik.split(",");
                    int index = 0;

                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            if (index < komorki.length && !komorki[index].isEmpty()) {
                                plansza[i][j] = komorki[index].charAt(0);
                            } else {
                                plansza[i][j] = null;
                            }
                            ++index;
                        }
                    }

                    przerysujPlansze();
                    continue;
                }

                if (komunikat.startsWith("TURA:")) {
                    char tura = komunikat.charAt(5);
                    mojaTura = tura == mojSymbol;
                    etykietaStatusu.setText(mojaTura ? "Twoja tura! Graczu 2 -> O" : "Zaczekaj na gracza 1 -> X");
                    continue;
                }

                if (komunikat.startsWith("KONIEC_GRy:")) {


                    wynik = komunikat.substring(12);
                    etykietaStatusu.setText("Koniec gry: " + wynik);
                    JOptionPane.showMessageDialog(this, "Koniec gry: " + wynik);
                    int opcja = JOptionPane.showConfirmDialog(this, "Czy chcesz zagrać ponownie?", "Restart gry", JOptionPane.YES_NO_OPTION);

                    if (opcja == JOptionPane.YES_OPTION) {
                        restartujGre();
                    } else {
                        System.exit(0);
                    }
                }
            } catch (IOException var7) {
                var7.printStackTrace();
            }
            return;
        }
    }

    private void przerysujPlansze() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (plansza[i][j] != null) {
                    przyciski[i][j].setText(plansza[i][j].toString());
                    if (plansza[i][j] == 'X') {
                        przyciski[i][j].setForeground(kolorX); // Kolor tekstu X
                        przyciski[i][j].setBackground(Color.BLACK); // Kolor tła X
                    } else if (plansza[i][j] == 'O') {
                        przyciski[i][j].setForeground(kolorO); // Kolor tekstu O
                        przyciski[i][j].setBackground(Color.BLACK); // Kolor tła O
                    }
                } else {
                    przyciski[i][j].setText("");
                    przyciski[i][j].setBackground(Color.BLACK); // Kolor tła pustych przycisków
                }
            }
        }
    }

    private void restartujGre() {
        try {
            socket.close();
            new Klient2();
            this.dispose();
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Klient2(); // Corrected to Klient2
    }

    private class NasluchujPrzycisku implements ActionListener {
        private final int wiersz;
        private final int kolumna;

        public NasluchujPrzycisku(int wiersz, int kolumna) {
            this.wiersz = wiersz;
            this.kolumna = kolumna;
        }

        public void actionPerformed(ActionEvent e) {
            if (mojaTura && przyciski[this.wiersz][this.kolumna].getText().isEmpty()) {                // Ustawienie koloru tła w zależności od symbolu
                przyciski[this.wiersz][this.kolumna].setText(String.valueOf(mojSymbol));
                if (mojSymbol == 'X') {
                    przyciski[this.wiersz][this.kolumna].setForeground(kolorX);
                    przyciski[this.wiersz][this.kolumna].setBackground(Color.BLACK); // Tło dla X
                } else {
                    przyciski[this.wiersz][this.kolumna].setForeground(kolorO);
                    przyciski[this.wiersz][this.kolumna].setBackground(Color.BLACK); // Tło dla O
                }

                wyjscie.println("RUCH," + this.wiersz + "," + this.kolumna);
                mojaTura = false;
                etykietaStatusu.setText("Czekaj na przeciwnika...");
            }
        }
    }
}

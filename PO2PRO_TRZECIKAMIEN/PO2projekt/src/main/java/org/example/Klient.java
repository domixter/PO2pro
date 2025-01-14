package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Klient extends JFrame {
    private static final String ADRES_SERWERA = "127.0.0.1";
    private static final int PORT_SERWERA = 1235;
    private final JButton[][] przyciski = new JButton[3][3];
    private final Character[][] plansza = new Character[3][3];
    private Socket socket;
    private PrintWriter wyjscie;
    private BufferedReader wejsciowe;
    private boolean mojaTura = false;
    private char mojSymbol = 'X';
    private final JLabel etykietaStatusu = new JLabel("Zaczynasz jako Gracz 1-> X");
    private final Color kolorX;
    private final Color kolorO;

    public Klient() {
        this.kolorX = Color.RED;
        this.kolorO = Color.GREEN;

        try {
            this.polaczZSerwerem();
            this.setTitle("Kółko i Krzyżyk");
            this.setLayout(new BorderLayout());

            JPanel panelPlanszy = new JPanel(new GridLayout(3, 3));
            this.inicjalizujPrzyciski(panelPlanszy);
            this.add(panelPlanszy, "Center");

           // this.etykietaStatusu.setHorizontalAlignment(0);
            etykietaStatusu.setHorizontalAlignment(SwingConstants.CENTER);
            add(etykietaStatusu, BorderLayout.SOUTH);

            //this.add(this.etykietaStatusu, "South");

            (new Thread(this::sluchajAktualizacjiPlanszy)).start();
            setSize(400, 400);
            setLocation(0, 0);

            setVisible(true);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    private void polaczZSerwerem() throws IOException {
        socket = new Socket(ADRES_SERWERA, PORT_SERWERA);
        wyjscie = new PrintWriter(this.socket.getOutputStream(), true);
        wejsciowe = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.plansza[i][j] = null;
            }
        }

        String statusGracza = wejsciowe.readLine();
        if (statusGracza != null) {
            JOptionPane.showMessageDialog(this, statusGracza);
            this.mojaTura = statusGracza.contains("X");
            this.mojSymbol = (statusGracza.contains("X") ? 'X' : 'O');
        }
    }

    private void inicjalizujPrzyciski(JPanel panelPlanszy) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton przycisk = new JButton("");
                przycisk.setFont(new Font("Arial", Font.BOLD, 60));
                przycisk.addActionListener(new Klient.NasluchujPrzycisku(i, j));
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
                                this.plansza[i][j] = komorki[index].charAt(0);
                            } else {
                                this.plansza[i][j] = null;
                            }
                            ++index;
                        }
                    }

                    this.przerysujPlansze();
                    continue;
                }

                if (komunikat.startsWith("TURA:")) {
                    char tura = komunikat.charAt(5);
                    mojaTura = tura == mojSymbol;
                    etykietaStatusu.setText(mojaTura ? "Twoja tura! Graczu 1 -> X" : "Zaczekaj na gracza 2 -> O");
                    continue;
                }

                if (komunikat.startsWith("KONIEC_GRy:")) {


                wynik = komunikat.substring(12);
                etykietaStatusu.setText("Koniec gry: " + wynik);
                JOptionPane.showMessageDialog(this, "Koniec gry: " + wynik);
                int opcja = JOptionPane.showConfirmDialog(this, "Czy chcesz zagrać ponownie?", "Restart gry", JOptionPane.YES_NO_OPTION);

                if (opcja == JOptionPane.YES_OPTION) {
                    this.restartujGre();
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
                if (this.plansza[i][j] != null) {
                    this.przyciski[i][j].setText(this.plansza[i][j].toString());
                    if (this.plansza[i][j] == 'X') {
                        this.przyciski[i][j].setForeground(this.kolorX); // Kolor tekstu X
                        this.przyciski[i][j].setBackground(Color.BLACK); // Kolor tła X (możesz zmienić na dowolny)
                    } else if (this.plansza[i][j] == 'O') {
                        this.przyciski[i][j].setForeground(this.kolorO); // Kolor tekstu O
                        this.przyciski[i][j].setBackground(Color.BLACK); // Kolor tła O (możesz zmienić na dowolny)
                    }
                } else {
                    this.przyciski[i][j].setText("");
                    this.przyciski[i][j].setBackground(Color.BLACK); // Kolor tła pustych przycisków
                }
            }
        }
    }

    private void restartujGre() {
        try {
            this.socket.close();
            new Klient();
            this.dispose();
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Klient();
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
                Klient.this.przyciski[this.wiersz][this.kolumna].setText(String.valueOf(Klient.this.mojSymbol));
                if (Klient.this.mojSymbol == 'X') {
                    Klient.this.przyciski[this.wiersz][this.kolumna].setForeground(Klient.this.kolorX);
                    Klient.this.przyciski[this.wiersz][this.kolumna].setBackground(Color.BLACK); // Tło dla X
                } else {
                    Klient.this.przyciski[this.wiersz][this.kolumna].setForeground(Klient.this.kolorO);
                    Klient.this.przyciski[this.wiersz][this.kolumna].setBackground(Color.BLACK); // Tło dla O
                }

                Klient.this.wyjscie.println("RUCH," + this.wiersz + "," + this.kolumna);
                Klient.this.mojaTura = false;
                Klient.this.etykietaStatusu.setText("Czekaj na przeciwnika...");
            }
        }
    }
}

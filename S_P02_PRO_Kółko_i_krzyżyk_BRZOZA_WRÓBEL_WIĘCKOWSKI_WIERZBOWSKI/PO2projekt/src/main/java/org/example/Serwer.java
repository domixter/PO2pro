package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Serwer {

    private static final int PORT_SERWERA = 1235;

    public Serwer() {
    }

    public static void main(String[] args) {
        try {
            ServerSocket serwerSocket = new ServerSocket(1235);

            try {
                System.out.println("Serwer uruchomiony...");

                while (true) {
                    Socket socketGracz1 = serwerSocket.accept();
                    Socket socketGracz2 = serwerSocket.accept();
                    (new Thread(new SesjaGry(socketGracz1, socketGracz2))).start();
                }
            } catch (Throwable var5) {
                try {
                    serwerSocket.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }
                throw var5;
            }
        } catch (IOException var6) {
            IOException e = var6;
            e.printStackTrace();
        }
    }
}

class SesjaGry implements Runnable {
    private Socket socketGracz1;
    private Socket socketGracz2;
    private PrintWriter wyjscieGracz1;
    private PrintWriter wyjscieGracz2;
    private BufferedReader wejscioweGracz1;
    private BufferedReader wejscioweGracz2;

    public SesjaGry(Socket socketGracz1, Socket socketGracz2) {
        this.socketGracz1 = socketGracz1;
        this.socketGracz2 = socketGracz2;
    }

    public void run() {
        try {
            this.wyjscieGracz1 = new PrintWriter(this.socketGracz1.getOutputStream(), true);
            this.wyjscieGracz2 = new PrintWriter(this.socketGracz2.getOutputStream(), true);
            this.wejscioweGracz1 = new BufferedReader(new InputStreamReader(this.socketGracz1.getInputStream()));
            this.wejscioweGracz2 = new BufferedReader(new InputStreamReader(this.socketGracz2.getInputStream()));
            this.wyjscieGracz1.println("Twoja tura! Jesteś X.");
            this.wyjscieGracz2.println("Zaczekaj na ruch przeciwnika! Jesteś O.");

            char[][] plansza = new char[3][3];
            char aktualnyGracz = 'X';

            while (true) {
                String wejscie;
                if (aktualnyGracz == 'X') {
                    wejscie = this.wejscioweGracz1.readLine();
                } else {
                    wejscie = this.wejscioweGracz2.readLine();
                }

                if (wejscie != null && wejscie.startsWith("RUCH")) {
                    int wiersz = Integer.parseInt(wejscie.split(",")[1]);
                    int kolumna = Integer.parseInt(wejscie.split(",")[2]);
                    if (plansza[wiersz][kolumna] == 0) {
                        plansza[wiersz][kolumna] = (char) aktualnyGracz;
                        this.wyslijPlanszeDoGraczy(plansza);
                        if (this.sprawdzWygrana(plansza, (char) aktualnyGracz)) {
                            this.wyslijKoniecGry(" Gratulacje ! " + aktualnyGracz + " wygrywa !");
                            break;
                        }

                        if (this.sprawdzPełność(plansza)) {
                            this.wyslijKoniecGry("Remis!");
                            break;
                        }

                        aktualnyGracz = aktualnyGracz == 'X' ? 'O' : 'X';
                        this.wyjscieGracz1.println("TURA:" + aktualnyGracz);
                        this.wyjscieGracz2.println("TURA:" + aktualnyGracz);
                    }
                }
            }
        } catch (IOException var14) {
            IOException e = var14;
            e.printStackTrace();
        } finally {
            try {
                this.socketGracz1.close();
                this.socketGracz2.close();
            } catch (IOException var13) {
                IOException e = var13;
                e.printStackTrace();
            }

        }

        this.zakonczGre();
    }

    private void zakonczGre() {
        try {
            this.socketGracz1.close();
            this.socketGracz2.close();
        } catch (IOException var3) {
            IOException e = var3;
            e.printStackTrace();
        }

        try {
            (new SesjaGry(this.socketGracz1, this.socketGracz2)).run();
        } catch (Exception var2) {
            Exception e = var2;
            e.printStackTrace();
        }

    }

    private void wyslijPlanszeDoGraczy(char[][] plansza) {
        StringBuilder danePlanszy = new StringBuilder();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                danePlanszy.append(plansza[i][j] == 0 ? "" : plansza[i][j]).append(",");
            }
        }

        this.wyjscieGracz1.println("PLANSZA:" + danePlanszy.toString());
        this.wyjscieGracz2.println("PLANSZA:" + danePlanszy.toString());
    }

    protected boolean sprawdzWygrana(char[][] plansza, char aktualnyGracz) {
        for (int i = 0; i < 3; ++i) {
            if (plansza[i][0] == aktualnyGracz && plansza[i][1] == aktualnyGracz && plansza[i][2] == aktualnyGracz) {
                return true;
            }

            if (plansza[0][i] == aktualnyGracz && plansza[1][i] == aktualnyGracz && plansza[2][i] == aktualnyGracz) {
                return true;
            }
        }

        if (plansza[0][0] == aktualnyGracz && plansza[1][1] == aktualnyGracz && plansza[2][2] == aktualnyGracz) {
            return true;
        } else if (plansza[0][2] == aktualnyGracz && plansza[1][1] == aktualnyGracz && plansza[2][0] == aktualnyGracz) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean sprawdzPełność(char[][] plansza) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (plansza[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private void wyslijKoniecGry(String komunikat) {
        this.wyjscieGracz1.println("KONIEC_GRy:" + komunikat);
        this.wyjscieGracz2.println("KONIEC_GRy:" + komunikat);
    }
}

import java.net.*;
import java.io.*;

public class Serwer {
    private static final int PORT_SERWERA = 12345;

    public static void main(String[] args) {
        try (ServerSocket serwerSocket = new ServerSocket(PORT_SERWERA)) {
            System.out.println("Serwer uruchomiony...");

            while (true) {
                Socket socketGracz1 = serwerSocket.accept();
                Socket socketGracz2 = serwerSocket.accept();


                new Thread(new SesjaGry(socketGracz1, socketGracz2)).start();
            }
        } catch (IOException e) {
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

    @Override
    public void run() {
        try {
            wyjscieGracz1 = new PrintWriter(socketGracz1.getOutputStream(), true);
            wyjscieGracz2 = new PrintWriter(socketGracz2.getOutputStream(), true);
            wejscioweGracz1 = new BufferedReader(new InputStreamReader(socketGracz1.getInputStream()));
            wejscioweGracz2 = new BufferedReader(new InputStreamReader(socketGracz2.getInputStream()));


            wyjscieGracz1.println("Twoja tura! Jesteś X.");
            wyjscieGracz2.println("Zaczekaj na ruch przeciwnika! Jesteś O.");


            char[][] plansza = new char[3][3];
            char aktualnyGracz = 'X';  // X zaczyna

            while (true) {
                String wejscie;
                if (aktualnyGracz == 'X') {
                    wejscie = wejscioweGracz1.readLine();
                } else {
                    wejscie = wejscioweGracz2.readLine();
                }


                if (wejscie != null && wejscie.startsWith("RUCH")) {
                    int wiersz = Integer.parseInt(wejscie.split(",")[1]);
                    int kolumna = Integer.parseInt(wejscie.split(",")[2]);


                    if (plansza[wiersz][kolumna] == 0) {
                        plansza[wiersz][kolumna] = aktualnyGracz;
                        wyslijPlanszeDoGraczy(plansza);


                        if (sprawdzWygrana(plansza, aktualnyGracz)) {
                            wyslijKoniecGry("Gratulacje! " + aktualnyGracz + " wygrywa!");
                            break;
                        } else if (sprawdzPełność(plansza)) {
                            wyslijKoniecGry("Remis!");
                            break;
                        }


                        aktualnyGracz = (aktualnyGracz == 'X') ? 'O' : 'X';

                        wyjscieGracz1.println("TURA:" + aktualnyGracz);
                        wyjscieGracz2.println("TURA:" + aktualnyGracz);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketGracz1.close();
                socketGracz2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        zakonczGre();
    }

    private void zakonczGre() {
        try {

            socketGracz1.close();
            socketGracz2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            new SesjaGry(socketGracz1, socketGracz2).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wyslijPlanszeDoGraczy(char[][] plansza) {
        StringBuilder danePlanszy = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                danePlanszy.append(plansza[i][j] == 0 ? "" : plansza[i][j]).append(",");
            }
        }

        wyjscieGracz1.println("PLANSZA:" + danePlanszy.toString());
        wyjscieGracz2.println("PLANSZA:" + danePlanszy.toString());
    }

    private boolean sprawdzWygrana(char[][] plansza, char aktualnyGracz) {
        for (int i = 0; i < 3; i++) {
            if (plansza[i][0] == aktualnyGracz && plansza[i][1] == aktualnyGracz && plansza[i][2] == aktualnyGracz)
                return true;
            if (plansza[0][i] == aktualnyGracz && plansza[1][i] == aktualnyGracz && plansza[2][i] == aktualnyGracz)
                return true;
        }
        if (plansza[0][0] == aktualnyGracz && plansza[1][1] == aktualnyGracz && plansza[2][2] == aktualnyGracz)
            return true;
        if (plansza[0][2] == aktualnyGracz && plansza[1][1] == aktualnyGracz && plansza[2][0] == aktualnyGracz)
            return true;

        return false;
    }

    private boolean sprawdzPełność(char[][] plansza) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (plansza[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    private void wyslijKoniecGry(String komunikat) {
        wyjscieGracz1.println("KONIEC_GRy:" + komunikat);
        wyjscieGracz2.println("KONIEC_GRy:" + komunikat);
    }
}

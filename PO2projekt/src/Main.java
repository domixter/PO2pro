import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private JButton[][] przyciski = new JButton[3][3];
    private gra gra = new gra();

    public Main() {
        setTitle("Gra w kółko i krzyżyk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu();
    }

    private void menu() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Gra w kółko i krzyżyk", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);

        JButton startButton = new JButton("Start gry");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.setBackground(Color.GREEN);
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);

        startButton.addActionListener(e -> plansza());
        add(startButton, BorderLayout.CENTER);

        setSize(500, 500);
        setVisible(true);
    }

    private void plansza() {
        getContentPane().removeAll();
        setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton przycisk = new JButton("");
                przycisk.setFont(new Font("Arial", Font.BOLD, 70));
                przycisk.setBackground(Color.RED);
                przycisk.setOpaque(true);
                przycisk.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                przycisk.setFocusPainted(false);

                przycisk.addActionListener(e -> {
                    JButton clickedButton = (JButton) e.getSource();
                    if (clickedButton.getText().equals("")) {
                        clickedButton.setText(gra.gracz());
                        if (check_wygrana(gra.gracz())) {
                            JOptionPane.showMessageDialog(this, gra.gracz() + " wygrał!");
                            menu();
                        } else if (pelnaPlansza()) {
                            JOptionPane.showMessageDialog(this, "Gra zakończyła się remisem.");
                            menu();
                        } else {
                            gra.switchGracz();
                        }
                    }
                });


                przycisk.getModel().addChangeListener(e -> {
                    if (przycisk.getModel().isPressed()) {
                        przycisk.setBackground(Color.ORANGE);
                    } else {
                        przycisk.setBackground(Color.RED);
                    }
                });

                przyciski[i][j] = przycisk;
                add(przycisk);
            }
        }

        setSize(500, 500);
        setVisible(true);
    }

    public boolean check_wygrana(String player) {
        for (int i = 0; i < 3; i++) {
            if (przyciski[i][0].getText().equals(player) &&
                    przyciski[i][1].getText().equals(player) &&
                    przyciski[i][2].getText().equals(player)) {
                highlightWygrana(i, 0, i, 1, i, 2);
                return true;
            }
            if (przyciski[0][i].getText().equals(player) &&
                    przyciski[1][i].getText().equals(player) &&
                    przyciski[2][i].getText().equals(player)) {
                highlightWygrana(0, i, 1, i, 2, i);
                return true;
            }
        }
        if (przyciski[0][0].getText().equals(player) &&
                przyciski[1][1].getText().equals(player) &&
                przyciski[2][2].getText().equals(player)) {
            highlightWygrana(0, 0, 1, 1, 2, 2);
            return true;
        }
        if (przyciski[0][2].getText().equals(player) &&
                przyciski[1][1].getText().equals(player) &&
                przyciski[2][0].getText().equals(player)) {
            highlightWygrana(0, 2, 1, 1, 2, 0);
            return true;
        }
        return false;
    }

    private void highlightWygrana(int... coords) {
        for (int i = 0; i < coords.length; i += 2) {
            przyciski[coords[i]][coords[i + 1]].setForeground(Color.ORANGE);
        }
    }

    public boolean pelnaPlansza() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (przyciski[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    public class gra {
        private String aktualgracz = "X";

        public String gracz() {
            return aktualgracz;
        }

        public void switchGracz() {
            aktualgracz = aktualgracz.equals("X") ? "O" : "X";
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}

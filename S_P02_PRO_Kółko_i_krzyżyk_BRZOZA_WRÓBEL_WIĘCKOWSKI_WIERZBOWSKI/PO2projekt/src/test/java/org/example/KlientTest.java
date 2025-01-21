package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class KlientTest {
    private Klient klient;

    @BeforeEach
    public void setUp() {
        klient = new Klient();
    }



    @Test
    public void testInicjalizujPrzyciski() {

        JPanel panelPlanszy = new JPanel(new GridLayout(3, 3));


        klient.inicjalizujPrzyciski(panelPlanszy);


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton przycisk = klient.przyciski[i][j];


                assertNotNull(przycisk, "Przycisk na pozycji [" + i + "][" + j + "] nie został poprawnie zainicjalizowany.");
                assertEquals("", przycisk.getText(), "Tekst przycisku na pozycji [" + i + "][" + j + "] powinien być pusty.");
                assertEquals(Color.BLACK, przycisk.getBackground(), "Kolor tła przycisku na pozycji [" + i + "][" + j + "] nie jest poprawny.");
                assertEquals(new Font("Arial", Font.BOLD, 60), przycisk.getFont(), "Czcionka przycisku na pozycji [" + i + "][" + j + "] jest niewłaściwa.");
            }
        }


        assertEquals(9, panelPlanszy.getComponentCount(), "Panel planszy powinien zawierać dokładnie 9 przycisków.");
    }
}

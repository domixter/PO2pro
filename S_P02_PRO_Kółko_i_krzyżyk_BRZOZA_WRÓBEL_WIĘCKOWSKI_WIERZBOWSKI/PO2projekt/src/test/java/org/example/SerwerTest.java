package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SerwerTest {

    @Test
    void testSprawdzWygrana_WygranaWiersza() {

        char[][] plansza = {
                {'X', 'X', 'X'},
                {'O', 'O', 0},
                {0, 0, 0}
        };
        SesjaGry sesjaGry = new SesjaGry(null, null);


        boolean wynik = sesjaGry.sprawdzWygrana(plansza, 'X');


        assertTrue(wynik, "Oczekiwano zwycięstwa dla X w wierszu.");
    }

    @Test
    void testSprawdzWygrana_WygranaKolumny() {
        // Arrange
        char[][] plansza = {
                {'X', 'O', 0},
                {'X', 'O', 0},
                {'X', 0, 0}
        };
        SesjaGry sesjaGry = new SesjaGry(null, null);


        boolean wynik = sesjaGry.sprawdzWygrana(plansza, 'X');


        assertTrue(wynik, "Oczekiwano zwycięstwa dla X w kolumnie.");
    }

    @Test
    void testSprawdzWygrana_BrakWygranej() {

        char[][] plansza = {
                {'X', 'O', 'X'},
                {'O', 'X', 'O'},
                {'O', 'X', 'O'}
        };
        SesjaGry sesjaGry = new SesjaGry(null, null);


        boolean wynik = sesjaGry.sprawdzWygrana(plansza, 'X');


        assertFalse(wynik, "Nie powinno być zwycięstwa.");
    }

    @Test
    void testSprawdzPełność_PlanszaPelna() {

        char[][] plansza = {
                {'X', 'O', 'X'},
                {'O', 'X', 'O'},
                {'O', 'X', 'O'}
        };
        SesjaGry sesjaGry = new SesjaGry(null, null);


        boolean wynik = sesjaGry.sprawdzPełność(plansza);


        assertTrue(wynik, "Oczekiwano, że plansza jest pełna.");
    }

    @Test
    void testSprawdzPełność_PlanszaNiePelna() {

        char[][] plansza = {
                {'X', 'O', 'X'},
                {'O', 'X', 0},
                {'O', 'X', 'O'}
        };
        SesjaGry sesjaGry = new SesjaGry(null, null);


        boolean wynik = sesjaGry.sprawdzPełność(plansza);


        assertFalse(wynik, "Oczekiwano, że plansza nie jest pełna.");
    }
}

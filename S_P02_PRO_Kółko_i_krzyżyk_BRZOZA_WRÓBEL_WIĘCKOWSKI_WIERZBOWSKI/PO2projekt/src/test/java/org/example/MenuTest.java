package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MenuTest {

    @Test
    void testUruchomKlientow() {

        MenuGry menuGry = new MenuGry();


        assertDoesNotThrow(() -> menuGry.uruchomKlientow());


    }
}

package com.javarush.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    @Test
    void shouldStoreCountryData() {
        Country country = new Country();

        country.setId(1);
        country.setCode("UKR");
        country.setCode2("UA");
        country.setName("Ukraine");
        country.setContinent(Continent.EUROPE);
        country.setRegion("Eastern Europe");
        country.setPopulation(41000000);
        country.setSurfaceArea(new BigDecimal("603700.00"));

        assertEquals(1, country.getId());
        assertEquals("UKR", country.getCode());
        assertEquals("UA", country.getCode2());
        assertEquals("Ukraine", country.getName());
        assertEquals(Continent.EUROPE, country.getContinent());
        assertEquals("Eastern Europe", country.getRegion());
        assertEquals(41000000, country.getPopulation());
        assertEquals(
                0,
                new BigDecimal("603700.00")
                        .compareTo(country.getSurfaceArea())
        );
    }

    @Test
    void shouldHaveEmptyCollections() {
        Country country = new Country();

        assertNotNull(country.getCities());
        assertNotNull(country.getLanguages());

        assertTrue(country.getCities().isEmpty());
        assertTrue(country.getLanguages().isEmpty());
    }
}
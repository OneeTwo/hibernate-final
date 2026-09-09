package com.javarush;

import com.javarush.dao.CityDAO;
import com.javarush.dao.CountryDAO;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private final SessionFactory sessionFactory;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public Main() {
        sessionFactory = HibernateUtil.getSessionFactory();

        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
    }

    private List<City> fetchData() {

        try (Session session = sessionFactory.getCurrentSession()) {

            List<City> allCities = new ArrayList<>();

            session.beginTransaction();

            List<Country> countries = countryDAO.getAll();

            int totalCount = cityDAO.getTotalCount();
            int step = 500;

            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityDAO.getItems(i, step));
            }

            session.getTransaction().commit();

            System.out.println("Countries loaded: " + countries.size());
            System.out.println("Cities loaded: " + allCities.size());

            return allCities;
        }
    }

    private void shutdown() {
        sessionFactory.close();
    }

    public static void main(String[] args) {

        Main main = new Main();

        List<City> allCities = main.fetchData();

        main.shutdown();
    }
}
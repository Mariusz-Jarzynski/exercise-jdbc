package co.uk.jarzynski.dao;

import co.uk.jarzynski.config.DbConnectionConfig;
import co.uk.jarzynski.dao.impl.PersonDaoImpl;
import co.uk.jarzynski.model.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PersonDaoTest {

    @Before
    public void initData() {

        // remove all data
        String deleteQuery = "DELETE FROM PERSONS   ";

        // init db
        String initQuery = " " +
                "INSERT INTO Persons (NAME, SURNAME, AGE) VALUES ('Mark', 'P', 30);          \n " +
                "INSERT INTO Persons (NAME, SURNAME, AGE) VALUES ('Maria', 'W', 18);         \n " +
                "INSERT INTO Persons (NAME, SURNAME, AGE) VALUES ('Eryk', 'S', 10);          \n " +
                "INSERT INTO Persons (NAME, SURNAME, AGE) VALUES ('Anna', 'L', 28);          \n " +
                "INSERT INTO Persons (NAME, SURNAME, AGE) VALUES ('Robert', 'L', 30);        \n ";

        Connection dbConnection = DbConnectionConfig.getInstance().getConnection();
        try {
            PreparedStatement deleteStatement = dbConnection.prepareStatement(deleteQuery);
            deleteStatement.executeUpdate();

            PreparedStatement insertStatement = dbConnection.prepareStatement(initQuery);
            insertStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void readAllPersonsFromDbTest() {
        try (Connection connection = DbConnectionConfig.getInstance().getConnection()) {
            PersonDao personDao = new PersonDaoImpl(connection);

            List<Person> peopleFromDb = personDao.readAllPersons();
            Assert.assertTrue("Didn't find any people inside db!", peopleFromDb.size() > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readOnlyAdultTest() {
        try (Connection connection = DbConnectionConfig.getInstance().getConnection()) {
            PersonDao personDao = new PersonDaoImpl(connection);

            List<Person> peopleFromDb = personDao.readOnlyAdult();
            Assert.assertTrue("Didn't find any adult inside db!", peopleFromDb.size() > 0);

            for (Person p : peopleFromDb) {
                Assert.assertTrue("This is not adult: " + p, p.getAge() >= 18);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void savePersonTest() {
        Person somebodyToSave = new Person("Helena", "J.", 15);

        Assert.assertTrue("Constructed object has wrong value of id", Person.ID_OF_NOT_PERSISTENT_PERSON == somebodyToSave.getId());

        try (Connection connection = DbConnectionConfig.getInstance().getConnection()) {
            PersonDao personDao = new PersonDaoImpl(connection);

            List<Person> peopleFromDbSoFar = personDao.readAllPersons();

            personDao.savePerson(somebodyToSave);
            Assert.assertTrue("Person wasn't save to db", somebodyToSave.getId() != Person.ID_OF_NOT_PERSISTENT_PERSON);
            // TODO:MP get all persons again and compare if returned list contains new added person
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------
    @Test
    public void checkPersonBySurname() {
        try (Connection connection = DbConnectionConfig.getInstance().getConnection()) {
            PersonDao personDao = new PersonDaoImpl(connection);

            List<Person> persons = personDao.findBySurname("L");
            System.out.println("Found persons " + persons);
            Assert.assertEquals("Wrong data found", 2, persons.size());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void checkPersonByName() {
        try (Connection connection = DbConnectionConfig.getInstance().getConnection()) {
            PersonDao personDao = new PersonDaoImpl(connection);

            List<Person> persons = personDao.findByName("Anna");
            System.out.println("Found persons: " + persons);
            Assert.assertEquals("Wrong data found", 2, persons.size());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void updateAgeTest() {

        try (Connection dbConnnectio = DbConnectionConfig.getInstance().getConnection();) {
            PersonDao personDao = new PersonDaoImpl(dbConnnectio);

            int numberOfChangedRecords = personDao.updatePersonAge(36, 36);
            Assert.assertEquals("Something wrong has happended ", 1, numberOfChangedRecords);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Assert.fail("Test failed: " + throwables.getMessage());
        }

    }

    @Test
    public void deletePersonBySurnameTest() {

        final String surnameToDelete = "L";

        try {
            Connection dbConnection = DbConnectionConfig.getInstance().getConnection();
            PersonDao personDao = new PersonDaoImpl(dbConnection);

            int numberOfDeletedRecords = personDao.deletePersonBySurname(surnameToDelete);
            Assert.assertEquals("Something wrong has happened ", 2, numberOfDeletedRecords);


        } catch (Exception e) {
            Assert.fail("deletePersonBySurnameTest - failed: " + e.getMessage());
        }

    }
}
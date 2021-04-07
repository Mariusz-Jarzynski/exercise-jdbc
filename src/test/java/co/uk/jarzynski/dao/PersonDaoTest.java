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
        Connection connection = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(connection);

        List<Person> peopleFromDb = personDao.readAllPersons();
        Assert.assertTrue("Didn't find any people inside db!", peopleFromDb.size() > 0);

    }

    @Test
    public void readOnlyAdultTest() {
        Connection connection = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(connection);

        List<Person> peopleFromDb = personDao.readOnlyAdult();
        Assert.assertTrue("Didn't find any adult inside db!", peopleFromDb.size() > 0);

    }

    @Test
    public void savePersonTest() {
        Person somebodyToSave = new Person("Helena", "J.", 15);

        Assert.assertTrue("Constructed object has wrong value of id", Person.ID_OF_NOT_PERSISTENT_PERSON == somebodyToSave.getId());

        Connection connection = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(connection);

        List<Person> peopleFromDbSoFar = personDao.readAllPersons();

        personDao.savePerson(somebodyToSave);
        Assert.assertTrue("Person wasn't save to db", somebodyToSave.getId() != Person.ID_OF_NOT_PERSISTENT_PERSON);
        // TODO:MP get all persons again and compare if returned list contains new added person

    }

    // --------
    @Test
    public void checkPersonBySurname() {
        Connection connection = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(connection);

        List<Person> persons = personDao.findBySurname("L");
        System.out.println("Found persons " + persons);
        Assert.assertEquals("Wrong data found", 2, persons.size());

    }

    @Test
    public void checkPersonByName() {
        Connection connection = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(connection);

        List<Person> persons = personDao.findByName("Anna");
        System.out.println("Found persons: " + persons);
        Assert.assertEquals("Wrong data found", 1, persons.size());

    }

    @Test
    public void updateAgeTest() {
        Connection dbConnnectio = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(dbConnnectio);

        int numberOfChangedRecords = personDao.updatePersonAge(36, 36);
        Assert.assertEquals("Something wrong has happended ", 1, numberOfChangedRecords);

    }

    @Test
    public void deletePersonBySurnameTest() {

        final String surnameToDelete = "L";

        Connection dbConnection = DbConnectionConfig.getInstance().getConnection();
        PersonDao personDao = new PersonDaoImpl(dbConnection);

        int numberOfDeletedRecords = personDao.deletePersonBySurname(surnameToDelete);
        Assert.assertEquals("Something wrong has happened ", 2, numberOfDeletedRecords);


    }
}
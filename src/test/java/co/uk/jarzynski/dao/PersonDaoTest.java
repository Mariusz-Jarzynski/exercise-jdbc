package co.uk.jarzynski.dao;

import co.uk.jarzynski.config.DbConnectionConfig;
import co.uk.jarzynski.dao.impl.PersonDaoImpl;
import co.uk.jarzynski.model.Person;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDaoTest {

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
    

}
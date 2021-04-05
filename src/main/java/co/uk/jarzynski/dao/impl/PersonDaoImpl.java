package co.uk.jarzynski.dao.impl;

import co.uk.jarzynski.dao.PersonDao;
import co.uk.jarzynski.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDaoImpl implements PersonDao {

    private Connection dbConnection;

    public PersonDaoImpl(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Person> readAllPersons() {
        List<Person> persons = new ArrayList<>();

        String query = "" +
                "SELECT ID, NAME, SURNAME, AGE    \n " +
                "FROM PERSONS                       ";


        try {
            Statement statement = dbConnection.createStatement();

            int readId = Person.ID_OF_NOT_PERSISTENT_PERSON;
            String readName;
            String readSurname;
            int readAge = 0;
            Person readPerson;

            ResultSet resultFromDb = statement.executeQuery(query);
            while (resultFromDb.next()) {
                readId = resultFromDb.getInt(1);
                readName = resultFromDb.getString(2);
                readSurname = resultFromDb.getString(3);
                readAge = resultFromDb.getInt(4);

                readPerson = new Person(readId, readName, readSurname, readAge);
                System.out.println(String.format("Person read from db: [%s]", readPerson));

                persons.add(readPerson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return persons;
    }

    @Override
    public List<Person> readOnlyAdult() {
        return readPeopleOlderThen(18);
    }

    @Override
    public List<Person> readChildren() {
        return null;
    }

    @Override
    public boolean savePerson(Person somebody) {
        boolean result = false;

        if (Person.ID_OF_NOT_PERSISTENT_PERSON != somebody.getId()) {
            System.out.println(String.format("This person has already been added to db: [%s]", somebody));
        } else {
            String insert = "" +
                    "INSERT INTO Persons (NAME, SURNAME, AGE)\n" +
                    "VALUES (?, ?, ?)";

            try {
                PreparedStatement insertStatement = dbConnection.prepareStatement(insert, new String[] { "ID" });
                insertStatement.setString(1, somebody.getName());
                insertStatement.setString(2, somebody.getSurname());
                insertStatement.setInt(3, somebody.getAge());

                int numberOfAddedRows = insertStatement.executeUpdate();
                if  (1 == numberOfAddedRows) {
                    System.out.println("Person was added to db");
                    result = true;

                    ResultSet generatedId = insertStatement.getGeneratedKeys();
                    if (generatedId.next()) {
                        somebody.setId(generatedId.getInt(1));
                        System.out.println(String.format("Id for person was set: [%s]", somebody));
                    } else {
                        System.out.println("Couldn't obtain generated key");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    private List<Person> readPeopleYoungerThen(int ageBoundaryExclusive) {
        return null;
    }

    private List<Person> readPeopleOlderThen(int ageBoundaryInclusive) {
        List<Person> persons = new ArrayList<>();

        String query = "" +
                "SELECT ID, NAME, SURNAME, AGE\n" +
                "FROM PERSONS  \n" +
                "WHERE AGE >= ?";

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setInt(1, ageBoundaryInclusive);
            ResultSet dataFromDb = preparedStatement.executeQuery();
            Person personFromDb;
            while (dataFromDb.next()) {
                personFromDb = new Person(dataFromDb.getInt(1), dataFromDb.getString(2),
                        dataFromDb.getString(3), dataFromDb.getInt(4));

                System.out.println(String.format("Person read from db: [%s]", personFromDb));
                persons.add(personFromDb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return persons;
    }
}

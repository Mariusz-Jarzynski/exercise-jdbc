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

    // -----

    @Override
    public List<Person> findBySurname(String surname) {
        List<Person> result = new ArrayList<>();

        // 1. Create string query
        String query = " "  +
                "SELECT ID, NAME, SURNAME, AGE  \n " +
                "FROM PERSONS                   \n " +
                "WHERE SURNAME = ?;             \n " ;

        // 2. Db connection
        // dbConnection already exists
        // 3. Create preparedStatement

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);

            // 4. add parameters to statement
            preparedStatement.setString(1, surname);

            // 5. Send query to db
            ResultSet cursor = preparedStatement.executeQuery();
            // 6. Parse result
            while (cursor.next()) {
                // 6.1. create Person per record
                Person person = new Person(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getNString(3),
                        cursor.getInt(4)
                );
                // 6.2. add to result
                result.add(person);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return result;
    }

    @Override
    public List<Person> findByName(String name) {
        List<Person> personList = new ArrayList<>();

        String query = " " +
                "SELECT ID, NAME, SURNAME, AGE    \n " +
                "FROM PERSONS                     \n " +
                "WHERE NAME = ?;                ";

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, name);

            ResultSet cursor = preparedStatement.executeQuery();

            while (cursor.next()) {
                Person person = new Person(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                personList.add(person);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return personList;
    }

    @Override
    public int updatePersonAge(int personId, int newAge) {
        int numberOfChangedRecords = 0;

        String updateQuery = " " +
                "UPDATE PERSONS       \n " +
                "SET AGE = ?          \n " +
                "WHERE ID = ?;        \n ";

        try {
            PreparedStatement updateStatement = dbConnection.prepareStatement(updateQuery);
            updateStatement.setInt(1, personId);
            updateStatement.setInt(2, newAge);

            numberOfChangedRecords = updateStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return numberOfChangedRecords;
    }

    @Override
    public int deletePersonBySurname(String personSurname) {
        int numberOfDeletedPersons = 0;

        String deleteQuery = " " +
                "DELETE FROM PERSONS      \n" +
                "WHERE SURNAME = ?        \n" ;

        try {
            PreparedStatement deleteStatement = dbConnection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, personSurname);

            numberOfDeletedPersons = deleteStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return numberOfDeletedPersons;
    }
}








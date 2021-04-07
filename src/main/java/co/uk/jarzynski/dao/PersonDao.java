package co.uk.jarzynski.dao;

import co.uk.jarzynski.model.Person;

import java.util.List;

public interface PersonDao {

    List<Person> readAllPersons();

    List<Person> readOnlyAdult();

    List<Person> readChildren();

    boolean savePerson(Person somebody);

    // ----
    List<Person> findBySurname(String surname);

    List<Person> findByName(String name);

    int updatePersonAge(int personId, int newAge);

    int deletePersonBySurname(String personSurname);

    int getMaxIdForPersons();

}

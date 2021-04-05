package co.uk.jarzynski.dao;

import co.uk.jarzynski.model.Person;

import java.util.List;

public interface PersonDao {

    List<Person> readAllPersons();

    List<Person> readOnlyAdult();

    List<Person> readChildren();

    boolean savePerson(Person somebody);

    // ----

}

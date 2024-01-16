package com.omid.service.api;

import com.omid.entity.Person;
import com.omid.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public Person savePerson(Person person) {
        return repository.save(person);
    }

    public Person getPersonById(Long id) {
        return repository.findById(id).get();
    }

    public List<Person> getAll() {
        return repository.findAll();
    }
}

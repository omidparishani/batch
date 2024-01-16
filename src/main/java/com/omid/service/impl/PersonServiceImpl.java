package com.omid.service.impl;

import com.omid.entity.Person;
import com.omid.repository.PersonRepository;
import com.omid.service.api.PersonService;

import java.util.List;

public class PersonServiceImpl implements PersonService {
    private final PersonRepository repository;

    public PersonServiceImpl(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public Person savePerson(Person person) {
        return repository.save(person);
    }

    @Override
    public Person getPersonById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public List<Person> getAll() {
        return repository.findAll();
    }
}

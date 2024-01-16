package com.omid.service.api;

import com.omid.entity.Person;
import com.omid.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PersonService {
     Person savePerson(Person person);

     Person getPersonById(Long id);

     List<Person> getAll();
}

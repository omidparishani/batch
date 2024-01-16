package com.omid.controller;

import com.omid.dto.Response;
import com.omid.dto.exampleDto;
import com.omid.entity.Person;
import com.omid.service.api.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/createPerson")
    public Response savePerson(@RequestBody Person person) {
        Response response;
        try {
            Person savedPerson = personService.savePerson(person);
            response = new Response("Ok", Collections.singletonList(savedPerson));
        } catch (Exception e) {
            response = new Response("Error: " + e.getMessage(), null);
        }
        return response;
    }

    @GetMapping("/getPerson/{personId}")
    @ResponseBody
    public Person getPerson(@PathVariable Long personId) {
        return personService.getPersonById(personId);
    }

    @GetMapping("/getAll")
    @ResponseBody
    public List<Person> getAll() {
        return personService.getAll();
    }
}
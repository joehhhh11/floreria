package com.sigis.controller;

import com.sigis.model.Persona;
import com.sigis.service.PersonaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public List<Persona> listar() {
        return personaService.listar();
    }

    @PostMapping
    public Persona guardar(@RequestBody Persona persona) {
        return personaService.guardar(persona);
    }
}

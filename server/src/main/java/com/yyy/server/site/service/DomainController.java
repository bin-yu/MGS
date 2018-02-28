package com.yyy.server.site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.site.repo.Domain;
import com.yyy.server.site.repo.DomainRepo;

@RestController
@RequestMapping({"/domains"})
public class DomainController {
    @Autowired
    private DomainRepo repo;

    @GetMapping
    public Domain getDomainByName(@RequestParam() String name) throws Exception {
        return repo.findOneByName(name);
    }


    @PostMapping()
    public Domain addDomain(@RequestBody Domain domain) {
        return repo.save(domain);
    }

    @PutMapping("/{id}")
    public Domain updateDomain(@PathVariable Long id, @RequestBody Domain domain) {
        Domain db = repo.findOne(id);
        if (db == null) {
            throw new IllegalArgumentException("domain id not found!");
        }
        db.setLabel(domain.getLabel());
        return repo.save(db);
    }

    @DeleteMapping("/{id}")
    public void deleteDomain(@PathVariable Long id) {
        repo.delete(id);
    }
}

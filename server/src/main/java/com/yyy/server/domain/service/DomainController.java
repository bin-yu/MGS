package com.yyy.server.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.domain.repo.Domain;
import com.yyy.server.domain.repo.DomainRepo;

@RestController
@RequestMapping({"/domains"})
public class DomainController {
    @Autowired
    private DomainRepo repo;

    @GetMapping
    public Domain getRootDomainByName(@RequestParam() String name) throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name parameter cannot be empty!");
        }
        return repo.findOneByName(name);
    }

    @PostMapping
    public Domain addRootDomain(@RequestBody Domain domain) {
        domain.setParent(null);//root domain has no parent
        return repo.save(domain);
    }

    @PostMapping("/{pid}/children")
    public Domain addChildDomain(@PathVariable Long pid, @RequestBody Domain domain) {
        Domain parent = repo.findOne(pid);
        if (parent == null) {
            throw new IllegalArgumentException("parent domain id not found!");
        }
        domain.setParent(parent);
        domain.setName(null);//use null name for child domain
        return repo.save(domain);
    }


    @PutMapping("/{id}")
    public Domain updateDomain(@PathVariable Long id, @RequestBody Domain domain) {
        Domain db = repo.findOne(id);
        if (db == null) {
            throw new IllegalArgumentException("domain id not found!");
        }
        //only allow update the label
        db.setLabel(domain.getLabel());
        return repo.save(db);
    }

    @DeleteMapping("/{id}")
    public void deleteDomain(@PathVariable Long id) {
        repo.delete(id);
    }
}

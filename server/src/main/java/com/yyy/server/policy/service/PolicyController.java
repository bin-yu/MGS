package com.yyy.server.policy.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.policy.executor.PolicyExecutor;
import com.yyy.server.policy.repo.Policy;
import com.yyy.server.policy.repo.PolicyRepo;

@RestController
@RequestMapping({"/policies"})
public class PolicyController {
    @Autowired
    private PolicyRepo repo;
    @Autowired
    private PolicyExecutor policyExecutor;

    @GetMapping
    public Page<Policy> getPolicys(Pageable pageable) throws Exception {
        return repo.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Policy getPolicy(@PathVariable Long id) throws IOException {
        return repo.findOne(id);
    }

    @PostMapping()
    public Policy addPolicy(@RequestBody Policy policy) throws IOException {
        return repo.save(policy);
    }

    @PutMapping("/{id}")
    public Policy updatePolicy(@PathVariable Long id, @RequestBody Policy policy) throws IOException {
        if (!id.equals(policy.getId())) {
            throw new IllegalArgumentException("Mismatched id between path variable and request body.");
        }
        return repo.save(policy);
    }

    @DeleteMapping("/{id}")
    public void deletePolicy(@PathVariable Long id) {
        repo.delete(id);
    }

    @PostMapping("/runNow")
    public void runNow() {
        policyExecutor.runNow();
    }

}

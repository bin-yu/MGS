package com.yyy.server.policy.repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PolicyRepo {
    @Autowired
    private PolicyInDbRepo dbRepo;

    public Policy save(Policy policy) throws IOException {
        PolicyInDb dbObj = dbRepo.save(policy.toDbObject());
        return Policy.fromDbObject(dbObj);
    }

    public List<Policy> findAll() throws IOException {
        Iterator<PolicyInDb> dbObjIter = dbRepo.findAll().iterator();
        List<Policy> policyList = new ArrayList<Policy>();
        while (dbObjIter.hasNext()) {
            policyList.add(Policy.fromDbObject(dbObjIter.next()));
        }
        return policyList;
    }

    public Page<Policy> findAll(Pageable pageable) throws IOException {
        Page<PolicyInDb> pageDb = dbRepo.findAll(pageable);
        Iterator<PolicyInDb> dbObjIter = pageDb.iterator();
        List<Policy> policyList = new ArrayList<Policy>();
        while (dbObjIter.hasNext()) {
            policyList.add(Policy.fromDbObject(dbObjIter.next()));
        }
        return new PageImpl<Policy>(policyList, pageable, pageDb.getTotalElements());
    }

    public Policy findOne(Long id) throws IOException {
        PolicyInDb dbObj = dbRepo.findOne(id);
        return Policy.fromDbObject(dbObj);
    }

    public void delete(Long id) {
        dbRepo.delete(id);
    }
}

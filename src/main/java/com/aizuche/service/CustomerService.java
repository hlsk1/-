package com.aizuche.service;

import com.aizuche.entity.Customer;
import com.aizuche.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findByConditions(String name, String phone) {
        return customerRepository.findByConditions(name, phone);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    @Transactional
    public Customer save(Customer customer) {
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer update(Customer customer) {
        Customer existing = customerRepository.findById(customer.getId()).orElse(null);
        if (existing != null) {
            existing.setName(customer.getName());
            existing.setPhone(customer.getPhone());
            existing.setIdCard(customer.getIdCard());
            existing.setLicense(customer.getLicense());
            existing.setRemark(customer.getRemark());
            existing.setUpdateTime(LocalDateTime.now());
            return customerRepository.save(existing);
        }
        return null;
    }

    @Transactional
    public void addToBlacklist(Long id, String reason) {
        Customer customer = findById(id);
        if (customer != null) {
            customer.setStatus("BLACKLIST");
            customer.setRemark(reason);
            customer.setUpdateTime(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }

    @Transactional
    public void removeFromBlacklist(Long id) {
        Customer customer = findById(id);
        if (customer != null) {
            customer.setStatus("NORMAL");
            customer.setUpdateTime(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }

    @Transactional
    public void addPoints(Long id, int points) {
        Customer customer = findById(id);
        if (customer != null) {
            customer.setPoints((customer.getPoints() == null ? 0 : customer.getPoints()) + points);
            customer.setUpdateTime(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }

    public List<Customer> getBlacklist() {
        return customerRepository.findByStatus("BLACKLIST");
    }
}
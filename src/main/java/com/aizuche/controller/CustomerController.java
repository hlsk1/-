package com.aizuche.controller;

import com.aizuche.entity.Customer;
import com.aizuche.service.CustomerService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String phone) {
        List<Customer> list = customerService.findByConditions(name, phone);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.success("客户创建成功");
    }

    @PutMapping("/edit/{id}")
    public Result edit(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        Customer updated = customerService.update(customer);
        if (updated == null) {
            return Result.error("客户不存在");
        }
        return Result.success("修改成功");
    }

    @PostMapping("/blacklist/{id}")
    public Result addToBlacklist(@PathVariable Long id, @RequestParam String reason) {
        customerService.addToBlacklist(id, reason);
        return Result.success("已加入黑名单");
    }

    @PostMapping("/unblacklist/{id}")
    public Result removeFromBlacklist(@PathVariable Long id) {
        customerService.removeFromBlacklist(id);
        return Result.success("已移出黑名单");
    }

    @GetMapping("/blacklist")
    public Result getBlacklist() {
        return Result.success(customerService.getBlacklist());
    }

    @PostMapping("/addPoints/{id}")
    public Result addPoints(@PathVariable Long id, @RequestParam int points) {
        customerService.addPoints(id, points);
        return Result.success("积分增加成功");
    }
}
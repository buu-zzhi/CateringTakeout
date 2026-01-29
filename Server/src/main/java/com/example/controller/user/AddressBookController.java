package com.example.controller.user;

import com.example.constant.MessageConstant;
import com.example.constant.StatusConstant;
import com.example.context.BaseContext;
import com.example.entity.AddressBook;
import com.example.result.Result;
import com.example.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder()
                .userId(userId).build();
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    @GetMapping("/default")
    public Result<AddressBook> listDefaultAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder()
                .userId(userId)
                .isDefault(StatusConstant.ENABLE)
                .build();
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && !list.isEmpty()) {
            return Result.success(list.get(0));
        }
        return Result.error(MessageConstant.NO_DEFAULT_ADDRESS);
    }

    @PutMapping("/default")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getAddressById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    @DeleteMapping("/")
    public Result deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }


    @PostMapping
    public Result save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    @PutMapping
    public Result updateById(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Result.success();
    }
}

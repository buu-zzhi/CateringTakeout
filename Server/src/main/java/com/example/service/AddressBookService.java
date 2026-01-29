package com.example.service;

import com.example.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list(AddressBook addressBook);

    AddressBook getById(Long id);

    void deleteById(Long id);

    void save(AddressBook addressBook);

    void updateById(AddressBook addressBook);

    void setDefault(AddressBook addressBook);
}

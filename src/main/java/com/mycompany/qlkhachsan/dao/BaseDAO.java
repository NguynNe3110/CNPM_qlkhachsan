package com.mycompany.qlkhachsan.dao;

import java.util.List;

public interface BaseDAO<T> {
    List<T> getAll();
    T getById(int id);
    boolean add(T t);
    boolean update(T t);
    boolean delete(int id);
}

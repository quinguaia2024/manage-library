package model.services;

import java.util.List;

import model.core.Irepository;

public abstract class ReserveRepo implements Irepository<Reserve> {
    void save(Reserve R){}
    Reserve findById(String id){ return null; }
    void delete(Reserve R){}
    List<Reserve> findAll(){return null; } 
}

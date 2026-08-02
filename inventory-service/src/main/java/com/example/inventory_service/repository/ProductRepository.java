package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Category;
import com.example.inventory_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);

    List<Product> findTop10ByOrderBySalesCountDesc();
}
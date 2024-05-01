package com.Emisoft.beststore.Services;

import com.Emisoft.beststore.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductService extends JpaRepository<Product, Long>
{

}

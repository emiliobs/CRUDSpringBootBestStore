package com.Emisoft.beststore.Controllers;

import com.Emisoft.beststore.Models.Product;
import com.Emisoft.beststore.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController
{
   @Autowired private ProductService productService;

    @GetMapping({"", "/"})
    public String showProductList(Model model )
    {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }
}

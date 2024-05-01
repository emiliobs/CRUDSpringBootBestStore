package com.Emisoft.beststore.Controllers;

import com.Emisoft.beststore.Models.Product;
import com.Emisoft.beststore.Models.ProductDto;
import com.Emisoft.beststore.Services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
        List<Product> products = productService.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public  String showCreatePage(Model model)
    {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto productDto, BindingResult result)
    {
        if (productDto.getImageFile().isEmpty())
        {
          result.addError(new FieldError("productDto", "imageFile", "The image file is required."));
        }

        if (result.hasErrors())
        {
            return "products/CreateProduct";
        }

        return "redirect:/products";
    }
}

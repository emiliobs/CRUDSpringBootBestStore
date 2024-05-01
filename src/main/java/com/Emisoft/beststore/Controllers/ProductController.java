package com.Emisoft.beststore.Controllers;

import com.Emisoft.beststore.Models.Product;
import com.Emisoft.beststore.Models.ProductDto;
import com.Emisoft.beststore.Services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

/**
 * Controller handling operations related to products.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Show the list of products.
     *
     * @param model the model to be populated with data
     * @return the view for displaying the product list
     */
    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = productService.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    /**
     * Show the form to create a new product.
     *
     * @param model the model to be populated with data
     * @return the view for creating a new product
     */
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    /**
     * Create a new product.
     *
     * @param productDto the DTO representing the product data
     * @param result     the binding result for validation errors
     * @return the view to redirect to after creating the product
     */
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto productDto, BindingResult result) {
        // Validate image file
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required."));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        // Save image file
        MultipartFile image = productDto.getImageFile();
        Date createAt = new Date();
        String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // Create and save the product
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createAt);
        product.setImageFileName(storageFileName);

        productService.save(product);

        return "redirect:/products";
    }

    /**
     * Show the form to edit a product.
     *
     * @param model the model to be populated with data
     * @param id    the id of the product to edit
     * @return the view for editing the product
     */
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam long id) {
        try {
            Product product = productService.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception ex) {
            System.out.printf("Exception: " + ex.getMessage());
            return "redirect:/products";
        }

        return "products/EditProduct";
    }

    /**
     * Update a product.
     *
     * @param model      the model to be populated with data
     * @param id         the id of the product to update
     * @param productDto the DTO representing the updated product data
     * @param result     the binding result for validation errors
     * @return the view to redirect to after updating the product
     */
    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam long id, @Valid @ModelAttribute ProductDto productDto,
            BindingResult result) {
        try {
            Product product = productService.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()) {
                // Delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                // Save new image file
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);
            }

            // Update product data
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            productService.save(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/products";
    }

    /**
     * Delete a product.
     *
     * @param id the id of the product to delete
     * @return the view to redirect to after deleting the product
     */
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam long id) {
        try {
            Product product = productService.findById(id).get();

            // Delete product image
            Path imagePath = Paths.get("public/images" + product.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            // Delete the product from the database
            productService.delete(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/products";
    }
}

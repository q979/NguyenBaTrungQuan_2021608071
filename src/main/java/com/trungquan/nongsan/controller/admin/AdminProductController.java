package com.trungquan.nongsan.controller.admin;

import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.dto.ProductSearchDTO;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.entity.Category;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.service.CategoryService;
import com.trungquan.nongsan.service.PromotionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/admin/products_management")
public class AdminProductController extends BaseController {

    private final ProductService productService;
    private final CategoryService categoryService;


    @GetMapping
    public String showProductsPage(Model model,
                                @RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "size", defaultValue = "4") int size,
                                @ModelAttribute("search") ProductSearchDTO search) {
        Page<Product> productPage = productService.searchProducts(search, PageRequest.of(page - 1, size));
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "admin/products";
    }


    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new Product());
        return "admin/products_add_or_update";
    }

    @PostMapping("/add")
    public String addOrUpdateProduct(@ModelAttribute("product") @Valid Product product,
                                  BindingResult bindingResult,
                                  @RequestParam("cover_image") MultipartFile coverImage,
                                  Model model
            , RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("error", "Đã có lỗi xảy ra vui lòng nhập lại");
            return "admin/products_add_or_update";
        }


        if (product.getId() != null) {
            // Check if there is an existing product with the given ID
            Product existingProduct = productService.getProductById(product.getId());
            if (existingProduct != null) {
                // Update the product with new data
                if (product.getPublishedDate() == null) {
                    product.setPublishedDate(existingProduct.getPublishedDate());
                }
                if (coverImage.isEmpty()) {
                    product.setCoverImage(existingProduct.getCoverImage());
                }

                productService.editProduct(product, coverImage);
                Product editedProduct = productService.getProductById(product.getId());
                model.addAttribute("product", editedProduct);
                redirectAttributes.addFlashAttribute("message", "Sửa thông tin nông sản thành công!");
            }
        } else {
            Product exist = productService.getProductByName(product.getTitle());

            if (exist != null) {
                model.addAttribute("error", "Đã tồn tại nông sản với tên này");
                return "admin/products_add_or_update";
            } else productService.addProduct(product, coverImage);   
            redirectAttributes.addFlashAttribute("message", "Thêm nông sản thành công!");
        }

        return "redirect:/admin/products_management";
    }


    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);

        return "admin/products_add_or_update";
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean activeFlag, RedirectAttributes redirectAttributes) {
        try{
            productService.setActiveFlag(id, activeFlag);
            // Add a success message to the model
            String action = activeFlag ? "kích hoạt lại trạng thái đang bán cho" : "cập nhật trạng thái không bán cho";
            redirectAttributes.addFlashAttribute("message", action + " nông sản thành công!");

            return "redirect:/admin/products_management";
        } catch (Exception ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/admin/products_management";
        }


    }

    @GetMapping("/remove/{id}")
    public String removeProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Xóa nông sản thành công!");
            return "redirect:/admin/products_management";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/admin/products_management";
        }
    }

    @GetMapping("/promotion/{id}")
    public String addProductToPromotion(@PathVariable String id, @RequestParam() boolean activeFlag, RedirectAttributes redirectAttributes) {
        try{
            if(!activeFlag){
                productService.removeProductFromPromotion(Long.parseLong(id));
            }
            else {
                productService.addProductToPromotion(Long.parseLong(id));
            }
            // Add a success message to the model
            redirectAttributes.addFlashAttribute("message", activeFlag ? "Thêm sản phẩm vào khuyến mãi thành công!" : "Xoá sản phẩm khỏi khuyến mãi thành công!");
            return "redirect:/admin/products_management";
        } catch (Exception ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/admin/products_management";
        }
    }


}

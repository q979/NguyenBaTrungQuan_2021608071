package com.trungquan.nongsan.controller.admin;

import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.dto.BlogSearchDTO;
import com.trungquan.nongsan.entity.Blog;
import com.trungquan.nongsan.entity.Category;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.service.BlogService;
import com.trungquan.nongsan.service.CategoryService;
import com.trungquan.nongsan.service.FileUploadService;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@RequestMapping("/admin/blogs_management")
public class AdminBlogController extends BaseController {

    BlogService blogService;
    UserService userService;
    FileUploadService fileUploadService;
    CategoryService categoryService;
    ProductService productService;

    @GetMapping
    public String showBlogPage(Model model,
                                @RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "size", defaultValue = "4") int size,
                                @ModelAttribute("search") BlogSearchDTO search) {
        Page<Blog> blogPage = blogService.findAllByKeywordPaginated(search, PageRequest.of(page - 1, size));
        List<User> users = userService.getAllUsers();

        model.addAttribute("blogPage", blogPage);
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());

        return "admin/blog";
    }

    @GetMapping("/add")
    public String showAddBlogForm(Model model) {
        List<User> users = userService.getAllUsers();
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.findAllActive();
        model.addAttribute("users", users);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("blog", new Blog());
        return "admin/blogs_add_or_update";
    }

    @PostMapping("/add")
    public String addOrUpdateBlog(@ModelAttribute("blog") @Valid Blog blog,
                                  BindingResult bindingResult,
                                  @RequestParam("blog_thumbnail") MultipartFile blog_thumbnail,
                                  @RequestParam(value = "selectedProducts", required = false) List<Long> selectedProducts,
                                  Model model,
                                  RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().toString();
            List<User> users = userService.getAllUsers();
            List<Category> categories = categoryService.getAllCategories();
            List<Product> products = productService.findAllActive();
            model.addAttribute("users", users);
            model.addAttribute("categories", categories);
            model.addAttribute("products", products);
            model.addAttribute("error", "Đã có lỗi xảy ra vui lòng nhập lại");
            return "admin/blogs_add_or_update";
        }

        if (blog.getId() != null) {
            // Check if there is an existing product with the given ID
            Blog existingBlog = blogService.getBlogById(blog.getId());
            if (existingBlog != null) {
                // Update the product with new data
                blogService.editBlog(blog.getId(), blog, blog_thumbnail, selectedProducts);
                Blog editedBlog = blogService.getBlogById(blog.getId());
                model.addAttribute("blog", editedBlog);
                List<Category> categories = categoryService.getAllCategories();
                List<Product> products = productService.findAllActive();
                model.addAttribute("categories", categories);
                model.addAttribute("products", products);
                redirectAttributes.addFlashAttribute("message", "Sửa thông tin bài viết thành công!");
            }
        } else {
            Blog exist = blogService.getBlogByTitle(blog.getTitle());

            if (exist != null) {
                List<User> users = userService.getAllUsers();
                List<Category> categories = categoryService.getAllCategories();
                List<Product> products = productService.findAllActive();
                model.addAttribute("users", users);
                model.addAttribute("categories", categories);
                model.addAttribute("products", products);
                model.addAttribute("error", "Đã tồn tại bài viết với tiêu đề này");
                return "admin/blogs_add_or_update";
            } else {
                blogService.addBlog(blog, blog_thumbnail, selectedProducts);
            }
            redirectAttributes.addFlashAttribute("message", "Thêm bài viết thành công!");
        }

        return "redirect:/admin/blogs_management/add";
    }


    @GetMapping("/edit/{id}")
    public String showEditBlogForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlogById(id);
        List<User> users = userService.getAllUsers();
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.findAllActive();
        model.addAttribute("blog", blog);
        model.addAttribute("users", users);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);

        return "admin/blogs_add_or_update";
    }


    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        blogService.deleteBlog(id);

        // Add a success message to the model
        redirectAttributes.addFlashAttribute("message", "Xoá bài viết thành công!");

        return "redirect:/admin/blogs_management";
    }

    @PostMapping("/upload-image")
    @CrossOrigin(origins = "http://localhost:8081")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile upload) throws IOException {
        if (upload.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.BAD_REQUEST);
        }


            String url = fileUploadService.uploadFile(upload);

            Map<String, Object> response = new HashMap<>();
            response.put("url", "/" + url);

            return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

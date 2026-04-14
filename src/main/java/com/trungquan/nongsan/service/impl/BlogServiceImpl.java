package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.dto.BlogSearchDTO;
import com.trungquan.nongsan.entity.Blog;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.repository.BlogRepository;
import com.trungquan.nongsan.service.BlogService;
import com.trungquan.nongsan.service.FileUploadService;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BlogServiceImpl implements BlogService {

    BlogRepository blogRepository;

    FileUploadService fileUploadService;

    UserService userService;

    ProductService productService;

    @Override
    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Override
    public Page<Blog> findAllPaginated(Pageable pageable){
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> findAllByKeywordPaginated(BlogSearchDTO search, Pageable pageable) {
        Long userId = search.getUserId();
        String keyword = search.getKeyword();
        if(userId != null && keyword != null){
            return blogRepository.findByUser_IdAndTitleContaining(userId, keyword, pageable);
        } else if (userId != null){
           return blogRepository.findByUser_Id(userId, pageable);
        } else if (keyword != null){
            return blogRepository.findByTitleContaining(keyword, pageable);
        }
        return blogRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void addBlog(Blog blog, MultipartFile coverImage, List<Long> selectedProducts) throws IOException {

        Blog savedBlog = blogRepository.save(blog);
        savedBlog.setThumbnail(fileUploadService.uploadFile(coverImage));

        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            Set<Product> products = new HashSet<>();
            for (Long productId : selectedProducts) {
                Product product = productService.getProductById(productId);
                if (product != null) {
                    products.add(product);
                }
            }
            savedBlog.setProducts(products);
        }

        blogRepository.save(savedBlog);

    }

    @Override
    @Transactional
    public void editBlog(Long blogId, Blog blog, MultipartFile thumbnail, List<Long> selectedProducts) throws IOException {
        Blog existedBlog = blogRepository.findById(blogId).orElse(null);
        if(existedBlog != null){
            existedBlog.setContent(blog.getContent());
            existedBlog.setTitle(blog.getTitle());
            existedBlog.setSummary(blog.getSummary());
            existedBlog.setContent(blog.getContent());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                existedBlog.setThumbnail(fileUploadService.uploadFile(thumbnail));
            }

            if (selectedProducts != null) {
                Set<Product> products = new HashSet<>();
                for (Long productId : selectedProducts) {
                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        products.add(product);
                    }
                }
                existedBlog.setProducts(products);
            }

            blogRepository.save(existedBlog);

        }
    }


    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    @Override
    public Blog getBlogByTitle(String title) {
        return blogRepository.findByTitle(title);
    }

    @Override
    public List<Blog> getTop6RecentBlog() {
        return blogRepository.findTop6ByOrderByCreatedAtDesc();
    }
}

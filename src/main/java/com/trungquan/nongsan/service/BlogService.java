package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Blog;
import com.trungquan.nongsan.dto.BlogSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BlogService {

    List<Blog> findAll();

    Page<Blog> findAllPaginated(Pageable pageable);

    Page<Blog> findAllByKeywordPaginated(BlogSearchDTO search, Pageable pageable);

    void addBlog(Blog blog, MultipartFile coverImage, List<Long> selectedProducts) throws IOException;

    void editBlog(Long blogId, Blog blog, MultipartFile coverImage, List<Long> selectedProducts) throws IOException;

    void deleteBlog(Long id);

    Blog getBlogById(Long id);

    Blog getBlogByTitle(String title);

    List<Blog> getTop6RecentBlog();
    
}

package com.trungquan.nongsan.repository;

import com.trungquan.nongsan.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    Page<Blog> findByTitleContaining(String keyword, Pageable pageable);

    Page<Blog> findByUser_IdAndTitleContaining(Long userId, String keyword, Pageable pageable);

    Page<Blog> findByUser_Id(Long userId, Pageable pageable);

    Blog findByTitle(String title);

    List<Blog> findTop6ByOrderByCreatedAtDesc();
}

package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.entity.Comment;
import com.trungquan.nongsan.dto.BlogSearchDTO;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.entity.Blog;
import com.trungquan.nongsan.service.BlogService;
import com.trungquan.nongsan.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/blogs")
@AllArgsConstructor
public class BlogController extends BaseController {

    private final BlogService blogService;
    private final CommentService commentService;

    @GetMapping
    public String getBlogPage(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, 6);
        Page<Blog> blogPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            BlogSearchDTO search = new BlogSearchDTO();
            search.setKeyword(keyword.trim());
            blogPage = blogService.findAllByKeywordPaginated(search, pageable);
        } else {
            blogPage = blogService.findAllPaginated(pageable);
        }

        model.addAttribute("blogList", blogPage);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("currentPage", blogPage.getNumber());
        model.addAttribute("top6RecentBlogList", blogService.getTop6RecentBlog());
        model.addAttribute("searchKeyword", keyword != null ? keyword : "");

        return "user/blog";
    }

    @GetMapping("/get/{blog_id}")
    public String viewBlogDetail(@PathVariable Long blog_id, Model model) {
        Blog blog = blogService.getBlogById(blog_id);
        if (blog == null) {
            return "redirect:/blogs";
        }
        List<Comment> commentList = commentService.findTopLevelCommentsWithReplies(blog_id);
        model.addAttribute("blog", blog);
        model.addAttribute("top6RecentBlogList", blogService.getTop6RecentBlog());
        model.addAttribute("comments", commentList);
        if (blog.getProducts() != null && !blog.getProducts().isEmpty()) {
            Map<String, List<Product>> grouped = blog.getProducts().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getCategory() != null ? p.getCategory().getName() : "Khác",
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
            model.addAttribute("relatedProductGroups", grouped);
        }
        return "user/blog_details";
    }

}

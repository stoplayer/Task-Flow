package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Comment;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Tasks;
import com.twd.SpringSecurityJWT.repository.CommentRepository;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private OurUserRepo ourUsersRepository;

    public Comment createComment(String content, Integer taskId, Integer userId) {
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        OurUsers user = ourUsersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = new Comment(content, task, user);
        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Integer id) {
        return commentRepository.findById(id).orElse(null);
    }

    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }
}
package com.springmvcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springmvcut.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}

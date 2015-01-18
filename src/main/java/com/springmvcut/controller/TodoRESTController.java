package com.springmvcut.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springmvcut.dto.TodoDTO;
import com.springmvcut.exception.TodoNotFoundException;
import com.springmvcut.model.Todo;
import com.springmvcut.service.TodoService;

@Controller
@SessionAttributes("todo")
public class TodoRESTController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TodoRESTController.class);

    protected static final String FEEDBACK_MESSAGE_KEY_TODO_ADDED = "feedback.message.todo.added";
    protected static final String FEEDBACK_MESSAGE_KEY_TODO_UPDATED = "feedback.message.todo.updated";
    protected static final String FEEDBACK_MESSAGE_KEY_TODO_DELETED = "feedback.message.todo.deleted";
    protected static final String FLASH_MESSAGE_KEY_FEEDBACK = "feedbackMessage";

    protected static final String MODEL_ATTRIBUTE_TODO = "todo";
    protected static final String MODEL_ATTRIBUTE_TODO_LIST = "todos";

    protected static final String PARAMETER_TODO_ID = "id";

    protected static final String REQUEST_MAPPING_TODO_LIST = "/";
    protected static final String REQUEST_MAPPING_TODO_VIEW = "/todo/{id}";

    protected static final String VIEW_TODO_ADD = "todo/add";
    protected static final String VIEW_TODO_LIST = "todo/list";
    protected static final String VIEW_TODO_UPDATE = "todo/update";
    protected static final String VIEW_TODO_VIEW = "todo/view";

    private final TodoService todoService;

    @Autowired
    public TodoRESTController(TodoService todoService) {
        this.todoService = todoService;
    }
    
    @RequestMapping(value = "/api/todo", method = RequestMethod.POST)
    @ResponseBody
    public TodoDTO add(@Valid @RequestBody TodoDTO dto) {
        LOGGER.debug("Adding a new to-do entry with information: {}", dto);

        Todo added = todoService.add(dto);
        LOGGER.debug("Added a to-do entry with information: {}", added);

       return createDTO(added);
    }

    @RequestMapping(value = "/api/todo/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public TodoDTO deleteById(@PathVariable("id") Long id) throws TodoNotFoundException {
        LOGGER.debug("Deleting a to-do entry with id: {}", id);

        Todo deleted = todoService.deleteById(id);
        LOGGER.debug("Deleted to-do entry with information: {}", deleted);

        return createDTO(deleted);
    }

    @RequestMapping(value = "/api/todo", method = RequestMethod.GET)
    @ResponseBody
    public List<TodoDTO> findAll() {
        LOGGER.debug("Finding all todo entries.");

        List<Todo> models = todoService.findAll();
        LOGGER.debug("Found {} to-do entries.", models.size());

        return createDTOs(models);
    }

    private List<TodoDTO> createDTOs(List<Todo> models) {
        List<TodoDTO> dtos = new ArrayList<TodoDTO>();

        for (Todo model: models) {
            dtos.add(createDTO(model));
        }

        return dtos;
    }

    @RequestMapping(value = "/api/todo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TodoDTO findById(@PathVariable("id") Long id) throws TodoNotFoundException {
        LOGGER.debug("Finding to-do entry with id: {}", id);

        Todo found = todoService.findById(id);
        LOGGER.debug("Found to-do entry with information: {}", found);

        return createDTO(found);
    }

    @RequestMapping(value = "/api/todo/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public TodoDTO update(@Valid @RequestBody TodoDTO dto, @PathVariable("id") Long todoId) throws TodoNotFoundException {
        LOGGER.debug("Updating a to-do entry with information: {}", dto);

        Todo updated = todoService.update(dto);
        LOGGER.debug("Updated the information of a to-entry to: {}", updated);

        return createDTO(updated);
    }

    private TodoDTO createDTO(Todo model) {
        TodoDTO dto = new TodoDTO();

        dto.setId(model.getId());
        dto.setDescription(model.getDescription());
        dto.setTitle(model.getTitle());

        return dto;
    }
 
}

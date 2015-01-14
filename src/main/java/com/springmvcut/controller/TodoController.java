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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springmvcut.dto.TodoDTO;
import com.springmvcut.exception.TodoNotFoundException;
import com.springmvcut.model.Todo;
import com.springmvcut.service.TodoService;

@Controller
@SessionAttributes("todo")
public class TodoController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TodoController.class);

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

    private final MessageSource messageSource;

    @Autowired
    public TodoController(MessageSource messageSource,TodoService todoService) {
        this.messageSource = messageSource;
        this.todoService = todoService;
    }

    @RequestMapping(value = "/todo/add", method = RequestMethod.GET)
    public String showAddTodoForm(Model model) {
        LOGGER.debug("Rendering add to-do entry form.");

        TodoDTO formObject = new TodoDTO();
        model.addAttribute(MODEL_ATTRIBUTE_TODO, formObject);

        return VIEW_TODO_ADD;
    }

    @RequestMapping(value = "/todo/add", method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute(MODEL_ATTRIBUTE_TODO) TodoDTO dto, BindingResult result, RedirectAttributes attributes) {
        LOGGER.debug("Adding a new to-do entry with information: {}", dto);

        if (result.hasErrors()) {
            LOGGER.debug("Add to-do form was submitted with binding errors. Rendering form view.");
            return VIEW_TODO_ADD;
        }

        Todo added = todoService.add(dto);
        LOGGER.debug("Added a to-do entry with information: {}", added);

        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_TODO_ADDED, added.getTitle());
        attributes.addAttribute(PARAMETER_TODO_ID, added.getId());

        return createRedirectViewPath(REQUEST_MAPPING_TODO_VIEW);
    }

    @RequestMapping(value = "/todo/delete/{id}", method = RequestMethod.GET)
    public String deleteById(@PathVariable("id") Long id, RedirectAttributes attributes) throws TodoNotFoundException {
        LOGGER.debug("Deleting a to-do entry with id: {}", id);

        Todo deleted = todoService.deleteById(id);
        LOGGER.debug("Deleted to-do entry with information: {}", deleted);

        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_TODO_DELETED, deleted.getTitle());

        return createRedirectViewPath(REQUEST_MAPPING_TODO_LIST);
    }

    @RequestMapping(value = REQUEST_MAPPING_TODO_LIST, method = RequestMethod.GET)
    public String findAll(Model model) {
        LOGGER.debug("Rendering to-do list.");

        List<Todo> models = todoService.findAll();
        LOGGER.debug("Found {} to-do entries.", models.size());

        model.addAttribute(MODEL_ATTRIBUTE_TODO_LIST, models);

        return VIEW_TODO_LIST;
    }

    @RequestMapping(value = REQUEST_MAPPING_TODO_VIEW, method = RequestMethod.GET)
    public String findById(@PathVariable("id") Long id, Model model) throws TodoNotFoundException {
        LOGGER.debug("Rendering to-do page for to-do entry with id: {}", id);

        Todo found = todoService.findById(id);
        LOGGER.debug("Found to-do entry with information: {}", found);

        model.addAttribute(MODEL_ATTRIBUTE_TODO, found);

        return VIEW_TODO_VIEW;
    }

    @RequestMapping(value = "/todo/update/{id}", method = RequestMethod.GET)
    public String showUpdateTodoForm(@PathVariable("id") Long id, Model model) throws TodoNotFoundException {
        LOGGER.debug("Rendering update to-do entry form for to-do entry with id: {}", id);

        Todo updated = todoService.findById(id);
        LOGGER.debug("Rendering update to-do form for to-do with information: {}", updated);

        TodoDTO formObject = constructFormObjectForUpdateForm(updated);
        model.addAttribute(MODEL_ATTRIBUTE_TODO, formObject);

        return VIEW_TODO_UPDATE;
    }

    @RequestMapping(value = "/todo/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute(MODEL_ATTRIBUTE_TODO) TodoDTO dto, BindingResult result, RedirectAttributes attributes) throws TodoNotFoundException {
        LOGGER.debug("Updating a to-do entry with information: {}", dto);

        if (result.hasErrors()) {
            LOGGER.debug("Update to-do entry form was submitted with validation errors. Rendering form view.");
            return VIEW_TODO_UPDATE;
        }

        Todo updated = todoService.update(dto);
        LOGGER.debug("Updated the information of a to-entry to: {}", updated);

        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_TODO_UPDATED, updated.getTitle());
        attributes.addAttribute(PARAMETER_TODO_ID, updated.getId());

        return createRedirectViewPath(REQUEST_MAPPING_TODO_VIEW);
    }

    private TodoDTO constructFormObjectForUpdateForm(Todo updated) {
        TodoDTO dto = new TodoDTO();

        dto.setId(updated.getId());
        dto.setDescription(updated.getDescription());
        dto.setTitle(updated.getTitle());

        return dto;
    }

    private void addFeedbackMessage(RedirectAttributes attributes, String messageCode, Object... messageParameters) {
        LOGGER.debug("Adding feedback message with code: {} and params: {}", messageCode, messageParameters);
        String localizedFeedbackMessage = getMessage(messageCode, messageParameters);
        LOGGER.debug("Localized message is: {}", localizedFeedbackMessage);
        attributes.addFlashAttribute(FLASH_MESSAGE_KEY_FEEDBACK, localizedFeedbackMessage);
    }

    private String getMessage(String messageCode, Object... messageParameters) {
        Locale current = LocaleContextHolder.getLocale();
        LOGGER.debug("Current locale is {}", current);
        return messageSource.getMessage(messageCode, messageParameters, current);
    }


    private String createRedirectViewPath(String requestMapping) {
        StringBuilder redirectViewPath = new StringBuilder();
        redirectViewPath.append("redirect:");
        redirectViewPath.append(requestMapping);
        return redirectViewPath.toString();
    }
}

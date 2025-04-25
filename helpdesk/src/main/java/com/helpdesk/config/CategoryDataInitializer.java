package com.helpdesk.config;

import com.helpdesk.model.Category;
import com.helpdesk.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if categories already exist
        if (categoryRepository.count() == 0) {
            // Create default categories
            createCategory("Technical Support", "Questions related to technical issues and troubleshooting");
            createCategory("Software Development", "Discussions about programming, coding, and software development");
            createCategory("Hardware", "Questions about computer hardware, peripherals, and components");
            createCategory("Networking", "Topics related to network configuration, protocols, and troubleshooting");
            createCategory("Security", "Discussions about cybersecurity, data protection, and privacy");
            createCategory("General", "General questions and discussions that don't fit in other categories");
            
            System.out.println("Default categories have been created");
        }
    }

    private void createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        categoryRepository.save(category);
    }
}
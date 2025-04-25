# HelpDesk Management System

A comprehensive help desk and knowledge base platform built with Spring Boot, allowing users to create posts, comment, vote, and collaborate on technical issues.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Admin Features](#admin-features)
- [Contributing](#contributing)
- [License](#license)

## Overview

The HelpDesk Management System is a web application designed to facilitate knowledge sharing and problem-solving within an organization or community. Users can post questions, share solutions, comment on posts, and vote for helpful content. The system includes user authentication, content moderation, and comprehensive admin features.

## Features

### User Management
- User registration and authentication
- Profile management
- Role-based access control (User, Moderator, Admin)
- Account suspension/banning for policy violations

### Content Management
- Create, edit, and delete posts
- Categorize posts by topic
- Tag posts for better searchability
- Rich text formatting for posts and comments

### Interaction
- Comment on posts
- Nested replies to comments
- Upvote/downvote posts and comments
- Report inappropriate content

### Admin Features
- User management (view, edit, suspend, ban)
- Content moderation
- Category management
- Report handling
- System statistics and analytics

### Search and Discovery
- Full-text search across posts and comments
- Filter by category, tag, or author
- Sort by relevance, date, or popularity

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.4.4
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: MySQL 8
- **Security**: Spring Security
- **Build Tool**: Maven
- **Dependencies**:
  - Spring Web
  - Spring Data JPA
  - MySQL Driver
  - Spring Security
  - Thymeleaf
  - Validation
  - Lombok

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git (optional, for version control)

## Setup and Installation

### Database Setup

1. Install MySQL if not already installed
2. Create a database named `helpdesk` (or update the application.properties file with your preferred database name)

### Initial Admin Setup
On first run, the system creates a default admin account:

- Username: admin
- Password: admin123

### Endpoints
- /api/posts - Post management
- /api/comments - Comment management
- /api/users - User management
- /api/categories - Category management
- /api/votes - Voting functionality
- /api/reports - Content reporting

## Admin Features
### User Management
Admins can:
- View all users
- Edit user details
- Suspend or ban users
- Assign roles

### Content Moderation
Admins can:
- View all posts and comments
- Edit or delete any content
- Hide inappropriate content
- Handle reported content

### System Management
Admins can:
- Create and manage categories
- View system statistics
- Monitor user activity


### Endpoints

- `/api/posts` - Post management
- `/api/comments` - Comment management
- `/api/users` - User management
- `/api/categories` - Category management
- `/api/votes` - Voting functionality
- `/api/reports` - Content reporting

### Admin APIs

- `/api/admin/users` - Admin user management
  - `GET /api/admin/users` - Get all users
  - `GET /api/admin/users/{id}` - Get user by ID
  - `PUT /api/admin/users/{id}` - Update user
  - `DELETE /api/admin/users/{id}` - Delete user
  - `POST /api/admin/users/{id}/suspend` - Suspend user
  - `POST /api/admin/users/{id}/unsuspend` - Unsuspend user
  - `POST /api/admin/users/{id}/ban` - Ban user
  - `POST /api/admin/users/{id}/unban` - Unban user

- `/api/admin/posts` - Admin post management
  - `GET /api/admin/posts` - Get all posts
  - `GET /api/admin/posts/{id}` - Get post by ID
  - `PUT /api/admin/posts/{id}` - Update post
  - `DELETE /api/admin/posts/{id}` - Delete post
  - `POST /api/admin/posts/{id}/hide` - Hide post
  - `POST /api/admin/posts/{id}/unhide` - Unhide post

- `/api/admin/comments` - Admin comment management
  - `GET /api/admin/comments` - Get all comments
  - `GET /api/admin/comments/{id}` - Get comment by ID
  - `PUT /api/admin/comments/{id}` - Update comment
  - `DELETE /api/admin/comments/{id}` - Delete comment
  - `POST /api/admin/comments/{id}/hide` - Hide comment
  - `POST /api/admin/comments/{id}/unhide` - Unhide comment

- `/api/admin/categories` - Admin category management
  - `GET /api/admin/categories` - Get all categories
  - `POST /api/admin/categories` - Create category
  - `PUT /api/admin/categories/{id}` - Update category
  - `DELETE /api/admin/categories/{id}` - Delete category

- `/api/admin/reports` - Admin report management
  - `GET /api/admin/reports` - Get all reports
  - `GET /api/admin/reports/{id}` - Get report by ID
  - `POST /api/admin/reports/{id}/resolve` - Resolve report
  - `DELETE /api/admin/reports/{id}` - Delete report

- `/api/admin/stats` - System statistics
  - `GET /api/admin/stats` - Get system statistics
  - `GET /api/admin/stats/users` - Get user statistics
  - `GET /api/admin/stats/posts` - Get post statistics
  - `GET /api/admin/stats/comments` - Get comment statistics
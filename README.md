# Introduction

Hi! Welcome to my final year project. In this project, I aimed to enhance the chess experience by creating a new website! This website allows users to find chess puzzles using natural language. You can visit the website on [https://chesspuzzletext2sql.com](https://chesspuzzletext2sql.com)

# Student Info

| Student Name (LAST NAME, First Name) | CABALLERO, Rakichaz Calimbahin |
| Student ID                           | 57140124                       |
| University                           | City University of Hong Kong   |
| Course Code                          | CS4514                         |

# Additional Documentation

<h1>
    This README.md does not contain the full documentation.
</h1>
<h2>
    Visit the project's [Github Wiki](https://github.com/M0bilizer/Chess-Puzzle-Text2SQL/wiki) for api documentation, and setting up your own chesspuzzletext2sql.com.
</h2>

# Gist of this project

To let user find chess puzzle using natural language, we're going to use Text2SQL!

```
┌───────────────────────────────┐             ┌─────────────────────────────────────────┐
│ I want a Dutch Defense Puzzle │────────────▶│SELECT * FROM t_puzzle                   │
└───────────────────────────────┘             │WHERE opening_tags LIKE '%Dutch_Defense%'│
                                              └─────────────────────────────────────────┘
```

Main Idea:
- Search Engines excels at using natural language to find unstructured data but struggle with precision when searching for structured data.
- Database Engines are really good at finding precise structured data, but it's not very user friendly.
**Text2SQL** can act as the glue between them.

# The Text2SQL process

Text2SQL isn't always accurate, sometimes it makes mistake so we'll need a detailed Text2SQL process.

# High Level System Architecture

```
                                                           ┌──────────────┐    
                                                           │              │    
                                                          /│ Microservice │    
                                                        // │              │    
                                                       /   └──────────────┘    
                                                     //                        
                                                    /                          
               ┌────────────┐      ┌───────────────┐       ┌──────────────────┐
┌───────┐      │            │      │               │       │                  │
│ Users │──────│  Frontend  │──────│   Backend     │───────│ External LLM API │
└───────┘      │            │      │               │       │                  │
               └────────────┘      └───────────────┘       └──────────────────┘
                                                    \                          
                                                     \\                        
                                                       \   ┌──────────┐        
                                                        \\ │          │        
                                                          \│ Database │        
                                                           │          │        
                                                           └──────────┘        
```


- **Frontend**: The website interface where users can view and play chess puzzles.
- **Backend**: Manages Text2SQL process and executes SQL queries on the database. It also orchestrates the microservice and external LLM API.
- **Microservice**: Supports the Text2SQL functionality by finding similar demonstrations.
- **External LLM API**: Handles the inference tasks.
- **Database**: Stores all chess puzzle data. Many thanks to lichess.org for providing their open database.

# File Structure

```
Chess-Puzzle-Text2SQL/
├─ backend/                   # Backend Project
├─ frontend/                  # Frontend Project
├─ microservice/              # Microservice Project
├─ misc/                      # Contains the benchmark result
├─ ngnix/                     # Ngnix Project
├─ scripts                    # Contains Scripts that may be useful
├─ README.md                  # README.md
└─ docker-compose-sample.yml  # Sample Docker-compose to set up your own containers
```

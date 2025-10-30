.PHONY: help build up down clean logs restart rebuild

# Default target
.DEFAULT_GOAL := help

# Variables
DOCKER_COMPOSE = docker-compose -f docker/docker-compose.yaml --env-file .env
PROJECT_NAME = isu-backend-pushnoti

help: ## Show this help message
	@echo "ISU-Backend-Pushnoti - Available Commands"
	@echo "=========================================="
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'
	@echo ""

build: ## Build Docker images
	@echo "Building Docker images..."
	$(DOCKER_COMPOSE) build
	@echo "Build completed!"

up: ## Start all services in detached mode
	@echo "Starting services..."
	$(DOCKER_COMPOSE) up -d
	@echo ""
	@echo "Services started successfully!"

down: ## Stop all services
	@echo "Stopping services..."
	$(DOCKER_COMPOSE) down
	@echo "Services stopped!"

clean: ## Stop services and remove volumes (clean all data)
	@echo "Stopping services and removing volumes..."
	$(DOCKER_COMPOSE) down -v
	@echo "All services stopped and data cleaned!"

rebuild_up: ## Rebuild and restart all services
	@$(MAKE) down
	@$(MAKE) build
	@$(MAKE) up

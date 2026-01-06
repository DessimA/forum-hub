#!/bin/bash
echo "Cleaning up ForumHub Docker environment..."

# Parar e remover todos os containers do projeto
docker-compose down -v --remove-orphans

# Remover imagens do projeto
docker rmi forum-hub-backend forum-hub-frontend infra-mysql 2>/dev/null || true

# Remover volumes não utilizados (especialmente os de teste)
docker volume prune -f >/dev/null 2>&1

echo "Cleanup complete."

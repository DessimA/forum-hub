#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"
COMPOSE_FILE="$PROJECT_ROOT/infra/docker-compose.yml"
TEST_COMPOSE_FILE="$PROJECT_ROOT/infra/docker-compose.test.yml"

if docker compose version >/dev/null 2>&1; then
    DOCKER_CMD="docker compose"
else
    DOCKER_CMD="docker-compose"
fi

header() {
    echo -e "${BLUE}=======================================${NC}"
    echo -e "${BLUE}    FórumHub Test Runner (DOCKER)      ${NC}"
    echo -e "${BLUE}=======================================${NC}"
}

clear
header
echo -e "${YELLOW}Preparando ambiente de testes no Docker...${NC}"

# Backend Tests (H2)
echo -e "\n${BLUE}>> Executando testes do BACKEND no Docker (JUnit + H2)...${NC}"
$DOCKER_CMD -f "$COMPOSE_FILE" -f "$TEST_COMPOSE_FILE" run --build --rm backend-test mvn test
BACKEND_EXIT=$?

# Frontend Tests
echo -e "\n${BLUE}>> Executando testes do FRONTEND no Docker (Vitest)...${NC}"
$DOCKER_CMD -f "$COMPOSE_FILE" -f "$TEST_COMPOSE_FILE" run --build --rm frontend-test npm run test:unit -- --run
FRONTEND_EXIT=$?

echo -e "\n${BLUE}=======================================${NC}"
echo -e "${BLUE}Relatório Final de Testes (Docker Execution):${NC}"
if [ $BACKEND_EXIT -eq 0 ]; then
    echo -e "${GREEN}[OK] Backend: Testes passaram!${NC}"
else
    echo -e "${RED}[FAIL] Backend: Falha nos testes!${NC}"
fi

if [ $FRONTEND_EXIT -eq 0 ]; then
    echo -e "${GREEN}[OK] Frontend: Testes passaram!${NC}"
else
    echo -e "${RED}[FAIL] Frontend: Falha nos testes!${NC}"
fi
echo -e "${BLUE}=======================================${NC}"

echo -e "\nPressione qualquer tecla para voltar ao menu..."
read -n 1

if [ $BACKEND_EXIT -eq 0 ] && [ $FRONTEND_EXIT -eq 0 ]; then
    exit 0
else
    exit 1
fi

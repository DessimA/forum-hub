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
LOG_DIR="$SCRIPT_DIR/logs"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/test_execution_$TIMESTAMP.log"

mkdir -p "$LOG_DIR"

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

# Redirecionar toda a saída para o terminal e para o arquivo de log
exec > >(tee -a "$LOG_FILE") 2>&1

clear
header
echo -e "${YELLOW}Iniciando execução em: $(date)${NC}"
echo -e "${YELLOW}Preparando ambiente de testes no Docker...${NC}"

ENV_FILE="$PROJECT_ROOT/.env"
if [ ! -f "$ENV_FILE" ]; then
    cp "$PROJECT_ROOT/.env.example" "$ENV_FILE"
fi

echo -e "${YELLOW}Subindo serviço Redis para testes unitários...${NC}"
$DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d redis

# 1. Backend Unit Tests
echo -e "\n${BLUE}>> Executando testes unitários do BACKEND...${NC}"
$DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" -f "$TEST_COMPOSE_FILE" run --build --rm backend-test mvn -B -ntp -q test
BACKEND_EXIT=$?

if [ $BACKEND_EXIT -ne 0 ]; then
    echo -e "\n${RED}![ERRO] Testes de BACKEND falharam. Abortando execução. ${NC}"
    echo -e "${BLUE}Relatório Final:${NC}"
    echo -e "${RED}[FAIL] Backend Unit${NC}"
    echo -e "${YELLOW}[SKIP] Frontend Unit (não executado)${NC}"
    echo -e "${YELLOW}[SKIP] Cypress E2E (não executado)${NC}"
    echo -e "${BLUE}Log salvo em: $LOG_FILE${NC}"
    $DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" stop redis
    exit 1
fi

# 2. Frontend Unit Tests
echo -e "\n${BLUE}>> Executando testes unitários do FRONTEND...${NC}"
$DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" -f "$TEST_COMPOSE_FILE" run --build --rm frontend-test npm run test:unit -- --run --reporter=dot --silent
FRONTEND_EXIT=$?

if [ $FRONTEND_EXIT -ne 0 ]; then
    echo -e "\n${RED}![ERRO] Testes de FRONTEND falharam. Abortando execução. ${NC}"
    echo -e "${BLUE}Relatório Final:${NC}"
    echo -e "${GREEN}[OK] Backend Unit${NC}"
    echo -e "${RED}[FAIL] Frontend Unit${NC}"
    echo -e "${YELLOW}[SKIP] Cypress E2E (não executado)${NC}"
    echo -e "${BLUE}Log salvo em: $LOG_FILE${NC}"
    $DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" stop redis
    exit 1
fi

# 3. Cypress E2E Tests
echo -e "\n${BLUE}>> Preparando ambiente para testes E2E (Cypress)...${NC}"
$DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d --build --force-recreate

echo -e "\n${BLUE}>> Executando testes E2E com CYPRESS...${NC}"
$DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" -f "$TEST_COMPOSE_FILE" run --rm cypress
CYPRESS_EXIT=$?

$DOCKER_CMD --env-file "$ENV_FILE" -f "$COMPOSE_FILE" stop

echo -e "\n${BLUE}=======================================${NC}"
echo -e "${BLUE}Relatório Final de Testes:${NC}"
echo -e "${GREEN}[OK] Backend Unit${NC}"
echo -e "${GREEN}[OK] Frontend Unit${NC}"
if [ $CYPRESS_EXIT -eq 0 ]; then
    echo -e "${GREEN}[OK] Cypress E2E${NC}"
else
    echo -e "${RED}[FAIL] Cypress E2E${NC}"
fi
echo -e "${BLUE}Log salvo em: $LOG_FILE${NC}"
echo -e "${BLUE}=======================================${NC}"

if [ $CYPRESS_EXIT -eq 0 ]; then
    exit 0
else
    exit 1
fi
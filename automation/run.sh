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
CURRENT_PROFILE="dev"

if docker compose version >/dev/null 2>&1; then
    DOCKER_CMD="docker compose"
else
    DOCKER_CMD="docker-compose"
fi

header() {
    clear
    echo -e "${BLUE}=======================================${NC}"
    echo -e "${BLUE}       FórumHub Docker Manager        ${NC}"
    echo -e "${BLUE}=======================================${NC}"
    echo -e "Project Root: $PROJECT_ROOT"
    echo -e "Profile Ativo: ${YELLOW}$CURRENT_PROFILE${NC}"
    echo ""
}

execute_docker() {
    local service=$1
    local mode=$2
    local profile_flag=""
    
    if [ "$CURRENT_PROFILE" == "prod" ]; then
        profile_flag="--profile prod"
    else
        profile_flag="--profile full"
    fi

    cd "$PROJECT_ROOT"

    case $mode in
        1) # Clean Build
            echo -e "${YELLOW}Executando Limpeza Total e Reconstrução ($CURRENT_PROFILE)...${NC}"
            $DOCKER_CMD -f "$COMPOSE_FILE" $profile_flag down -v
            $DOCKER_CMD -f "$COMPOSE_FILE" $profile_flag build --no-cache $service
            SPRING_PROFILES_ACTIVE=$CURRENT_PROFILE $DOCKER_CMD -f "$COMPOSE_FILE" $profile_flag up -d $service
            ;;
        2) # Rebuild
            echo -e "${YELLOW}Reconstruindo e Iniciando ($CURRENT_PROFILE)...${NC}"
            SPRING_PROFILES_ACTIVE=$CURRENT_PROFILE $DOCKER_CMD -f "$COMPOSE_FILE" $profile_flag up -d --build $service
            ;;
        3) # Start
            echo -e "${YELLOW}Iniciando containers ($CURRENT_PROFILE)...${NC}"
            SPRING_PROFILES_ACTIVE=$CURRENT_PROFILE $DOCKER_CMD -f "$COMPOSE_FILE" $profile_flag up -d $service
            ;;
    esac
    
    echo -e "\n${GREEN}Ação concluída para: ${service:-Todos os serviços}${NC}"
    echo -e "Pressione qualquer tecla para voltar ao menu..."
    read -n 1
}

select_profile() {
    header
    echo "1. Development (Default)"
    echo "2. Test (H2/Mock)"
    echo "3. Production"
    echo "0. Voltar"
    echo -n "Selecione o profile: "
    read p_choice
    case $p_choice in
        1) CURRENT_PROFILE="dev" ;;
        2) CURRENT_PROFILE="test" ;;
        3) CURRENT_PROFILE="prod" ;;
    esac
}

select_mode() {
    local service=$1
    header
    echo -e "${BLUE}Serviço Selecionado: ${service:-Full Stack}${NC}"
    echo "1. Clean Build (Limpa volumes, reconstrói sem cache e inicia)"
    echo "2. Rebuild (Reconstrói e inicia)"
    echo "3. Quick Start (Apenas inicia)"
    echo "0. Voltar"
    echo -n "Escolha o modo de execução: "
    read mode
    
    if [ "$mode" != "0" ]; then
        execute_docker "$service" "$mode"
    fi
}

while true; do
    header
    echo "1. Selecionar Profile (Atual: $CURRENT_PROFILE)"
    echo "2. Executar Backend (inclui MySQL)"
    echo "3. Executar Frontend (inclui Backend e MySQL)"
    echo "4. Executar Ambos (Full Stack)"
    echo "5. Executar Testes Completos (Docker)"
    echo "6. Parar e Remover Tudo (Limpeza Total)"
    echo "0. Sair"
    echo -n "Selecione a opção: "
    read target

    case $target in
        1) select_profile ;;
        2) select_mode "backend" ;; 
        3) select_mode "frontend" ;; 
        4) select_mode "" ;; 
        5) "$SCRIPT_DIR/test.sh" ;;
        6) 
            echo -e "${RED}Parando TODOS os serviços e removendo recursos...${NC}"
            
            # 1. Tenta parar tudo via docker compose (incluindo testes e orfãos)
            # Adiciona -v para remover volumes (limpeza completa de dados)
            $DOCKER_CMD -f "$COMPOSE_FILE" -f "$TEST_COMPOSE_FILE" down -v --remove-orphans
            
            # 2. Força bruta: Remove containers pelo nome explícito caso o compose falhe em rastreá-los
            # Isso garante que a rede seja liberada
            echo -e "${YELLOW}Garantindo remoção de containers remanescentes...${NC}"
            docker rm -f forumhub-backend forumhub-frontend forumhub-mysql forumhub-backend-test forumhub-frontend-test >/dev/null 2>&1
            
            echo -e "${GREEN}Limpeza concluída!${NC}"
            echo "Pressione qualquer tecla..."
            read -n 1
            ;; 
        0) exit 0 ;; 
        *) echo -e "${RED}Opção inválida${NC}"; sleep 1 ;; 
    esac
done
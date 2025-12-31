# 09 - Padrões de Integração Backend/Frontend

Este documento descreve como o Frontend consome de forma plena os recursos avançados do Backend para entregar uma UX superior.

## 🔗 Consumo Inteligente de APIs

### 1. Filtragem Dinâmica (Implementado)
O Frontend consome o endpoint `GET /topics` passando parâmetros de filtragem complexos (`courseId`, `status`, `year`, `title`).
- **Lógica:** A Store centraliza esses parâmetros, permitindo que a paginação funcione de forma "reagente" aos filtros aplicados na barra de busca.

### 2. Sincronização de Estado de Bloqueio (Implementado)
As respostas de detalhe do tópico (`TopicDetailResponseDTO`) incluem o status atualizado do autor (`authorIsBlocked`).
- **Efeito UX:** Se o autor estiver punido, a interface exibe um indicador visual imediato e oferece ao moderador o botão de "Remover Punição" diretamente na tela de leitura.

### 3. Moderação com Remoção Contextual (Implementado)
O sistema de bloqueio foi integrado à estrutura de dados do fórum.
- **Fluxo:** Ao aplicar uma punição através de um tópico, o ID do recurso é enviado no `BlockRequestDTO`, disparando a exclusão automática e o backup do conteúdo ofensivo no Backend.

### 4. Ciclo de Vida Reativo (Implementado)
A interface reage ao status do tópico (`OPEN`, `CLOSED`, `RESOLVED`).
- **Bloqueio de Escrita:** O campo de resposta é automaticamente substituído por um alerta informativo quando o tópico não aceita mais interações, prevenindo erros de submissão antes mesmo que o usuário tente enviar.
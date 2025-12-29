Write-Host "Starting final deep cleaning..." -ForegroundColor Cyan

$obsolete_paths = @(
    "backend/src/main/java/com/forumhub/modules/usuario",
    "backend/src/main/java/com/forumhub/modules/topico",
    "backend/src/main/java/com/forumhub/modules/resposta",
    "backend/src/main/java/com/forumhub/modules/curso",
    "backend/src/main/java/com/forumhub/modules/hello",
    "backend/src/main/java/com/forumhub/security/AutenticacaoService.java",
    "backend/src/test/java/com/forumhub/modules/topico",
    "backend/src/test/java/com/forumhub/modules/curso",
    "backend/src/test/java/com/forumhub/modules/hello",
    "frontend/src/views/usuario",
    "frontend/src/views/topico",
    "frontend/src/stores/topico.ts",
    "backend/target",
    "frontend/dist"
)

foreach ($p in $obsolete_paths) {
    if (Test-Path $p) {
        Remove-Item -Recurse -Force $p
        Write-Host "Removed: $p" -ForegroundColor Yellow
    }
}

if (Test-Path "diagrama_banco_de_dados_forumhub.png") {
    Rename-Item "diagrama_banco_de_dados_forumhub.png" "database_diagram_forumhub.png" -ErrorAction SilentlyContinue
}

Write-Host "Cleanup completed!" -ForegroundColor Green

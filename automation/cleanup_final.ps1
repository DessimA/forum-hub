# Cleanup script to resolve Flyway and other environment issues
$migrationDir = "backend/src/main/resources/db/migration"

Write-Host "Cleaning up duplicate Flyway migrations..." -ForegroundColor Yellow

# We only want V1, V2__insert-admin-user.sql, V3, V4, V5
$filesToDelete = @(
    "V2__insert-initial-data.sql",
    "V2_1__insert-initial-data.sql"
)

foreach ($file in $filesToDelete) {
    $path = Join-Path $migrationDir $file
    if (Test-Path $path) {
        Remove-Item $path -Force -ErrorAction SilentlyContinue
        Write-Host "Deleted: $file" -ForegroundColor Green
    }
}

Write-Host "Cleanup complete." -ForegroundColor Cyan

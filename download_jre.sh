#!/bin/bash
# Script opcional para baixar JRE manualmente se precisar

echo "Baixando JRE para PojavLauncher..."

# Criar diretório
mkdir -p app_pojavlauncher/src/main/assets/components/jre

# Baixar JRE de release oficial (exemplo - ajuste URL conforme necessário)
# wget -O jre8.zip https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/releases/download/latest/jre8-arm64.zip

echo "JRE baixado em: app_pojavlauncher/src/main/assets/components/jre/"
echo "Agora rode: ./gradlew assembleDebug"

# Correção do JRE - Usando Adoptium

## Problema Anterior
O workflow tentava baixar JRE de:
```
PojavLauncherTeam/android-openjdk-build-multiarch
```
Esse repositório não tem artifacts públicos, causando:
```
Error: no matching workflow run found with any artifacts
```

## Solução: Adoptium (Eclipse Temurin)
Agora baixa JREs do **Adoptium** (projeto oficial da Eclipse Foundation):
- **JRE 8** → Para Minecraft 1.16 e anteriores
- **JRE 17** → Para Minecraft 1.17-1.20.4
- **JRE 21** → Para Minecraft 1.20.5+

### Por que Adoptium?
✅ **Público e gratuito** - Sem necessidade de autenticação
✅ **OpenJDK oficial** - Mantido pela Eclipse Foundation
✅ **ARM64 Linux** - Compatível com Android
✅ **Estável** - Releases versionados e confiáveis

## URLs utilizadas
```
JRE 8:  https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u392-b08/OpenJDK8U-jre_aarch64_linux_hotspot_8u392b08.tar.gz
JRE 17: https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9%2B9/OpenJDK17U-jre_aarch64_linux_hotspot_17.0.9_9.tar.gz
JRE 21: https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.1%2B12/OpenJDK21U-jre_aarch64_linux_hotspot_21.0.1_12.tar.gz
```

## Tamanho do APK
Com os 3 JREs incluídos, o APK ficará maior (~300-400MB), mas será **totalmente funcional** sem precisar baixar nada extra.

## Como usar
1. Execute este script para aplicar a correção
2. Aguarde o build no GitHub Actions (~10-15 min por causa dos downloads)
3. Baixe o APK da Release
4. Instale e jogue - **não precisa baixar JRE dentro do app**

## Se as URLs do Adoptium mudarem
Atualize as URLs no workflow `.github/workflows/build.yml` para as versões mais recentes em:
https://adoptium.net/temurin/releases/

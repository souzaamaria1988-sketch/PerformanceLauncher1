#!/bin/bash
# Teste local do download de JREs

echo "Testando download de JREs do Adoptium..."

# JRE 8
echo "=== JRE 8 ==="
mkdir -p test/jre8
cd test/jre8
wget -q --spider https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u392-b08/OpenJDK8U-jre_aarch64_linux_hotspot_8u392b08.tar.gz
if [ $? -eq 0 ]; then
  echo "✓ JRE 8 URL válida"
else
  echo "✗ JRE 8 URL inválida"
fi
cd ../..

# JRE 17
echo "=== JRE 17 ==="
mkdir -p test/jre17
cd test/jre17
wget -q --spider https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9%2B9/OpenJDK17U-jre_aarch64_linux_hotspot_17.0.9_9.tar.gz
if [ $? -eq 0 ]; then
  echo "✓ JRE 17 URL válida"
else
  echo "✗ JRE 17 URL inválida"
fi
cd ../..

# JRE 21
echo "=== JRE 21 ==="
mkdir -p test/jre21
cd test/jre21
wget -q --spider https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.1%2B12/OpenJDK21U-jre_aarch64_linux_hotspot_21.0.1_12.tar.gz
if [ $? -eq 0 ]; then
  echo "✓ JRE 21 URL válida"
else
  echo "✗ JRE 21 URL inválida"
fi
cd ../..

echo "Teste completo!"

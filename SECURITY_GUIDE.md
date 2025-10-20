```bash
# Buscar en el historial de Git si application.properties fue commiteado
git log --all --full-history -- "**/application.properties"

# Ver si hay commits con el archivo
git log --all --oneline | findstr "application.properties"
```

**Si encuentras commits:**
```bash
# Ver el contenido del archivo en un commit específico
git show <commit-hash>:src/main/resources/application.properties
```

---

### **PASO 2: Si YA subiste las contraseñas a Git (CÓDIGO ROJO)** 🔴

#### **A. Eliminar archivo del historial completo:**
```bash
# Navegar al directorio del proyecto
cd D:\CopiaF\AnalisisYDesarrolloDeSoftware\2025sena\ProyectoFinalClinVet\gestion-citas

# Eliminar application.properties de TODO el historial
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch src/main/resources/application.properties" --prune-empty --tag-name-filter cat -- --all

# O usa git-filter-repo (más rápido y seguro):
# git filter-repo --path src/main/resources/application.properties --invert-paths
```

#### **B. Cambiar INMEDIATAMENTE:**
1. **Contraseña de MySQL:**
   ```sql
   -- Conectar a MySQL
   mysql -u root -p

   -- Cambiar contraseña
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'NuevaContraseñaSegura2025!';
   FLUSH PRIVILEGES;
   ```

2. **JWT Secret:**
   ```bash
   # Generar nuevo secret (32+ caracteres aleatorios)
   # Puedes usar este script en PowerShell:
   -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 40 | % {[char]$_})
   ```

#### **C. Forzar push al repositorio:**
```bash
git push origin --force --all
git push origin --force --tags
```

#### **D. Notificar al equipo:**
> "He limpiado credenciales del historial de Git. Todos deben hacer:
> ```
> git fetch origin
> git reset --hard origin/main
> ```
> Y reconfigurar su `application.properties` local."

---

### **PASO 3: Si NO has subido nada a Git todavía (CÓDIGO AMARILLO)** 🟡

**¡TIENES SUERTE!** Solo haz esto:

```bash
cd D:\CopiaF\AnalisisYDesarrolloDeSoftware\2025sena\ProyectoFinalClinVet\gestion-citas

# Asegurar que application.properties NO se agregue
git rm --cached src/main/resources/application.properties

# Agregar el .gitignore
git add .gitignore
git add src/main/resources/application.properties.example

# Commit de seguridad
git commit -m "feat: Agregar .gitignore y proteger credenciales"
```

---

### **PASO 4: Configurar variables de entorno (RECOMENDADO)**

En vez de usar `application.properties` con contraseñas, usa variables de entorno:

#### **Crear archivo `.env` (gitignoreado):**
```bash
# Crear en raíz del proyecto:
# D:\CopiaF\...\gestion-citas\.env

DB_PASSWORD=TuPasswordReal2025
JWT_SECRET=tu_secret_jwt_muy_largo_y_seguro_minimo_32_caracteres
```

#### **Modificar `application.properties` para usar variables:**
```properties
spring.datasource.password=${DB_PASSWORD:pontuclaveaqui}
jwt.secret=${JWT_SECRET:clavesecretamuyseguraylargacon32caracteres123}
```

#### **Configurar en IntelliJ IDEA:**
1. Run → Edit Configurations
2. Environment variables: `DB_PASSWORD=...; JWT_SECRET=...`
3. Apply → OK

#### **Configurar en Windows (permanente):**
```cmd
setx DB_PASSWORD "TuPasswordReal2025"
setx JWT_SECRET "tu_secret_jwt_muy_largo"
```

---

## 📋 CHECKLIST DE SEGURIDAD

### **Configuración de Git:**
- [x] `.gitignore` creado
- [ ] Verificado que `application.properties` NO está en Git
- [ ] Si estaba en Git, eliminado del historial
- [ ] Contraseñas cambiadas si fueron expuestas
- [ ] `.gitignore` commiteado y pusheado

### **Configuración de aplicación:**
- [ ] `application.properties` movido a `application-local.properties`
- [ ] `application.properties.example` commiteado (sin credenciales)
- [ ] Variables de entorno configuradas
- [ ] JWT secret regenerado (mínimo 40 caracteres)
- [ ] Contraseña de BD segura (8+ caracteres, mayúsculas, números, símbolos)

### **Seguridad adicional:**
- [ ] CORS configurado solo para dominio específico (no "*")
- [ ] Passwords de BD encriptados con BCrypt
- [ ] JWT con expiración configurada (24 horas)
- [ ] Logs NO muestran información sensible
- [ ] Puerto 8080 NO expuesto públicamente sin firewall

---

## 🛡️ MEJORES PRÁCTICAS DE SEGURIDAD

### **1. NUNCA commitear:**
- ❌ Contraseñas de base de datos
- ❌ JWT secrets o API keys
- ❌ Certificados SSL (*.pem, *.key)
- ❌ Backups de base de datos (*.sql, *.dump)
- ❌ Archivos de logs con datos reales
- ❌ Tokens de acceso de terceros

### **2. SIEMPRE usar:**
- ✅ Variables de entorno para credenciales
- ✅ Archivos `.example` para plantillas
- ✅ `.gitignore` desde el inicio del proyecto
- ✅ Secrets diferentes para dev/prod
- ✅ Contraseñas generadas aleatoriamente (no "admin123")

### **3. Para PRODUCCIÓN:**
- ✅ Usar gestor de secrets (Azure Key Vault, AWS Secrets Manager)
- ✅ Habilitar HTTPS (no HTTP)
- ✅ Firewall configurado (solo puerto 443)
- ✅ Autenticación de 2 factores para Git
- ✅ Revisar permisos de base de datos (no usar "root")

---

## 🔍 COMANDOS ÚTILES PARA AUDITORÍA

### **Verificar qué archivos están trackeados:**
```bash
git ls-files
```

### **Buscar contraseñas en el código:**
```bash
# Buscar palabra "password" en todos los archivos
git grep -i "password"

# Buscar archivos .properties
git ls-files | findstr ".properties"
```

### **Ver archivos ignorados:**
```bash
git status --ignored
```

### **Verificar configuración actual:**
```bash
git config --list
```

---

## 📞 EN CASO DE COMPROMISO DE SEGURIDAD

### **Si descubres que las credenciales fueron expuestas públicamente:**

1. **CAMBIAR INMEDIATAMENTE:**
   - ✅ Contraseña de MySQL/MariaDB
   - ✅ JWT Secret
   - ✅ Passwords de usuarios administradores

2. **REVISAR LOGS:**
   ```sql
   -- MySQL: Ver conexiones recientes
   SELECT * FROM mysql.general_log WHERE argument LIKE '%login%';
   
   -- Ver intentos de conexión
   SHOW PROCESSLIST;
   ```

3. **NOTIFICAR:**
   - Equipo de desarrollo
   - Administrador de sistemas
   - Si hay datos de clientes, evaluar notificación RGPD/GDPR

4. **DOCUMENTAR:**
   - Qué se expuso
   - Cuándo ocurrió
   - Qué acciones se tomaron
   - Lecciones aprendidas

---

## 📚 RECURSOS ADICIONALES

### **Generar contraseñas seguras:**
- Online: https://passwordsgenerator.net/
- PowerShell: `[System.Web.Security.Membership]::GeneratePassword(32, 10)`

### **Generar JWT secrets:**
```bash
# Linux/Mac/Git Bash:
openssl rand -base64 32

# PowerShell:
[Convert]::ToBase64String((1..32|%{Get-Random -Max 256}))
```

### **Verificar fortaleza de contraseña:**
- https://howsecureismypassword.net/

---

## ✅ ARCHIVO DE CONFIGURACIÓN SEGURO

### **Estructura recomendada:**

```
gestion-citas/
├── .gitignore                          ← Protege archivos sensibles
├── .env                                ← Gitignoreado (variables locales)
├── src/main/resources/
│   ├── application.properties.example  ← Commiteado (sin secrets)
│   ├── application.properties          ← Gitignoreado (con secrets)
│   ├── application-dev.properties      ← Gitignoreado
│   └── application-prod.properties     ← Gitignoreado
```

### **Para nuevos desarrolladores:**

1. Clonar el repositorio
2. Copiar `application.properties.example` → `application.properties`
3. Pedir credenciales al líder técnico (por canal seguro)
4. Configurar variables de entorno
5. Verificar que `.gitignore` esté funcionando:
   ```bash
   git status
   # NO debe aparecer application.properties
   ```

---

## 🎯 RESUMEN DE ARCHIVOS CREADOS

1. **`.gitignore`** ✅
   - Protege información sensible
   - Ignora archivos de build y temporales
   - 13 categorías de protección

2. **`application.properties.example`** ✅
   - Plantilla sin credenciales reales
   - Para compartir con el equipo
   - Safe para commitear

3. **`SECURITY_GUIDE.md`** ✅ (este archivo)
   - Guía completa de seguridad
   - Pasos de remediación
   - Mejores prácticas

---

## ⚠️ ADVERTENCIA FINAL

**SI TU REPOSITORIO ES PÚBLICO EN GITHUB/GITLAB:**

Y ya subiste `application.properties` con contraseñas:

1. **Considera el repositorio COMPROMETIDO**
2. **Cambia TODAS las credenciales INMEDIATAMENTE**
3. **Elimina el repositorio público y crea uno nuevo (o privado)**
4. **Revisa logs de acceso a tu base de datos**

**NO es suficiente con "borrar el archivo"** - queda en el historial de Git.

---

**Última actualización:** Enero 20, 2025  
**Estado:** DOCUMENTO DE SEGURIDAD CRÍTICO  
**Prioridad:** MÁXIMA - ACCIÓN INMEDIATA REQUERIDA

**Contacto:** Revisar con líder técnico antes de hacer push a repositorio remoto.
- `.eclipse/` - Eclipse
- `*.iml` - Archivos de módulos

#### 🟢 **Logs y temporales:**
- `logs/` - Logs de aplicación
- `*.log` - Archivos de log
- `*.tmp`, `*.temp` - Archivos temporales

### 2. **Archivo `application.properties.example` creado**
Plantilla segura para que el equipo configure su entorno local sin exponer credenciales.

---

## 🚨 ACCIONES URGENTES QUE DEBES HACER AHORA

### **PASO 1: Verificar si ya subiste contraseñas a Git**

# 🔐 GUÍA DE SEGURIDAD - CLÍNICA VETERINARIA

> **Proyecto:** GA7-220501096-AA4-EV03  
> **Fecha:** Enero 20, 2025  
> **Estado:** CRÍTICO - ACCIÓN INMEDIATA REQUERIDA ⚠️

---

## 🚨 VULNERABILIDADES CRÍTICAS DETECTADAS

### ⛔ **PROBLEMA #1: Archivo `.gitignore` NO EXISTÍA**
**Riesgo:** TODO el código, incluidas contraseñas, puede estar en Git  
**Estado:** ✅ **RESUELTO** - `.gitignore` creado

### ⛔ **PROBLEMA #2: Credenciales expuestas en `application.properties`**
**Encontrado:**
```properties
spring.datasource.password=pontuclaveaqui
jwt.secret=clavesecretamuyseguraylargacon32caracteres123
```

**Riesgo:** Si este archivo está en Git público, tu base de datos y JWT están comprometidos  
**Estado:** ⚠️ **REQUIERE ACCIÓN INMEDIATA**

---

## ✅ SOLUCIONES IMPLEMENTADAS

### 1. **Archivo `.gitignore` creado**
He creado un `.gitignore` completo que protege:

#### 🔴 **Información sensible (CRÍTICO):**
- `application.properties` - Contraseñas y secrets
- `application-*.properties` - Perfiles de configuración
- `.env` - Variables de entorno
- `*.sql`, `*.dump` - Backups de base de datos
- `*.key`, `*.pem`, `*.jks` - Certificados y claves

#### 🟠 **Archivos de build:**
- `target/` - Compilados de Maven
- `.mvn/` - Configuración local de Maven
- `*.jar` (opcional) - Ejecutables

#### 🟡 **IDEs:**
- `.idea/` - IntelliJ IDEA
- `.vscode/` - Visual Studio Code


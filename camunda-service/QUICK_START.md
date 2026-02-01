# Guide de Démarrage Rapide - Camunda Service

## ⚠️ Problème Résolu: Compatibilité jakarta vs javax

**Problème initial**: Camunda 7.21.0 utilise les packages `jakarta.*` qui ne sont PAS compatibles avec Spring Boot 2.7.18 (qui utilise `javax.*`).

**Solution appliquée**: Downgrade vers Camunda 7.18.0 qui utilise `javax.*` packages.

## 🚀 Étapes de Démarrage

### 1. Créer la Base de Données MySQL

Ouvrez MySQL et exécutez:

```sql
CREATE DATABASE `camunda-service` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Note**: Le nom de la base est `camunda-service` (avec tiret), pas `camunda`.

### 2. Configurer les Credentials MySQL

Si votre MySQL utilise un mot de passe, modifiez `src/main/resources/application.properties`:

```properties
spring.datasource.password=votre_mot_de_passe
```

### 3. Compiler le Projet

```bash
cd d:/projerPfeDev/springBoot/moduleSuiviProcessusInscription/camunda-service
mvn clean install -DskipTests
```

### 4. Démarrer l'Application

```bash
mvn spring-boot:run
```

**OU** depuis IntelliJ IDEA: Clic droit sur `CamundaServiceApplication.java` → Run

### 5. Vérifier le Démarrage

Vous devriez voir dans les logs:

```
Camunda BPM Platform initialized successfully
Started CamundaServiceApplication in X.XXX seconds
```

### 6. Accéder aux Applications Camunda

Une fois démarré, ouvrez votre navigateur:

- **Camunda Cockpit**: http://localhost:8085/camunda/app/cockpit/default/
- **Camunda Tasklist**: http://localhost:8085/camunda/app/tasklist/default/
- **Camunda Admin**: http://localhost:8085/camunda/app/admin/default/

**Credentials**: `admin` / `admin`

## 🧪 Tester l'API REST

### Test 1: Santé de l'Application

```bash
curl http://localhost:8085/actuator/health
```

### Test 2: Démarrer un Processus

```bash
curl -X POST http://localhost:8085/api/process/start \
  -H "Content-Type: application/json" \
  -d "{\"processDefinitionKey\":\"inscription-process\",\"businessKey\":\"INS-001\",\"variables\":{\"studentId\":\"12345\",\"studentName\":\"Jean Dupont\",\"assignee\":\"admin\"}}"
```

### Test 3: Récupérer les Tâches

```bash
curl http://localhost:8085/api/tasks/user/admin
```

## 🔧 Dépannage

### Erreur: "ClassNotFoundException: jakarta.servlet"

✅ **Résolu**: Utilisez Camunda 7.18.0 au lieu de 7.21.0

### Erreur: "Communications link failure"

❌ **Cause**: MySQL n'est pas démarré ou les credentials sont incorrects

✅ **Solution**: 
1. Vérifiez que MySQL est démarré
2. Vérifiez les credentials dans `application.properties`
3. Créez la base de données `camunda-service`

### Erreur: "Table doesn't exist"

✅ **Solution**: Camunda créera automatiquement les tables au premier démarrage si `camunda.bpm.database.schema-update=true`

## 📋 Versions Compatibles

| Composant | Version | Packages |
|-----------|---------|----------|
| Spring Boot | 2.7.18 | javax.* |
| Camunda BPM | 7.18.0 | javax.* |
| Java | 11+ | - |
| MySQL | 8.0+ | - |

## ⚠️ Important

- **NE PAS** utiliser Camunda 7.19+ avec Spring Boot 2.7.x (incompatibilité jakarta/javax)
- **NE PAS** utiliser Spring Boot 3.x avec Camunda 7.18.0 (incompatibilité inverse)
- Pour Spring Boot 3.x, utilisez Camunda 7.19+ ou Camunda 8

## 📚 Prochaines Étapes

1. ✅ Démarrer le service
2. ✅ Tester les endpoints REST
3. ✅ Accéder à Camunda Webapp
4. 📝 Créer vos propres processus BPMN
5. 🔗 Intégrer avec vos autres microservices via Feign

Consultez le [README.md](README.md) pour la documentation complète.

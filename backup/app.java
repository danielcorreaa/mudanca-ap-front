/*
 * Requisitos para rodar:
 * 1. Java 17+
 * 2. MongoDB rodando localmente (mongodb://localhost:27017)
 * 3. Dependências Spring Boot (pom.xml / build.gradle):
 * - spring-boot-starter-web
 * - spring-boot-starter-data-mongodb
 * - lombok
 * - org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0 (Para Swagger/OpenAPI)
 */

package com.planejador.mudanca;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
public class MudancaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MudancaApplication.class, args);
    }
}

// --- CONFIGURAÇÃO DO SWAGGER/OPENAPI ---
@Configuration
class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Planeamento de Mudança")
                        .version("1.0")
                        .description("Documentação dos endpoints para gestão de tarefas e custos de mudança de apartamento."));
    }
}

// --- MODELO ---
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "itens_mudanca")
class ItemMudanca {
    @Id
    private String id;
    private String name;
    private Double value;
    private String category; // "novo" ou "antigo"
    private Boolean completed;
}

// --- REPOSITÓRIO ---
interface ItemRepository extends MongoRepository<ItemMudanca, String> {
}

// --- CONTROLADOR ---
@RestController
@RequestMapping("/api/itens")
@CrossOrigin(origins = "*")
class ItemController {

    private final ItemRepository repository;

    public ItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ItemMudanca> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ItemMudanca create(@RequestBody ItemMudanca item) {
        item.setId(null); 
        return repository.save(item);
    }

    @PostMapping("/batch")
    public List<ItemMudanca> createBatch(@RequestBody List<ItemMudanca> itens) {
        itens.forEach(item -> item.setId(null));
        return repository.saveAll(itens);
    }

    @PutMapping("/{id}")
    public ItemMudanca update(@PathVariable String id, @RequestBody ItemMudanca item) {
        item.setId(id);
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }
}
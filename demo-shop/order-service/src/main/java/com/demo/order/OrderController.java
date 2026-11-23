package com.demo.order;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    record OrderRequest(Long userId, Long productId, Integer quantity) {}
    record ProductDto(Long id, String name, BigDecimal price) {}

    private final OrderRepository repo;
    private final RestTemplate rest = new RestTemplate();

    @Value("${user.service.url}") private String userUrl;
    @Value("${product.service.url}") private String productUrl;

    public OrderController(OrderRepository repo) { this.repo = repo; }

    @GetMapping
    public List<OrderEntity> all() { return repo.findAll(); }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderRequest r) {
        try {
            // inter-service calls: validate user, fetch product
            rest.getForObject(userUrl + "/api/users/" + r.userId(), String.class);
            ProductDto p = rest.getForObject(productUrl + "/api/products/" + r.productId(), ProductDto.class);

            OrderEntity o = new OrderEntity();
            o.userId = r.userId();
            o.productId = r.productId();
            o.productName = p.name();
            o.quantity = r.quantity();
            o.total = p.price().multiply(BigDecimal.valueOf(r.quantity()));
            return ResponseEntity.ok(repo.save(o));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.badRequest().body("Invalid user or product");
        }
    }
}

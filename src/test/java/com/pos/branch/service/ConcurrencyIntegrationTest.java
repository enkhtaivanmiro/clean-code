package com.pos.branch.service;

import com.pos.branch.dto.SaleItemRequest;
import com.pos.branch.dto.SaleRequest;
import com.pos.branch.model.*;
import com.pos.branch.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
@ActiveProfiles("h2")
public class ConcurrencyIntegrationTest {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private POSTerminalRepository posTerminalRepository;
    @Autowired
    private PaymentTypeRepository paymentTypeRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private Integer productId;
    private Integer branchId;
    private Integer posTerminalId;
    private Integer paymentTypeId;

    @BeforeEach
    public void setup() {
        transactionTemplate.execute(status -> {
            categoryRepository.deleteAll();
            productRepository.deleteAll();
            branchRepository.deleteAll();
            posTerminalRepository.deleteAll();
            paymentTypeRepository.deleteAll();
            saleRepository.deleteAll();

            Category cat = new Category();
            cat.setName("Test Cat");
            cat = categoryRepository.save(cat);

            Product prod = new Product();
            prod.setName("Test Prod");
            prod.setCategory(cat);
            prod.setActive(true);
            prod = productRepository.save(prod);

            Branch branch = new Branch();
            branch.setName("Test Branch");
            branch.setAddress("Address");
            branch = branchRepository.save(branch);

            POSTerminal pos = new POSTerminal();
            pos.setBranch(branch);
            pos.setName("POS-1");
            pos = posTerminalRepository.save(pos);

            PaymentType pt = new PaymentType();
            pt.setName("CASH");
            pt = paymentTypeRepository.save(pt);
            
            productId = prod.getId();
            branchId = branch.getId();
            posTerminalId = pos.getId();
            paymentTypeId = pt.getId();
            return null;
        });
    }

    @Test
    public void testConcurrentIdempotentSales() throws InterruptedException {
        int threadCount = 20;
        UUID duplicateSaleId = UUID.randomUUID();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    SaleRequest request = new SaleRequest(
                            duplicateSaleId,
                            branchId,
                            posTerminalId,
                            paymentTypeId,
                            List.of(new SaleItemRequest(productId, 1, new BigDecimal("3500.00"), null, BigDecimal.ZERO)),
                            new BigDecimal("3500.00")
                    );
                    saleService.createSale(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // One will succeed, others might throw Conflict or return OK depending on timing
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long count = saleRepository.count();
        assertEquals(1, count, "Only one sale should be created for same UUID despite concurrency");
    }
}

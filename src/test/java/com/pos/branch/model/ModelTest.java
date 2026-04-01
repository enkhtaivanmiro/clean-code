package com.pos.branch.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    void testCategory() {
        Category c = new Category();
        c.setId(1);
        c.setName("Test");
        assertEquals(1, c.getId());
        assertEquals("Test", c.getName());
    }

    @Test
    void testProduct() {
        Product p = new Product();
        p.setId(1);
        p.setName("P");
        Category c = new Category();
        p.setCategory(c);
        p.setBarcodes(Collections.emptyList());
        assertEquals(1, p.getId());
        assertEquals("P", p.getName());
        assertEquals(c, p.getCategory());
        assertTrue(p.getBarcodes().isEmpty());
    }

    @Test
    void testBarcode() {
        Barcode b = new Barcode();
        b.setId(1);
        b.setCode("123");
        Product p = new Product();
        b.setProduct(p);
        assertEquals(1, b.getId());
        assertEquals("123", b.getCode());
        assertEquals(p, b.getProduct());
    }

    @Test
    void testProductPrice() {
        ProductPrice pp = new ProductPrice();
        pp.setId(1);
        pp.setPrice(BigDecimal.TEN);
        pp.setUpdatedAt(LocalDateTime.now());
        Product p = new Product();
        pp.setProduct(p);
        assertEquals(1, pp.getId());
        assertEquals(BigDecimal.TEN, pp.getPrice());
        assertNotNull(pp.getUpdatedAt());
        assertEquals(p, pp.getProduct());
    }

    @Test
    void testBranch() {
        Branch b = new Branch();
        b.setId(1);
        b.setName("B");
        b.setAddress("A");
        assertEquals(1, b.getId());
        assertEquals("B", b.getName());
        assertEquals("A", b.getAddress());
    }

    @Test
    void testPOSTerminal() {
        POSTerminal t = new POSTerminal();
        t.setId(1);
        t.setName("T");
        Branch b = new Branch();
        t.setBranch(b);
        assertEquals(1, t.getId());
        assertEquals("T", t.getName());
        assertEquals(b, t.getBranch());
    }

    @Test
    void testPaymentType() {
        PaymentType pt = new PaymentType();
        pt.setId(1);
        pt.setName("CASH");
        assertEquals(1, pt.getId());
        assertEquals("CASH", pt.getName());
    }

    @Test
    void testDiscountRule() {
        DiscountRule d = new DiscountRule();
        d.setId(1);
        d.setName("D");
        d.setType("PERCENT");
        d.setValue(BigDecimal.ONE);
        d.setActive(true);
        assertEquals(1, d.getId());
        assertEquals("D", d.getName());
        assertEquals("PERCENT", d.getType());
        assertEquals(BigDecimal.ONE, d.getValue());
        assertTrue(d.getActive());
    }

    @Test
    void testSale() {
        Sale s = new Sale();
        UUID id = UUID.randomUUID();
        s.setId(id);
        s.setTotalAmount(BigDecimal.TEN);
        s.setCreatedAt(LocalDateTime.now());
        s.setSynced(true);
        Branch b = new Branch();
        s.setBranch(b);
        POSTerminal t = new POSTerminal();
        s.setPosTerminal(t);
        PaymentType pt = new PaymentType();
        s.setPaymentType(pt);
        s.setItems(Collections.emptyList());

        assertEquals(id, s.getId());
        assertEquals(BigDecimal.TEN, s.getTotalAmount());
        assertNotNull(s.getCreatedAt());
        assertTrue(s.getSynced());
        assertEquals(b, s.getBranch());
        assertEquals(t, s.getPosTerminal());
        assertEquals(pt, s.getPaymentType());
        assertTrue(s.getItems().isEmpty());
    }

    @Test
    void testSaleItem() {
        SaleItem i = new SaleItem();
        i.setId(1);
        i.setQuantity(2);
        i.setPrice(BigDecimal.ONE);
        i.setDiscountAmount(BigDecimal.ZERO);
        Sale s = new Sale();
        i.setSale(s);
        Product p = new Product();
        i.setProduct(p);
        DiscountRule d = new DiscountRule();
        i.setDiscountRule(d);

        assertEquals(1, i.getId());
        assertEquals(2, i.getQuantity());
        assertEquals(BigDecimal.ONE, i.getPrice());
        assertEquals(BigDecimal.ZERO, i.getDiscountAmount());
        assertEquals(s, i.getSale());
        assertEquals(p, i.getProduct());
        assertEquals(d, i.getDiscountRule());
    }

    @Test
    void testInventory() {
        Inventory inv = new Inventory();
        inv.setId(1);
        inv.setQuantity(10);
        Branch b = new Branch();
        inv.setBranch(b);
        Product p = new Product();
        inv.setProduct(p);

        assertEquals(1, inv.getId());
        assertEquals(10, inv.getQuantity());
        assertEquals(b, inv.getBranch());
        assertEquals(p, inv.getProduct());
    }
}

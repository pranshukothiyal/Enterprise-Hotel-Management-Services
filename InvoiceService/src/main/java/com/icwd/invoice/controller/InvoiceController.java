package com.icwd.invoice.controller;

import com.icwd.invoice.entity.Invoice;
import com.icwd.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/invoices","/api/invoices"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.generateInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getInvoices(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String bookingId) {
        if (userId != null) return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId));
        if (bookingId != null) return ResponseEntity.ok(invoiceService.getInvoicesByBookingId(bookingId));
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
}

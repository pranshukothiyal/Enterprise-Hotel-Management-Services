package com.icwd.invoice.controller;

import com.icwd.invoice.entity.Invoice;
import com.icwd.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/invoices", "/api/invoices"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody Invoice invoice
    ) {

        log.info(
                "Received request to create invoice"
        );

        Invoice createdInvoice =
                invoiceService.generateInvoice(
                        invoice
                );

        log.info(
                "Invoice created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdInvoice);
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getInvoices(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String bookingId
    ) {

        if (userId != null) {

            log.info(
                    "Received request to fetch invoices by user. userId={}",
                    userId
            );

            List<Invoice> invoices =
                    invoiceService
                            .getInvoicesByUserId(
                                    userId
                            );

            log.debug(
                    "Invoices fetched successfully for user. userId={}, count={}",
                    userId,
                    invoices.size()
            );

            return ResponseEntity.ok(
                    invoices
            );
        }

        if (bookingId != null) {

            log.info(
                    "Received request to fetch invoices by booking. bookingId={}",
                    bookingId
            );

            List<Invoice> invoices =
                    invoiceService
                            .getInvoicesByBookingId(
                                    bookingId
                            );

            log.debug(
                    "Invoices fetched successfully for booking. bookingId={}, count={}",
                    bookingId,
                    invoices.size()
            );

            return ResponseEntity.ok(
                    invoices
            );
        }

        log.info(
                "Received request to fetch all invoices"
        );

        List<Invoice> invoices =
                invoiceService
                        .getAllInvoices();

        log.debug(
                "Fetched all invoices successfully. count={}",
                invoices.size()
        );

        return ResponseEntity.ok(
                invoices
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(
            @PathVariable String id
    ) {

        log.info(
                "Received request to fetch invoice. invoiceId={}",
                id
        );

        Invoice invoice =
                invoiceService
                        .getInvoiceById(
                                id
                        );

        log.debug(
                "Invoice fetched successfully. invoiceId={}",
                id
        );

        return ResponseEntity.ok(
                invoice
        );
    }
}
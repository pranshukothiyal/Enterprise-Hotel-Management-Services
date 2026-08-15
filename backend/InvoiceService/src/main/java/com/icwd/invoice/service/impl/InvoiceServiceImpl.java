package com.icwd.invoice.service.impl;

import com.icwd.invoice.entity.Invoice;
import com.icwd.invoice.entity.InvoiceStatus;
import com.icwd.invoice.exception.ResourceNotFoundException;
import com.icwd.invoice.repository.InvoiceRepository;
import com.icwd.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private static final double TAX_RATE = 0.18;

    @Override
    public Invoice generateInvoice(
            Invoice invoice
    ) {

        log.info(
                "Starting invoice generation"
        );

        String invoiceId =
                UUID.randomUUID().toString();

        invoice.setInvoiceId(
                invoiceId
        );

        log.debug(
                "Generated invoice ID. invoiceId={}",
                invoiceId
        );

        String invoiceNumber =
                "INV-" + System.currentTimeMillis();

        invoice.setInvoiceNumber(
                invoiceNumber
        );

        log.debug(
                "Generated invoice number. invoiceId={}, invoiceNumber={}",
                invoiceId,
                invoiceNumber
        );

        invoice.setStatus(
                InvoiceStatus.PENDING
        );

        log.debug(
                "Invoice status set to PENDING. invoiceId={}",
                invoiceId
        );

        invoice.setIssuedAt(
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        )
        );

        log.debug(
                "Invoice issue timestamp generated. invoiceId={}, issuedAt={}",
                invoiceId,
                invoice.getIssuedAt()
        );

        // Calculate tax and total
        if (invoice.getTax() == null) {

            log.debug(
                    "Tax was not provided. Calculating tax using default rate. invoiceId={}, taxRate={}",
                    invoiceId,
                    TAX_RATE
            );

            invoice.setTax(
                    invoice.getAmount()
                            * TAX_RATE
            );
        }

        invoice.setTotalAmount(
                invoice.getAmount()
                        + invoice.getTax()
        );

        log.debug(
                "Invoice amount calculated. invoiceId={}, amount={}, tax={}, totalAmount={}",
                invoiceId,
                invoice.getAmount(),
                invoice.getTax(),
                invoice.getTotalAmount()
        );

        // Set due date to 30 days from now
        if (invoice.getDueDate() == null) {

            invoice.setDueDate(
                    LocalDate.now()
                            .plusDays(30)
                            .toString()
            );

            log.debug(
                    "Due date was not provided. Default due date generated. invoiceId={}, dueDate={}",
                    invoiceId,
                    invoice.getDueDate()
            );
        }

        Invoice savedInvoice =
                invoiceRepository.save(
                        invoice
                );

        log.info(
                "Invoice generated successfully. invoiceId={}, invoiceNumber={}, status={}, totalAmount={}",
                savedInvoice.getInvoiceId(),
                savedInvoice.getInvoiceNumber(),
                savedInvoice.getStatus(),
                savedInvoice.getTotalAmount()
        );

        return savedInvoice;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(
            String id
    ) {

        log.debug(
                "Fetching invoice by ID. invoiceId={}",
                id
        );

        return invoiceRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Invoice not found. invoiceId={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Invoice",
                            "id",
                            id
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {

        log.debug(
                "Fetching all invoices from repository"
        );

        List<Invoice> invoices =
                invoiceRepository.findAll();

        log.info(
                "Invoices fetched successfully. count={}",
                invoices.size()
        );

        return invoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByBookingId(
            String bookingId
    ) {

        log.debug(
                "Fetching invoices by booking. bookingId={}",
                bookingId
        );

        List<Invoice> invoices =
                invoiceRepository
                        .findByBookingId(
                                bookingId
                        );

        log.info(
                "Invoices fetched successfully for booking. bookingId={}, count={}",
                bookingId,
                invoices.size()
        );

        return invoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByUserId(
            String userId
    ) {

        log.debug(
                "Fetching invoices by user. userId={}",
                userId
        );

        List<Invoice> invoices =
                invoiceRepository
                        .findByUserId(
                                userId
                        );

        log.info(
                "Invoices fetched successfully for user. userId={}, count={}",
                userId,
                invoices.size()
        );

        return invoices;
    }
}
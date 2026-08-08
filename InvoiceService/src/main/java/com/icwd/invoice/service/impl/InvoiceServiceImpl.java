package com.icwd.invoice.service.impl;

import com.icwd.invoice.entity.Invoice;
import com.icwd.invoice.entity.InvoiceStatus;
import com.icwd.invoice.exception.ResourceNotFoundException;
import com.icwd.invoice.repository.InvoiceRepository;
import com.icwd.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private static final double TAX_RATE = 0.18;

    @Override
    public Invoice generateInvoice(Invoice invoice) {
        invoice.setInvoiceId(UUID.randomUUID().toString());
        // Auto-generate invoice number
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setIssuedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Calculate tax and total
        if (invoice.getTax() == null) {
            invoice.setTax(invoice.getAmount() * TAX_RATE);
        }
        invoice.setTotalAmount(invoice.getAmount() + invoice.getTax());

        // Set due date to 30 days from now
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDate.now().plusDays(30).toString());
        }

        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByBookingId(String bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByUserId(String userId) {
        return invoiceRepository.findByUserId(userId);
    }
}

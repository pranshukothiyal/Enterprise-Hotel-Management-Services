package com.icwd.invoice.service;

import com.icwd.invoice.entity.Invoice;
import java.util.List;

public interface InvoiceService {
    Invoice generateInvoice(Invoice invoice);
    Invoice getInvoiceById(String id);
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByBookingId(String bookingId);
    List<Invoice> getInvoicesByUserId(String userId);
}

package com.gra.local.controllers;

import com.gra.local.exceptions.CustomException;
import com.gra.local.persistence.domain.PreOrders;
import com.gra.local.persistence.services.PreOrderProductsService;
import com.gra.local.persistence.services.dtos.VendorsAndTheirProductsResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pre-order")
public class PreOrderProductsController {

    private PreOrderProductsService preOrderProductsService;

    @Autowired
    public PreOrderProductsController(PreOrderProductsService preOrderProductsService) {
        this.preOrderProductsService = preOrderProductsService;
    }

    @PostMapping("/")
    public ResponseEntity<?> preOrderProducts(@Valid @NonNull @RequestBody List<VendorsAndTheirProductsResponse> vendorsAndTheirProducts) {
        try {
            // For all the vendors that have products in the list, send them pre-order SMS. They should confirm by SMS as well
            for (VendorsAndTheirProductsResponse data : vendorsAndTheirProducts) {
                // Save pre order
                preOrderProductsService.save(data);

                // Send SMS to vendor
                preOrderProductsService.sendOrderDetailsBySmsToVendors(data.getCustomerPhoneNumber(), data.getProducts());
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            throw new CustomException("Error sending vendors sms with order details", HttpStatus.BAD_REQUEST, ex);
        }
    }

    @GetMapping("/accept/{uuid}")
    public ResponseEntity<?> acceptPreOrderAsVendor(@Valid @NonNull @PathVariable String uuid) {
        Optional<PreOrders> optionalPreOrder = preOrderProductsService.findAcceptedUrl(uuid);
        if (optionalPreOrder.isPresent()) {
            // Update status of preorder to Accepted = true;
            preOrderProductsService.updateStatusToAccepted(optionalPreOrder.get().getId());
            // Send sms to customer telling them the order is accepted and on it's way
            //TODO

            // Respond
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/deny/{uuid}")
    public ResponseEntity<?> denyPreOrderAsVendor(@Valid @NonNull @PathVariable String uuid) {
        Optional<PreOrders> optionalPreOrder = preOrderProductsService.findAcceptedUrl(uuid);
        if (optionalPreOrder.isPresent()) {
            // Update status of preorder to Denied = true;
            preOrderProductsService.updateStatusToDenied(optionalPreOrder.get().getId());
            // Send sms to customer telling them the order is accepted and on it's way
            // TODO

            // Respond
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

package javadev.project.producer.controller;

import javadev.project.producer.dto.supplier.SupplierDTO;
import javadev.project.producer.dto.supplier.SupplierRequest;
import javadev.project.producer.dto.supplier.SupplierResponse;
import javadev.project.producer.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@Tag(name = "Supplier Management", description = "APIs for managing suppliers")
@RequestMapping("/api/v1")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;

    /**
     * Creates a new supplier
     * Accepts supplier details in the request body and creates a new supplier in the system
     * 
     * @param supplierRequest the supplier data to be created
     * @return SupplierResponse with success message
     */
    @PostMapping(path = "/supplier", consumes = "application/json", produces = "application/json")
    public SupplierResponse<String> createSupplier(@RequestBody SupplierRequest supplierRequest) {
        supplierService.createSupplier(supplierRequest);
        return SupplierResponse.<String>builder()
                .message("Supplier created successfully")
                .data(null)
                .build();
    }

    /**
     * Retrieves all suppliers
     * Returns a list of all suppliers available in the system
     * 
     * @return SupplierResponse containing a list of all suppliers
     */
    @GetMapping(path = "/suppliers", produces = "application/json")
    public SupplierResponse<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return SupplierResponse.<List<SupplierDTO>>builder()
                .message("Suppliers retrieved successfully")
                .data(suppliers)
                .build();
    }

    /**
     * Retrieves a specific supplier by ID
     * Returns detailed information about a single supplier
     * 
     * @param id the ID of the supplier to retrieve
     * @return SupplierResponse containing the supplier details
     */
    @GetMapping(path = "/supplier/{id}", produces = "application/json")
    public SupplierResponse<SupplierDTO> getSupplierById(@PathVariable Integer id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return SupplierResponse.<SupplierDTO>builder()
                .message("Supplier retrieved successfully")
                .data(supplier)
                .build();
    }

    /**
     * Updates an existing supplier
     * Accepts updated supplier details and modifies the supplier with the given ID
     * 
     * @param id the ID of the supplier to update
     * @param supplierRequest the updated supplier data
     * @return SupplierResponse containing the updated supplier details
     */
    @PutMapping(path = "/supplier/{id}", consumes = "application/json", produces = "application/json")
    public SupplierResponse<SupplierDTO> updateSupplier(
            @PathVariable Integer id,
            @RequestBody SupplierRequest supplierRequest) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierRequest);
        return SupplierResponse.<SupplierDTO>builder()
                .message("Supplier updated successfully")
                .data(updatedSupplier)
                .build();
    }

    /**
     * Deletes a supplier by ID
     * Removes the supplier with the specified ID from the system
     * Checks if the supplier is being used by any products before deletion
     * 
     * @param id the ID of the supplier to delete
     * @return SupplierResponse with success message
     */
    @DeleteMapping(path = "/supplier/{id}", produces = "application/json")
    public SupplierResponse<String> deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteSupplier(id);
        return SupplierResponse.<String>builder()
                .message("Supplier deleted successfully")
                .data(null)
                .build();
    }
}

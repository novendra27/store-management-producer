package javadev.project.producer.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javadev.project.producer.dto.supplier.SupplierDTO;
import javadev.project.producer.dto.supplier.SupplierRequest;
import javadev.project.producer.entity.supplier;
import javadev.project.producer.repository.ProductRepository;
import javadev.project.producer.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Validator validator;

    /**
     * Creates a new supplier in the system
     * Validates the supplier request and persists the supplier to the database
     * 
     * @param supplierRequest the supplier data to be created
     * @throws ConstraintViolationException if validation fails
     */
    @Transactional
    public void createSupplier(SupplierRequest supplierRequest) {
        // Validate the incoming request
        Set<ConstraintViolation<SupplierRequest>> violations = validator.validate(supplierRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Map SupplierRequest to Supplier entity
        supplier supplier = new supplier();
        supplier.setSupplierName(supplierRequest.getSupplierName());
        supplier.setContact(supplierRequest.getContact());
        supplier.setAddress(supplierRequest.getAddress());
        supplierRepository.save(supplier);
    }

    /**
     * Retrieves all suppliers from the database
     * Converts each supplier entity to SupplierDTO for response
     * 
     * @return List of SupplierDTO containing all suppliers
     */
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single supplier by its ID
     * 
     * @param id the ID of the supplier to retrieve
     * @return SupplierDTO containing the supplier details
     * @throws ResponseStatusException if supplier is not found with the given ID
     */
    public SupplierDTO getSupplierById(Integer id) {
        supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with id: " + id));
        return convertToDTO(supplier);
    }

    /**
     * Updates an existing supplier with new data
     * Validates the supplier request and updates the supplier
     * 
     * @param id the ID of the supplier to update
     * @param supplierRequest the new supplier data
     * @return SupplierDTO containing the updated supplier details
     * @throws ConstraintViolationException if validation fails
     * @throws ResponseStatusException if supplier is not found
     */
    @Transactional
    public SupplierDTO updateSupplier(Integer id, SupplierRequest supplierRequest) {
        Set<ConstraintViolation<SupplierRequest>> violations = validator.validate(supplierRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with id: " + id));

        existingSupplier.setSupplierName(supplierRequest.getSupplierName());
        existingSupplier.setContact(supplierRequest.getContact());
        existingSupplier.setAddress(supplierRequest.getAddress());

        supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return convertToDTO(updatedSupplier);
    }

    /**
     * Deletes a supplier from the database by its ID
     * Checks if the supplier is currently being used by any products
     * If the supplier is in use, the deletion is prevented
     * 
     * @param id the ID of the supplier to delete
     * @throws ResponseStatusException if supplier is not found or is currently in use by products
     */
    @Transactional
    public void deleteSupplier(Integer id) {
        supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with id: " + id));

        // Check if supplier is being used by any products
        long productCount = productRepository.countBySupplier(existingSupplier);
        if (productCount > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete supplier. It is currently being used by " + productCount + " product(s)");
        }

        supplierRepository.delete(existingSupplier);
    }

    /**
     * Converts a supplier entity to SupplierDTO for API response
     * 
     * @param supplier the supplier entity to convert
     * @return SupplierDTO containing the supplier data
     */
    private SupplierDTO convertToDTO(supplier supplier) {
        return SupplierDTO.builder()
                .id(supplier.getId())
                .supplierName(supplier.getSupplierName())
                .contact(supplier.getContact())
                .address(supplier.getAddress())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}

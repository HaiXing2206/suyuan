package org.Tracing.controller;

import org.Tracing.dto.GovernanceMaskRequest;
import org.Tracing.dto.GovernanceValidationRequest;
import org.Tracing.entity.DataElementLedger;
import org.Tracing.service.DataElementLedgerService;
import org.Tracing.util.DataGovernanceUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data-elements")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DataElementLedgerController {
    private final DataElementLedgerService ledgerService;

    public DataElementLedgerController(DataElementLedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public ResponseEntity<List<DataElementLedger>> list() {
        return ResponseEntity.ok(ledgerService.listAll());
    }

    @GetMapping("/{elementId}")
    public ResponseEntity<?> detail(@PathVariable String elementId) {
        return ledgerService.findById(elementId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DataElementLedger> create(@RequestBody DataElementLedger request) {
        return ResponseEntity.ok(ledgerService.save(request));
    }

    @PutMapping("/{elementId}")
    public ResponseEntity<?> update(@PathVariable String elementId, @RequestBody DataElementLedger request) {
        return ledgerService.findById(elementId)
                .map(existing -> {
                    request.setElementId(existing.getElementId());
                    return ResponseEntity.ok(ledgerService.save(request));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{elementId}")
    public ResponseEntity<Void> delete(@PathVariable String elementId) {
        ledgerService.delete(elementId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/governance/validate")
    public ResponseEntity<Map<String, Object>> validateGovernance(@RequestBody GovernanceValidationRequest request) {
        return ResponseEntity.ok(ledgerService.runGovernanceValidation(request));
    }

    @PostMapping("/governance/mask")
    public ResponseEntity<Map<String, String>> mask(@RequestBody GovernanceMaskRequest request) {
        Map<String, String> result = new HashMap<>();
        result.put("type", request.getType());
        result.put("original", request.getValue());
        result.put("masked", DataGovernanceUtil.maskValue(request.getType(), request.getValue()));
        return ResponseEntity.ok(result);
    }
}

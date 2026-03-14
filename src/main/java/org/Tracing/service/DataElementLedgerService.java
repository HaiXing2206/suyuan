package org.Tracing.service;

import org.Tracing.dto.GovernanceValidationRequest;
import org.Tracing.entity.DataElementLedger;
import org.Tracing.repository.DataElementLedgerRepository;
import org.Tracing.util.DataGovernanceUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DataElementLedgerService {
    private final DataElementLedgerRepository ledgerRepository;

    public DataElementLedgerService(DataElementLedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    public List<DataElementLedger> listAll() {
        return ledgerRepository.findAll();
    }

    public Optional<DataElementLedger> findById(String elementId) {
        return ledgerRepository.findById(elementId);
    }

    public DataElementLedger save(DataElementLedger ledger) {
        if (ledger.getElementId() == null || ledger.getElementId().isEmpty()) {
            ledger.setElementId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (ledger.getArchiveStatus() == null || ledger.getArchiveStatus().isEmpty()) {
            ledger.setArchiveStatus("ACTIVE");
        }
        if (ledger.getDataLevel() == null || ledger.getDataLevel().isEmpty()) {
            ledger.setDataLevel("L2");
        }
        if (ledger.getSensitiveFlag() == null) {
            ledger.setSensitiveFlag(false);
        }
        return ledgerRepository.save(ledger);
    }

    public void delete(String elementId) {
        ledgerRepository.deleteById(elementId);
    }

    public Map<String, Object> runGovernanceValidation(GovernanceValidationRequest request) {
        Map<String, Object> result = new HashMap<>();

        boolean complete = isNotBlank(request.getElementName()) && isNotBlank(request.getDepartment());
        result.put("completeness", complete);

        boolean unique = !ledgerRepository.existsByElementNameAndDepartment(request.getElementName(), request.getDepartment());
        result.put("uniqueness", unique);

        if (isNotBlank(request.getFormatType()) && isNotBlank(request.getFormatValue())) {
            result.put("formatValid", DataGovernanceUtil.isFormatValid(request.getFormatType(), request.getFormatValue()));
        } else {
            result.put("formatValid", null);
        }

        boolean pass = complete && unique && !Boolean.FALSE.equals(result.get("formatValid"));
        result.put("pass", pass);
        return result;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

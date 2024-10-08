package com.example.pharmacyrecommendation.pharmacy.service;

import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy;
import com.example.pharmacyrecommendation.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRepositoryService {

    private final PharmacyRepository pharmacyRepository;

    // self invocation test  - 자기 자신 호출
    public void bar(List<Pharmacy> pharmacyList) {
        log.info("bar CurrentTransactionName:" + TransactionSynchronizationManager.getCurrentTransactionName());
        foo(pharmacyList);
    }

    // self invocation test
    @Transactional
    public void foo(List<Pharmacy> pharmacyList) {
        log.info("foo CurrentTransactionName" + TransactionSynchronizationManager.getCurrentTransactionName());
        pharmacyList.forEach(pharmacy -> {
            pharmacyRepository.save(pharmacy);
            throw new RuntimeException("error"); // 예외 발생
        });
    } // this.foo 를 호출하기 때문에 트랜잭션이 실행이 안 되므로 rollback 이 안되는 문제가 발생한다.

    // dirty checking test
    // 주소 업데이트 메서드, @Transactional 있음
    @Transactional
    public void updateAddress(Long id, String address) {

        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        // 엔티티가 null 이면 return
        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
        }

        entity.changePharmacyAddress(address);
    }

    // 주소 업데이트 메서드, @Transactional 없음
    public void updateAddressWithoutTransaction(Long id, String address) {

        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        // 엔티티가 null 이면 return
        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
        }

        entity.changePharmacyAddress(address);
    }

    // 고객이 주소를 입력했을 때 가까운 약국을 찾기 위한 메서드
    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}

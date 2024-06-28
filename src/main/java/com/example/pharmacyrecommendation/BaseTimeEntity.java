package com.example.pharmacyrecommendation;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 엔티티의 공통 매핑 정보
@EntityListeners(AuditingEntityListener.class) // 엔티티에 jpa auditing 기능을 담당하는 엔트리 리스너 지정
public abstract class BaseTimeEntity { // 추상 클래스로 선언

    @CreatedDate // 엔티티가 생성될 때 시간이 자동 저장
    @Column(updatable = false) // 명식적으로 업데이트가 안되게 선언
    private LocalDateTime createdDate; // 생성 날짜, 시간

    @LastModifiedDate // 엔티티가 수정될 때 시간이 자동 저장
    private LocalDateTime modifiedDate; // 수정 날짜, 시간
}

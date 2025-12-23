package in.kishorbongu.billingsoftware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kishorbongu.billingsoftware.entity.OrderItemEntity;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, Long> {

    
    
}

# 정리

JPA Repository 를 통해<br>
Entity 로 조회하냐, DTO 로 직접 조회하냐 두가지 방식

1. Entity 조회
    엔티티를 조회해서 그대로 반환하는 것은 안된다. V1<br>
    엔티티로 조회해서 DTO 로 변환해야한다. V2<br>
    N+1 문제를 페치 조인으로 최적화한다. V3<br>
    페치 조인 시, 컬렉션에 해당하는 것은 페이징이 어려움 V3.1<br>
        ToOne 관계는 페치 조인으로 최적화<br>
        컬렉션은 지연 로딩을 유지하고 @BatchSize 로 최적화

2. DTO 직접 조회
    JPA 에서 DTO 직접 조회 V4<br>
    컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN 절을 활용하여 메모리에 미리 조회해서 최적화 V5<br>
    플랫 데이터 최적화 - JOIN 결과를 그대로 조회 후, 애플리케이션에서 원하는 모양으로 직접 변환 V6

### 권장 순서
1. 엔티티 조회 방식으로 우선 접근
   1) 페치 조인으로 쿼리 수 최적화
   2) 컬렉션 최적화
       A) 페이징 필요, @BatchSize 최적화
       B) 페이징 안씀 -> 페치 조인 사용
2. 엔티티 조회 방식으로 해결 안된다면 DTO 조회 방식 사용
3. DTO 조회로도 안되면 NativeSQL or Spring JdbcTemplate 사용

(엔티티 조회 방식은 페치 조인이나 @BatchSize 를 사용할 수 있다.
코드를 거의 수정하지 않고 옵션만 약간 수정해서 다양한 최적화가 가능하다.
반면에 DTO 조회 방식은 최적화 또는 최적화 방식을 변경할 때 많은 코드를 변경해야 한다.)

트래픽이 많다면 캐시로 처리하자. 위의 방법은 큰 영향을 할 수 없다.
엔티티는 캐시에 올리면 안된다.(영속성 컨텍스트에 있을 때 캐시에 올라가면 안바뀌기에 문제 있음)

##### DTO 조회 방식의 선택지
참고로 V4 V5 V6 모두 엔티티에선 해결된다.

1. V4 는 코드가 단순하다.
    특정 주문 한건만 조회하면 사용해도 성능이 잘 나온다.
    Order 데이터가 1건이면 OrderItem 을 찾기 위한 쿼리도 1번만 실행된다.
2. V5 는 코드가 복잡하다 
    여러 주문을 한번에 조회하는 경우, V4 대신 V4 방식을 사용해야한다.
    Order 데이터가 1000건이면 V4에서는 1+ 1000 쿼리가 실행된다.
3. V6 는 다른 접근방식이다.
    쿼리 한번으로 최적화되어 좋아보이지만 Order 기준으로 페이징이 불가능하다.
    실무에서는 이정도 데이터면 수백 수천건 단위로 페이징이 필요하기에 선택하기 어렵다.
    데이터가 많으면 중복 전송이 증가하여 V5 와 비교해서 성능 차이도 미비하다.

package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = this.createMember();
        Book book = this.createBook("JPA 기본", 10000, 10);
        int orderCount = 2; //주문 수량

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, order.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, order.getOrderItems().size(), "주문한 상품 종류수가 정확해야한다");
        assertEquals(10000 * 2, order.getTotalPrice(), "주문 가격은 가격 곱하기 수량이다");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어들어야한다");

    }

    @Test
    public void 상품주문_재고수량초과() {
        //given
        Member member = this.createMember();
        Book book = this.createBook("JPA 기본", 10000, 10);
        int orderCount = 11; //재고보다 많은 주문수량

        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(),
                book.getId(), orderCount));

    }

    @Test
    public void 주문취소() {
        //given
        Member member = this.createMember();
        Book book = this.createBook("JPA 기본", 10000, 10);
        int orderCount = 2; //주문 수량

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order order = orderRepository.findOne(orderId);
        order.cancel();

        assertEquals(10, book.getStockQuantity(),"주문이 취소된 상품은 그만큼 재고가 증가해야한다");
        assertEquals(OrderStatus.CANCEL,order.getStatus(),"주문취소시 상태는 CANCEL");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("임채환");
        member.setAddress(new Address("광주광역시", "수등로", "62307"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        book.setAuthor("김영한");
        book.setIsbn("111111");
        em.persist(book);
        return book;
    }

}
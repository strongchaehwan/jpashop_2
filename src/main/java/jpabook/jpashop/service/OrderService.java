package jpabook.jpashop.service;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) { //count 주문 수량

        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId).orElseThrow();

        //배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setDeliveryStatus(DeliveryStatus.READY);

        //주문 상품
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order); //cascde때문에 orderItem,delivery 같이 persist

        return order.getId();
    }

    //취소

    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티조회
        Order order = orderRepository.findOne(orderId);
        //주문취소
        order.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }
}

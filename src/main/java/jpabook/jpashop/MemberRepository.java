package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId(); //사이드 이펙트가 생길수있으므로 ID값만 조회하는게 좋음
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }


}

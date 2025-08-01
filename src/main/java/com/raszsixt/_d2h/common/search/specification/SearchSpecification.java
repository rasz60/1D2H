package com.raszsixt._d2h.common.search.specification;

import com.raszsixt._d2h.common.search.dto.SearchDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
public class SearchSpecification {
    public static <T> Specification<T> searchWiths(SearchDto searchDto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 컬럼명 기준 like 검색
            if ( !"".equals(searchDto.getType()) && !"".equals(searchDto.getKeyword()) ) {
                predicates.add(
                        cb.like(root.get(searchDto.getType()), "%" + searchDto.getKeyword() + "%")
                );
            }
            
            // USER 검색 시 탈퇴회원 포함 시 true
            if (! searchDto.isSignOut() ) {
                predicates.add(
                        cb.equal(root.get("userSignOutYn"), "N")
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

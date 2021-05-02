package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.Entity;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUserNameList();

    @Query("select new com.study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    // 전체 totalcount를 할 경우 성능 이슈 발생 가능성이 있으므로 countQuery로 분리
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

//    Slice<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // excuteUpdate를 실행시키는 어노테이션 || 벌크 연산시 flush를 시켜주는 속성
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * EntityGraph를 통해 내부적으로 FetchJoin 사용
     * */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * JPQL을 사용하면서 EntityGraph를 사용하고 싶은 경우
     * */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    /**
     * By...을 쓸때 Team 데이터를 쓸 일이 많을 경우
     * */
    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(@Param("username") String username);

    /**
     * JPA의 변경감지 기능은 스냅샷을 기본으로 해버린다. 이때 오로직 readonly만 할 경우 최적화 할 수 있는 로직
     * (이런것 도입할때 진짜 이게 필요한지에 대하여 한번 쯤 생각해 볼 것)
     * */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(@Param("username") String username);
}
